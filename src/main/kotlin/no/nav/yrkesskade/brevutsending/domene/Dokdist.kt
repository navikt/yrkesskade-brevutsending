package no.nav.yrkesskade.brevutsending.domene

data class DistribuerJournalpostRequest(
    val journalpostId: String,
    val bestillendeFagsystem: String = "yrkesskade-saksbehandling",//Fagsystemet som bestiller distribusjon. Felles kode for fagsystemet. Konsumenter bestemmer selv verdi, men må være lik for alle forsendelser fra fagsystemet.
    val dokumentProdApp: String = "yrkesskade-brevutsending", //Applikasjon som har produsert hoveddokumentet (for sporing og feilsøking)
    val distribusjonstype: String = Distribusjonstype.ANNET.name,
    val distribusjonstidspunkt: String = Distribusjonstidspunkt.KJERNETID.name
)

data class DistribuerJournalpostResponse(val bestillingsId: String)

enum class Distribusjonstype {
    VEDTAK,
    VIKTIG,
    ANNET
}

enum class Distribusjonstidspunkt {
    UMIDDELBART, // hele døgnet
    KJERNETID // 7-23
}