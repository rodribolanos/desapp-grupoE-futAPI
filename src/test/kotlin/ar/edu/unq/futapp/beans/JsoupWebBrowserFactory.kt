package ar.edu.unq.futapp.beans

class JsoupWebBrowserFactory: WebBrowserFactory {
    override fun create(headless: Boolean): WebBrowser {
        return JsoupWebBrowser()
    }
}