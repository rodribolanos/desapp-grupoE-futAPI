package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.ParsingException
import ar.edu.unq.futapp.model.HtmlElement
import ar.edu.unq.futapp.model.UpcomingMatch
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Clock

@Component
class TeamFixturesExtractor(
    private val clock: Clock = Clock.systemDefaultZone()
) {

    private val rowSelector = "#team-fixture-wrapper .divtable-row"
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy")

    fun getUpcomingFixturesFromUrl(browser: WebBrowser, fixturesUrl: String): List<UpcomingMatch> {
        navigateAndWait(browser, fixturesUrl)

        val rows = browser.queryAll(rowSelector)
        if (rows.isEmpty()) throw EntityNotFound("No se encontraron filas de partidos en la URL: $fixturesUrl")

        return parseRows(rows)
    }

    private fun navigateAndWait(browser: WebBrowser, url: String) {
        browser.goTo(url)
        browser.waitFor(rowSelector, Duration.ofSeconds(5))
    }

    private fun parseRows(rows: List<HtmlElement>): List<UpcomingMatch> {
        val today = LocalDate.now(clock)
        val upcoming = mutableListOf<UpcomingMatch>()

        for (row in rows) {
            parseRow(row, today, upcoming)
        }

        return upcoming
    }

    private fun parseRow(
        row: HtmlElement,
        today: LocalDate,
        upcoming: MutableList<UpcomingMatch>
    ) {
        val dateText = extractDateText(row)
        val resultText = extractResultText(row)

        if (!isFutureMatch(dateText, resultText, today)) return

        val (home, away) = extractTeams(row)

        val tournament = extractTournament(row)

        upcoming.add(UpcomingMatch(dateText, home, away, tournament))
    }

    private fun extractDateText(row: HtmlElement): String {
        return row.queryAll(".date.divtable-data").firstOrNull()?.text()?.trim() ?: throw ParsingException("Fecha no encontrada en la fila del fixture")
    }

    private fun extractResultText(row: HtmlElement): String {
        return row.queryAll(".result, .score-placeholder, .stacked-score-display a, .horiz-match-link, .ns-match-link")
            .firstOrNull()?.text()?.trim() ?: throw ParsingException("Resultado/placeholder no encontrado en la fila del fixture")
    }

    private fun isFutureMatch(dateText: String, resultText: String, today: LocalDate): Boolean {
        if (dateText.isNotBlank()) {
            val parsed = runCatching { LocalDate.parse(dateText, formatter) }
                .getOrNull() ?: throw ParsingException("No se pudo parsear la fecha: '$dateText'")
            return parsed.isAfter(today)
        }

        if (resultText.contains("vs", ignoreCase = true)) return true

        throw ParsingException("No hay fecha válida ni placeholder en la fila del fixture. result='$resultText'")
    }

    private fun extractTeams(row: HtmlElement): Pair<String, String> {
        // Intentar layout horizontal: exigir home y away
        val homeHoriz = row.queryAll(".horizontal-match-display.team.home .team-link").firstOrNull()?.text()
        val awayHoriz = row.queryAll(".horizontal-match-display.team.away .team-link").firstOrNull()?.text()
        if (!homeHoriz.isNullOrBlank() && !awayHoriz.isNullOrBlank()) {
            return homeHoriz to awayHoriz
        }

        // Intentar layout stacked: exigir home y away
        val stackedTeams = row.queryAll(".stacked-teams-display .team")
        val homeStack = stackedTeams.getOrNull(0)?.queryAll("a")?.firstOrNull()?.text()
        val awayStack = stackedTeams.getOrNull(1)?.queryAll("a")?.firstOrNull()?.text()
        if (!homeStack.isNullOrBlank() && !awayStack.isNullOrBlank()) {
            return homeStack to awayStack
        }

        // Fallback: buscar dos team-links y exigir ambos
        val teamLinks = row.queryAll(".team-link")
        val h = teamLinks.getOrNull(0)?.text()
        val a = teamLinks.getOrNull(1)?.text()
        if (h.isNullOrBlank() || a.isNullOrBlank()) {
            throw ParsingException("No se pudieron extraer ambos equipos (home y away) desde la fila del fixture")
        }
        return h to a
    }

    private fun extractTournament(row: HtmlElement): String {
        return row.queryAll(".tournament .tournament-link, .tournament-link").firstOrNull()?.text()
            ?: throw ParsingException("Torneo no encontrado en la fila del fixture")
    }

}
