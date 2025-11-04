package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.beans.WebBrowser
import ar.edu.unq.futapp.beans.WebBrowserFactory
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Primary
@Component
@Profile("test", "integration-test")
class TestWebBrowserFactory(private val jsoupWebBrowser: JsoupWebBrowser) : WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        return jsoupWebBrowser
    }
}