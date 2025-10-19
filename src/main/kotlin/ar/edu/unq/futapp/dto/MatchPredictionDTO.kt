package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.MatchPrediction

data class MatchPredictionDTO(
    val homeTeam: String,
    val awayTeam: String,
    val homeWinProbability: Double,
    val drawProbability: Double,
    val awayWinProbability: Double
)

fun MatchPrediction.toDTO() = MatchPredictionDTO(
    homeTeam = this.homeTeam,
    awayTeam = this.awayTeam,
    homeWinProbability = this.homeWinProbability,
    drawProbability = this.drawProbability,
    awayWinProbability = this.awayWinProbability
)
