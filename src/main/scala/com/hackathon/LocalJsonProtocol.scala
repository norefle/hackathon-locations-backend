package com.hackathon

import spray.json.DefaultJsonProtocol

object LocalJsonProtocol extends DefaultJsonProtocol {
    implicit val issueTypeFormat = jsonFormat2(IssueType.apply)
    implicit val issueFormat = jsonFormat8(Issue.apply)
    implicit val issueCombinatorFormat = jsonFormat4(IssueCombinator)
    implicit val radarFormat = jsonFormat2(Radar.apply)
    implicit val distancedIssueFormat = jsonFormat7(DistancedIssue)
    implicit val timestampedIssueFormat = jsonFormat6(TimestampedIssue)
    implicit val issuesSinceFormat = jsonFormat2(IssuesSince)
    implicit val pointFormat = jsonFormat2(Point)
    implicit val watchPositionFormat = jsonFormat7(WatchPosition)
}
