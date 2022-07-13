package no.nav.yrkesskade.brevutsending.task

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.yrkesskade.brevutsending.service.BrevService
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles


@TaskStepBeskrivelse(
    taskStepType = ProsesserBrevTilUtsendingTask.TASK_STEP_TYPE,
    beskrivelse = "Prosessering av brev til utsending",
    maxAntallFeil = 10,
    triggerTidVedFeilISekunder = 60 * 30
)
@Component
class ProsesserBrevTilUtsendingTask(
    val brevService: BrevService
) : AsyncTaskStep {

    val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private val secureLogger = getSecureLogger()
    val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun doTask(task: Task) {
        log.info("Starter ProsesserBrevTilUtsendingTask")
        secureLogger.info("ProsesserBrevTilUtsendingTask kjoerer med payload ${task.payload}")

        val payload = jacksonObjectMapper.readValue<ProsesserBrevTilUtsendingTaskPayloadDto>(task.payload)

        brevService.behandleBrevutsendingBestilling(payload.brevutsendingBestiltHendelse)

        log.info("ProsesserBrevTilUtsendingTask ferdig")
    }

    companion object {
        fun opprettTask(brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse): Task {
            val jacksonObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
            return Task(
                type = TASK_STEP_TYPE,
                payload = jacksonObjectMapper.writeValueAsString(ProsesserBrevTilUtsendingTaskPayloadDto(brevutsendingBestiltHendelse))
            )
        }

        const val TASK_STEP_TYPE = "ProsesserBrevTilUtsending"
    }
}

data class ProsesserBrevTilUtsendingTaskPayloadDto(val brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse)