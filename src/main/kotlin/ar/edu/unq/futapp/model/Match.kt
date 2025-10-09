package ar.edu.unq.futapp.model

import java.time.LocalDate

data class Match(
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val date: LocalDate,
    val isHomeMatch: Boolean
)
