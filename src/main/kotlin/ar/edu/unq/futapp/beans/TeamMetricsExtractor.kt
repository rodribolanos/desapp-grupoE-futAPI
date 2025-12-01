package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.FairPlay
import ar.edu.unq.futapp.model.HtmlElement
import ar.edu.unq.futapp.model.TeamMetrics
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TeamMetricsExtractor {
    fun getTeamMetricsFromUrl(browser: WebBrowser, url: String): TeamMetrics {
        browser.goTo(url)

        browser.waitFor(
            "#top-team-stats-summary-content",
            Duration.ofSeconds(15)
        )

        val columns = extractTotalRow(browser)

        return TeamMetrics(
            teamName = extractTeamName(browser),
            goalsAverage = extractGoalsAverageValue(columns),
            shotsPerGame = extractShotsPerGameValue(columns),
            fairPlay = extractFairPlay(columns[4]),
            possessionPercentage = extractPossessionPercentageValue(columns),
            passAccuracy = extractPassAccuracyValue(columns),
            aerialDuelsWonPercentage = extractAerialsWonValue(columns)
        )
    }

    private fun extractTeamName(browser: WebBrowser): String {
        val teamNameElement = browser.queryAll("h1.team-header span.team-header-name").firstOrNull()
            ?: throw ParsingException("Could not find team name.")
        return teamNameElement.text().trim()
    }

    private fun extractTotalRow(browser: WebBrowser): List<HtmlElement> {
        val totalRow = browser.queryAll("#top-team-stats-summary-content > tr:last-child").firstOrNull()
            ?: throw ParsingException("Could not find the total metrics row in the table.")

        val columns = totalRow.queryAll("td")
        if (columns.size < 8) {
            throw ParsingException("Unexpected number of columns (${columns.size}) in total metrics row. Expected at least 8.")
        }

        return columns
    }

    private fun extractGoalsAverageValue(columns: List<HtmlElement>): Double {
        val totalApps = extractTotalApps(columns)
        val totalGoals = extractTotalGoals(columns)

        val goalsAverage = if (totalApps > 0) totalGoals.toDouble() / totalApps.toDouble() else 0.0

        return goalsAverage
    }

    private fun extractShotsPerGameValue(columns: List<HtmlElement>): Double =
        extractMetricValue(columns, 3, "Shots Per Game")

    private fun extractPossessionPercentageValue(columns: List<HtmlElement>): Double =
        extractMetricValue(columns, 5, "Possession Percentage")

    private fun extractPassAccuracyValue(columns: List<HtmlElement>): Double =
        extractMetricValue(columns, 6, "Pass Accuracy")

    private fun extractAerialsWonValue(columns: List<HtmlElement>): Double =
        extractMetricValue(columns, 7, "Aerial Duels Won Average")

    private fun extractColumnValue(columns: List<HtmlElement>, index: Int): String {
        // Usamos innerHTML y regex replacement para quitar etiquetas anidadas
        val inner = columns[index].attr("innerHTML") ?: ""

        return inner.replace(Regex("<.*?>"), "")
            .trim()
            .replace(',', '.') // Manejar el separador decimal español
    }

    private fun isInvalidData(text: String): Boolean {
        return text.isBlank() || text == "-" || text.equals("N/A", ignoreCase = true)
    }

    private fun extractMetricValue(columns: List<HtmlElement>, index: Int, metricName: String): Double {
        val text = extractColumnValue(columns, index)
        if (isInvalidData(text)) return 0.0

        return text.toDoubleOrNull()
            ?: throw ParsingException("Unable to parse Double value for $metricName from: '$text'")
    }

    private fun extractTotalApps(columns: List<HtmlElement>): Int {
        val appsText = extractColumnValue(columns, 1)
        if (isInvalidData(appsText)) return 0

        val match = Regex("\\d+").find(appsText)?.value
        return match?.toIntOrNull() ?: throw ParsingException("Unable to parse total apps from: '$appsText'")
    }

    private fun extractTotalGoals(columns: List<HtmlElement>): Int {
        val goalsText = extractColumnValue(columns, 2)
        if (isInvalidData(goalsText)) return 0
        return goalsText.toIntOrNull()
            ?: throw ParsingException("Unable to parse total goals from: '$goalsText'")
    }

    private fun extractFairPlay(disciplineContainer: HtmlElement): FairPlay {
        val yellowCardsRaw = disciplineContainer.queryAll("span.yellow-card-box").firstOrNull()?.text()
        val redCardsRaw = disciplineContainer.queryAll("span.red-card-box").firstOrNull()?.text()

        val yellowCardsClean = yellowCardsRaw?.replace(Regex("<.*?>"), "")?.trim()
        val redCardsClean = redCardsRaw?.replace(Regex("<.*?>"), "")?.trim()

        val yellowCards = yellowCardsClean?.toIntOrNull() ?: 0
        val redCards = redCardsClean?.toIntOrNull() ?: 0

        return FairPlay(redCards = redCards, yellowCards = yellowCards)
    }

}