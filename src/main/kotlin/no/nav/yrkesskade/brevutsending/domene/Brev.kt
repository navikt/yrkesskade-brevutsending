package no.nav.yrkesskade.brevutsending.domene

import java.time.Instant

data class Brev(
    val tittel: String,
    val template: String,
    val brevkode: String,
    val enhet: String,
    val tekst: String,
)

data class BrevutsendelseBestiltHendelse(
    val brev: Brev,
    val metadata: BrevutsendelseMetadata
)

data class BrevutsendelseMetadata(
    val innkommendeJournalpostId: String,
    val tidspunktBestilt: Instant,
    val navCallId: String
)
