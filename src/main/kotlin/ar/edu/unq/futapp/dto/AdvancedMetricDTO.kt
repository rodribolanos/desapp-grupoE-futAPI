package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.AdvancedMetric

data class AdvancedMetricDTO(
    val teamName: String,
    val mostPlayedLineUp: String,
    val highestYellowCardsPeriod: String,
    val highestGoalsScoredPeriod: String,
    val highestGoalsConcededPeriod: String
)

fun AdvancedMetric.toDTO(): AdvancedMetricDTO {
    return AdvancedMetricDTO(
        teamName = this.teamName,
        mostPlayedLineUp = this.mostPlayedLineUp,
        highestYellowCardsPeriod = this.highestYellowCardsPeriod,
        highestGoalsScoredPeriod = this.highestGoalsScoredPeriod,
        highestGoalsConcededPeriod = this.highestGoalsConcededPeriod
    )
}
