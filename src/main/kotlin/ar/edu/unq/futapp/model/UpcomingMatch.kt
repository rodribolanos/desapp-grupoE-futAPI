package ar.edu.unq.futapp.model

data class UpcomingMatch(
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val tournament: String?
)
