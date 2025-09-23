package ar.edu.unq.futapp.testconfig

import ar.edu.unq.futapp.beans.JsoupWebBrowserFactory
import ar.edu.unq.futapp.beans.WebBrowserFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class JsoupBrowserTestConfig {
    @Bean
    @Primary
    fun testWebBrowser(): WebBrowserFactory = JsoupWebBrowserFactory()
}

