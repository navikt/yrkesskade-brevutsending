package no.nav.yrkesskade.brevutsending.config

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@TestConfiguration
class KafkaTestConfig {

    @Bean
    fun brevutsendingBestiltProducerFactory(
        properties: KafkaProperties
    ): DefaultKafkaProducerFactory<String, BrevutsendingBestiltHendelse> =
        DefaultKafkaProducerFactory(
            properties.buildProducerProperties().apply {
                this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
            }
        )

    @Bean
    fun brevutsendingBestiltKafkaTemplate(
        skadeforklaringProducerFactory: ProducerFactory<String, BrevutsendingBestiltHendelse>
    ): KafkaTemplate<String, BrevutsendingBestiltHendelse> = KafkaTemplate(skadeforklaringProducerFactory)
}