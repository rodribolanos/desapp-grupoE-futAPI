package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.model.HtmlElement
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class SeleniumWebBrowser(private val driver: WebDriver) : WebBrowser {
    override fun goTo(url: String) {
        driver.get(url)
    }

    override fun waitFor(selector: String, timeout: Duration): Boolean {
        return try {
            WebDriverWait(driver, timeout).until { it.findElements(By.cssSelector(selector)).isNotEmpty() }
            true
        } catch (_: TimeoutException) {
            false
        }
    }

    override fun queryAll(selector: String): List<HtmlElement> {
        return driver.findElements(By.cssSelector(selector)).map { SeleniumHtmlElement(it) }
    }

    override fun close() {
        // No hacemos nada: la sesión se preserva y el cierre lo gestiona Spring (destroyMethod="quit").
    }
}

class SeleniumHtmlElement(private val element: WebElement) : HtmlElement {
    override fun tag(): String = element.tagName
    override fun text(): String = element.text
    override fun attr(name: String): String? = element.getAttribute(name)
    override fun queryAll(selector: String): List<HtmlElement> =
        element.findElements(By.cssSelector(selector)).map { SeleniumHtmlElement(it) }
}
