package com.hackathon

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import spray.routing.directives.OnSuccessFutureMagnet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.joda.time.DateTime

object BSONSerializer {
    implicit object WatchFormatter extends BSONDocumentReader[Watch] with BSONDocumentWriter[Watch] {
        def read(bson: BSONDocument): Watch =
            Watch(
                bson.getAs[BSONObjectID]("_id").get.stringify,
                bson.getAs[String]("user").get,
                bson.getAs[String]("description").get
            )

        def write(watch: Watch): BSONDocument =
            BSONDocument(
                "_id" -> BSONObjectID.generate.stringify,
                "user" -> watch.user,
                "description" -> watch.description
            )
    }

    implicit object IssueFormatter extends BSONDocumentReader[Issue] with BSONDocumentWriter[Issue] {
        def read(bson: BSONDocument): Issue =
            Issue(
                bson.getAs[Long]("timestamp").get,
                bson.getAs[Long]("type").get,
                bson.getAs[String]("name").get,
                bson.getAs[Double]("longitude").get,
                bson.getAs[Double]("latitude").get,
                bson.getAs[Double]("altitude").get
            )

        def write(item: Issue): BSONDocument =
            BSONDocument(
                "timestamp" -> DateTime.now.getMillis,
                "type" -> item.issueType,
                "name" -> item.name,
                "longitude" -> item.longitude,
                "latitude" -> item.latitude,
                "altitude" -> item.altitude
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

    val ADDRESS = "localhost"
    val PORT = 27017
    val DATABASE = "hackathon"
    val LOCATIONS = "issue"

    val driver = new MongoDriver
    val connection = driver.connection(List(ADDRESS))
    val db = connection(DATABASE)
    val locations = db(LOCATIONS)

    def add(item: Issue): Future[Issue] = locations.insert(item).map { case _ => item }

    def add(item: Watch): Future[Watch] = locations.insert(item).map { case _ => item }

    def add(item: IssueType): Future[IssueType] = locations.insert(item).map { case _ => item }

    def getIssues: Future[List[Issue]] =
        locations.find(BSONDocument.empty).cursor[Issue].collect[List]()

    def getWatches: Future[List[Watch]] =
        locations.find(BSONDocument.empty).cursor[Watch].collect[List]()
}
