package com.hackathon

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._
import spray.http.MediaTypes._

class LocalServiceSpec extends Specification with Specs2RouteTest with LocalService {
    def actorRefFactory = system

    "LocalService" should {
        "returns error 404 for GET request to root path" in {
            Get() ~> route ~> check {
                handled must beFalse
            }
        }
    }
}
