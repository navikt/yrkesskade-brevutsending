package no.nav.yrkesskade.brevutsending.task

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.familie.log.mdc.MDCConstants
import no.nav.yrkesskade.brevutsending.fixtures.brevutsendingBestiltHendelse
import no.nav.yrkesskade.brevutsending.fixtures.opprettJournalpostOkRespons
import no.nav.yrkesskade.brevutsending.service.BrevService
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

internal class GenererOgJournalfoerBrevTaskMockTest {
    private val brevServiceMock: BrevService = mockk()
    private val taskRepositoryMock: TaskRepository = mockk()

    private val brevutsendingBestiltHendelse = brevutsendingBestiltHendelse()
    private val task = GenererOgJournalfoerBrevTask.opprettTask(brevutsendingBestiltHendelse)
    private val genererOgJournalfoerBrevTask = GenererOgJournalfoerBrevTask(brevServiceMock, taskRepositoryMock)

    @BeforeEach
    fun init() {
        MDC.put(MDCConstants.MDC_CALL_ID, "mock")
        val opprettJournalpostOkRespons = opprettJournalpostOkRespons()
        every { brevServiceMock.genererPdf(any()) } returns "pdf".toByteArray()
        every { brevServiceMock.journalfoerUtgaaendeDokument(any(), any(), any()) } returns opprettJournalpostOkRespons
        every { taskRepositoryMock.save(any()) } returns DistribuerBrevTask.opprettTask(
            opprettJournalpostOkRespons.journalpostId,
            brevutsendingBestiltHendelse.behandlingId
        )

    }

    @Test
    fun `fullført GenererOgJournalfoerBrevTask oppretter en DistribuerBrevTask`() {
        genererOgJournalfoerBrevTask.doTask(task)

        verify(exactly = 1) { taskRepositoryMock.save(any()) }
        verify(exactly = 1) { brevServiceMock.journalfoerUtgaaendeDokument(any(), any(), any()) }
        verify(exactly = 1) { taskRepositoryMock.save(any()) }
    }

    @Test
    fun `oppretter ikke journalpost hvis pdf-generering feiler`() {
        every { brevServiceMock.genererPdf(any()) } throws WebClientResponseException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "En feilmelding", null, null, null
        )

        assertThrows<WebClientResponseException> { genererOgJournalfoerBrevTask.doTask(task) }

        verify(exactly = 1) { brevServiceMock.genererPdf(any()) }
        verify(exactly = 0) { brevServiceMock.journalfoerUtgaaendeDokument(any(), any(), any()) }
        verify(exactly = 0) { taskRepositoryMock.save(any()) }
    }

    @Test
    fun `oppretter ikke journalpost hvis journalføring feiler`() {
        every { brevServiceMock.journalfoerUtgaaendeDokument(any(), any(), any()) } throws WebClientResponseException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "En feilmelding", null, null, null
        )

        assertThrows<WebClientResponseException> { genererOgJournalfoerBrevTask.doTask(task) }

        verify(exactly = 1) { brevServiceMock.genererPdf(any()) }
        verify(exactly = 1) { brevServiceMock.journalfoerUtgaaendeDokument(any(), any(), any()) }
        verify(exactly = 0) { taskRepositoryMock.save(any()) }
    }
}