package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import org.springframework.stereotype.Component

@Component
class TeamPlayersExtractor {
    fun getTeamFromUrl(driver: WebDriver, url: String): Team {
        driver.get(url)
        // Si ya está el encabezado del equipo, no esperamos más
        val hasHeader = driver.findElements(By.cssSelector("h2.panel-header")).isNotEmpty()
        if (!hasHeader) {
            WebDriverWait(driver, Duration.ofSeconds(15)).until {
                it.findElements(By.cssSelector("h2.panel-header")).isNotEmpty() ||
                it.findElements(By.cssSelector(".table-ranking")).isNotEmpty() ||
                it.findElements(By.cssSelector("#top-player-stats-summary-grid tbody tr")).isNotEmpty()
            }
        }
        val teamNameElem = driver.findElements(By.cssSelector("h2.panel-header")).firstOrNull()
        val teamName =
            teamNameElem?.text?.replace("Plantilla del ", "")?.trim() ?: throw ParsingException("Team name not found")
        val playerRows = driver.findElements(By.cssSelector("#top-player-stats-summary-grid tbody tr"))
        val players = extractedPlayers(playerRows)
        return Team(name = teamName, players = players)
    }

    private fun extractedPlayers(playerRows: List<WebElement>): List<Player> {
        return playerRows.map { row -> extractedPlayer(row) }
    }

    private fun extractedPlayer(row: WebElement): Player {
        val columns = row.findElements(By.cssSelector("td"))
        if (columns.size < 15) throw ParsingException("Unexpected number of columns: ${columns.size}")
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

    private fun extractColumnValue(columns: List<WebElement>, index: Int): String {
        return columns[index].getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
    }

    private fun extractPlayerName(columns: List<WebElement>): String {
        val nameElement = columns[0].findElements(By.cssSelector("a.player-link span")).firstOrNull()
            ?: throw ParsingException("Player name not found")
        val name = nameElement.getAttribute("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        return name
    }

    private fun extractPlayedGames(columns: List<WebElement>): Int {
        val appsText = extractColumnValue(columns, 4)
        if (appsText.isBlank() || appsText == "-") return 0
        val match = Regex("\\d+").find(appsText)?.value
        return match?.toIntOrNull() ?: throw ParsingException("Unable to parse played games from: '$appsText'")
    }

    private fun extractGoals(columns: List<WebElement>): Int {
        val goalsText = extractColumnValue(columns, 6)
        if (goalsText.isBlank() || goalsText == "-") return 0
        return goalsText.toIntOrNull() ?: throw ParsingException("Unable to parse goals from: '$goalsText'")
    }

    private fun extractAssists(columns: List<WebElement>): Int {
        val assistsText = extractColumnValue(columns, 7)
        if (assistsText.isBlank() || assistsText == "-") return 0
        return assistsText.toIntOrNull() ?: throw ParsingException("Unable to parse assists from: '$assistsText'")
    }

    private fun extractRating(columns: List<WebElement>): Double {
        val ratingText = extractColumnValue(columns, 14)
        if (ratingText.isBlank() || ratingText == "-") return 0.0
        return ratingText.toDoubleOrNull() ?: throw ParsingException("Unable to parse rating from: '$ratingText'")
    }
}
