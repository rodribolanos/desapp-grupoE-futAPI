package ar.edu.unq.futapp.model

import lombok.Builder

@Builder
class PlayerPerformance(
    val name: String,
    val seasons: List<Performance>
) {
    fun averageSeasons(): PerformanceAverage {
        if (seasons.isEmpty()) {
            return PerformanceAverage(0, 0, 0, 0.0, 0.0)
        }

        val appearances = seasons.map { it.appearances }.average().toInt()
        val goals = seasons.map { it.goals }.average().toInt()
        val assists = seasons.map { it.assists }.average().toInt()
        val aerialWons = seasons.map { it.aerialWons }.average()
        val rating = seasons.map { it.rating }.average()

        return PerformanceAverage(appearances, goals, assists, aerialWons, rating)
    }
}