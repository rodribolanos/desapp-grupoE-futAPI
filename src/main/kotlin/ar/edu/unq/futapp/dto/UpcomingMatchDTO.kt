package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.UpcomingMatch

data class UpcomingMatchDTO(
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val tournament: String?
)

fun UpcomingMatch.toDTO(): UpcomingMatchDTO = UpcomingMatchDTO(
    date = this.date,
    homeTeam = this.homeTeam,
    awayTeam = this.awayTeam,
    tournament = this.tournament
)

fun List<UpcomingMatch>.toDTO(): List<UpcomingMatchDTO> = this.map { it.toDTO() }
