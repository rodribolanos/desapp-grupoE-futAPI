package ar.edu.unq.futapp.beans

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class TestWebBrowserFactory : WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        // Ignoramos headless en tests. Usamos Jsoup para evitar Selenium.
        return JsoupWebBrowser()
    }
}

