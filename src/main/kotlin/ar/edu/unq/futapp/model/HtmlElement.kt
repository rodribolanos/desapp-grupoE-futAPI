package ar.edu.unq.futapp.model

interface HtmlElement {
    fun tag(): String
    fun text(): String
    fun attr(name: String): String?
    fun queryAll(selector: String): List<HtmlElement>
}