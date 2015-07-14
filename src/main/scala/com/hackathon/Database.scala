package com.hackathon

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BSONSerializer {
    implicit object LocationWriter extends BSONDocumentWriter[GeoPoint] {
        def write(item: GeoPoint): BSONDocument = {
            BSONDocument(
                "longitude" -> item.longitude,
                "latitude" -> item.latitude,
                "altitude" -> item.altitude
            )
        }
    }

    implicit object LocationReader extends BSONDocumentReader[GeoPoint] {
        def read(bson: BSONDocument): GeoPoint = {
            GeoPoint(
                bson.getAs[Double]("longitude").get,
                bson.getAs[Double]("latitude").get,
                bson.getAs[Double]("altitude").get
            )
        }
    }
}

object Database {
    import BSONSerializer.{LocationReader, LocationWriter}

    val ADDRESS = "localhost"
    val PORT = 27017
    val DATABASE = "hackathon"
    val LOCATIONS = "location"

    val driver = new MongoDriver
    val connection = driver.connection(List(ADDRESS))
    val db = connection(DATABASE)
    val locations = db(LOCATIONS)

    def add(item: GeoPoint): Future[GeoPoint] = {
        locations.insert(item).map { case _ => item }
    }

    def getLocations : Future[List[GeoPoint]] =
        locations.find(BSONDocument.empty).cursor[GeoPoint].collect[List]()
}
