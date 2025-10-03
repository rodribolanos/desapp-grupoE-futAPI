package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.HtmlElement
import ar.edu.unq.futapp.model.Performance
import ar.edu.unq.futapp.model.PlayerPerformance
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class PlayerPerformanceExtractor {

    fun getPlayerPerformanceFromUrl(browser: WebBrowser, playerUrl: String) : PlayerPerformance {
        browser.goTo(playerUrl)
        browser.waitFor(
            "#player-history tbdody tr",
            Duration.ofSeconds(15)
        )
        val rows = browser.queryAll("#player-tournament-stats-summary table tbody tr")
        val playerName = extractPlayerName(browser)
        val performances = extractedParticipations(rows)
        return PlayerPerformance(name = playerName, seasons = performances)
    }

    private fun extractPlayerName(browser: WebBrowser): String {
        val nameElement = browser.queryAll("h1.header-name").firstOrNull()
            ?: throw ParsingException("Player name not found")
        return nameElement.text().trim()
    }

    private fun extractedParticipations(rows: List<HtmlElement>): List<Performance> =
        rows.filter { it.queryAll("td a.team-link").isNotEmpty() }
            .map { extractedParticipation(it) }

    private fun extractedParticipation(row: HtmlElement): Performance {
        val columns = row.queryAll("td")
        if (columns.isEmpty()) throw ParsingException("No performance found for player")

        val season = extractSeason(columns)
        val team = extractTeam(columns)
        val competition = extractCompetition(columns)
        val appearances = extractAppearances(columns)
        val goals = extractGoals(columns)
        val assists = extractAssists(columns)
        val aerialWons = extractAerialWons(columns)
        val rating = extractRating(columns)

        return Performance(
            season = season,
            team = team,
            competition = competition,
            appearances = appearances,
            goals = goals,
            assists = assists,
            aerialWons = aerialWons,
            rating = rating
        )
    }

    private fun extractColumnValue(columns: List<HtmlElement>, index: Int): String {
        val inner = columns.getOrNull(index)?.attr("innerHTML") ?: ""
        return inner.replace(Regex("<.*?>"), "").trim()
    }

    private fun extractSeason(columns: List<HtmlElement>): String =
        extractColumnValue(columns, 0)

    private fun extractTeam(columns: List<HtmlElement>): String =
        columns.getOrNull(1)
            ?.queryAll("a.team-link")
            ?.firstOrNull()
            ?.text()
            ?.trim()
            ?: extractColumnValue(columns, 1)

    private fun extractCompetition(columns: List<HtmlElement>): String =
        columns.getOrNull(4)
            ?.queryAll("a.tournament-link")
            ?.firstOrNull()
            ?.text()
            ?.trim()
            ?: extractColumnValue(columns, 4)

    private fun extractAppearances(columns: List<HtmlElement>): Int {
        val text = extractColumnValue(columns, 5)
        val match = Regex("\\d+").find(text)?.value
        return match?.toIntOrNull() ?: 0
    }

    private fun extractGoals(columns: List<HtmlElement>): Int =
        extractColumnValue(columns, 7).toIntOrNull() ?: 0

    private fun extractAssists(columns: List<HtmlElement>): Int =
        extractColumnValue(columns, 8).toIntOrNull() ?: 0

    private fun extractAerialWons(columns: List<HtmlElement>): Double =
        extractColumnValue(columns, 13).toDoubleOrNull() ?: 0.0

    private fun extractRating(columns: List<HtmlElement>): Double =
        extractColumnValue(columns, 15).toDoubleOrNull() ?: 0.0
}