package no.nav.yrkesskade.brevutsending.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfiguration(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${dokarkiv.url}") val dokarkivServiceURL: String,
    @Value("\${YRKESSKADE_DOKGEN_API_URL}") val pdfServiceURL: String
    ) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Bean
    fun dokarkivWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(dokarkivServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }

    @Bean
    fun pdfWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(pdfServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }
}