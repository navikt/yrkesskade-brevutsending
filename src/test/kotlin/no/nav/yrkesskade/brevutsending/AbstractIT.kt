package no.nav.yrkesskade.brevutsending

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.yrkesskade.brevutsending.docker.KafkaDockerContainer
import no.nav.yrkesskade.brevutsending.docker.PostgresDockerContainer
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.transaction.annotation.Transactional


@Transactional
@ActiveProfiles("integration")
@EnableMockOAuth2Server
@ContextConfiguration(initializers = [AbstractIT.DockerConfigInitializer::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
@SpringBootTest
abstract class AbstractIT {

    init {
        PostgresDockerContainer.container
        KafkaDockerContainer.container
    }

    class DockerConfigInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + PostgresDockerContainer.container.jdbcUrl,
                "spring.datasource.username=" + PostgresDockerContainer.container.username,
                "spring.datasource.password=" + PostgresDockerContainer.container.password,
                "spring.kafka.bootstrap-servers=" + KafkaDockerContainer.container.bootstrapServers
            )
        }
    }
}