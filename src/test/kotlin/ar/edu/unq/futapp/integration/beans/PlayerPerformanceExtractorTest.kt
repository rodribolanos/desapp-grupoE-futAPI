package ar.edu.unq.futapp.integration.beans

import ar.edu.unq.futapp.beans.PlayerPerformanceExtractor
import ar.edu.unq.futapp.utils.JsoupWebBrowser
import ar.edu.unq.futapp.utils.PlayerApiUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class PlayerPerformanceExtractorTest {
    lateinit var browser : JsoupWebBrowser
    private val extractor = PlayerPerformanceExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Parses player performance from player performance page")
    fun whenValidPerformancePage_thenParsesPerformance() {
        val expectedPerformance = PlayerApiUtils.expectedPlayerPerformance()
        val uri = PlayerApiUtils.playerPerformancePageUri()
        browser = JsoupWebBrowser(uri)
        val performance = extractor.getPlayerPerformanceFromUrl(browser, uri.toString())
        assert(expectedPerformance.name == performance.name)
        assert(expectedPerformance.seasons.size == performance.seasons.size)
        expectedPerformance.seasons.forEachIndexed { index, season ->
            val parsedSeason = performance.seasons[index]
            assert(season == parsedSeason)
        }
    }

    @Test
    @DisplayName("Gives an empty performance when the player has no history")
    fun whenNoHistory_thenThrowsParsingException() {
        val uri = PlayerApiUtils.playerWithoutHistoryUri()
        browser = JsoupWebBrowser(uri)
        val performance = extractor.getPlayerPerformanceFromUrl(browser, uri.toString())
        assert(performance.seasons.isEmpty())
        assert(performance.name == "Álvaro Madrid")
    }

    @Test
    @DisplayName("Parses player performance with more than 7 seasons from player performance page")
    fun whenValidBarcelonaPerformancePage_thenParsesPerformance() {
        val expectedPerformance = PlayerApiUtils.playerPerformanceWith7SeasonsExpected()
        val uri = PlayerApiUtils.playerPerformanceWith7SeasonsUri()
        browser = JsoupWebBrowser(uri)
        val performance = extractor.getPlayerPerformanceFromUrl(browser, uri.toString())

        assertEquals(expectedPerformance.name, performance.name)
        assertEquals(expectedPerformance.seasons.size, performance.seasons.size)
        expectedPerformance.seasons.forEachIndexed { index, season ->
            val parsedSeason = performance.seasons[index]
            assertEquals(season, parsedSeason)
        }
    }

}