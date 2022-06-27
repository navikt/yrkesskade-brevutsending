package no.nav.yrkesskade.brevutsending.domene

data class Brev(
    val tittel: String,
    val brevkode: String,
    val enhet: String,
    val tekst: String,
    val innkommendeJournalpostId: String,
)
