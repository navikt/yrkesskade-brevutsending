package no.nav.yrkesskade.brevutsending.kafka;

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class BrevSkalSendesConsumer {

    @KafkaListener(
        id = "brev-skal-sendes",
        topics = ["\${kafka.topic.brev-skal-sendes}"],
        containerFactory = "brevSkalSendesListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional
    fun listen() {
        println("her skal vi opprette en task")
    }
}
