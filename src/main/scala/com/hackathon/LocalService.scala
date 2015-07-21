package com.hackathon

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing.HttpService

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
            //GET /hazard/issue/next/${watchid}?cmd=get&lat=-xx.xxxxxx&lon=-xx.xxxxxx&heading=xxx.xxx&radius=xxx.xx
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
                                onSuccess(Database.getAround(lat, lon, radius)) { issues: List[DistancedIssue] =>
                                    if (0 < issues.length) complete(issues.sortBy(_.distance).head)
                                    else complete("""{ "count": 0 } """)
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        }




        /*~
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
        } ~
        path("hazard" / "around" / RestPath) { watchId =>
            get {
                parameters("lat".as[Double], "lon".as[Double], "alt".as[Double], "radius".as[Int]) { (lat, lon, alt, radius) =>
                    println("GET /hazard/around/"
                        ++ watchId.toString ++ " "
                        ++ lat.toString ++ " "
                        ++ lon.toString ++ " "
                        ++ alt.toString ++ " "
                        ++ radius.toString
                    )
                    respondWithMediaType(`application/json`) {
                        onSuccess(Database.getAround(lat, lon, alt, radius)) {
                            issues => complete(issues)
                        }
                    }
                }
            }
        }*/
}
