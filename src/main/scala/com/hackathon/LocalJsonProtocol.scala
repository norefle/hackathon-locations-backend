package com.hackathon

import spray.json.DefaultJsonProtocol

object LocalJsonProtocol extends DefaultJsonProtocol {
    implicit val issueTypeFormat = jsonFormat2(IssueType.apply)
    implicit val issueFormat = jsonFormat7(Issue.apply)
    implicit val watchFormat = jsonFormat3(Watch)
    implicit val issueCombinatorFormat = jsonFormat4(IssueCombinator)
    implicit val radarFormat = jsonFormat2(Radar.apply)
}
