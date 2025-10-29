package ar.edu.unq.futapp.beans

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile

@Primary
@Component
@Profile("test", "integration-test")
class TestWebBrowserFactory(private val jsoupWebBrowser: JsoupWebBrowser) : WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        return jsoupWebBrowser
    }
}
