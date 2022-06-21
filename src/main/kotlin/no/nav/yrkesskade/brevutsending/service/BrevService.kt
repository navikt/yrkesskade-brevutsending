package no.nav.yrkesskade.brevutsending.service

import no.nav.yrkesskade.brevutsending.client.DokarkivClient
import no.nav.yrkesskade.brevutsending.client.DokdistClient
import no.nav.yrkesskade.brevutsending.client.PdfClient
import no.nav.yrkesskade.brevutsending.domene.AvsenderMottaker
import no.nav.yrkesskade.brevutsending.domene.Brev
import no.nav.yrkesskade.brevutsending.domene.Bruker
import no.nav.yrkesskade.brevutsending.domene.BrukerIdType
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.Dokument
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariant
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariantformat
import no.nav.yrkesskade.brevutsending.domene.Fagsaksystem
import no.nav.yrkesskade.brevutsending.domene.Filtype
import no.nav.yrkesskade.brevutsending.domene.Journalposttype
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostResponse
import no.nav.yrkesskade.brevutsending.domene.Sak
import no.nav.yrkesskade.brevutsending.domene.Sakstype
import no.nav.yrkesskade.brevutsending.util.TokenUtil
import org.springframework.stereotype.Component

@Component
class BrevService(
    val dokarkivClient: DokarkivClient,
    val dokdistClient: DokdistClient,
    val pdfClient: PdfClient
) {

    fun opprettPdf(brev: Brev) {
//        pdfClient.lagPdf()
    }

    fun distribuerJournalpost(journalpostId: String) {
        val distribuerJournalpostRequest = DistribuerJournalpostRequest(journalpostId = journalpostId)
        dokdistClient.distribuerJournalpost(distribuerJournalpostRequest)
    }

    fun journalfoerUtgaaendeDokument(brev: Brev): OpprettJournalpostResponse? {
        val opprettJournalpostRequest = OpprettJournalpostRequest(
            forsoekFerdigstill = true,
            tittel = brev.tittel,
            journalposttype = Journalposttype.UTGAAENDE,
            avsenderMottaker = AvsenderMottaker(
                navn = null, // mottakers navn -- ikke nødvendig når idtype er fnr
                id = "", // mottakers fnr
                idType = BrukerIdType.FNR
            ),
            bruker = Bruker(
                id = "", // mottakers fnr
                idType = BrukerIdType.FNR
            ),
            tema = "YRK",
            kanal = "NAV_NO", // stemmer dette??
            journalfoerendeEnhet = "9999", // erstatte med enhet fra token
            eksternReferanseId = "", // noe lurt
            datoMottatt = null,
            sak = Sak(
                sakstype = Sakstype.FAGSAK,
                fagsakId = "YRK-SAK-BLABLA0123",
                fagsaksystem = Fagsaksystem.YRKESSKADE
            ),
            dokumenter = listOf(
                Dokument(
                    brevkode = brev.brevkode,
                    tittel = brev.tittel,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            filtype = Filtype.PDFA,
                            variantformat = Dokumentvariantformat.ARKIV,
                            fysiskDokument = ByteArray(1) // bytte ut med ekte pdf
                        )
                    )
                ),
            )
        )

        return dokarkivClient.journalfoerDokument(opprettJournalpostRequest)
    }
}


