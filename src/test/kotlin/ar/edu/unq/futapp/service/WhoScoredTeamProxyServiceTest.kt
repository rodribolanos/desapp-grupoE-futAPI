package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.beans.TeamPlayersExtractor
import ar.edu.unq.futapp.beans.TeamUrlExtractor
import ar.edu.unq.futapp.beans.WebDriverFactory
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.openqa.selenium.WebDriver
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WhoScoredTeamProxyServiceTest {
    private lateinit var teamUrlExtractor: TeamUrlExtractor
    private lateinit var teamPlayersExtractor: TeamPlayersExtractor
    private lateinit var webDriverFactory: WebDriverFactory
    private lateinit var driver: WebDriver

    private lateinit var service: WhoScoredTeamProxyService

    @BeforeEach
    fun setUp() {
        teamUrlExtractor = mock(TeamUrlExtractor::class.java)
        teamPlayersExtractor = mock(TeamPlayersExtractor::class.java)
        webDriverFactory = mock(WebDriverFactory::class.java)
        driver = mock(WebDriver::class.java)
        service = WhoScoredTeamProxyService(teamUrlExtractor, teamPlayersExtractor, webDriverFactory)
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
        `when`(webDriverFactory.createDriver(true)).thenReturn(driver)
        `when`(teamUrlExtractor.getFirstTeamUrl(driver, teamName)).thenReturn(url)
        `when`(teamPlayersExtractor.getTeamFromUrl(driver, url)).thenReturn(expected)
        // Act
        val result = service.findTeam(teamName)
        // Assert
        assertTrue(result.isPresent)
        assertEquals(expected, result.get())
        verify(webDriverFactory, times(1)).createDriver(true)
        verify(teamUrlExtractor, times(1)).getFirstTeamUrl(driver, teamName)
        verify(teamPlayersExtractor, times(1)).getTeamFromUrl(driver, url)
        verify(driver, times(1)).quit()
        verifyNoMoreInteractions(teamPlayersExtractor, teamUrlExtractor, webDriverFactory, driver)
    }

    @Test
    @DisplayName("findTeam rethrows EntityNotFound and closes the driver")
    fun whenTeamNotFound_thenRethrowEntityNotFoundAndQuitDriver() {
        // Arrange
        val teamName = "Unknown Team"
        `when`(webDriverFactory.createDriver(true)).thenReturn(driver)
        `when`(teamUrlExtractor.getFirstTeamUrl(driver, teamName)).thenThrow(EntityNotFound("not found"))
        // Act & Assert
        assertThrows<EntityNotFound> { service.findTeam(teamName) }
        verify(webDriverFactory, times(1)).createDriver(true)
        verify(teamUrlExtractor, times(1)).getFirstTeamUrl(driver, teamName)
        verify(driver, times(1)).quit()
        verifyNoMoreInteractions(teamPlayersExtractor, teamUrlExtractor, webDriverFactory, driver)
    }

    @Test
    @DisplayName("findTeam wraps unexpected exceptions into InternalServerException and closes the driver")
    fun whenUnexpectedException_thenThrowInternalServerExceptionAndQuitDriver() {
        // Arrange
        val teamName = "Boca Juniors"
        val url = "http://test/url"
        `when`(webDriverFactory.createDriver(true)).thenReturn(driver)
        `when`(teamUrlExtractor.getFirstTeamUrl(driver, teamName)).thenReturn(url)
        `when`(teamPlayersExtractor.getTeamFromUrl(driver, url)).thenThrow(RuntimeException("boom"))
        // Act & Assert
        assertThrows<InternalServerException> { service.findTeam(teamName) }
        verify(webDriverFactory, times(1)).createDriver(true)
        verify(teamUrlExtractor, times(1)).getFirstTeamUrl(driver, teamName)
        verify(teamPlayersExtractor, times(1)).getTeamFromUrl(driver, url)
        verify(driver, times(1)).quit()
        verifyNoMoreInteractions(teamPlayersExtractor, teamUrlExtractor, webDriverFactory, driver)
    }
}

