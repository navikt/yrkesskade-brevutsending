package no.nav.yrkesskade.brevutsending.kafka

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class BrevutsendingUtfoertKafkaConsumer {

    private val latch = CountDownLatch(1)
    private lateinit var payload: ConsumerRecord<String, BrevutsendingUtfoertHendelse>

    @KafkaListener(topics = ["\${kafka.topic.brevutsending-utfoert}"])
    fun receive(consumerRecord: ConsumerRecord<String, BrevutsendingUtfoertHendelse>) {
        payload = consumerRecord
        latch.countDown()
    }

    fun getLatch() = latch

    fun getPayload() = payload
}