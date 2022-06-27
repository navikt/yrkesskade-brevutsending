package no.nav.yrkesskade.brevutsending.config

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@Configuration
class CommonKafkaConfig : AbstractKafkaConfig() {
}