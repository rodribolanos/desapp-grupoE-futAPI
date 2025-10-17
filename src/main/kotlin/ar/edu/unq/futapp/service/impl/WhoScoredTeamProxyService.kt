package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.beans.TeamPlayersExtractor
import ar.edu.unq.futapp.beans.InitialSearchExtractor
import ar.edu.unq.futapp.beans.PlayerPerformanceExtractor
import ar.edu.unq.futapp.beans.TeamFixturesExtractor
import ar.edu.unq.futapp.beans.WebBrowserFactory
import ar.edu.unq.futapp.model.PlayerPerformance
import ar.edu.unq.futapp.model.UpcomingMatch
import ar.edu.unq.futapp.service.WhoScoredApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class WhoScoredTeamProxyService @Autowired constructor(
    private val initialSearchExtractor: InitialSearchExtractor,
    private val teamPlayersExtractor: TeamPlayersExtractor,
    private val webBrowserFactory: WebBrowserFactory,
    private val playerPerformanceExtractor: PlayerPerformanceExtractor,
    private val teamFixturesExtractor: TeamFixturesExtractor
): WhoScoredApiClient {
    override fun findTeam(teamName: String): Optional<Team> {
        val browser = webBrowserFactory.create(headless = true)
        try {
            val teamUrl = initialSearchExtractor.getFirstTeamUrl(browser, teamName)
            val team = teamPlayersExtractor.getTeamFromUrl(browser, teamUrl)
            return Optional.of(team)
        } catch (e: EntityNotFound) {
            throw e
        } catch (e: Exception) {
            throw InternalServerException(e.message)
        } finally {
            browser.close()
        }
    }

    override fun findPlayerPerformance(playerName: String): Optional<PlayerPerformance> {
        val browser = webBrowserFactory.create(headless = true)
        try {
            val playerUrl = initialSearchExtractor.getFirstPlayerHistoryUrl(browser, playerName)
            val playerPerformance = playerPerformanceExtractor.getPlayerPerformanceFromUrl(browser, playerUrl)
            return Optional.of(playerPerformance)
        } catch (e: EntityNotFound) {
            throw e
        } catch (e: Exception) {
            throw InternalServerException(e.message)
        } finally {
            browser.close()
        }
    }

    override fun findUpcomingFixtures(teamName: String): Optional<List<UpcomingMatch>> {
        val browser = webBrowserFactory.create(headless = true)
        try {
            val teamUrl = initialSearchExtractor.getFirstTeamUrl(browser, teamName)
            val fixturesUrl = teamUrl.replace("/show/", "/fixtures/")
            val matches = teamFixturesExtractor.getUpcomingFixturesFromUrl(browser, fixturesUrl)
            return Optional.of(matches)
        } catch (e: EntityNotFound) {
            throw e
        } catch (e: Exception) {
            throw InternalServerException(e.message)
        } finally {
            browser.close()
        }
    }
}