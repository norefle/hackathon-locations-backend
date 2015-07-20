package com.hackathon

case class IssueType(issueType: Long, name: String)

case class Issue(
    timestamp: Long,
    issueType: Long,
    name: String,
    longitude: Double,
    latitude: Double,
    altitude: Double
)
