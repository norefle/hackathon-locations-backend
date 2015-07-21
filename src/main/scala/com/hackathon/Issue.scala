package com.hackathon

import org.joda.time.DateTime

case class IssueType(issueType: Long, name: String)

object IssueType {
    def apply(issueType: Long): IssueType = issueType match {
        case 1 => IssueType(1, "Glass on road")
        case 2 => IssueType(2, "Construction")
        case 3 => IssueType(3, "Car on bike lane")
        case 4 => IssueType(4, "Bike crash")
        case _ => IssueType(0, "Unknown")
    }
    def apply: IssueType = apply(0)
}

object Severity {
    def apply(severity: Long): String = severity match {
        case 1 => "orange"
        case 2 => "red"
        case _ => "unknown"
    }
}

case class Issue(
    id: String,
    timestamp: Long,
    issueType: Long,
    issueName: String,
    severity: Long,
    longitude: Double,
    latitude: Double,
    creator: String
)

case class DistancedIssue(count: Int, id: String, `type`: Long, severity: Long, latitude: Double, longitude: Double, distance: Double)

object Issue {
    def apply(latitude: Double, longitude: Double, severity: Long, creator: String): Issue = Issue(
        "",
        DateTime.now.getMillis,
        0,
        IssueType(0).name,
        severity,
        longitude,
        latitude,
        creator
    )
}