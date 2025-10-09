package ar.edu.unq.futapp.model

data class TeamStats(
    val teamName: String,
    val homeWins: Int,
    val homeDraws: Int,
    val homeLosses: Int,
    val awayWins: Int,
    val awayDraws: Int,
    val awayLosses: Int,
    val goalsScored: Int,
    val goalsConceded: Int,
    val recentForm: List<MatchResult>
)