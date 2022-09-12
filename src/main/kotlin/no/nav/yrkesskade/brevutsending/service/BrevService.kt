package no.nav.yrkesskade.brevutsending.service

import no.nav.yrkesskade.brevutsending.client.DokarkivClient
import no.nav.yrkesskade.brevutsending.client.DokdistClient
import no.nav.yrkesskade.brevutsending.client.JsonToPdfClient
import no.nav.yrkesskade.brevutsending.domene.AvsenderMottaker
import no.nav.yrkesskade.brevutsending.domene.Bruker
import no.nav.yrkesskade.brevutsending.domene.BrukerIdType
import no.nav.yrkesskade.brevutsending.domene.DistribuerJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.Dokument
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariant
import no.nav.yrkesskade.brevutsending.domene.Dokumentvariantformat
import no.nav.yrkesskade.brevutsending.domene.Filtype
import no.nav.yrkesskade.brevutsending.domene.Journalposttype
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostRequest
import no.nav.yrkesskade.brevutsending.domene.OpprettJournalpostResponse
import no.nav.yrkesskade.brevutsending.domene.Sak
import no.nav.yrkesskade.brevutsending.domene.Sakstype
import no.nav.yrkesskade.brevutsending.util.getSecureLogger
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BrevService(
    val dokarkivClient: DokarkivClient,
    val dokdistClient: DokdistClient,
    val jsonToPdfClient: JsonToPdfClient
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun behandleBrevutsendingBestilling(brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse) {
        val brev = brevutsendingBestiltHendelse.brev
        val pdf = jsonToPdfClient.genererPdfFraJson(brev.innhold.innhold)
//        val opprettJournalpostResponse = journalfoerUtgaaendeDokument(brev, pdf)
//        distribuerJournalpost(opprettJournalpostResponse.journalpostId)
//        giBeskjedTilbakeTilSaksbehandling()
    }

    fun distribuerJournalpost(journalpostId: String) {
        val distribuerJournalpostRequest = DistribuerJournalpostRequest(journalpostId = journalpostId)
        dokdistClient.distribuerJournalpost(distribuerJournalpostRequest)
    }

    fun journalfoerUtgaaendeDokument(brev: Brev, pdf: ByteArray): OpprettJournalpostResponse? {
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
            eksternReferanseId = "", // noe lurt; kanskje referanse-id fra Behandling-tabellen i ys-sak?
            datoMottatt = null,
            sak = Sak(sakstype = Sakstype.GENERELL_SAK),
            dokumenter = listOf(
                Dokument(
                    brevkode = brev.brevkode,
                    tittel = brev.tittel,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            filtype = Filtype.PDFA,
                            variantformat = Dokumentvariantformat.ARKIV,
                            fysiskDokument = pdf
                        )
                    )
                ),
            )
        )

        return dokarkivClient.journalfoerDokument(opprettJournalpostRequest)
    }
}


