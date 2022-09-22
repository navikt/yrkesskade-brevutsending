package no.nav.yrkesskade.brevutsending.docker

import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName


class KafkaDockerContainer private constructor() : KafkaContainer(DockerImageName.parse(IMAGE_NAME)) {
    companion object {
        const val IMAGE_NAME = "confluentinc/cp-kafka:7.0.5"
        val container: KafkaDockerContainer by lazy {
            KafkaDockerContainer().apply {
                withReuse(true)
                start()
                waitUntilContainerStarted()
            }
        }
    }
}