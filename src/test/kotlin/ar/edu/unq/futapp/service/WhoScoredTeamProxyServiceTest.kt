package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.beans.TeamPlayersExtractor
import ar.edu.unq.futapp.beans.TeamUrlExtractor
import ar.edu.unq.futapp.beans.WebBrowser
import ar.edu.unq.futapp.beans.WebBrowserFactory
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.service.impl.WhoScoredTeamProxyService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class WhoScoredTeamProxyServiceTest {
    private lateinit var teamUrlExtractor: TeamUrlExtractor
    private lateinit var teamPlayersExtractor: TeamPlayersExtractor
    private lateinit var webBrowserFactory: WebBrowserFactory

    @Autowired
    private lateinit var service: WhoScoredTeamProxyService

    private lateinit var browser: WebBrowser

    @BeforeEach
    fun setUp() {
        browser = mockk<WebBrowser>()
        teamPlayersExtractor = mockk<TeamPlayersExtractor>()
        teamUrlExtractor = mockk<TeamUrlExtractor>()
        webBrowserFactory = mockk<WebBrowserFactory>()
        service = WhoScoredTeamProxyService(teamUrlExtractor, teamPlayersExtractor, webBrowserFactory)
    }

    @AfterEach
    fun tearDown() {
        browser.close()
    }

    @Test
    @DisplayName("findTeam returns Team on success and closes the driver")
    fun whenFindTeamSucceeds_thenReturnTeamAndQuitDriver() {
        // Arrange
        val teamName = "Boca Juniors"
        val url = "http://test/url"
        val expected = Team(teamName, listOf(
            Player("Rodrigo Battaglia", 3, 1, 0, 7.21)
        ))
        every { webBrowserFactory.create(true) } returns browser
        every { teamUrlExtractor.getFirstTeamUrl(browser, teamName) } returns url
        every { teamPlayersExtractor.getTeamFromUrl(browser, url) } returns expected
        every { browser.close() } returns Unit
        // Act
        val result = service.findTeam(teamName)
        // Assert
        assertTrue(result.isPresent)
        assertEquals(expected, result.get())
        verify(exactly = 1) {
            webBrowserFactory.create(true)
            teamUrlExtractor.getFirstTeamUrl(browser, teamName)
            teamPlayersExtractor.getTeamFromUrl(browser, url)
            browser.close()
        }
    }

    @Test
    @DisplayName("findTeam rethrows EntityNotFound and closes the driver")
    fun whenTeamNotFound_thenRethrowEntityNotFoundAndQuitDriver() {
        // Arrange
        val teamName = "Unknown Team"
        every { webBrowserFactory.create(true) } returns browser
        every { teamUrlExtractor.getFirstTeamUrl(browser, teamName) } throws EntityNotFound("not found")
        every { browser.close() } returns Unit
        // Act & Assert
        assertThrows<EntityNotFound> { service.findTeam(teamName) }
        verify(exactly = 1) {
            webBrowserFactory.create(true)
            teamUrlExtractor.getFirstTeamUrl(browser, teamName)
            browser.close()
        }
        verify(exactly = 0) {
            teamPlayersExtractor.getTeamFromUrl(any(), any())
        }
    }

    @Test
    @DisplayName("findTeam wraps unexpected exceptions into InternalServerException and closes the driver")
    fun whenUnexpectedException_thenThrowInternalServerExceptionAndQuitDriver() {
        // Arrange
        val teamName = "Boca Juniors"
        val url = "http://test/url"
        every { webBrowserFactory.create(true) } returns browser
        every { teamUrlExtractor.getFirstTeamUrl(browser, teamName) } returns url
        every { teamPlayersExtractor.getTeamFromUrl(browser, url) } throws RuntimeException("boom")
        every { browser.close() } returns Unit
        // Act & Assert
        assertThrows<InternalServerException> { service.findTeam(teamName) }
        verify(exactly = 1) {
            webBrowserFactory.create(true)
            teamUrlExtractor.getFirstTeamUrl(browser, teamName)
            teamPlayersExtractor.getTeamFromUrl(browser, url)
            browser.close()
        }
    }
}
