package com.hackathon

case class Point(latitude: Double, longitude: Double)

case class Watch(
    user: String,
    description: String,
    splits: List[Point],
    traveled: Double
)

object Watch {
    def apply(id: String, lat: Double, lon: Double): Watch = Watch(id, "Vivoactive watch", List(Point(lat, lon)), 0)
}
