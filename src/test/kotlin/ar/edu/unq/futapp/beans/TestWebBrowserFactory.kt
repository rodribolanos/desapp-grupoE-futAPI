package ar.edu.unq.futapp.beans

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class TestWebBrowserFactory(private val jsoupWebBrowser: JsoupWebBrowser) : WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        return jsoupWebBrowser
    }
}

