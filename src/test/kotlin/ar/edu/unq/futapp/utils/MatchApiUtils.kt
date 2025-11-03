package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.utils.builders.MatchBuilder
import ar.edu.unq.futapp.model.Match
import java.io.File
import java.net.URI
import java.time.LocalDate

object MatchApiUtils {
    private const val STATIC_PATH = "src/test/resources/static"

    fun barcelonaFixturesPageUri(): URI = File("$STATIC_PATH/team-history-with-matches.html").toURI()

    fun expectedBarcelonaMatches(): List<Match> = listOf(
        MatchBuilder()
            .withHomeTeam("Mallorca")
            .withAwayTeam("Barcelona")
            .withHomeScore(0)
            .withAwayScore(3)
            .withDate(LocalDate.of(2025, 8, 16))
            .withIsHomeMatch(false)
            .build(),
        MatchBuilder()
            .withHomeTeam("Levante")
            .withAwayTeam("Barcelona")
            .withHomeScore(2)
            .withAwayScore(3)
            .withDate(LocalDate.of(2025, 8, 23))
            .withIsHomeMatch(false)
            .build(),
        MatchBuilder()
            .withHomeTeam("Rayo Vallecano")
            .withAwayTeam("Barcelona")
            .withHomeScore(1)
            .withAwayScore(1)
            .withDate(LocalDate.of(2025, 8, 31))
            .withIsHomeMatch(false)
            .build(),
        MatchBuilder()
            .withHomeTeam("Barcelona")
            .withAwayTeam("Valencia")
            .withHomeScore(6)
            .withAwayScore(0)
            .withDate(LocalDate.of(2025, 9, 14))
            .withIsHomeMatch(true)
            .build(),
        MatchBuilder()
            .withHomeTeam("Newcastle")
            .withAwayTeam("Barcelona")
            .withHomeScore(1)
            .withAwayScore(2)
            .withDate(LocalDate.of(2025, 9, 18))
            .withIsHomeMatch(false)
            .build(),
        MatchBuilder()
            .withHomeTeam("Barcelona")
            .withAwayTeam("Getafe")
            .withHomeScore(3)
            .withAwayScore(0)
            .withDate(LocalDate.of(2025, 9, 21))
            .withIsHomeMatch(true)
            .build(),
        MatchBuilder()
            .withHomeTeam("Real Oviedo")
            .withAwayTeam("Barcelona")
            .withHomeScore(1)
            .withAwayScore(3)
            .withDate(LocalDate.of(2025, 9, 25))
            .withIsHomeMatch(false)
            .build(),
        MatchBuilder()
            .withHomeTeam("Barcelona")
            .withAwayTeam("Real Sociedad")
            .withHomeScore(2)
            .withAwayScore(1)
            .withDate(LocalDate.of(2025, 9, 28))
            .withIsHomeMatch(true)
            .build(),
        MatchBuilder()
            .withHomeTeam("Barcelona")
            .withAwayTeam("Paris Saint-Germain")
            .withHomeScore(1)
            .withAwayScore(2)
            .withDate(LocalDate.of(2025, 10, 1))
            .withIsHomeMatch(true)
            .build(),
        MatchBuilder()
            .withHomeTeam("Sevilla")
            .withAwayTeam("Barcelona")
            .withHomeScore(4)
            .withAwayScore(1)
            .withDate(LocalDate.of(2025, 10, 5))
            .withIsHomeMatch(false)
            .build()
    )
}