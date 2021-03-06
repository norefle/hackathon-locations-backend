package com.hackathon

import com.hackathon.BSONSerializer.PointFormatter
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import reactivemongo.core.nodeset.Authenticate
import spray.routing.directives.OnSuccessFutureMagnet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.joda.time.DateTime
import com.typesafe.config.ConfigFactory


object BSONSerializer {
    implicit object PointFormatter extends BSONDocumentReader[Point] with BSONDocumentWriter[Point] {
        def read(bson: BSONDocument): Point =
            Point(
                bson.getAs[Double]("latitude").get,
                bson.getAs[Double]("longitude").get
            )

        def write(item: Point): BSONDocument =
            BSONDocument(
                "latitude" -> item.latitude,
                "longitude" -> item.longitude
            )
    }

    implicit object WatchFormatter extends BSONDocumentReader[Watch] with BSONDocumentWriter[Watch] {
        def read(bson: BSONDocument): Watch =
            Watch(
                bson.getAs[String]("user").get,
                bson.getAs[String]("description").getOrElse(""),
                bson.getAs[List[Point]]("splits").getOrElse(List.empty),
                bson.getAs[Double]("traveled").getOrElse(0.0),
                bson.getAs[Double]("heading").getOrElse(0.0),
                bson.getAs[Double]("speed").getOrElse(0.0)
            )

        def write(watch: Watch): BSONDocument =
            BSONDocument(
                "user" -> watch.user,
                "description" -> watch.description,
                "splits" -> watch.splits,
                "traveled" -> watch.traveled,
                "heading" -> watch.heading,
                "speed" -> watch.speed
            )
    }

    implicit object IssueFormatter extends BSONDocumentReader[Issue] with BSONDocumentWriter[Issue] {
        def read(bson: BSONDocument): Issue =
            Issue(
                bson.getAs[BSONObjectID]("_id").get.stringify,
                bson.getAs[Long]("timestamp").get,
                bson.getAs[Long]("type").get,
                bson.getAs[String]("name").get,
                bson.getAs[Long]("severity").get,
                bson.getAs[Double]("longitude").get,
                bson.getAs[Double]("latitude").get,
                bson.getAs[String]("creator").get
            )

        def write(item: Issue): BSONDocument =
            BSONDocument(
                "timestamp" -> DateTime.now.getMillis,
                "type" -> item.issueType,
                "name" -> item.issueName,
                "severity" -> item.severity,
                "longitude" -> item.longitude,
                "latitude" -> item.latitude,
                "creator" -> item.creator
            )
    }

    implicit object IssueTypeFormatter extends BSONDocumentReader[IssueType] with BSONDocumentWriter[IssueType] {
        def read(bson: BSONDocument): IssueType =
            IssueType(
                bson.getAs[Long]("type").get,
                bson.getAs[String]("name").get
            )

        def write(issueType: IssueType): BSONDocument =
            BSONDocument(
                "type" -> issueType.issueType,
                "name" -> issueType.name
            )
    }
}

object Database {
    import BSONSerializer.{IssueFormatter, WatchFormatter, IssueTypeFormatter}
    val config = ConfigFactory.load()

    val ADDRESS = config.getString("database.server")
    val DATABASE = config.getString("database.name")
    val USER = config.getString("database.user")
    val PASSWD = config.getString("database.password")
    val ISSUES = "issue"
    val ISSUETYPES = "issuetype"
    val WATCHES = "watch"

    val driver = new MongoDriver
    val credentials = List(Authenticate(DATABASE, USER, PASSWD))
    val connection = driver.connection(List(ADDRESS), authentications = credentials)
    val db = connection(DATABASE)
    val issues = db(ISSUES)
    val issueTypes = db(ISSUETYPES)
    val watches = db(WATCHES)

