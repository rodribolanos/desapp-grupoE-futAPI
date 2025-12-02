package ar.edu.unq.futapp.utils.builders

import ar.edu.unq.futapp.model.FairPlay
import ar.edu.unq.futapp.model.TeamMetrics

class TeamMetricsBuilder {
    var teamName: String = "Test Team"
    var goalsAverage: Double = 1.0
    var fairPlay: FairPlay = FairPlayBuilder().build()
    var possessionPercentage: Double = 50.0
    var passAccuracy: Double = 80.0
    var shotsPerGame: Double = 10.0
    var aerialDuelsWonPercentage: Double = 50.0

    fun withTeamName(teamName: String) = apply { this.teamName = teamName }
    fun withGoalsAverage(goalsAverage: Double) = apply { this.goalsAverage = goalsAverage }
    fun withFairPlay(fairPlay: FairPlay) = apply { this.fairPlay = fairPlay }
    fun withPossessionPercentage(possessionPercentage: Double) =
        apply { this.possessionPercentage = possessionPercentage }

    fun withPassAccuracy(passAccuracy: Double) = apply { this.passAccuracy = passAccuracy }
    fun withShotsPerGame(shotsPerGame: Double) = apply { this.shotsPerGame = shotsPerGame }
    fun withAerialDuelsWonPercentage(aerialDuelsWonPercentage: Double) =
        apply { this.aerialDuelsWonPercentage = aerialDuelsWonPercentage }

    fun build() = TeamMetrics(
        teamName = teamName,
        goalsAverage = goalsAverage,
        fairPlay = fairPlay,
        possessionPercentage = possessionPercentage,
        passAccuracy = passAccuracy,
        shotsPerGame = shotsPerGame,
        aerialDuelsWonPercentage = aerialDuelsWonPercentage
    )
}