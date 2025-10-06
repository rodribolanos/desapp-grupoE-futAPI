package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.Performance
import ar.edu.unq.futapp.model.PerformanceAverage
import ar.edu.unq.futapp.model.PlayerPerformance

data class PlayerPerformanceDTO(
    val name: String,
    val seasons: List<Performance>,
    val performanceAverage: PerformanceAverage
)

fun PlayerPerformance.toDTO(): PlayerPerformanceDTO {
    return PlayerPerformanceDTO(
        name = this.name,
        seasons = this.seasons,
        performanceAverage = this.averageSeasons()
    )
}