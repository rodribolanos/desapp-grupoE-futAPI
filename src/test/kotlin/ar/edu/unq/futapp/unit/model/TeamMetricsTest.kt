package ar.edu.unq.futapp.unit.model

import ar.edu.unq.futapp.model.Winner
import ar.edu.unq.futapp.utils.builders.FairPlayBuilder
import ar.edu.unq.futapp.utils.builders.TeamMetricsBuilder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TeamMetricsTest {
    private val EPSILON = 0.05

    @Test
    @DisplayName("Goals Average Comparison - Team 1 wins when its average is significantly higher")
    fun team1Wins_whenGoalsAverageIsHigher() {
        val team1 = TeamMetricsBuilder().withGoalsAverage(2.0).build()
        val team2 = TeamMetricsBuilder().withGoalsAverage(1.5).build()
        val result = team1.compareTo(team2).goalsAverage

        assertEquals(Winner.TEAM1, result.betterTeam)
        assertEquals(2.0, result.team1Value)
        assertEquals(1.5, result.team2Value)
    }

    @Test
    @DisplayName("Goals Average Comparison - Team 2 wins when its average is significantly higher")
    fun team2Wins_whenGoalsAverageIsHigher() {

        val team1 = TeamMetricsBuilder().withGoalsAverage(2.0).build()
        val team2 = TeamMetricsBuilder().withGoalsAverage(2.5).build()
        val result = team1.compareTo(team2).goalsAverage

        assertEquals(Winner.TEAM2, result.betterTeam)
    }

    @Test
    @DisplayName("Goals Average Comparison - Draw when averages are exactly identical")
    fun comparisonIsDraw_whenGoalsAveragesAreIdentical() {
        val team1 = TeamMetricsBuilder().withGoalsAverage(2.0).build()
        val team2 = TeamMetricsBuilder().withGoalsAverage(2.0).build()
        val result = team1.compareTo(team2).goalsAverage
        assertEquals(Winner.DRAW, result.betterTeam)
    }

    @Test
    @DisplayName("Goals Average Comparison - Draw when difference is within Epsilon tolerance (5%)")
    fun comparisonIsDraw_whenDifferenceIsWithinEpsilonTolerance() {
        val team1 = TeamMetricsBuilder().withGoalsAverage(2.0).build()
        val team2 = TeamMetricsBuilder().withGoalsAverage(team1.goalsAverage * (1 + EPSILON) - 0.001).build()
        val result = team1.compareTo(team2).goalsAverage

        assertEquals(Winner.DRAW, result.betterTeam)
    }

    @Test
    @DisplayName("Fair Play Comparison - Team 1 wins when it has a lower fair play score")
    fun team1Wins_whenFairPlayScoreIsLower() {
        val team1 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(0)
                .withYellowCards(5)
                .build()
        ).build()
        val team2 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(1)
                .withYellowCards(10)
                .build()
        ).build()

        val result = team1.compareTo(team2).fairPlay

        assertEquals(Winner.TEAM1, result.betterTeam)
        assertEquals(5.0, result.team1Value)
        assertEquals(13.0, result.team2Value)
    }

    @Test
    @DisplayName("Fair Play Comparison - Team 2 wins when it has a lower fair play score")
    fun team2Wins_whenFairPlayScoreIsLower() {
        val team1 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(1)
                .withYellowCards(7)
                .build()
        ).build()
        val team2 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(0)
                .withYellowCards(8)
                .build()
        ).build()

        val result = team1.compareTo(team2).fairPlay

        assertEquals(Winner.TEAM2, result.betterTeam)
        assertEquals(10.0, result.team1Value)
        assertEquals(8.0, result.team2Value)
    }

    @Test
    @DisplayName("Fair Play Comparison - Draw when penalty scores are identical")
    fun comparisonIsDraw_whenFairPlayScoresAreEqual() {
        val team1 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(1)
                .withYellowCards(7)
                .build()
        ).build()
        val team2 = TeamMetricsBuilder().withFairPlay(
            FairPlayBuilder()
                .withRedCards(3)
                .withYellowCards(1)
                .build()
        ).build()

        val result = team1.compareTo(team2).fairPlay
        assertEquals(Winner.DRAW, result.betterTeam)
    }

    // 3. POSSESSION PERCENTAGE COMPARISON (Higher is better)
    @Test
    @DisplayName("Possession Comparison - Team 1 wins when its possession percentage is higher")
    fun team1Wins_whenPossessionPercentageIsHigher() {
        val team1 = TeamMetricsBuilder()
            .withPossessionPercentage(55.0)
            .build()
        val team2 = TeamMetricsBuilder()
            .withPossessionPercentage(50.0)
            .build()

        val result = team1.compareTo(team2).posession

        assertEquals(Winner.TEAM1, result.betterTeam)
        assertEquals(55.0, result.team1Value)
        assertEquals(50.0, result.team2Value)
    }

    @Test
    @DisplayName("Possession Comparison - Draw when values are close enough (within Epsilon)")
    fun comparisonIsDraw_whenPossessionValuesAreClose() {
        val team1 = TeamMetricsBuilder()
            .withPossessionPercentage(53.3)
            .build()
        val team2 = TeamMetricsBuilder()
            .withPossessionPercentage(55.0)
            .build()

        val result = team1.compareTo(team2).posession

        assertEquals(Winner.DRAW, result.betterTeam)
    }


    @Test
    @DisplayName("Shots Per Game Comparison - Team 2 wins when it has more shots per game")
    fun team2Wins_whenShotsPerGameIsHigher() {
        val team1 = TeamMetricsBuilder()
            .withShotsPerGame(8.0)
            .build()
        val team2 = TeamMetricsBuilder()
            .withShotsPerGame(16.0)
            .build()

        val result = team1.compareTo(team2).shotsAverage

        assertEquals(Winner.TEAM2, result.betterTeam)
    }

    @Test
    @DisplayName("Shots Per Game Comparison - Draw when values are identical")
    fun comparisonIsDraw_whenShotsPerGameValuesAreIdentical() {
        val team1 = TeamMetricsBuilder()
            .withShotsPerGame(8.0)
            .build()
        val team2 = TeamMetricsBuilder()
            .withShotsPerGame(8.0)
            .build()

        val result = team1.compareTo(team2).shotsAverage

        assertEquals(Winner.DRAW, result.betterTeam)
    }

    @Test
    @DisplayName("Pass Accuracy Comparison - Team 1 wins when its accuracy is higher")
    fun team1Wins_whenPassAccuracyIsHigher() {
        val team1 = TeamMetricsBuilder()
            .withPassAccuracy(88.2)
            .build()
        val team2 = TeamMetricsBuilder()
            .withPassAccuracy(80.0)
            .build()

        val result = team1.compareTo(team2).passAccuracy

        assertEquals(Winner.TEAM1, result.betterTeam)
    }

    @Test
    @DisplayName("Aerial Duels Comparison - Team 2 wins when its aerial win percentage is higher")
    fun team2Wins_whenAerialDuelsWonPercentageIsHigher() {
        val team1 = TeamMetricsBuilder()
            .withAerialDuelsWonPercentage(20.2)
            .build()
        val team2 = TeamMetricsBuilder()
            .withAerialDuelsWonPercentage(65.0)
            .build()

        val result = team1.compareTo(team2).aerialsWinsAverage

        assertEquals(Winner.TEAM2, result.betterTeam)
    }

    // 7. MAIN COMPARISON FUNCTION TEST

    @Test
    @DisplayName("Main Comparison - Should correctly determine winners for all mixed metrics")
    fun compareTo_whenMetricsAreMixed_thenReturnsCorrectWinners() {
        val team1 = TeamMetricsBuilder()
            .withGoalsAverage(3.0)
            .withPassAccuracy(90.0)
            .withFairPlay(
                FairPlayBuilder()
                    .withRedCards(2)
                    .withYellowCards(0)
                    .build()
            )
            .withPossessionPercentage(60.0)
            .build()

        val team2 = TeamMetricsBuilder()
            .withGoalsAverage(2.0)
            .withPassAccuracy(80.0)
            .withPossessionPercentage(50.0)
            .withFairPlay(
                FairPlayBuilder()
                    .withRedCards(0)
                    .withYellowCards(5)
                    .build()
            )
            .withShotsPerGame(20.0)
            .withAerialDuelsWonPercentage(50.0)
            .build()

        val comparison = team1.compareTo(team2)

        assertEquals(Winner.TEAM1, comparison.goalsAverage.betterTeam)
        assertEquals(Winner.TEAM1, comparison.passAccuracy.betterTeam)
        assertEquals(Winner.TEAM1, comparison.posession.betterTeam)

        assertEquals(Winner.TEAM2, comparison.fairPlay.betterTeam)
        assertEquals(Winner.TEAM2, comparison.shotsAverage.betterTeam)

        assertEquals(Winner.DRAW, comparison.aerialsWinsAverage.betterTeam)
    }
}