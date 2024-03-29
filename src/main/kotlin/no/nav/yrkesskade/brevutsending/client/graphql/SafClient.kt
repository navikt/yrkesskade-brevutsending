package no.nav.yrkesskade.brevutsending.client.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.Journalpost
import kotlinx.coroutines.runBlocking
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import no.nav.yrkesskade.brevutsending.util.getLogger
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component


/**
 * Klient for å hente oppdatert journalpost fra saf (Sak og arkiv fasade)
 */
@Component
class SafClient(
    @Value("\${api.client.saf.url}") private val safGraphqlUrl: String,
    @Value("\${spring.application.name}") val applicationName: String,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    private val client = GraphQLWebClient(url = safGraphqlUrl)

    fun hentOppdatertJournalpost(journalpostId: String): Journalpost.Result? {
        val token = tokenUtil.getAppAccessTokenWithSafScope()
        val journalpostQuery = Journalpost(Journalpost.Variables(journalpostId))

        logger.info("Henter oppdatert journalpost for id $journalpostId")
        val oppdatertJournalpost: Journalpost.Result?
        runBlocking {
            val response: GraphQLClientResponse<Journalpost.Result> = client.execute(journalpostQuery) {
                headers {
                    it.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
//                    it.add("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
                    it.add("Nav-Consumer-Id", applicationName)
                }
            }
            oppdatertJournalpost = response.data
            if (!response.errors.isNullOrEmpty()) {
                secureLogger.error("Responsen fra SAF inneholder feil: ${response.errors}")
                throw RuntimeException("Responsen fra SAF inneholder feil! Se securelogs")
            }
        }
        return oppdatertJournalpost
    }
}