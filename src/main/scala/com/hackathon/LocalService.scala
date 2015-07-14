package com.hackathon

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.json.JsonParser
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

class LocalServiceActor extends Actor with LocalService {
    def actorRefFactory = context

    def receive = runRoute(route)
}

trait LocalService extends HttpService {
    val route =
        path("all") {
            get {
                respondWithMediaType(`application/json`) {
                    complete { """{ "all": [] }""" }
                }
            }
        }
}
