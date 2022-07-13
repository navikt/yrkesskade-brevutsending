package no.nav.yrkesskade.brevutsending.kafka

import no.nav.yrkesskade.brevutsending.AbstractIT
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired

internal class BrevutsendingBestiltHendelseConsumerIT : AbstractIT() {

    @Autowired
    private lateinit var consumer: BrevutsendingBestiltHendelseConsumer

    @Test
    fun listen() {

    }
}