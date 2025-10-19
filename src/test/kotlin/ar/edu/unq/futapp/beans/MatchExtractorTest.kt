package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.utils.MatchApiUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import ar.edu.unq.futapp.model.Match
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchExtractorTest {

    private lateinit var browser: JsoupWebBrowser
    private val scraper = MatchExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Parses Barcelona matches from fixtures page")
    fun whenValidFixturesPage_thenParsesMatches() {
        val expectedMatches = MatchApiUtils.expectedBarcelonaMatches()
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())

        assertEquals(expectedMatches.size, teamMatches.matches.size, "Should parse correct number of matches")

        expectedMatches.forEachIndexed { index, expected ->
            val actual = teamMatches.matches[index]
            assertMatchEquals(expected, actual, "Match at index $index")
        }
    }

    @Test
    @DisplayName("Parses home team correctly")
    fun whenValidFixturesPage_thenParsesHomeTeam() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())

        assertTrue(teamMatches.matches.isNotEmpty(), "Should have matches")

        val firstMatch = teamMatches.matches[0]
        assertEquals("Mallorca", firstMatch.homeTeam)
        assertEquals("Barcelona", firstMatch.awayTeam)
    }

    @Test
    @DisplayName("Parses scores correctly")
    fun whenValidFixturesPage_thenParsesScores() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())

        val firstMatch = teamMatches.matches[0] // Mallorca 0-3 Barcelona
        assertEquals(0, firstMatch.homeScore)
        assertEquals(3, firstMatch.awayScore)
    }

    @Test
    @DisplayName("Determines home/away correctly for Barcelona")
    fun whenValidFixturesPage_thenDeterminesHomeAway() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())

        val awayMatch = teamMatches.matches[0] // Mallorca vs Barcelona (away)
        assertFalse(awayMatch.isHomeMatch, "Mallorca vs Barcelona should be away match")

        val homeMatch = teamMatches.matches.first { it.homeTeam == "Barcelona" }
        assertTrue(homeMatch.isHomeMatch, "Barcelona vs X should be home match")
    }

    @Test
    @DisplayName("Parses dates correctly")
    fun whenValidFixturesPage_thenParsesDates() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())

        val firstMatch = teamMatches.matches[0]
        assertEquals(LocalDate.of(2025, 8, 16), firstMatch.date)
    }

    @Test
    @DisplayName("Ignores future matches without results")
    fun whenFixturesPageWithFutureMatches_thenIgnoresThem() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        // Act
        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())
        val matches = teamMatches.matches

        matches.forEach{ match ->
            assertTrue(
                match.homeScore >= 0 && match.awayScore >= 0,
                "Match ${match.homeTeam} vs ${match.awayTeam} should have valid scores"
            )
        }
    }

    @Test
    @DisplayName("Counts wins, draws and losses correctly")
    fun whenValidFixturesPage_thenCountsResultsCorrectly() {
        val uri = MatchApiUtils.barcelonaFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val teamMatches = scraper.getLastMatchesFromUrl(browser, uri.toString())
        val matches = teamMatches.matches

        val wins = matches.count { match ->
            (match.isHomeMatch && match.homeScore > match.awayScore) || (!match.isHomeMatch && match.awayScore > match.homeScore)
        }
        val draws = matches.count { it.homeScore == it.awayScore }
        val losses = matches.count { match ->
            (match.isHomeMatch && match.homeScore < match.awayScore) || (!match.isHomeMatch && match.awayScore < match.homeScore)
        }

        assertEquals(7, wins, "Should have 7 wins")
        assertEquals(1, draws, "Should have 1 draw")
        assertEquals(2, losses, "Should have 2 losses")
    }

    private fun assertMatchEquals(expected: Match, actual: Match, message: String) {
        assertEquals(expected.homeTeam, actual.homeTeam, "$message: home team mismatch")
        assertEquals(expected.awayTeam, actual.awayTeam, "$message: away team mismatch")
        assertEquals(expected.homeScore, actual.homeScore, "$message: home score mismatch")
        assertEquals(expected.awayScore, actual.awayScore, "$message: away score mismatch")
        assertEquals(expected.date, actual.date, "$message: date mismatch")
        assertEquals(expected.isHomeMatch, actual.isHomeMatch, "$message: isHomeMatch mismatch")
    }
}