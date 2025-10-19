package ar.edu.unq.futapp.model

import ar.edu.unq.futapp.builders.MatchBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertTrue

class StatisticalCalculatorTest {

    @Test
    @DisplayName("calculateWeightedWinPercentage - Team with ALL recent wins should have high percentage")
    fun whenTeamHasAllRecentWins_thenWeightedPercentageIsHigh() {
        // Arrange: Create matches where team won ALL recent matches
        val matches = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // Recent WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(8))
                .build(),  // Recent WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(15))
                .build()  // Recent WIN
        )

        // Act
        val winPercentage = StatisticalCalculator.calculateWeightedWinPercentage(
            matches, "TeamA", true
        )

        assertTrue(winPercentage > 0.95, "All wins should give > 95%")
    }

    @Test
    @DisplayName("calculateWeightedWinPercentage - Team with ALL recent losses should have low percentage")
    fun whenTeamHasAllRecentLosses_thenWeightedPercentageIsLow() {
        // Arrange: Create matches where team lost ALL recent matches
        val matches = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(2)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // Recent LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(0)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(8))
                .build(),  // Recent LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(0)
                .withAwayScore(3)
                .withDate(LocalDate.now().minusDays(15))
                .build()  // Recent LOSS
        )

        val winPercentage = StatisticalCalculator.calculateWeightedWinPercentage(
            matches, "TeamA", true
        )

        // Assert: Should be very low (close to 0%)
        assertTrue(winPercentage < 0.05, "All losses should give < 5%")
    }

    @Test
    @DisplayName("calculateWeightedWinPercentage - Recent form matters MORE than old form")
    fun whenRecentWinsAreMoreRelevant_thenWeightedPercentageIsHigherThanOldWins() {
        // Arrange: Two scenarios with same number of wins but different timing

        // Scenario A: Recent wins (2 recent wins, 1 old loss)
        val matchesRecentWins = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // Recent WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(8))
                .build(),  // Recent WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(2)
                .withDate(LocalDate.now().minusDays(30))
                .build()  // Old LOSS
        )

        // Scenario B: Old wins (2 old wins, 1 recent loss)
        val matchesOldWins = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(2)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // Recent LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(25))
                .build(),  // Old WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(30))
                .build()  // Old WIN
        )

        // Act
        val percentageRecentWins = StatisticalCalculator.calculateWeightedWinPercentage(
            matchesRecentWins, "TeamA", true
        )
        val percentageOldWins = StatisticalCalculator.calculateWeightedWinPercentage(
            matchesOldWins, "TeamA", true
        )

        // Assert: Recent wins should give MUCH better percentage than old wins
        assertTrue(percentageRecentWins > percentageOldWins,
            "Recent wins (${percentageRecentWins * 100}%) should be > old wins (${percentageOldWins * 100}%)")

        // The difference should be significant (at least 20 percentage points)
        assertTrue(percentageRecentWins - percentageOldWins > 0.2,
            "Difference should be > 20%: ${(percentageRecentWins - percentageOldWins) * 100}%")

        println("Win % with recent wins: ${percentageRecentWins * 100}%")
        println("Win % with old wins: ${percentageOldWins * 100}%")
        println("Difference: ${(percentageRecentWins - percentageOldWins) * 100}%")
    }

    @Test
    @DisplayName("calculateWeightedWinPercentage - Empty matches should return 0")
    fun whenTeamHasNoMatches_thenReturn0Percentage() {
        val matches = emptyList<Match>()

        val winPercentage = StatisticalCalculator.calculateWeightedWinPercentage(
            matches, "TeamA", true
        )

        assertEquals(0.0, winPercentage, "Empty matches should return 0%")
    }

    @Test
    @DisplayName("calculateWeightedWinPercentage - Should filter by home/away correctly")
    fun whenTeamHasMixedResults_thenWeightedPercentageReflectsBalance() {
        // Arrange: Mix of home and away matches
        val matches = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withAwayTeam("TeamB")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // TeamA home WIN
            MatchBuilder()
                .withHomeTeam("TeamB")
                .withAwayTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(8))
                .build(),  // TeamA away LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withAwayTeam("TeamC")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(15))
                .build()  // TeamA home WIN
        )

        val homeWinPercentage = StatisticalCalculator.calculateWeightedWinPercentage(
            matches, "TeamA", true
        )
        val awayWinPercentage = StatisticalCalculator.calculateWeightedWinPercentage(
            matches, "TeamA", false
        )

        assertTrue(homeWinPercentage > 0.9, "Home win percentage should be high (2 wins)")
        assertTrue(awayWinPercentage < 0.1, "Away win percentage should be low (1 loss)")
    }

    @Test
    @DisplayName("calculateHomeAdvantage - Empty matches should return default advantage")
    fun whenNoMatchesForHomeAdvantage_thenReturnDefault() {
        // Arrange
        val matches = emptyList<Match>()

        // Act
        val homeAdvantage = StatisticalCalculator.calculateHomeAdvantage("TeamA", matches)

        // Assert
        assertEquals(0.5, homeAdvantage, "Empty matches should return default home advantage of 0.5")
    }

    @Test
    @DisplayName("calculateHomeAdvantage - Team with no home matches should return default advantage")
    fun whenTeamHasNoHomeMatches_thenReturnDefault() {
        val matches = listOf(
            MatchBuilder()
                .withAwayTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(2)
                .withDate(LocalDate.now())
                .build() // Away LOSS
        )

        val homeAdvantage = StatisticalCalculator.calculateHomeAdvantage("TeamA", matches)

        assertEquals(0.5, homeAdvantage, "No home matches should return default advantage of 0.5")
    }

    @Test
    @DisplayName("calculateHomeAdvantage - Should calculate overall home advantage")
    fun testCalculateHomeAdvantage() {
        // Arrange: 6 home wins out of 10 matches (60% home advantage)
        val matches = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now())
                .build(),  // Home win
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(0)
                .withDate(LocalDate.now())
                .build(),  // Home win
            MatchBuilder()
                .withHomeScore(3)
                .withAwayScore(3)
                .withDate(LocalDate.now())
                .build(),  // Draw
            MatchBuilder()
                .withHomeScore(0)
                .withAwayScore(1)
                .withDate(LocalDate.now())
                .build(),  // Away win
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(0)
                .withDate(LocalDate.now())
                .build(),  // Home win
            MatchBuilder()
                .withHomeScore(1)
                .withAwayScore(1)
                .withDate(LocalDate.now())
                .build(),  // Draw
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now())
                .build(),  // Home win
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now())
                .build(),  // Home win
            MatchBuilder()
                .withHomeScore(0)
                .withAwayScore(2)
                .withDate(LocalDate.now())
                .build(),  // Away win
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(0)
                .withDate(LocalDate.now())
                .build()   // Home win
        )

        // Act
        val homeAdvantage = StatisticalCalculator.calculateHomeAdvantage("TeamA", matches)

        // Assert
        assertEquals(0.6, homeAdvantage, 0.01, "6 out of 10 home wins = 60%")
        println("Home advantage: ${homeAdvantage * 100}%")
    }

    @Test
    @DisplayName("calculateRecentForm - Empty matches should return default form")
    fun whenNoMatchesForRecentForm_thenReturnDefault() {
        // Arrange
        val matches = emptyList<Match>()

        // Act
        val recentForm = StatisticalCalculator.calculateRecentForm(matches, "TeamA")

        // Assert
        assertEquals(0.5, recentForm, "Empty matches should return default recent form of 0.5")
    }

    @Test
    @DisplayName("calculateRecentForm - Poor recent form should return low score")
    fun testCalculateRecentForm_PoorForm() {
        // Arrange: TeamA lost last 4 matches and drew 1
        val matches = listOf(
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(0)
                .withAwayScore(2)
                .withDate(LocalDate.now().minusDays(1))
                .build(),  // LOSS
            MatchBuilder()
                .withAwayTeam("TeamA")
                .withHomeScore(3)
                .withAwayScore(0)
                .withDate(LocalDate.now().minusDays(8))
                .build(),  // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(1)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(15))
                .build(),  // DRAW
            MatchBuilder()
                .withAwayTeam("TeamA")
                .withHomeScore(2)
                .withAwayScore(1)
                .withDate(LocalDate.now().minusDays(22))
                .build(),  // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withHomeScore(0)
                .withAwayScore(3)
                .withDate(LocalDate.now().minusDays(29))
                .build()  // LOSS
        )

        val form = StatisticalCalculator.calculateRecentForm(matches, "TeamA", lastN = 10)

        assertTrue(form < 0.2, "4 losses + 1 draw should give form < 20%")
    }

    @Test
    @DisplayName("calculateRecentForm - Should give higher form when wins are among most recent matches")
    fun whenWinsAreAmongLastMatches_thenFormIsHigher() {
        val matchesRecentWins = listOf(
            // últimos 5 partidos: W W L L L
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(1))
                .withHomeScore(2)
                .withAwayScore(1)
                .build(), // WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(2))
                .withHomeScore(3)
                .withAwayScore(0)
                .build(), // WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(3))
                .withHomeScore(0)
                .withAwayScore(1)
                .build(), // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(4))
                .withHomeScore(1)
                .withAwayScore(2)
                .build(), // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(5))
                .withHomeScore(0)
                .withAwayScore(2)
                .build()  // LOSS
        )

        val matchesOldWins = listOf(
            // últimos 5 partidos: L L L W W
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(1))
                .withHomeScore(0)
                .withAwayScore(2)
                .build(), // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(2))
                .withHomeScore(1)
                .withAwayScore(2)
                .build(), // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(3))
                .withHomeScore(0)
                .withAwayScore(1)
                .build(), // LOSS
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(4))
                .withHomeScore(3)
                .withAwayScore(0)
                .build(), // WIN
            MatchBuilder()
                .withHomeTeam("TeamA")
                .withDate(LocalDate.now().minusDays(5))
                .withHomeScore(2)
                .withAwayScore(1)
                .build(), // WIN
        )

        val formRecentWins = StatisticalCalculator.calculateRecentForm(matchesRecentWins, "TeamA")
        val formOldWins = StatisticalCalculator.calculateRecentForm(matchesOldWins, "TeamA")

        assertTrue(
            formRecentWins > formOldWins,
            "Wins in more recent matches should yield a higher form value"
        )
    }

}