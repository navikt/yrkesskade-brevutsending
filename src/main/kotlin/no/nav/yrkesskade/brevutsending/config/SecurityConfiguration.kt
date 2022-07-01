package no.nav.yrkesskade.brevutsending.config

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation(ignore = ["springfox"])
@EnableOAuth2Client(cacheEnabled = true)
@Configuration
internal class SecurityConfiguration
