package ar.edu.unq.futapp.beans

import org.springframework.stereotype.Component

@Component
class SeleniumWebBrowserFactory(
    private val webDriverFactory: WebDriverFactory
): WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        val driver = webDriverFactory.createDriver(headless)
        return SeleniumWebBrowser(driver)
    }
}
