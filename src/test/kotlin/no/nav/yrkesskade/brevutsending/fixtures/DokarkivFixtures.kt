package no.nav.yrkesskade.brevutsending.fixtures

import no.nav.yrkesskade.brevutsending.domene.AvsenderMottaker
import no.nav.yrkesskade.brevutsending.domene.Bruker
import no.nav.yrkesskade.brevutsending.domene.BrukerIdType
import no.nav.yrkesskade.brevutsending.domene.Dokument
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariant
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariantformat
import no.nav.yrkesskade.brevutsending.domene.Filtype
import no.nav.yrkesskade.brevutsending.domene.Journalposttype
import no.nav.yrkesskade.brevutsending.domene.Kanal
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostResponse
import no.nav.yrkesskade.brevutsending.domene.Sak
import no.nav.yrkesskade.brevutsending.domene.Sakstype
import java.time.Instant
import java.util.UUID

fun opprettJournalpostOkRespons() =
    OpprettJournalpostResponse(
        journalpostferdigstilt = false,
        journalpostId = "1234",
        dokumenter = emptyList()
    )

fun opprettJournalpostRequest() = OpprettJournalpostRequest(
    tittel = "veiledningsbrev tannlegeerklæring",
    journalposttype = Journalposttype.INNGAAENDE,
    journalfoerendeEnhet = "4849",
    avsenderMottaker = AvsenderMottaker(
        id = "12345699999",
        idType = BrukerIdType.FNR
    ),
    bruker = Bruker(
        id = "12345699999",
        idType = BrukerIdType.FNR
    ),
    tema = "YRK",
    kanal = null,
    datoMottatt = Instant.now().toString(),
    eksternReferanseId = UUID.randomUUID().toString(),
    sak = Sak(sakstype = Sakstype.GENERELL_SAK),
    dokumenter = listOf(
        Dokument(
            brevkode = "NAV 13-something",
            tittel = "veiledningsbrev tannlegeerklæring",
            dokumentvarianter = listOf(
                Dokumentvariant(
                    filtype = Filtype.PDFA,
                    variantformat = Dokumentvariantformat.ARKIV,
                    fysiskDokument = "test".encodeToByteArray()
                )
            )
        )
    )
)
