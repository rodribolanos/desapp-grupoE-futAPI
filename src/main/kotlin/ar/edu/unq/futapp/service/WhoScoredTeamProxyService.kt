package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.net.URI
import java.time.Duration
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class WhoScoredTeamProxyService {
    private fun createDriver(headless: Boolean = true): WebDriver {
        System.setProperty("webdriver.edge.driver", "C:/WebDriver/msedgedriver.exe")
        val options = EdgeOptions()
        if (headless) {
            options.addArguments("--headless")
            options.addArguments("--disable-gpu")
        }
        options.addArguments("--window-size=1920,1080")
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        return EdgeDriver(options)
    }

    fun findTeam(teamName: String): Optional<Team> {
        val uri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("es.whoscored.com")
            .path("/search/")
            .queryParam("t", teamName)
            .build()
            .toUri()

        var driver: WebDriver? = null
        try {
            driver = createDriver()
            driver.get(uri.toString())
            // Espera hasta que aparezca el contenedor de resultados de búsqueda O el mensaje de no encontrado
            WebDriverWait(driver, Duration.ofSeconds(15))
                .until { d ->
                    val foundTable = d.findElements(By.cssSelector(".search-result")).isNotEmpty()
                    val notFoundMsg = d.findElements(By.cssSelector("span.search-message")).isNotEmpty()
                    foundTable || notFoundMsg
                }
            val notFound = driver.findElements(By.cssSelector("span.search-message")).firstOrNull()
            if (notFound != null) {
                return Optional.empty()
            }
            // Tomar el primer equipo de la tabla
            val teamRows = driver.findElements(By.cssSelector(".search-result table tbody tr"))
            val firstTeamRow = teamRows.firstOrNull { row -> row.findElements(By.cssSelector("td a")).isNotEmpty() }
                ?: return Optional.empty()
            val link = firstTeamRow.findElement(By.cssSelector("td a"))
            val href = link.getAttribute("href")
            val fullUrl = if (href.startsWith("http")) href else "https://es.whoscored.com$href"
            val team = teamFromURI(URI.create(fullUrl))
            return Optional.of(team)
        } catch (e: EntityNotFound) {
            throw e
        } catch (e: Exception) {
            throw InternalServerException(e.message)
        } finally {
            driver?.quit()
        }
    }

    fun teamFromURI(teamUri: URI): Team {
        var driver: WebDriver? = null
        try {
            driver = createDriver()
            driver.get(teamUri.toString())
            WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-ranking")))
            val teamNameElem = driver.findElements(By.cssSelector("h2.panel-header")).firstOrNull()
            val teamName = teamNameElem?.text?.replace("Plantilla del ", "")?.trim() ?: throw ParsingException("Team name not found")
            val playerRows = driver.findElements(By.cssSelector("#top-player-stats-summary-grid tbody tr"))
            val players = extractedPlayers(playerRows)
            return Team(name = teamName, players = players)
        } catch (e: Exception) {
            throw InternalServerException("Error fetching team data from $teamUri: ${e.message}")
        } finally {
            driver?.quit()
        }
    }

    private fun extractedPlayers(playerRows: List<WebElement>): List<Player> {
        return playerRows.mapNotNull { row -> extractedPlayer(row) }
    }

    private fun extractedPlayer(row: WebElement): Player? {
        val columns = row.findElements(By.cssSelector("td"))
        if (columns.size < 15) return null
        val name = extractPlayerName(columns)
        val playedGames = extractPlayedGames(columns)
        val goals = extractGoals(columns)
        val assists = extractAssists(columns)
        val rating = extractRating(columns)
        return Player(
            name = name,
            playedGames = playedGames,
            goals = goals,
            assists = assists,
            rating = rating
        )
    }

    private fun extractPlayerName(columns: List<WebElement>): String {
        val nameElement = columns[0].findElements(By.cssSelector("a.player-link span")).firstOrNull()
        if (nameElement == null) {
            val html = columns[0].getAttribute("innerHTML")
            throw ParsingException("Unable to find player name element. Cell HTML: $html")
        }
        var name = nameElement.getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        if (name.isBlank()) {
            val html = nameElement.getAttribute("outerHTML")
            throw ParsingException("Player name is blank. Element HTML: $html")
        }
        return name
    }

    private fun extractPlayedGames(columns: List<WebElement>): Int {
        val appsText = columns[4].getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        if (appsText.isBlank() || appsText == "-") return 0
        return Regex("\\d+").find(appsText)?.value?.toIntOrNull() ?: 0
    }

    private fun extractGoals(columns: List<WebElement>): Int {
        val goalsText = columns[6].getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        if (goalsText.isBlank() || goalsText == "-") return 0
        return goalsText.toIntOrNull() ?: 0
    }

    private fun extractAssists(columns: List<WebElement>): Int {
        val assistsText = columns[7].getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        if (assistsText.isBlank() || assistsText == "-") return 0
        return assistsText.toIntOrNull() ?: 0
    }

    private fun extractRating(columns: List<WebElement>): Double {
        val ratingText = columns[14].getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        if (ratingText.isBlank() || ratingText == "-") return 0.0
        return ratingText.toDoubleOrNull() ?: 0.0
    }
}