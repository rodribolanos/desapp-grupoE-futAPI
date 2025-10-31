package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.builders.PerformanceBuilder
import ar.edu.unq.futapp.builders.PlayerPerformanceBuilder
import ar.edu.unq.futapp.model.Performance
import ar.edu.unq.futapp.model.PlayerPerformance
import java.io.File
import java.net.URI

object PlayerApiUtils {
    private const val STATIC_PATH = "src/test/resources/static"
    fun playerPerformancePageUri(): URI = File("$STATIC_PATH/player-performance-with-results.html").toURI()
    fun playerWithoutHistoryUri(): URI = File("$STATIC_PATH/player-performance-without-history.html").toURI()
    fun playerPerformanceWith7SeasonsUri(): URI = File("$STATIC_PATH/player-performance-with-more-than-7-seasons.html").toURI()


    fun expectedPlayerPerformance(): PlayerPerformance = PlayerPerformance(
        name = "Franco Mastantuono",
        seasons = listOf(
            Performance(
                season = "2025/2026",
                team = "Real Madrid",
                competition = "UCL",
                appearances = 2,
                goals = 0,
                assists = 0,
                aerialWons = 0.5,
                rating = 7.2
            ),
            Performance(
                season = "2025/2026",
                team = "Real Madrid",
                competition = "SLL",
                appearances = 4,
                goals = 1,
                assists = 0,
                aerialWons = 0.3,
                rating = 6.82
            ),
            Performance(
                season = "2025",
                team = "River Plate",
                competition = "IWcc",
                appearances = 3,
                goals = 0,
                assists = 0,
                aerialWons = 1.0,
                rating = 6.79
            )
        )
    )

    fun playerPerformanceWith7SeasonsExpected(): PlayerPerformance =
        PlayerPerformanceBuilder()
            .withName("Lamine Yamal")
            .withSeasons(
                listOf(
                    PerformanceBuilder()
                        .withSeason("2025/2026").withTeam("Barcelona").withCompetition("UCL")
                        .withAppearances(1).withGoals(0).withAssists(0).withAerialWons(0.0).withRating(7.08)
                        .build(),
                    PerformanceBuilder()
                        .withSeason("2025/2026").withTeam("Barcelona").withCompetition("SLL")
                        .withAppearances(3).withGoals(2).withAssists(3).withAerialWons(0.0).withRating(8.33)
                        .build(),
                    PerformanceBuilder()
                        .withSeason("2025/2026").withTeam("Spain").withCompetition("WCQ")
                        .withAppearances(2).withGoals(0).withAssists(3).withAerialWons(0.0).withRating(9.34)
                        .build()
                )
            )
            .build()
}