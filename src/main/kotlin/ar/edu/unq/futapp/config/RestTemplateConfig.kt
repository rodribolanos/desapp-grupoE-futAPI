package ar.edu.unq.futapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun customRestTemplate(): RestTemplate {
        val template = RestTemplate()
        template.errorHandler = DefaultResponseErrorHandler()
        return template
    }
}