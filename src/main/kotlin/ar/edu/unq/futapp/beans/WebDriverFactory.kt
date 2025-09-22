package ar.edu.unq.futapp.beans

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.springframework.stereotype.Component

@Component
class WebDriverFactory {
    fun createDriver(headless: Boolean = true): WebDriver {
        val driverPath = System.getenv("GECKO_DRIVER_PATH")
            ?: throw IllegalStateException("La variable de entorno GECKO_DRIVER_PATH no está definida")
        System.setProperty("webdriver.gecko.driver", driverPath)

        val options = FirefoxOptions()
        val firefoxBin = System.getenv("FIREFOX_BIN")
        if (firefoxBin != null) {
            options.setBinary(firefoxBin)
        }
        if (headless) {
            options.addArguments("-headless")
            options.addArguments("--width=1920")
            options.addArguments("--height=1080")
        }
        return FirefoxDriver(options)
    }
}