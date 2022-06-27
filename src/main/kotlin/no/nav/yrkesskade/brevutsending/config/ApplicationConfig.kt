package no.nav.yrkesskade.brevutsending.config

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootConfiguration
@ComponentScan("no.nav.yrkesskade.prosessering", "no.nav.yrkesskade.brevutsending", "no.nav.familie.sikkerhet")
@EnableRetry
@ConfigurationPropertiesScan
@EnableScheduling
class ApplicationConfig
