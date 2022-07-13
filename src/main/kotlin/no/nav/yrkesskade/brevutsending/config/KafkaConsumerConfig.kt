package no.nav.yrkesskade.brevutsending.config

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig : AbstractKafkaConfig() {

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
}