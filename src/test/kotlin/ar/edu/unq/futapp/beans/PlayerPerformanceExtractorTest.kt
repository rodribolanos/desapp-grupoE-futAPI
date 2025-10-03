package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.utils.PlayerApiUtils
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
}