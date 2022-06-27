package no.nav.yrkesskade.brevutsending.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig : AbstractKafkaConfig() {

    @Bean
    fun brevSkalSendesListenerContainerFactory(kafkaProperties: KafkaProperties, environment: Environment):
            ConcurrentKafkaListenerContainerFactory<String, String> {

        val consumerProperties = kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        }
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProperties)

        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }
}