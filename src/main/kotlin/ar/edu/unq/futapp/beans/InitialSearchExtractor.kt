package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration

@Component
class InitialSearchExtractor {
    fun buildSearchUri(teamName: String): URI {
        return UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("es.whoscored.com")
            .path("/search/")
            .queryParam("t", teamName)
            .build()
            .toUri()
    }

    fun getFirstTeamUrl(browser: WebBrowser, teamName: String): String {
        val uri = buildSearchUri(teamName)
        browser.goTo(uri.toString())
        browser.waitFor(".search-result, span.search-message", Duration.ofSeconds(15))
        return parseFirstTeamUrlFromCurrentPage(browser, teamName)
    }

    private fun parseFirstTeamUrlFromCurrentPage(browser: WebBrowser, teamName: String): String {
        if (browser.queryAll("span.search-message").isNotEmpty()) {
            throw EntityNotFound("Team with name $teamName not found")
        }
        val firstTeamRow = browser
            .queryAll(".search-result table tbody tr")
            .firstOrNull { it.queryAll("td a").isNotEmpty() }
            ?: throw EntityNotFound("Team with name $teamName not found")
        val href = firstTeamRow.queryAll("td a").first().attr("href") ?: ""
        return if (href.startsWith("http")) href else "https://es.whoscored.com$href"
    }

    fun getFirstPlayerHistoryUrl(browser: WebBrowser, playerName: String): String {
        val uri = buildSearchUri(playerName)
        browser.goTo(uri.toString())
        browser.waitFor(".search-result, span.search-message", Duration.ofSeconds(15))
        return parseFirstPlayerUrlFromCurrentPage(browser, playerName)
        
    }

    private fun parseFirstPlayerUrlFromCurrentPage(browser: WebBrowser, playerName: String): String {
        if (browser.queryAll("span.search-message").isNotEmpty()) {
            throw EntityNotFound("Player with name $playerName not found")
        }

        val playerTable = browser.queryAll(".search-result table")
            .firstOrNull { table ->
                table.queryAll("tbody tr td a")
                    .any { it.attr("href")?.startsWith("/players/") == true }
            } ?: throw EntityNotFound("Player with name $playerName not found")

        val firstPlayerRow = playerTable.queryAll("tbody tr")
            .firstOrNull { row ->
                row.queryAll("td a").any { it.attr("href")?.startsWith("/players/") == true }
            } ?: throw EntityNotFound("Player with name $playerName not found")


        val href = firstPlayerRow.queryAll("td a")
            .first { it.attr("href")?.startsWith("/players/") == true }
            .attr("href")!!
            .replace("show", "history")

        return if (href.startsWith("http")) href else "https://es.whoscored.com$href"
    }
}
