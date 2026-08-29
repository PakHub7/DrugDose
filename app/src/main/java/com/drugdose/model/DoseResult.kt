package com.drugdose.model

import java.util.Locale

data class DoseResult(
    val farmacoNome: String,
    val indicazione: String,
    val doseTeorica: Double,
    val doseTotale: Double,
    val doseUnita: DoseUnit,
    val limiteMassimoApplicato: Double? = null,
    val dosePerSomministrazione: Double? = null,
    val bsa: Double? = null,
    val nCompresse: Int? = null,
    val compressaMg: Double? = null,
    val nSomministrazioni: Int = 1,
    val frequenza: FrequencyUnit = FrequencyUnit.DAY,
    val alerts: List<String> = emptyList(),
    val controindicazioni: List<String> = emptyList(),
    val errore: String? = null,
    val fonte: String = ""
) {
    val isError: Boolean get() = errore != null

    fun formatDoseTeorica(): String = formatAmount(doseTeorica, doseUnita)

    fun formatDoseTotale(): String = formatAmount(doseTotale, doseUnita)

    fun formatDosePerSomm(): String = dosePerSomministrazione?.let {
        formatAmount(it, doseUnita)
    } ?: "—"

    fun formatFrequency(): String = when (frequenza) {
        FrequencyUnit.SINGLE -> "Dose singola"
        else -> "$nSomministrazioni volta${if (nSomministrazioni == 1) "" else "e"} ${frequenza.label}"
    }

    private fun formatAmount(value: Double, unit: DoseUnit): String {
        val decimals = if (value % 1.0 == 0.0) "%.0f" else "%.1f"
        return String.format(Locale.ITALY, "$decimals ${unit.label}", value)
    }
}
