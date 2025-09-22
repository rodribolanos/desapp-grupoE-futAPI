package ar.edu.unq.futapp.service

import org.openqa.selenium.WebDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.springframework.stereotype.Component

@Component
class WebDriverFactory {
    fun createDriver(headless: Boolean = true): WebDriver {
        System.setProperty("webdriver.edge.driver", "C:/WebDriver/msedgedriver.exe")
        val options = EdgeOptions()
        if (headless) {
            options.addArguments("--headless")
            options.addArguments("--disable-gpu")
        }
        options.addArguments("--window-size=1920,1080")
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        return EdgeDriver(options)
    }
}

