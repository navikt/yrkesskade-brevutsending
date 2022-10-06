package no.nav.yrkesskade.brevutsending.fixtures

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfInnholdElement
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTekstElement
import java.time.Instant
import java.util.UUID

fun brevutsendingBestiltHendelse(): BrevutsendingBestiltHendelse {
    return BrevutsendingBestiltHendelse(
        brevinnhold = listOf(
            PdfInnholdElement(
                type = "paragraph",
                children = listOf(
                    PdfTekstElement(
                        text = "test"
                    )
                ),
                align = "left"
            )
        ),
        behandlingId = 1234,
        mottaker = Mottaker(foedselsnummer = "012345678910"),
        metadata = BrevutsendingMetadata(
            tidspunktBestilt = Instant.now(),
            navCallId = UUID.randomUUID().toString()
        ),
        enhet = "4849",
        tittel = "Veiledningsbrev Tannlegeerklæring"
    )
}