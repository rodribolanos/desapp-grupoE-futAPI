package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.exception.EntityNotFound
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait

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

    fun getFirstTeamUrl(driver: WebDriver, teamName: String): String {
        val uri = buildSearchUri(teamName)
        driver.get(uri.toString())
        WebDriverWait(driver, Duration.ofSeconds(15)).until {
            it.findElements(By.cssSelector(".search-result")).isNotEmpty() ||
                    it.findElements(By.cssSelector("span.search-message")).isNotEmpty()
        }
        if (driver.findElements(By.cssSelector("span.search-message")).isNotEmpty()) {
            throw EntityNotFound("Team with name $teamName not found")
        }
        val firstTeamRow = driver.findElements(By.cssSelector(".search-result table tbody tr"))
            .firstOrNull { it.findElements(By.cssSelector("td a")).isNotEmpty() }
            ?: throw EntityNotFound("Team with name $teamName not found")
        val href = firstTeamRow.findElement(By.cssSelector("td a")).getAttribute("href")
        return if (href.startsWith("http")) href else "https://es.whoscored.com$href"
    }
}
