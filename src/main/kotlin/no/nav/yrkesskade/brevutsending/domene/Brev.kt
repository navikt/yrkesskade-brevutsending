package no.nav.yrkesskade.brevutsending.domene

data class Brev(
    val tittel: String,
    val brevkode: String,
    val tekst: String,
    val innkommendeJournalpostId: String,
)
