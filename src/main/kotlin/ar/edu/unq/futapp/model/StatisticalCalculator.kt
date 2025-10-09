package ar.edu.unq.futapp.model

import java.time.temporal.ChronoUnit
import kotlin.math.exp

object StatisticalCalculator {

    fun calculateWeightedWinPercentage(
        matches: List<Match>,
        teamName: String,
        isHome: Boolean
    ): Double {
        val relevantMatches = matches.filter {
            if (isHome) it.homeTeam == teamName else it.awayTeam == teamName
        }

        if (relevantMatches.isEmpty()) return 0.0

        val mostRecentDate = relevantMatches.maxOf { it.date }

        var weightedWins = 0.0
        var totalWeight = 0.0

        for (match in relevantMatches) {
            val daysAgo = ChronoUnit.DAYS.between(match.date, mostRecentDate).toDouble()
            val weight = exp(-0.1 * daysAgo) // lim x -> inf = 0 :D
            totalWeight += weight

            val isWin = if (isHome) {
                match.homeScore > match.awayScore
            } else {
                match.awayScore > match.homeScore
            }

            if (isWin) weightedWins += weight
        }

        return weightedWins / totalWeight
    }

    fun calculateHomeAdvantage(teamName: String, allMatches: List<Match>): Double {
        val homeMatches = allMatches.filter { it.homeTeam == teamName }

        if (homeMatches.isEmpty()) return 0.5

        val homeWins = homeMatches.count { it.homeScore > it.awayScore }

        return homeWins.toDouble() / allMatches.size
    }

    fun calculateRecentForm(
        matches: List<Match>,
        teamName: String,
        lastN: Int = 8
    ): Double {
        val recentMatches = matches
            .filter { hasPlayedTheMatch(teamName, it) }
            .sortedByDescending { it.date }
            .take(lastN)

        if (recentMatches.isEmpty()) return 0.5

        val formScore = recentMatches
            .mapIndexed { index, match ->
                val weight = 1.0 - index * 0.1
                val points = when {
                    hasWinTheMatch(teamName, match) -> 3.0
                    itsDraw(match) -> 1.0
                    else -> 0.0
                }
                points * weight
            }
            .sum()

        val maxScore = recentMatches.size * 3.0
        return formScore / maxScore
    }

    private fun hasWinTheMatch(teamName: String, match: Match): Boolean {
        return (match.homeTeam == teamName && match.homeScore > match.awayScore) ||
               (match.awayTeam == teamName && match.awayScore > match.homeScore)
    }

    private fun itsDraw(match: Match): Boolean {
        return match.homeScore == match.awayScore
    }

    private fun hasPlayedTheMatch(teamName: String, match: Match): Boolean {
        return match.homeTeam == teamName || match.awayTeam == teamName
    }
}