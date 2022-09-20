package no.nav.yrkesskade.brevutsending.client

import no.nav.familie.log.mdc.MDCConstants
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostResponse
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import no.nav.yrkesskade.brevutsending.util.getLogger
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class DokdistClient(
    private val dokdistWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    @Value("\${spring.application.name}") val applicationName: String
): AbstractRestClient("Dokdist") {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun distribuerJournalpost(distribuerJournalpostRequest: DistribuerJournalpostRequest): DistribuerJournalpostResponse {
        log.info("Distribuerer journalpost ${distribuerJournalpostRequest.journalpostId}")
        return logTimingAndWebClientResponseException("distribuerJournalpost") {
            dokdistWebClient.post()
                .uri { uriBuilder ->
                    uriBuilder.pathSegment("rest")
                        .pathSegment("v1")
                        .pathSegment("distribuerjournalpost")
                        .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${tokenUtil.getAppAccessTokenWithSafScope()}")
                .header("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
                .header("Nav-Consumer-Id", applicationName)
                .bodyValue(distribuerJournalpostRequest)
                .retrieve()
                .bodyToMono<DistribuerJournalpostResponse>()
                .block() ?: throw RuntimeException("Kunne ikke distribuere dokument")
        }.also {
            log.info("Distribuert journalpost ${distribuerJournalpostRequest.journalpostId}, bestillingsId: ${it.bestillingsId}")
        }
    }
}