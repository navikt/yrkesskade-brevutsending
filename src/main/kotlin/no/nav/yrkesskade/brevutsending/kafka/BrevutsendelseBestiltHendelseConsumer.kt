package no.nav.yrkesskade.brevutsending.kafka;

import no.nav.yrkesskade.brevutsending.domene.BrevutsendelseBestiltHendelse
import no.nav.yrkesskade.brevutsending.task.ProsesserBrevTilUtsendingTask
import no.nav.yrkesskade.brevutsending.util.kallMetodeMedCallId
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class BrevutsendelseBestiltHendelseConsumer(
    private val taskRepository: TaskRepository
) {

    @KafkaListener(
        id = "brevutsendelse-bestilt",
        topics = ["\${kafka.topic.brevutsendelse-bestilt}"],
        containerFactory = "brevutsendelseBestiltListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional
    fun listen(record: BrevutsendelseBestiltHendelse) {
        kallMetodeMedCallId(record.metadata.navCallId) {
            taskRepository.save(ProsesserBrevTilUtsendingTask.opprettTask(record))
        }
    }
}
