package ar.edu.unq.futapp.beans

import org.openqa.selenium.WebDriver
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@ConditionalOnBean(WebDriver::class)
@Component
class SeleniumWebBrowserFactory(
    private val webDriver: WebDriver
): WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        // Ignoramos headless: el WebDriver es una instancia singleton gestionada por Spring
        return SeleniumWebBrowser(webDriver)
    }
}
