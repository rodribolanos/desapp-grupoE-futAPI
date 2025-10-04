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
        performanceAverage = averageSeasons(seasons)
    )
}

fun averageSeasons(seasons: List<Performance>): PerformanceAverage {
    val totalSeasons = seasons.size

    return PerformanceAverage(
        totalAppareances = seasons.sumOf { it.appearances },
        totalGoals = seasons.sumOf { it.goals },
        totalAssists = seasons.sumOf { it.assists },
        averageAerialsWon = seasons.sumOf { it.aerialWons },
        averageRating = if (totalSeasons > 0) seasons.sumOf { it.rating } / totalSeasons else 0.0    )
}
