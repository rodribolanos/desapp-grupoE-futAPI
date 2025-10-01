package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamPlayersExtractorTest {
    lateinit var browser : JsoupWebBrowser
    private val extractor = TeamPlayersExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Parses team name from player list page")
    fun whenValidPlayerListPage_thenParsesTeamName() {
        val expectedTeam = TeamApiUtils.expectedTeamForPlayerListPage()
        val uri = TeamApiUtils.playerListPageUri()
        browser = JsoupWebBrowser(TeamApiUtils.playerListPageUri())
        val team = extractor.getTeamFromUrl(browser, uri.toString())
        Assertions.assertEquals(expectedTeam, team)
    }

    @Test
    @DisplayName("Handles malformed player list page")
    fun whenMalformedPlayerListPage_thenHandlesParsing() {
        val uri = TeamApiUtils.malformedPlayerListPageUri()
        browser = JsoupWebBrowser(TeamApiUtils.malformedPlayerListPageUri())
        Assertions.assertThrows(ParsingException::class.java, {
            extractor.getTeamFromUrl(browser, uri.toString())
        })
    }
}
