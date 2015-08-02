package com.hackathon

case class IssueCombinator(up: Int, right: Int, down: Int, left: Int)

case class Radar(orange: IssueCombinator, red: IssueCombinator)

object Radar {
    def apply(issues: List[DistancedIssue]): Radar = {
        val (orangeList, redList) = issues.partition(_.severity == 1)
        val orange = IssueCombinator(
            orangeList.count(el => -45 < el.angle && el.angle <= 45),
            orangeList.count(el => 45 < el.angle && el.angle <= 135),
            orangeList.count(el => 135 <= el.angle || el.angle < -135),
            orangeList.count(el => -135 < el.angle && el.angle <= -45)
        )
        val red = IssueCombinator(
            redList.count(el => Math.abs(el.angle) <= 45),
            redList.count(el => 45 < el.angle && el.angle <= 135),
            redList.count(el => 135 < el.angle || el.angle < -135),
            redList.count(el => -135 < el.angle && el.angle < -45)
        )
        Radar(orange, red)
    }
}