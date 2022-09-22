package no.nav.yrkesskade.brevutsending.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.yrkesskade.brevutsending.createShortCircuitWebClientWithStatus
import no.nav.yrkesskade.brevutsending.fixtures.opprettJournalpostOkRespons
import no.nav.yrkesskade.brevutsending.fixtures.opprettJournalpostRequest
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

@ExtendWith(MockKExtension::class)
internal class DokarkivClientMockTest {

    private lateinit var dokarkivClient: DokarkivClient

    @MockK(relaxed = true)
    lateinit var tokenUtilMock: TokenUtil

    @Test
    fun `journalfoerSkademelding skal håndtere OK-respons`() {
        dokarkivClient = DokarkivClient(
            createShortCircuitWebClientWithStatus(
                jacksonObjectMapper().writeValueAsString(opprettJournalpostOkRespons()),
                HttpStatus.OK
            ),
            tokenUtilMock,
            "mock"
        )
        assertDoesNotThrow {
            dokarkivClient.journalfoerDokument(opprettJournalpostRequest())
        }
    }

    @Test
    fun `journalfoerSkademelding skal kaste exception ved 409 CONFLICT-respons`() {
        dokarkivClient = DokarkivClient(
            createShortCircuitWebClientWithStatus("", HttpStatus.CONFLICT),
            tokenUtilMock,
            "mock"
        )
        assertThrows<WebClientResponseException> {
            dokarkivClient.journalfoerDokument(opprettJournalpostRequest())
        }
    }

    @Test
    fun `journalfoerSkademelding skal kaste exception ved 5xx-respons`() {
        dokarkivClient = DokarkivClient(
            createShortCircuitWebClientWithStatus("", HttpStatus.INTERNAL_SERVER_ERROR),
            tokenUtilMock,
            "mock"
        )
        assertThrows<WebClientResponseException> {
            dokarkivClient.journalfoerDokument(opprettJournalpostRequest())
        }
    }
}


