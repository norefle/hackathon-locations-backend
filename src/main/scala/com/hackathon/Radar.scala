package com.hackathon

case class IssueCombinator(up: Int, right: Int, down: Int, left: Int)

case class Radar(orange: IssueCombinator, red: IssueCombinator)

object Radar {
    def apply(issues: List[Issue]): Radar = {
        /// @todo Calculate amount of issues by sectors.
        val total = issues.length
        val orangeCount = issues.filter(_.severity == 1).length
        val redCount = total - orangeCount
        val orange = IssueCombinator(orangeCount, 0, 0, 0)
        val red = IssueCombinator(redCount, 0, 0, 0)
        Radar(orange, red)
    }
}