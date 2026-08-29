package com.drugdose.model

import com.google.gson.annotations.SerializedName

enum class FormulaType {
    @SerializedName("per_kg") PER_KG,
    @SerializedName("per_m2") PER_M2,
    @SerializedName("fissa") FISSA,
    @SerializedName("fasce") FASCE
}

enum class DoseUnit(val label: String) {
    @SerializedName("mg") MG("mg"),
    @SerializedName("µg") MICROGRAM("µg"),
    @SerializedName("applicazione") APPLICATION("applicazione"),
    @SerializedName("compressa") TABLET("compressa")
}

enum class FrequencyUnit(val label: String) {
    @SerializedName("giorno") DAY("al giorno"),
    @SerializedName("settimana") WEEK("a settimana"),
    @SerializedName("dose_singola") SINGLE("dose singola")
}

data class FasciaDose(
    @SerializedName("peso_min_kg") val pesoMinKg: Double,
    @SerializedName("peso_max_kg") val pesoMaxKg: Double,
    @SerializedName("dose_mg") val doseMg: Double
)

data class Drug(
    val id: String,
    val nome: String,
    val indicazione: String,
    @SerializedName("tipo_formula") val tipoFormula: FormulaType,
    @SerializedName("dose_unitaria") val doseUnitaria: Double,
    @SerializedName("unita_misura") val unitaMisura: DoseUnit = DoseUnit.MG,
    @SerializedName("unita_frequenza") val unitaFrequenza: FrequencyUnit = FrequencyUnit.DAY,
    @SerializedName("dose_minima") val doseMinima: Double? = null,
    @SerializedName("dose_massima") val doseMassima: Double? = null,
    @SerializedName("peso_minimo_kg") val pesoMinimoKg: Double? = null,
    @SerializedName("peso_massimo_kg") val pesoMassimoKg: Double? = null,
    @SerializedName("eta_minima_anni") val etaMinimaAnni: Int? = null,
    @SerializedName("n_somministrazioni") val nSomministrazioni: Int = 1,
    @SerializedName("compressa_mg") val compressaMg: Double? = null,
    @SerializedName("fasce_peso") val fascePeso: List<FasciaDose>? = null,
    val alert: List<String> = emptyList(),
    val controindicazioni: List<String> = emptyList(),
    val fonte: String = ""
) {
    fun formulaLabel(): String = when (tipoFormula) {
        FormulaType.PER_KG -> "${formatDose(doseUnitaria)} ${unitaMisura.label}/kg"
        FormulaType.PER_M2 -> "${formatDose(doseUnitaria)} ${unitaMisura.label}/m²"
        FormulaType.FISSA -> "${formatDose(doseUnitaria)} ${unitaMisura.label}"
        FormulaType.FASCE -> "a fasce di peso"
    }

    fun frequencyLabel(): String = when (unitaFrequenza) {
        FrequencyUnit.SINGLE -> "dose singola"
        else -> "$nSomministrazioni volta${if (nSomministrazioni == 1) "" else "e"} ${unitaFrequenza.label}"
    }

    private fun formatDose(value: Double): String = if (value % 1.0 == 0.0) "%.0f".format(value) else "%.1f".format(value)
}
