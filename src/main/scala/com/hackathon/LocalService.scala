package com.hackathon

import java.io._
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import spray.http._
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

    val config = ConfigFactory.load()
    val authorized = config.getStringList("database.authorized")

    def hasAccess(clientId: String): Boolean = authorized.contains(clientId)

    def readContent(name: String): String = scala.io.Source.fromFile(name).getLines.mkString("\n")

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
                        if ("post" == cmd && hasAccess(watchId.toString)) {
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
                        if ("get" == cmd && hasAccess(watchId.toString)) {
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
                        if ("get" == cmd && hasAccess(watchId.toString)) {
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
                        if ("post" == cmd && hasAccess(watchId.toString)) {
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
                        if ("get" == cmd && hasAccess(watchId.toString)) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.getIssuesSince(since)) {
                                        issues => complete(IssuesSince(issues.length, issues.sortBy(_.timestamp)))
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
                        if ("post" == cmd && hasAccess(watchId.toString)) {
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
        } ~
        path("hazard" / "watch" / "start" / RestPath) { watchId =>
            println("GET /hazard/watch/start/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "lat".as[Double], "lon".as[Double], "heading".as[Double]) {
                    (cmd, lat, lon, heading) => {
                        println("?" ++ cmd ++ " " ++
                            lat.toString ++ " " ++
                            lon.toString ++ " " ++
                            heading.toString
                        )
                        if ("post" == cmd && hasAccess(watchId.toString)) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.setOrigin(watchId.toString, lat, lon, heading)) {
                                    _ => complete( """{ "code": 0, "description": "Success" }""")
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "watch" / "report" / RestPath) { watchId =>
            println("GET /hazard/watch/report/" ++ watchId.toString)
            get {
                parameters("cmd".as[String], "lat".as[Double], "lon".as[Double], "heading".as[Double], "speed".as[Double]) {
                    (cmd, lat, lon, heading, speed) => {
                        println("?" ++ cmd ++ " " ++
                            lat.toString ++ " " ++
                            lon.toString ++ " " ++
                            heading.toString ++ " " ++
                            speed.toString
                        )
                        if ("post" == cmd && hasAccess(watchId.toString)) {
                            respondWithMediaType(`application/json`) {
                                onSuccess(Database.addSplit(watchId.toString, lat, lon, heading, speed)) {
                                    _ => complete( """{ "code": 0, "description": "Success" }""")
                                }
                            }
                        }
                        else reject
                    }
                }
            }
        } ~
        path("hazard" / "watch" / "last" / RestPath) { watchId =>
            println("GET /hazard/watch/last/" ++ watchId.toString)
            get {
                respondWithMediaType(`application/json`) {
                    if (hasAccess(watchId.toString)) {
                        onSuccess(Database.getWatch(watchId.toString)) {
                            watches => if (!watches.isEmpty) complete(watches.head) else reject
                        }
                    }
                    else reject
                }
            }
        } ~
        path("") {
            get {
                respondWithMediaType(`text/html`) {
                    complete(readContent("public/index.html"))
                }
            }
        } ~
        path("img" / "favicon.ico") {
            println("GET /img/favicon.ico")
            get {
                complete(HttpEntity(`image/x-icon`, HttpData(new File("public/img/favicon.ico"))))
            }
        } ~
        path("img" / RestPath) { image =>
            println("GET /img/" ++ image.toString)
            get {
                complete(HttpEntity(`image/png`, HttpData(new File("public/img/" ++ image.toString))))
            }
        }
}
