package no.nav.yrkesskade.brevutsending.kafka

import no.nav.yrkesskade.brevutsending.AbstractIT
import no.nav.yrkesskade.brevutsending.fixtures.brevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.kotlin.eq

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.core.KafkaTemplate

internal class BrevutsendingBestiltHendelseConsumerIT : AbstractIT() {

    @Value("\${kafka.topic.brevutsending-bestilt}")
    private lateinit var topic: String

    @SpyBean
    private lateinit var consumer: BrevutsendingBestiltHendelseConsumer

    @Autowired
    private lateinit var brevutsendingBestiltKafkaTemplate: KafkaTemplate<String, BrevutsendingBestiltHendelse>

    @Test
    fun listen() {
        val brevutsendingBestiltHendelse = brevutsendingBestiltHendelse()
        brevutsendingBestiltKafkaTemplate.send(topic, brevutsendingBestiltHendelse)
        verify(consumer, timeout(20000L).times(1)).listen(eq(brevutsendingBestiltHendelse))
    }
}