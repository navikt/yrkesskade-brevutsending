package no.nav.yrkesskade.brevutsending.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.yrkesskade.brevutsending.AbstractIT
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit

internal class BrevutsendingUtfoertClientTest : AbstractIT() {

    @Autowired
    private lateinit var brevutsendingUtfoertClient: BrevutsendingUtfoertClient

    @Autowired
    private lateinit var consumer: BrevutsendingUtfoertKafkaConsumer

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun sendDokumentdistribusjonUtfoertHendelse() {
        val brevutsendingUtfoertHendelse = BrevutsendingUtfoertHendelse("1234")
        brevutsendingUtfoertClient.sendDokumentdistribusjonUtfoertHendelse(
            brevutsendingUtfoertHendelse
        )
        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS)
        assertThat(consumer.getPayload().value()).isEqualTo(brevutsendingUtfoertHendelse)
    }
}