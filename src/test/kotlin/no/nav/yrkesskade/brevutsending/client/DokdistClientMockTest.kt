package no.nav.yrkesskade.brevutsending.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.yrkesskade.brevutsending.createShortCircuitWebClientWithStatus
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostResponse
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

@ExtendWith(MockKExtension::class)
internal class DokdistClientMockTest {

    private lateinit var dokdistClient: DokdistClient

    @MockK(relaxed = true)
    lateinit var tokenUtilMock: TokenUtil

    @Test
    fun `journalfoerSkademelding skal håndtere OK-respons`() {
        dokdistClient = DokdistClient(
            createShortCircuitWebClientWithStatus(
                jacksonObjectMapper().writeValueAsString(DistribuerJournalpostResponse("1234")),
                HttpStatus.OK
            ),
            tokenUtilMock,
            "mock"
        )
        assertDoesNotThrow {
            dokdistClient.distribuerJournalpost(DistribuerJournalpostRequest("123"))
        }
    }

    @Test
    fun `journalfoerSkademelding skal kaste exception ved 400-respons`() {
        dokdistClient = DokdistClient(
            createShortCircuitWebClientWithStatus("", HttpStatus.BAD_REQUEST),
            tokenUtilMock,
            "mock"
        )
        assertThrows<WebClientResponseException> {
            dokdistClient.distribuerJournalpost(DistribuerJournalpostRequest("123"))
        }
    }

    @Test
    fun `journalfoerSkademelding skal kaste exception ved 5xx-respons`() {
        dokdistClient = DokdistClient(
            createShortCircuitWebClientWithStatus("", HttpStatus.INTERNAL_SERVER_ERROR),
            tokenUtilMock,
            "mock"
        )
        assertThrows<WebClientResponseException> {
            dokdistClient.distribuerJournalpost(DistribuerJournalpostRequest("123"))
        }
    }
}
