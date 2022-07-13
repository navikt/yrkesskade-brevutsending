package no.nav.yrkesskade.brevutsending.kafka;

import no.nav.yrkesskade.brevutsending.task.ProsesserBrevTilUtsendingTask
import no.nav.yrkesskade.brevutsending.util.kallMetodeMedCallId
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class BrevutsendingBestiltHendelseConsumer(
    private val taskRepository: TaskRepository
) {

    @KafkaListener(
        id = "brevutsending-bestilt",
        topics = ["\${kafka.topic.brevutsending-bestilt}"],
        containerFactory = "brevutsendingBestiltListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional
    fun listen(record: BrevutsendingBestiltHendelse) {
        kallMetodeMedCallId(record.metadata.navCallId) {
            taskRepository.save(ProsesserBrevTilUtsendingTask.opprettTask(record))
        }
    }
}
