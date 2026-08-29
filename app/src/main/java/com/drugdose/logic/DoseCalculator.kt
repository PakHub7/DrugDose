package com.drugdose.logic

import com.drugdose.model.DoseResult
import com.drugdose.model.DoseUnit
import com.drugdose.model.Drug
import com.drugdose.model.FormulaType
import com.drugdose.model.FrequencyUnit
import kotlin.math.roundToInt
import kotlin.math.sqrt

object DoseCalculator {

    fun calcola(
        drug: Drug,
        pesoKg: Double,
        altezzaCm: Double? = null,
        etaAnni: Int? = null
    ): DoseResult {

        if (!pesoKg.isFinite() || pesoKg <= 0) {
            return errore(drug, "Il peso deve essere maggiore di 0.")
        }

        if (drug.pesoMassimoKg != null && pesoKg > drug.pesoMassimoKg) {
            return errore(drug, "Peso troppo alto: massimo ${drug.pesoMassimoKg} kg.")
        }

        if (altezzaCm != null && (!altezzaCm.isFinite() || altezzaCm <= 0)) {
            return errore(drug, "L'altezza deve essere maggiore di 0.")
        }

        drug.pesoMinimoKg?.let { pesoMinimo ->
            if (pesoKg < pesoMinimo) {
                return errore(drug, "Peso troppo basso: richiede almeno $pesoMinimo kg.")
            }
        }

        drug.etaMinimaAnni?.let { etaMinima ->
            if (etaAnni == null) {
                return errore(drug, "Inserisci l'età: il farmaco è indicato dai $etaMinima anni.")
            }
            if (etaAnni < etaMinima) {
                return errore(drug, "Età troppo bassa: indicato dai $etaMinima anni.")
            }
        }

        if (etaAnni != null && etaAnni < 0) {
            return errore(drug, "L'età non può essere negativa.")
        }

        var bsa: Double? = null

        val doseTeoricaMg = when (drug.tipoFormula) {
            FormulaType.PER_KG -> {
                val doseMgPerKg = when (drug.unitaMisura) {
                    DoseUnit.MICROGRAM -> drug.doseUnitaria / 1000.0
                    DoseUnit.MG -> drug.doseUnitaria
                    else -> return errore(drug, "Unità non valida per una dose per kg.")
                }
                doseMgPerKg * pesoKg
            }

            FormulaType.PER_M2 -> {
                val altezza = altezzaCm ?: return errore(drug, "Serve l'altezza per questo calcolo.")
                if (drug.unitaMisura != DoseUnit.MG) {
                    return errore(drug, "La dose per m² deve essere espressa in mg.")
                }
                bsa = sqrt((altezza * pesoKg) / 3600.0)
                drug.doseUnitaria * bsa
            }

            FormulaType.FISSA -> drug.doseUnitaria

            FormulaType.FASCE -> {
                drug.fascePeso?.firstOrNull { fascia ->
                    pesoKg >= fascia.pesoMinKg && pesoKg < fascia.pesoMaxKg
                }?.doseMg ?: return errore(drug, "Nessuna fascia di peso trovata.")
            }
        }

        val alerts = drug.alert.toMutableList()
        var doseFinale = doseTeoricaMg
        var limiteMassimoApplicato: Double? = null

        drug.doseMinima?.let { min ->
            if (doseFinale < min) {
                doseFinale = min
                alerts.add(0, "Dose minima applicata: $min mg.")
            }
        }

        drug.doseMassima?.let { max ->
            if (doseFinale > max) {
                doseFinale = max
                limiteMassimoApplicato = max
                alerts.add(0, "Dose massima applicata: $max mg.")
            }
        }

        val outputUnit = when {
            drug.tipoFormula == FormulaType.FISSA && drug.unitaMisura == DoseUnit.APPLICATION -> DoseUnit.APPLICATION
            drug.tipoFormula == FormulaType.FISSA && drug.unitaMisura == DoseUnit.TABLET -> DoseUnit.TABLET
            else -> DoseUnit.MG
        }

        val nCompresse = if (drug.compressaMg != null && outputUnit == DoseUnit.MG) {
            (doseFinale / drug.compressaMg).roundToInt().coerceAtLeast(1)
        } else null

        return DoseResult(
            farmacoNome = drug.nome,
            indicazione = drug.indicazione,
            doseTeorica = doseTeoricaMg,
            doseTotale = doseFinale,
            doseUnita = outputUnit,
            limiteMassimoApplicato = limiteMassimoApplicato,
            dosePerSomministrazione = doseFinale,
            bsa = bsa,
            nCompresse = nCompresse,
            compressaMg = drug.compressaMg,
            nSomministrazioni = drug.nSomministrazioni,
            frequenza = drug.unitaFrequenza,
            alerts = alerts,
            controindicazioni = drug.controindicazioni,
            fonte = drug.fonte
        )
    }

    private fun errore(drug: Drug, messaggio: String) = DoseResult(
        farmacoNome = drug.nome,
        indicazione = drug.indicazione,
        doseTeorica = 0.0,
        doseTotale = 0.0,
        doseUnita = DoseUnit.MG,
        errore = messaggio,
        controindicazioni = drug.controindicazioni,
        fonte = drug.fonte
    )
}
