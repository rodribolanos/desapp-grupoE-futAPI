package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Profile(value = ["test"])
class TeamUrlExtractorTest {
    lateinit var browser : JsoupWebBrowser
    private val extractor = TeamUrlExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Extracts the first team URL when the page has results")
    fun whenPageHasResults_thenExtractsFirstTeamUrl() {
        val expectedURL = "https://es.whoscored.com/teams/889/show/argentina-boca-juniors"
        browser = JsoupWebBrowser(TeamApiUtils.pageWithResultsUri())

        val actualURL = extractor.getFirstTeamUrl(browser, "Boca Juniors")

        Assertions.assertEquals(expectedURL, actualURL)
    }

    @Test
    @DisplayName("Throws EntityNotFound when the page has no results")
    fun whenPageHasNoResults_thenThrowsEntityNotFound() {
        browser = JsoupWebBrowser(TeamApiUtils.pageWithoutResultsUri())
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstTeamUrl(browser, "EquipoInexistente")
        }
    }
}