    private def getDistance(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double = {
        val earthRadius = 6378.137 // km
        val dlat = (latitude1 - latitude2) * Math.PI / 180.0
        val dlon = (longitude1 - longitude2) * Math.PI / 180.0
        val a = Math.sin(dlat / 2) * Math.sin( dlat / 2) +
            Math.cos(latitude1 * Math.PI / 180) * Math.cos(latitude2 * Math.PI / 180) *
                Math.sin(dlon / 2) * Math.sin(dlon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        earthRadius * c * 1000
    }

    private def angle(center: Point, heading: Double, issue: Point): Double = {
        val dlat = issue.latitude - center.latitude
        val dlon = issue.longitude - center.longitude
        (Math.toDegrees(Math.atan2(dlat, dlon)) - heading) % 360.0
    }

    private def closeEnough(latitude: Double, longitude: Double, radius: Double)(issue: DistancedIssue): Boolean = {
        println(s"Distance is ${issue.distance}m")
        issue.distance <= radius
    }

    private def issueToDistanced(latitude: Double, longitude: Double, heading: Double)(issue: Issue): DistancedIssue =
        DistancedIssue(
            1,
            issue.id,
            issue.issueType,
            issue.severity,
            issue.latitude,
            issue.longitude,
            getDistance(latitude, longitude, issue.latitude, issue.longitude),
            angle(Point(latitude, longitude), heading, Point(issue.latitude, issue.longitude))
        )

    private def issueToTimestamped(issue: Issue): TimestampedIssue = TimestampedIssue(
        issue.timestamp,
        issue.id,
        issue.severity,
        issue.issueType,
        issue.latitude,
        issue.longitude
    )

    private def watchToWatchPosition(watch: Watch): WatchPosition = {
        val lastPosition = if (watch.splits.isEmpty) Point(0, 0) else watch.splits.last
        WatchPosition(
            watch.user,
            watch.description,
            lastPosition.latitude,
            lastPosition.longitude,
            watch.splits,
            watch.heading,
            watch.speed
        )
    }

    def add(item: Issue): Future[Issue] = issues.insert(item).map { case _ => item }

    def add(item: Watch): Future[Watch] = watches.insert(item).map { case _ => item }

    def add(item: IssueType): Future[IssueType] = issueTypes.insert(item).map { case _ => item }

    def getIssues: Future[List[Issue]] =
        issues.find(BSONDocument.empty).cursor[Issue].collect[List]()

    def getWatch(id: String): Future[List[WatchPosition]] =
        watches.find(BSONDocument("user" -> id)).cursor[Watch].collect[List]().map(_.map(watchToWatchPosition(_)))

    def getAround(latitude: Double, longitude: Double, radius: Double, heading: Double): Future[List[DistancedIssue]] = {
        println("Get around here", latitude, longitude, radius)
        issues.find(BSONDocument("$and" ->
                BSONArray(
                    BSONDocument("latitude" -> BSONDocument("$gt" -> (latitude - 0.01))),
                    BSONDocument("latitude" -> BSONDocument("$lt" -> (latitude + 0.01))),
                    BSONDocument("longitude" -> BSONDocument("$gt" -> (longitude - 0.01))),
                    BSONDocument("longitude" -> BSONDocument("$lt" -> (longitude + 0.01)))
                )
            )
        )
        .cursor[Issue]
        .collect[List]()
        .map(_.map(issueToDistanced(latitude, longitude, heading))
        .filter(closeEnough(latitude, longitude, radius)))
    }

    def removeIssue(id: String) = issues.remove(BSONDocument("_id" -> BSONObjectID(id)))

    def getIssuesSince(since: Long): Future[List[TimestampedIssue]] =
        issues.find(
            BSONDocument("timestamp" -> BSONDocument("$gt" -> since))
        ).cursor[Issue].collect[List]().map(_.map(issueToTimestamped))

    def updateIssue(id: String, severity: Long, issueType: Long) =
        issues.update(
            BSONDocument("_id" -> BSONObjectID(id)),
            BSONDocument("$set" -> BSONDocument("severity" -> severity, "type" -> issueType))
        )

    def setOrigin(watchId: String, lat: Double, lon: Double, heading: Double) =
        watches.update(
            BSONDocument("user" -> watchId),
            Watch(watchId, lat, lon),
            upsert = true
        )

    def addSplit(watchId: String, lat: Double, lon: Double, heading: Double, speed: Double) =
        watches.update(
            BSONDocument("user" -> watchId),
            BSONDocument(
                "$set" -> BSONDocument("heading" -> heading, "speed" -> speed),
                "$push" -> BSONDocument("splits" -> PointFormatter.write(Point(lat, lon)))
            )
        )

}
