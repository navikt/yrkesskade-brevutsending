package no.nav.yrkesskade.brevutsending.config

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler

@Configuration
class KafkaConfig(val kafkaProperties: KafkaProperties) : AbstractKafkaConfig() {

    @Bean
    fun brevutsendingBestiltListenerContainerFactory(
        kafkaProperties: KafkaProperties
    ): ConcurrentKafkaListenerContainerFactory<String, BrevutsendingBestiltHendelse> {

        val consumerFactory =
            DefaultKafkaConsumerFactory<String, BrevutsendingBestiltHendelse>(kafkaProperties.buildConsumerProperties())

        return ConcurrentKafkaListenerContainerFactory<String, BrevutsendingBestiltHendelse>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }

    @Bean
    fun brevutsendingUtfoertHendelseProducerFactory(): ProducerFactory<String, BrevutsendingUtfoertHendelse> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun brevutsendingUtfoertHendelseKafkaTemplate(
        brevutsendingUtfoertHendelseProducerFactory: ProducerFactory<String,  BrevutsendingUtfoertHendelse>
    ): KafkaTemplate<String, BrevutsendingUtfoertHendelse> {
        return KafkaTemplate(brevutsendingUtfoertHendelseProducerFactory)
    }
}