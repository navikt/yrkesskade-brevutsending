package no.nav.yrkesskade.brevutsending.kafka

import no.nav.yrkesskade.brevutsending.AbstractIT
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

internal class BrevutsendelseBestiltHendelseConsumerIT : AbstractIT() {

    @Autowired
    private lateinit var consumer: BrevutsendelseBestiltHendelseConsumer

    @Test
    fun listen() {

    }
}