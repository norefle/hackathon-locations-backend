package com.hackathon

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

object LocalJsonProtocol extends DefaultJsonProtocol {
    implicit val issueTypeFormat = jsonFormat2(IssueType)
    implicit val issueFormat = jsonFormat7(Issue)
    implicit val watchFormat = jsonFormat3(Watch)
}

class LocalServiceActor extends Actor with LocalService {
    def actorRefFactory = context

    def receive = runRoute(route)
}

trait LocalService extends HttpService {

    import LocalJsonProtocol._

    val route =
        path("hazard" / "issue" / RestPath) { watchId =>
            get {
                println("GET /hazard/issue/" ++ watchId.toString)
                respondWithMediaType(`application/json`) {
                    onSuccess(Database.getIssues) {
                        issues => complete(issues)
                    }
                }
            } ~
            post {
                decompressRequest() {
                    entity(as[String]) { request =>
                        println("POST /hazard/issue/" ++ watchId.toString ++ ": " ++ request)
                        val jsonItem = JsonParser(request).convertTo[Issue]
                        respondWithMediaType(`application/json`) {
                            onSuccess(Database.add(jsonItem.copy(creator = watchId.toString))) {
                                item => complete(item)
                            }
                        }
                    }
                }
            }
        } ~
        path("hazard" / "watch" / RestPath) { watchId =>
            get {
                println("GET /hazard/watch/" ++ watchId.toString)
                respondWithMediaType(`application/json`) {
                    onSuccess(Database.getWatches) {
                        watches => complete(watches)
                    }
                }
            } ~
            post {
                decompressRequest() {
                    entity(as[String]) { request =>
                        println("POST /hazard/watch/" ++ watchId.toString ++ ": " ++ request)
                        val jsonItem = JsonParser(request).convertTo[Watch]
                        respondWithMediaType(`application/json`) {
                            onSuccess(Database.add(jsonItem.copy(user = watchId.toString))) {
                                item => complete(item)
                            }
                        }
                    }
                }
            }
        }
}
