package com.drugdose.logic

import com.drugdose.model.DoseUnit
import com.drugdose.model.Drug
import com.drugdose.model.FasciaDose
import com.drugdose.model.FormulaType
import com.drugdose.model.FrequencyUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DoseCalculatorTest {

    @Test
    fun ivermectinaConverteMicrogrammiECalcola14Mg() {
        val result = DoseCalculator.calcola(
            drug = drug(FormulaType.PER_KG, dose = 200.0, unit = DoseUnit.MICROGRAM, minAge = 5),
            pesoKg = 70.0,
            etaAnni = 18
        )
        assertTrue(!result.isError)
        assertEquals(14.0, result.doseTotale, 0.0001)
        assertEquals(DoseUnit.MG, result.doseUnita)
    }

    @Test
    fun limiteMassimoRiduceLaDoseTeorica() {
        val result = DoseCalculator.calcola(
            drug = drug(FormulaType.PER_KG, dose = 200.0, unit = DoseUnit.MICROGRAM, maxDose = 12.0),
            pesoKg = 100.0, // 200µg * 100kg = 20mg teorici
            etaAnni = 25
        )
        // La dose teorica deve restare 20mg
        assertEquals(20.0, result.doseTeorica, 0.0001)
        // La dose finale deve essere limitata a 12mg
        assertEquals(12.0, result.doseTotale, 0.0001)
        assertEquals(12.0, result.limiteMassimoApplicato!!, 0.0001)
    }

    @Test
    fun bsaMostellerCalcolaIlValoreAtteso() {
        val result = DoseCalculator.calcola(
            drug = drug(FormulaType.PER_M2, dose = 50.0, unit = DoseUnit.MG),
            pesoKg = 70.0,
            altezzaCm = 170.0
        )
        assertTrue(!result.isError)
        assertEquals(1.818, result.bsa!!, 0.001)
        assertEquals(90.92, result.doseTotale, 0.1)
    }

    @Test
    fun fasciaDiPesoGestisceIlConfineInferiore() {
        val result = DoseCalculator.calcola(
            drug = drug(FormulaType.FASCE, dose = 0.0, unit = DoseUnit.MG,
                fasce = listOf(FasciaDose(25.0, 45.0, 50.0), FasciaDose(45.0, 999.0, 100.0))),
            pesoKg = 45.0,
            etaAnni = 18
        )
        assertEquals(100.0, result.doseTotale, 0.0001)
    }

    @Test
    fun etaObbligatoriaQuandoIlFarmacoHaUnLimite() {
        val result = DoseCalculator.calcola(
            drug = drug(FormulaType.FISSA, dose = 1.0, unit = DoseUnit.APPLICATION, minAge = 18),
            pesoKg = 70.0
        )
        assertTrue(result.isError)
        assertNotNull(result.errore)
    }

    private fun drug(
        formula: FormulaType,
        dose: Double,
        unit: DoseUnit,
        minAge: Int? = null,
        maxDose: Double? = null,
        fasce: List<FasciaDose>? = null
    ) = Drug(
        id = "test",
        nome = "Farmaco test",
        indicazione = "Indicazione test",
        tipoFormula = formula,
        doseUnitaria = dose,
        unitaMisura = unit,
        etaMinimaAnni = minAge,
        doseMassima = maxDose,
        nSomministrazioni = 1,
        unitaFrequenza = FrequencyUnit.DAY,
        fascePeso = fasce,
        fonte = "Fonte test"
    )
}
