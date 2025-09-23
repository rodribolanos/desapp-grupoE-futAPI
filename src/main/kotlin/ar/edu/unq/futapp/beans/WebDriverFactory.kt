package ar.edu.unq.futapp.beans

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.springframework.stereotype.Component

@Component
class WebDriverFactory {
    fun createDriver(headless: Boolean = true): WebDriver {
        val options = FirefoxOptions()
        if (headless) {
            options.addArguments("--headless")
            options.addArguments("--width=800")
            options.addArguments("--height=600")
        }
        return FirefoxDriver(options)
    }
}