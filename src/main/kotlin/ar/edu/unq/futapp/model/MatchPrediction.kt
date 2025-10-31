package ar.edu.unq.futapp.model

data class MatchPrediction(
    val homeTeam: String,
    val awayTeam: String,
    val homeWinProbability: Double,
    val drawProbability: Double,
    val awayWinProbability: Double
)
