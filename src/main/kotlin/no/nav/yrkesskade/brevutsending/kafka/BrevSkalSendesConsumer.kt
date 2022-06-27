package no.nav.yrkesskade.brevutsending.kafka;

import no.nav.yrkesskade.brevutsending.util.kallMetodeMedCallId
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class BrevSkalSendesConsumer(
    private val taskRepository: TaskRepository
) {

    @KafkaListener(
        id = "brev-skal-sendes",
        topics = ["\${kafka.topic.brev-skal-sendes}"],
        containerFactory = "brevSkalSendesListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional
    fun listen(record: String) {
        kallMetodeMedCallId {
//            taskRepository.save(ProsesserBrevTilUtsendingTask.opprettTask(record))
        }
    }
}
