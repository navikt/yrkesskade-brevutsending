package no.nav.yrkesskade.brevutsending.domene


/**
 * Abstrakt klasse som PDF-dataklasser kan arve fra, som f.eks. skademelding og skadeforklaring.
 */
abstract class PdfData

enum class PdfTemplate(val templatenavn: String) {
    TANNLEGEERKLAERING_VEILEDNING("tannlegeerklaering-veiledning"),
    ARBEIDSTILSYNET_KOPI_VEILEDNING("arbeidstilsynet-kopi-veiledning")
}