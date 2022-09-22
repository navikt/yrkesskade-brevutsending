package no.nav.yrkesskade.brevutsending

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

fun createShortCircuitWebClientWithStatus(jsonResponse: String, status: HttpStatus): WebClient {
    val clientResponse: ClientResponse = ClientResponse
        .create(status)
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body(jsonResponse).build()

    return WebClient.builder()
        .exchangeFunction { Mono.just(clientResponse) }
        .build()
}