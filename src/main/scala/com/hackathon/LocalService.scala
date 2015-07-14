package com.hackathon

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

object LocalJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat3(GeoPoint)
}

class LocalServiceActor extends Actor with LocalService {
    def actorRefFactory = context

    def receive = runRoute(route)
}

trait LocalService extends HttpService {
    import LocalJsonProtocol._

    val route =
        path("all") {
            get {
                respondWithMediaType(`application/json`) {
                    onSuccess(Database.getLocations) {
                        locations => complete(locations)
                    }
                }
            }
        } ~
        path("location") {
                post {
                    decompressRequest() {
                        entity(as[String]) { request =>
                            println("Request: " ++ request)
                            val jsonItem = JsonParser(request).convertTo[GeoPoint]
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.add(jsonItem)) {
                                    item => complete(item)
                                }
                            }
                        }
                    }
                }
            }
}
