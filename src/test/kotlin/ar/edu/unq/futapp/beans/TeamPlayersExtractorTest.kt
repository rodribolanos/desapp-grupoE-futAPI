package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.*
import org.openqa.selenium.WebDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamPlayersExtractorTest {
    private val webDriverFactory = WebDriverFactory()
    private lateinit var driver: WebDriver
    private val extractor = TeamPlayersExtractor()

    @BeforeAll
    fun setup() {
        driver = webDriverFactory.createDriver()
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
    }

    @Test
    @DisplayName("Parses team name from player list page")
    fun whenValidPlayerListPage_thenParsesTeamName() {
        val expectedTeam = TeamApiUtils.expectedTeamForPlayerListPage()
        val uri = TeamApiUtils.playerListPageUri()
        val team = extractor.getTeamFromUrl(driver, uri.toString())
        Assertions.assertEquals(expectedTeam, team)
    }

    @Test
    @DisplayName("Handles malformed player list page")
    fun whenMalformedPlayerListPage_thenHandlesParsing() {
        val uri = TeamApiUtils.malformedPlayerListPageUri()
        Assertions.assertThrows(ParsingException::class.java, {
            extractor.getTeamFromUrl(driver, uri.toString())
        })
    }
}
