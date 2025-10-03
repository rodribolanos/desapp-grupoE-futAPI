package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.model.Performance
import ar.edu.unq.futapp.model.PlayerPerformance
import java.io.File
import java.net.URI

object PlayerApiUtils {
    private const val STATIC_PATH = "src/test/resources/static"
    fun playerPerformancePageUri(): URI = File("$STATIC_PATH/player-performance-with-results.html").toURI()
    fun playerWithoutHistoryUri(): URI = File("$STATIC_PATH/player-performance-without-history.html").toURI()

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
            ),
            Performance(
                season = "2024",
                team = "River Plate",
                competition = "ICL",
                appearances = 1,
                goals = 0,
                assists = 0,
                aerialWons = 0.0,
                rating = 6.5
            )
        )
    )
}