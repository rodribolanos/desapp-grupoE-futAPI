package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.utils.TeamApiUtils
import ar.edu.unq.futapp.testconfig.JsoupBrowserTestConfig
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(JsoupBrowserTestConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamPlayersExtractorTest {
    @Autowired
    private lateinit var webBrowserFactory: WebBrowserFactory
    private lateinit var browser: WebBrowser
    private val extractor = TeamPlayersExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @BeforeAll
    fun setUp() {
        browser = webBrowserFactory.create()
    }

    @Test
    @DisplayName("Parses team name from player list page")
    fun whenValidPlayerListPage_thenParsesTeamName() {
        val expectedTeam = TeamApiUtils.expectedTeamForPlayerListPage()
        val uri = TeamApiUtils.playerListPageUri()
        val team = extractor.getTeamFromUrl(browser, uri.toString())
        Assertions.assertEquals(expectedTeam, team)
    }

    @Test
    @DisplayName("Handles malformed player list page")
    fun whenMalformedPlayerListPage_thenHandlesParsing() {
        val uri = TeamApiUtils.malformedPlayerListPageUri()
        Assertions.assertThrows(ParsingException::class.java) {
            extractor.getTeamFromUrl(browser, uri.toString())
        }
    }
}
