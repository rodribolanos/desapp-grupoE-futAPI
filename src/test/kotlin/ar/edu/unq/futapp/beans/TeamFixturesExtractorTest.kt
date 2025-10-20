package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.UpcomingMatch
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Clock
import java.time.ZoneId

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamFixturesExtractorTest {
    lateinit var browser : JsoupWebBrowser
    private val extractor = TeamFixturesExtractor(
        Clock.fixed(
            LocalDate.of(2025, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        )
    )

    @AfterAll
    fun tearDown() {
        if (this::browser.isInitialized) browser.close()
    }

    @Test
    @DisplayName("Parses upcoming fixtures from team fixtures page")
    fun whenValidTeamFixturesPage_thenParsesUpcomingFixtures() {
        val uri = TeamApiUtils.teamFixturesPageUri()
        browser = JsoupWebBrowser(uri)

        val fixtures = extractor.getUpcomingFixturesFromUrl(browser, uri.toString())

        val expected = listOf(
            UpcomingMatch("18-10-25", "Boca Juniors", "Belgrano", "ALP"),
            UpcomingMatch("27-10-25", "Barracas Central", "Boca Juniors", "ALP"),
            UpcomingMatch("02-11-25", "Estudiantes", "Boca Juniors", "ALP"),
            UpcomingMatch("09-11-25", "Boca Juniors", "River Plate", "ALP"),
            UpcomingMatch("16-11-25", "Boca Juniors", "Tigre", "ALP"),
        )

        Assertions.assertEquals(expected, fixtures)
    }

    @Test
    @DisplayName("Handles malformed team fixtures page")
    fun whenMalformedTeamFixturesPage_thenHandlesParsing() {
        val uri = TeamApiUtils.malformedTeamFixturesPageUri()
        browser = JsoupWebBrowser(uri)
        Assertions.assertThrows(ParsingException::class.java) {
            extractor.getUpcomingFixturesFromUrl(browser, uri.toString())
        }
    }
}
