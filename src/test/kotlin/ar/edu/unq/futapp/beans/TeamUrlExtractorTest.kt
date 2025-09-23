package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.testconfig.JsoupBrowserTestConfig
import ar.edu.unq.futapp.utils.TeamApiUtils
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Import(JsoupBrowserTestConfig::class)
class TeamUrlExtractorTest {
    @Autowired
    private lateinit var webBrowserFactory: WebBrowserFactory
    private lateinit var realBrowser: WebBrowser
    private lateinit var browser: WebBrowser
    private val extractor = TeamUrlExtractor()

    @BeforeEach
    fun setup() {
        realBrowser = webBrowserFactory.create()
        browser = spyk(realBrowser, recordPrivateCalls = false)
    }

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Extracts the first team URL when the page has results")
    fun whenPageHasResults_thenExtractsFirstTeamUrl() {
        val expectedURL = "https://es.whoscored.com/teams/889/show/argentina-boca-juniors"

        every { browser.goTo(any()) } answers {
            callOriginal()
            realBrowser.goTo(TeamApiUtils.pageWithResultsUri().toString())
        }

        val actualURL = extractor.getFirstTeamUrl(browser, "Boca Juniors")

        Assertions.assertEquals(expectedURL, actualURL)
    }



    @Test
    @DisplayName("Throws EntityNotFound when the page has no results")
    fun whenPageHasNoResults_thenThrowsEntityNotFound() {
        every { browser.goTo(any()) } answers {
            callOriginal()
            realBrowser.goTo(TeamApiUtils.pageWithoutResultsUri().toString())
        }
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstTeamUrl(browser, "EquipoInexistente")
        }
    }
}
