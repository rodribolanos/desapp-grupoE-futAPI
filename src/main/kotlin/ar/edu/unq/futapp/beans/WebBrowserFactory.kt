package ar.edu.unq.futapp.beans

interface WebBrowserFactory {
    fun create(headless: Boolean = true): WebBrowser
}