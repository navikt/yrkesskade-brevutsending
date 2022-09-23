package no.nav.yrkesskade.brevutsending.task

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.yrkesskade.brevutsending.service.BrevService
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles


@TaskStepBeskrivelse(
    taskStepType = GenererOgJournalfoerBrevTask.TASK_STEP_TYPE,
    beskrivelse = "Generere PDF og lage journalpost",
    maxAntallFeil = 10,
    triggerTidVedFeilISekunder = 60 * 30
)
@Component
class GenererOgJournalfoerBrevTask(
    val brevService: BrevService,
    val taskRepository: TaskRepository
) : AsyncTaskStep {

    val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private val secureLogger = getSecureLogger()
    val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun doTask(task: Task) {
        log.info("Starter GenererOgJournalfoerBrevTask")
        secureLogger.info("GenererOgJournalfoerBrevTask kjoerer med payload ${task.payload}")

        val payload = jacksonObjectMapper.readValue<GenererOgJournalfoerBrevTaskPayloadDto>(task.payload)

        val pdf = brevService.genererPdf(payload.brevutsendingBestiltHendelse.brev)
        val opprettJournalpostResponse = brevService.journalfoerUtgaaendeDokument(
            payload.brevutsendingBestiltHendelse.brev,
            payload.brevutsendingBestiltHendelse.mottaker,
            pdf
        )

        val distribuerBrevTask = taskRepository.save(
            DistribuerBrevTask.opprettTask(
                opprettJournalpostResponse.journalpostId,
                payload.brevutsendingBestiltHendelse.behandlingId
            )
        )

        log.info("GenererOgJournalfoerBrevTask ferdig, starter DistribuerBrevTask med id ${distribuerBrevTask.id}")
    }

    companion object {
        fun opprettTask(brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse): Task {
            val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
            return Task(
                type = TASK_STEP_TYPE,
                payload = jacksonObjectMapper.writeValueAsString(
                    GenererOgJournalfoerBrevTaskPayloadDto(brevutsendingBestiltHendelse)
                )
            )
        }

        const val TASK_STEP_TYPE = "GenererOgJournalfoerBrev"
    }
}

data class GenererOgJournalfoerBrevTaskPayloadDto(val brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse)