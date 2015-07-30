package com.hackathon

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.tools.nsc.Properties

object LocalApp extends App {
    implicit val system = ActorSystem("lockal-server")

    val service = system.actorOf(Props[LocalServiceActor], "api-service")

    implicit val timeout = Timeout(5.seconds)
    val port = Properties.envOrElse("PORT", "8080").toInt

    IO(Http) ? Http.Bind(service, "0.0.0.0", port)
}
