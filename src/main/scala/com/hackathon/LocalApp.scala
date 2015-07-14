package com.hackathon

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object LocalApp extends App {
    implicit val system = ActorSystem("lockal-server")

    val service = system.actorOf(Props[LocalServiceActor], "api-service")

    implicit val timeout = Timeout(5.seconds)

    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
