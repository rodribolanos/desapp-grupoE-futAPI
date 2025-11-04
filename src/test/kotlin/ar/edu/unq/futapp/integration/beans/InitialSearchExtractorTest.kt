package ar.edu.unq.futapp.integration.beans

import ar.edu.unq.futapp.beans.InitialSearchExtractor
import ar.edu.unq.futapp.utils.JsoupWebBrowser
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.utils.TeamApiUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("integration-test")
class InitialSearchExtractorTest {
    lateinit var browser : JsoupWebBrowser
    private val extractor = InitialSearchExtractor()

    @AfterAll
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("Extracts the first team URL when the page has results")
    fun whenPageHasResults_thenExtractsFirstTeamUrl() {
        val expectedURL = "https://es.whoscored.com/teams/889/show/argentina-boca-juniors"
        browser = JsoupWebBrowser(TeamApiUtils.searchTeamWithResults())

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

    @Test
    @DisplayName("Extract the first player URL when the page has results")
    fun whenPageHasResults_thenExtractsFirstPlayerUrl() {
        val expectedURL = "https://es.whoscored.com/players/134925/history/álvaro-madrid"
        browser = JsoupWebBrowser(TeamApiUtils.playerSearchPageWithResults())

        val actualURL = extractor.getFirstPlayerHistoryUrl(browser, "madrid")

        Assertions.assertEquals(expectedURL, actualURL)
    }

    @Test
    @DisplayName("Throws EntityNotFound when the player search page has no results")
    fun whenPlayerSearchPageHasNoResults_thenThrowsEntityNotFound() {
        browser = JsoupWebBrowser(TeamApiUtils.playerSearchPageWithoutResults())
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstPlayerHistoryUrl(browser, "JugadorInexistente")
        }
    }

    @Test
    @DisplayName("Throws EntityNotFound when there are no players but there are teams")
    fun whenThereAreNoPlayersButThereAreTeams_thenThrowsEntityNotFound() {
        browser = JsoupWebBrowser(TeamApiUtils.searchTeamWithResults())
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstPlayerHistoryUrl(browser, "Rodrigo Battaglia")
        }
    }
}