package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.model.HtmlElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URI
import java.time.Duration

class JsoupWebBrowser : WebBrowser {
    private var doc: Document? = null

    override fun goTo(url: String) {
        val uri = URI(url)
        doc = if (uri.scheme.equals("file", ignoreCase = true)) {
            Jsoup.parse(File(uri), "UTF-8")
        } else {
            // Permite también cargar desde http/https si se usa esta impl (no recomendado en prod)
            Jsoup.connect(url).get()
        }
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
