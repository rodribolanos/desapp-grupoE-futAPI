package ar.edu.unq.futapp.beans

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.MalformedURLException
import java.net.URI
import java.time.Duration

@Component
class WebDriverFactory {
    @Value("\${webdriver.url}")
    private lateinit var REMOTE_URL_ENV: String

    fun createDriver(headless: Boolean = true): WebDriver {
        val remoteUrlString = System.getenv(REMOTE_URL_ENV)
            ?: throw IllegalArgumentException("Environment variable $REMOTE_URL_ENV no encontrada")

        val remoteUrl = try {
            URI.create(remoteUrlString).toURL()
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("La env var $REMOTE_URL_ENV no es una URL válida: $remoteUrlString", e)
        }

        val options = ChromeOptions()
        val driver = RemoteWebDriver(remoteUrl, options)

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60))
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30))

        return driver
    }

}