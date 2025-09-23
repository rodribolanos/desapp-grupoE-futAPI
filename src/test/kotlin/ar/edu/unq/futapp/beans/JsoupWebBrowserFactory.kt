package ar.edu.unq.futapp.beans

import org.springframework.stereotype.Component
import java.net.URI

@Component
class JsoupWebBrowserFactory {
    fun createFromUri(uri: URI): WebBrowser {
        return JsoupWebBrowser(uri)
    }
}