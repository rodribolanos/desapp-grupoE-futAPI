package ar.edu.unq.futapp.unit

import ar.edu.unq.futapp.utils.builders.PerformanceBuilder
import ar.edu.unq.futapp.utils.builders.PlayerPerformanceBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class PlayerPerformanceAverageTest {

    @Test
    fun whenPlayerHasNoSeasons_thenReturnZeroAverage() {
        val player = PlayerPerformanceBuilder()
            .withName("Empty Player")
            .withSeasons(emptyList())
            .build()

        val result = player.averageSeasons()

        Assertions.assertEquals(0, result.totalAppareances)
        Assertions.assertEquals(0, result.totalGoals)
        Assertions.assertEquals(0, result.totalAssists)
        Assertions.assertEquals(0.0, result.averageAerialsWon)
        Assertions.assertEquals(0.0, result.averageRating)
    }

    @Test
    fun whenPlayerHasSingleSeason_thenReturnSameValuesAsAverage() {
        val season = PerformanceBuilder()
            .withSeason("2020/21")
            .withCompetition("LaLiga")
            .withTeam("Barcelona")
            .withAppearances(30)
            .withGoals(25)
            .withAssists(10)
            .withAerialWons(2.5)
            .withRating(8.8)
            .build()

        val player = PlayerPerformanceBuilder()
            .withName("Messi")
            .withSeasons(listOf(season))
            .build()

        val result = player.averageSeasons()

        Assertions.assertEquals(30, result.totalAppareances)
        Assertions.assertEquals(25, result.totalGoals)
        Assertions.assertEquals(10, result.totalAssists)
        Assertions.assertEquals(2.5, result.averageAerialsWon)
        Assertions.assertEquals(8.8, result.averageRating)
    }

    @Test
    fun whenPlayerHasMultipleSeasons_thenReturnAverages() {
        val season1 = PerformanceBuilder()
            .withSeason("2020/21")
            .withCompetition("LaLiga")
            .withTeam("Barcelona")
            .withAppearances(30)
            .withGoals(25)
            .withAssists(10)
            .withAerialWons(2.5)
            .withRating(8.8)
            .build()

        val season2 = PerformanceBuilder()
            .withSeason("2021/22")
            .withCompetition("Ligue 1")
            .withTeam("PSG")
            .withAppearances(28)
            .withGoals(6)
            .withAssists(14)
            .withAerialWons(1.0)
            .withRating(7.5)
            .build()

        val player = PlayerPerformanceBuilder()
            .withName("Messi")
            .withSeasons(listOf(season1, season2))
            .build()

        val result = player.averageSeasons()

        Assertions.assertEquals(29, result.totalAppareances)
        Assertions.assertEquals(15, result.totalGoals)
        Assertions.assertEquals(12, result.totalAssists)
        Assertions.assertEquals(1.75, result.averageAerialsWon)
        Assertions.assertEquals(8.15, result.averageRating)
    }
}