package ar.edu.unq.futapp.beans

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.MalformedURLException
import java.net.URI
import java.time.Duration

@Configuration
@ConditionalOnMissingBean(WebBrowserFactory::class)
class WebDriverFactory {
    @Value("\${webdriver.url}")
    private lateinit var REMOTE_URL_ENV: String

    @Bean(destroyMethod = "quit")
    fun webDriver(): WebDriver {
        val remoteUrl = try {
            URI.create(REMOTE_URL_ENV).toURL()
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("La env var $REMOTE_URL_ENV no es una URL válida", e)
        }
        val options = ChromeOptions()
        val driver: WebDriver = RemoteWebDriver(remoteUrl, options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60))
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30))
        return driver
    }
}