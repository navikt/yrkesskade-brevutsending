package no.nav.yrkesskade.brevutsending.task

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.familie.log.mdc.MDCConstants
import no.nav.yrkesskade.brevutsending.kafka.BrevutsendingUtfoertClient
import no.nav.yrkesskade.brevutsending.service.BrevService
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles


@TaskStepBeskrivelse(
    taskStepType = DistribuerBrevTask.TASK_STEP_TYPE,
    beskrivelse = "Distribuere dokument på en gitt journalpost",
    maxAntallFeil = 10,
    triggerTidVedFeilISekunder = 60 * 30
)
@Component
class DistribuerBrevTask(
    val brevService: BrevService,
    val brevutsendingUtfoertClient: BrevutsendingUtfoertClient
) : AsyncTaskStep {

    val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private val secureLogger = getSecureLogger()
    val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun doTask(task: Task) {
        log.info("Starter DistribuerBrevTask")
        secureLogger.info("DistribuerBrevTask kjoerer med payload ${task.payload}")

        val payload = jacksonObjectMapper.readValue<DistribuerBrevTaskPayloadDto>(task.payload)
        brevService.distribuerJournalpost(payload.journalpostId)
        brevutsendingUtfoertClient.sendDokumentdistribusjonUtfoertHendelse(
            BrevutsendingUtfoertHendelse(
                journalpostId = payload.journalpostId,
                metadata = BrevutsendingUtfoertMetadata(MDC.get(MDCConstants.MDC_CALL_ID))
            )
        )

        log.info("DistribuerBrevTask ferdig")
    }

    companion object {
        fun opprettTask(journalpostId: String): Task {
            val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
            return Task(
                type = TASK_STEP_TYPE,
                payload = jacksonObjectMapper.writeValueAsString(DistribuerBrevTaskPayloadDto(journalpostId))
            )
        }

        const val TASK_STEP_TYPE = "DistribuerBrev"
    }
}

data class DistribuerBrevTaskPayloadDto(val journalpostId: String)