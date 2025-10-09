package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.model.HtmlElement
import java.time.Duration

interface WebBrowser {
    fun goTo(url: String)
    fun waitFor(selector: String, timeout: Duration = Duration.ofSeconds(15)): Boolean
    fun queryAll(selector: String): List<HtmlElement>
    fun close()

}
