package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.*
import org.openqa.selenium.WebDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamUrlExtractorTest {
    private val webDriverFactory = WebDriverFactory()
    private lateinit var driver: WebDriver
    private val extractor = TeamUrlExtractor()

    @BeforeAll
    fun setup() {
        driver = webDriverFactory.createDriver()
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
    }

    @Test
    @DisplayName("Extracts the first team URL when the page has results")
    fun whenPageHasResults_thenExtractsFirstTeamUrl() {
        driver.get(TeamApiUtils.pageWithResultsUri().toString())
        val url = extractor.getFirstTeamUrl(driver, "Boca Juniors")
        Assertions.assertTrue(url.startsWith("https://es.whoscored.com"))
    }

    @Test
    @DisplayName("Throws EntityNotFound when the page has no results")
    fun whenPageHasNoResults_thenThrowsEntityNotFound() {
        driver.get(TeamApiUtils.pageWithoutResultsUri().toString())
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstTeamUrl(driver, "EquipoInexistente")
        }
    }
}
