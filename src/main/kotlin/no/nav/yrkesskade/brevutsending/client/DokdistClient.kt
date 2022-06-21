package no.nav.yrkesskade.brevutsending.client

import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostResponse
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import no.nav.yrkesskade.brevutsending.util.getLogger
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun distribuerJournalpost(distribuerJournalpostRequest: DistribuerJournalpostRequest): DistribuerJournalpostResponse? {
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
//                .header("Nav-Callid", UUID.randomUUID().toString())
                .header("Nav-Consumer-Id", applicationName)
                .bodyValue(distribuerJournalpostRequest)
                .retrieve()
                .bodyToMono<DistribuerJournalpostResponse>()
                .block() ?: throw RuntimeException("Kunne ikke journalføre dokument")
        }
    }

    private fun <T> logTimingAndWebClientResponseException(methodName: String, function: () -> T): T? {
        val start: Long = System.currentTimeMillis()
        try {
            return function.invoke()
        } catch (ex: WebClientResponseException) {
            secureLogger.error(
                "Got a {} error calling Dokarkiv {} {} with message {}",
                ex.statusCode,
                ex.request?.method ?: "-",
                ex.request?.uri ?: "-",
                ex.responseBodyAsString
            )
            if (ex.statusCode == HttpStatus.CONFLICT) {
                log.warn("Dokumentet har allerede blitt journalført.")
                return null
            }
            throw ex
        } catch (rtex: RuntimeException) {
            log.warn("Caught RuntimeException", rtex)
            throw rtex
        } finally {
            val end: Long = System.currentTimeMillis()
            log.info("Method {} took {} millis", methodName, (end - start))
        }
    }
}