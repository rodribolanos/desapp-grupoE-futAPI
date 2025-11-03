package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.beans.WebBrowser
import ar.edu.unq.futapp.model.HtmlElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.io.File
import java.net.URI
import java.time.Duration

@Component
class JsoupWebBrowser : WebBrowser {
    private var doc: Document? = null

    constructor(uri: URI) {
        doc = Jsoup.parse(File(uri), "UTF-8")
    }

    constructor()

    override fun goTo(url: String) {
        // Preload
    }

    override fun waitFor(selector: String, timeout: Duration): Boolean {
        return doc?.select(selector)?.isNotEmpty() == true
    }

    override fun queryAll(selector: String): List<HtmlElement> {
        val d = doc ?: return emptyList()
        return d.select(selector).map { JsoupHtmlElement(it) }
    }

    override fun close() {
        // No resources to close for static HTML
    }
}

class JsoupHtmlElement(private val element: Element) : HtmlElement {
    override fun tag(): String = element.tagName()
    override fun text(): String = element.text()
    override fun attr(name: String): String? =
        if (name.equals("innerHTML", ignoreCase = true)) element.html() else element.attr(name).ifBlank { null }

    override fun queryAll(selector: String): List<HtmlElement> =
        element.select(selector).map { JsoupHtmlElement(it) }
}