package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.model.Team
import org.openqa.selenium.WebDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class WhoScoredTeamProxyService @Autowired constructor(
    private val teamUrlExtractor: TeamUrlExtractor,
    private val teamPlayersExtractor: TeamPlayersExtractor,
    private val webDriverFactory: WebDriverFactory
) {
    fun findTeam(teamName: String): Optional<Team> {
        var driver: WebDriver? = null
        try {
            driver = webDriverFactory.createDriver()
            val teamUrl = teamUrlExtractor.getFirstTeamUrl(driver, teamName)
            val team = teamPlayersExtractor.getTeamFromUrl(driver, teamUrl)
            return Optional.of(team)
        } catch (e: EntityNotFound) {
            throw e
        } catch (e: Exception) {
            throw InternalServerException(e.message)
        } finally {
            driver?.quit()
        }
    }
}