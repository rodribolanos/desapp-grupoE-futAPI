package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.model.HtmlElement
import ar.edu.unq.futapp.model.Match
import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.TeamMatches
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class MatchExtractor(
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy")
) {

    @Cacheable("teamMatchesCache", key = "#url")
    fun getLastMatchesFromUrl(browser: WebBrowser, url: String): TeamMatches {
        browser.goTo(url)

        browser.waitFor(
            "#team-fixtures .divtable-row[data-id]",
            Duration.ofSeconds(15)
        )
        val teamName = extractTeamNameFromPage(browser)

        val matchRows = browser.queryAll("#team-fixtures .divtable-row[data-id]")
        val matches = extractMatches(matchRows, teamName)

        return TeamMatches(
            teamName = teamName,
            matches = matches
        )
    }

    private fun extractTeamNameFromPage(browser: WebBrowser): String {
        val teamNameElement = browser.queryAll("span.team-header-name")
            .firstOrNull()
            ?: throw ParsingException("Team name not found in page header")
        println(teamNameElement.text())
        return teamNameElement.text().trim()
    }

    private fun extractMatches(rows: List<HtmlElement>, teamName: String): List<Match> {
        return rows.mapNotNull { row ->
            try {
                if (hasResult(row)) {
                    extractMatch(row, teamName)
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Error parsing match: ${e.message}")
                null
            }
        }
    }

    private fun hasResult(row: HtmlElement): Boolean {
        val resultLink = row.queryAll("a.result-1, a.horiz-match-link.result-1, a.stacked-match-link.result-1")
        return resultLink.isNotEmpty()
    }

    private fun extractMatch(row: HtmlElement, teamName: String): Match {
        val homeTeam = extractHomeTeam(row)
        val awayTeam = extractAwayTeam(row)
        val (homeScore, awayScore) = extractScore(row)
        val date = extractDate(row)
        val isHomeMatch = homeTeam.equals(teamName, ignoreCase = true)

        return Match(
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeScore = homeScore,
            awayScore = awayScore,
            date = date,
            isHomeMatch = isHomeMatch
        )
    }

    private fun extractHomeTeam(row: HtmlElement): String {
        // Intentar primero horizontal display
        val horizontalTeam = row.queryAll("div.horizontal-match-display.team.home a.team-link")
            .firstOrNull()
            ?.text()
            ?.trim()

        if (!horizontalTeam.isNullOrEmpty()) {
            return horizontalTeam
        }

        val stackedTeam = row.queryAll("div.stacked-teams-display div.team")
            .firstOrNull()
            ?.queryAll("a.team-link")
            ?.firstOrNull()
            ?.text()
            ?.trim()

        return stackedTeam ?: throw ParsingException("Home team not found")
    }

    private fun extractAwayTeam(row: HtmlElement): String {
        val horizontalTeam = row.queryAll("div.horizontal-match-display.team.away a.team-link")
            .firstOrNull()
            ?.text()
            ?.trim()

        if (!horizontalTeam.isNullOrEmpty()) {
            return horizontalTeam
        }

        val stackedTeams = row.queryAll("div.stacked-teams-display div.team")
        val stackedTeam = stackedTeams.lastOrNull()
            ?.queryAll("a.team-link")
            ?.firstOrNull()
            ?.text()
            ?.trim()

        return stackedTeam ?: throw ParsingException("Away team not found")
    }

    private fun extractScore(row: HtmlElement): Pair<Int, Int> {
        val horizontalScore = row.queryAll("a.horiz-match-link.result-1")
            .firstOrNull()
            ?.text()
            ?.trim()

        if (!horizontalScore.isNullOrEmpty()) {
            return parseScore(horizontalScore)
        }

        // Si no, buscar en stacked display
        val homeScoreText = row.queryAll("div.stacked-score-display div.home-score")
            .firstOrNull()
            ?.text()
            ?.trim()
            ?: throw ParsingException("Home score not found")

        val awayScoreText = row.queryAll("div.stacked-score-display div.away-score")
            .firstOrNull()
            ?.text()
            ?.trim()
            ?: throw ParsingException("Away score not found")

        val homeScore = homeScoreText.toIntOrNull()
            ?: throw ParsingException("Invalid home score: $homeScoreText")
        val awayScore = awayScoreText.toIntOrNull()
            ?: throw ParsingException("Invalid away score: $awayScoreText")

        return Pair(homeScore, awayScore)
    }

    private fun parseScore(scoreText: String): Pair<Int, Int> {
        // Formato: "0 : 3" o "2:1"
        val parts = scoreText.split(":").map { it.trim() }

        if (parts.size != 2) {
            throw ParsingException("Invalid score format: $scoreText")
        }

        val homeScore = parts[0].toIntOrNull()
            ?: throw ParsingException("Invalid home score in: $scoreText")
        val awayScore = parts[1].toIntOrNull()
            ?: throw ParsingException("Invalid away score in: $scoreText")

        return Pair(homeScore, awayScore)
    }

    private fun extractDate(row: HtmlElement): LocalDate {
        // Buscar en cualquier div con clase "date"
        val dateText = row.queryAll("div.date")
            .firstOrNull { it.text().trim().isNotEmpty() }
            ?.text()
            ?.trim()
            ?: throw ParsingException("Date not found")

        return parseDate(dateText)
    }

    private fun parseDate(dateText: String): LocalDate {
        return try {
            // Formato: "16-08-25" (DD-MM-YY)
            LocalDate.parse(dateText, dateFormatter)
        } catch (e: Exception) {
            throw ParsingException("Invalid date format: $dateText")
        }
    }
}