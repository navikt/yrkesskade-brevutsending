package no.nav.yrkesskade.brevutsending.service

import no.nav.familie.log.mdc.MDCConstants
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
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import org.slf4j.LoggerFactory
import org.slf4j.MDC
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
        journalfoerUtgaaendeDokument(brev, brevutsendingBestiltHendelse.mottaker, pdf).also {
            log.info("Journalfoert utgående brev med journalpostId=${it?.journalpostId}, ferdigstilt=${it?.journalpostferdigstilt}")
        }
    }

    fun distribuerJournalpost(journalpostId: String) {
        val distribuerJournalpostRequest = DistribuerJournalpostRequest(journalpostId = journalpostId)
        dokdistClient.distribuerJournalpost(distribuerJournalpostRequest)
    }

    fun journalfoerUtgaaendeDokument(brev: Brev, mottaker: Mottaker, pdf: ByteArray): OpprettJournalpostResponse? {
        val opprettJournalpostRequest = OpprettJournalpostRequest(
            forsoekFerdigstill = true,
            tittel = brev.tittel,
            journalposttype = Journalposttype.UTGAAENDE,
            avsenderMottaker = AvsenderMottaker(
                id = mottaker.foedselsnummer,
                idType = BrukerIdType.FNR
            ),
            bruker = Bruker(
                id = mottaker.foedselsnummer,
                idType = BrukerIdType.FNR
            ),
            tema = "YRK",
            kanal = null,
            journalfoerendeEnhet = brev.enhet,
            eksternReferanseId = MDC.get(MDCConstants.MDC_CALL_ID), // eventuelt behandlingId
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


