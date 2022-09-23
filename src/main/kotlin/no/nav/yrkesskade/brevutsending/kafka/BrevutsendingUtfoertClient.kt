package no.nav.yrkesskade.brevutsending.kafka

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BrevutsendingUtfoertClient(
    @Value("\${kafka.topic.brevutsending-utfoert}") private val topic: String,
    private val kafkaTemplate: KafkaTemplate<String, BrevutsendingUtfoertHendelse>
) {

    fun sendDokumentdistribusjonUtfoertHendelse(brevutsendingUtfoertHendelse: BrevutsendingUtfoertHendelse) {
        kafkaTemplate.send(topic, brevutsendingUtfoertHendelse).get()
    }
}