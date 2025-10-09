package ar.edu.unq.futapp.builders

import ar.edu.unq.futapp.model.Match
import java.time.LocalDate

class MatchBuilder {
    var homeTeam: String = "No Team A"
    var awayTeam: String = "No Team B"
    var homeScore: Int = 0
    var awayScore: Int = 0
    var date: LocalDate = LocalDate.now()
    var homeMatch: Boolean = true

    fun withHomeTeam(homeTeam: String) = apply { this.homeTeam = homeTeam }
    fun withAwayTeam(awayTeam: String) = apply { this.awayTeam = awayTeam }
    fun withHomeScore(homeScore: Int) = apply { this.homeScore = homeScore }
    fun withAwayScore(awayScore: Int) = apply { this.awayScore = awayScore }
    fun withDate(date: LocalDate) = apply { this.date = date }
    fun withHomeMatch(homeMatch: Boolean) = apply { this.homeMatch = homeMatch }

    fun build() = Match(
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeScore = homeScore,
        awayScore = awayScore,
        date = date,
        isHomeMatch = homeMatch
    )
}