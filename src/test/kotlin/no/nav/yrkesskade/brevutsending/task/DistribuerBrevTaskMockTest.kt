package no.nav.yrkesskade.brevutsending.task

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import no.nav.familie.log.mdc.MDCConstants
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostResponse
import no.nav.yrkesskade.brevutsending.fixtures.brevutsendingBestiltHendelse
import no.nav.yrkesskade.brevutsending.fixtures.opprettJournalpostOkRespons
import no.nav.yrkesskade.brevutsending.kafka.BrevutsendingUtfoertClient
import no.nav.yrkesskade.brevutsending.service.BrevService
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

internal class DistribuerBrevTaskMockTest {
    private val brevServiceMock: BrevService = mockk()
    private val brevutsendingUtfoertClient: BrevutsendingUtfoertClient = mockk()

    private val task = DistribuerBrevTask.opprettTask(opprettJournalpostOkRespons().journalpostId)
    private val distribuerBrevTask = DistribuerBrevTask(brevServiceMock, brevutsendingUtfoertClient)

    @BeforeEach
    fun init() {
        MDC.put(MDCConstants.MDC_CALL_ID, "mock")
        every { brevServiceMock.distribuerJournalpost(any()) } returns DistribuerJournalpostResponse("1234")
        justRun { brevutsendingUtfoertClient.sendDokumentdistribusjonUtfoertHendelse(any()) }
    }

    @Test
    fun `distribuer brev`() {
        distribuerBrevTask.doTask(task)
        verify(exactly = 1) { brevServiceMock.distribuerJournalpost(any()) }
    }

    @Test
    fun `kaster exception hvis dokdist gir 4xx-respons`() {
        every { brevServiceMock.distribuerJournalpost(any()) } throws WebClientResponseException(
            HttpStatus.BAD_REQUEST.value(), "En feilmelding", null, null, null
        )

        assertThrows<WebClientResponseException> { distribuerBrevTask.doTask(task) }
    }

    @Test
    fun `kaster exception hvis dokdist gir 5xx-respons`() {
        every { brevServiceMock.distribuerJournalpost(any()) } throws WebClientResponseException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "En feilmelding", null, null, null
        )

        assertThrows<WebClientResponseException> { distribuerBrevTask.doTask(task) }
    }
}