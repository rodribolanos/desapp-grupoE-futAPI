package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration

@Component
class InitialSearchExtractor {
    fun buildSearchUri(searchParam: String): URI {
        return UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("es.whoscored.com")
            .path("/search/")
            .queryParam("t", searchParam)
            .build()
            .toUri()
    }

    @Cacheable("teamUrls", key = "#teamName")
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
            throw EntityNotFound("Player with name $playerName not found.")
        }

        val tables = browser.queryAll(".search-result table")
        if (tables.isEmpty()) {
            throw EntityNotFound("Player with name $playerName not found.")
        }

        val playerTable = tables.firstOrNull { table ->
            table.queryAll("a[href^='/players/']").isNotEmpty()
        } ?: throw EntityNotFound("Player with name $playerName not found.")

        var href = playerTable.queryAll("a[href^='/players/']").firstOrNull()!!.attr("href")
            ?: throw EntityNotFound("Player with name $playerName not found.")

        href = href.replace("show", "history")

        return if (href.startsWith("http")) href else "https://es.whoscored.com$href"
    }
}
