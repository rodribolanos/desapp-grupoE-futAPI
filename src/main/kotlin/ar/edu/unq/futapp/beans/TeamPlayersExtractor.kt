package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.model.HtmlElement
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TeamPlayersExtractor {
    fun getTeamFromUrl(browser: WebBrowser, url: String): Team {
        browser.goTo(url)
        // Si ya está el encabezado del equipo, no esperamos más
        val hasHeader = browser.queryAll("h2.panel-header").isNotEmpty()
        if (!hasHeader) {
            browser.waitFor(
                "h2.panel-header, .table-ranking, #top-player-stats-summary-grid tbody tr",
                Duration.ofSeconds(15)
            )
        }
        val teamNameElem = browser.queryAll("h2.panel-header").firstOrNull()
        val teamName = teamNameElem?.text()?.replace("Plantilla del ", "")?.trim()
            ?: throw ParsingException("Team name not found")
        val playerRows = browser.queryAll("#top-player-stats-summary-grid tbody tr")
        val players = extractedPlayers(playerRows)
        return Team(name = teamName, players = players)
    }

    private fun extractedPlayers(playerRows: List<HtmlElement>): List<Player> =
        playerRows.map { row -> extractedPlayer(row) }

    private fun extractedPlayer(row: HtmlElement): Player {
        val columns = row.queryAll("td")
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

    private fun extractColumnValue(columns: List<HtmlElement>, index: Int): String {
        val inner = columns[index].attr("innerHTML") ?: ""
        return inner.replace(Regex("<.*?>"), "").trim()
    }

    private fun extractPlayerName(columns: List<HtmlElement>): String {
        val nameElement = columns[0].queryAll("a.player-link span").firstOrNull()
            ?: throw ParsingException("Player name not found")
        val name = nameElement.attr("innerHTML")?.replace(Regex("<.*?>"), "")?.trim() ?: ""
        return name
    }

    private fun extractPlayedGames(columns: List<HtmlElement>): Int {
        val appsText = extractColumnValue(columns, 4)
        if (isInvalidData(appsText)) return 0
        val match = Regex("\\d+").find(appsText)?.value
        return match?.toIntOrNull() ?: throw ParsingException("Unable to parse played games from: '$appsText'")
    }

    private fun extractGoals(columns: List<HtmlElement>): Int {
        val goalsText = extractColumnValue(columns, 6)
        if (isInvalidData(goalsText)) return 0
        return goalsText.toIntOrNull() ?: throw ParsingException("Unable to parse goals from: '$goalsText'")
    }

    private fun extractAssists(columns: List<HtmlElement>): Int {
        val assistsText = extractColumnValue(columns, 7)
        if (isInvalidData(assistsText)) return 0
        return assistsText.toIntOrNull() ?: throw ParsingException("Unable to parse assists from: '$assistsText'")
    }

    private fun extractRating(columns: List<HtmlElement>): Double {
        val ratingText = extractColumnValue(columns, 14)
        if (isInvalidData(ratingText)) return 0.0
        return ratingText.toDoubleOrNull() ?: throw ParsingException("Unable to parse rating from: '$ratingText'")
    }

    private fun isInvalidData(text: String): Boolean {
        return text.isBlank() || text == "-" || text.equals("N/A", ignoreCase = true)
    }
}
