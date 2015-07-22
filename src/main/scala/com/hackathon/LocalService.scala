package com.hackathon

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing.HttpService
import spray.http.HttpHeaders.RawHeader

import scala.concurrent.ExecutionContext.Implicits.global

class LocalServiceActor extends Actor with LocalService {
    def actorRefFactory = context

    def receive = runRoute(route)
}

trait LocalService extends HttpService {

    import LocalJsonProtocol._

    val route =
        path("hazard" / "issue" / "report" / RestPath) { watchId =>
            println("GET /hazard/issue/report/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "lat".as[Double], "lon".as[Double], "severity".as[Int], "heading".as[Double], "speed".as[Double]) {
                    (cmd, lat, lon, severity, heading, speed) => {
                        println("?" ++ cmd ++ " " ++
                            lat.toString ++ " " ++
                            lon.toString ++ " " ++
                            severity.toString ++ " " ++
                            heading.toString ++ " " ++
                            speed.toString
                        )
                        if ("post" == cmd) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.add(Issue(lat, lon, severity, watchId.toString))) {
                                    _ => complete( """{ "code": 0, "description": "Success" }""")
                                }
                            }
                        } else reject
                    }
                }
            }
        } ~
        path("hazard" / "issue" / "count" / RestPath) { watchId =>
            println("GET /hazard/issue/count/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "lat".as[Double], "lon".as[Double], "heading".as[Double], "radius".as[Double]) {
                    (cmd, lat, lon, heading, radius) => {
                        println("?" ++ cmd ++ " " ++
                            lat.toString ++ " " ++
                            lon.toString ++ " " ++
                            heading.toString ++ " " ++
                            radius.toString
                        )
                        if ("get" == cmd) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.getAround(lat, lon, radius)) {
                                    issues => complete(Radar(issues))
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "issue" / "next" / RestPath) { watchId =>
            println("GET /hazard/issue/next/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "lat".as[Double], "lon".as[Double], "heading".as[Double], "radius".as[Double]) {
                    (cmd, lat, lon, heading, radius) => {
                        println("?" ++ cmd ++ " " ++
                            lat.toString ++ " " ++
                            lon.toString ++ " " ++
                            heading.toString ++ " " ++
                            radius.toString
                        )
                        if ("get" == cmd) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.getAround(lat, lon, radius)) { issues =>
                                    if (0 < issues.length) complete(issues.sortBy(_.distance).head)
                                    else complete("""{ "count": 0 } """)
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "issue" / "confirm" / RestPath) { watchId =>
            println("GET /hazard/issue/confirm/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "id".as[String], "confirm".as[Int]) {
                    (cmd, id, confirm) => {
                        println("?" ++ cmd ++ " " ++
                            id ++ " " ++
                            confirm.toString
                        )
                        if ("post" == cmd) {
                            respondWithMediaType(`application/json`) {
                                if (confirm == 0) {
                                    onSuccess(Database.removeIssue(id)) {
                                        _ => complete( """{ "code": 0, "description": "Success" }""")
                                    }
                                } else complete( """{ "code": 0, "description": "Success" }""")
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "issue" / "new" / RestPath) { watchId =>
            println("GET /hazard/issue/new/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "since".as[Long]) {
                    (cmd, since) => {
                        println("?" ++ cmd ++ " " ++ since.toString)
                        if ("get" == cmd) {
                            respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
                                respondWithMediaType(`application/json`) {
                                    onSuccess(Database.getIssuesSince(since)) {
                                            issues => complete(IssuesSince(issues.length, issues.sortBy(_.timestamp)))
                                        }
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "issue" / "update" / RestPath) { watchId =>
            println("GET /hazard/issue/update/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "id".as[String], "severity".as[Int], "type".as[Int]) {
                    (cmd, id, severity, `type`) => {
                        println("?" ++ cmd ++ " " ++
                            id ++ " " ++
                            severity.toString ++ " " ++
                            `type`.toString
                        )
                        if ("post" == cmd) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.updateIssue(id, severity, `type`)) {
                                    _ => complete( """{ "code": 0, "description": "Success" }""")
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        }
}
