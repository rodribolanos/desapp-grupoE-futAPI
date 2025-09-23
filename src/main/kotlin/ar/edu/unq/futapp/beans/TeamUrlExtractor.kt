package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration

@Component
class TeamUrlExtractor {
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
}
