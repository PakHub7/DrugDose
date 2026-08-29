package com.drugdose.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drugdose.data.DrugRepository
import com.drugdose.logic.DoseCalculator
import com.drugdose.model.DoseResult
import com.drugdose.model.Drug
import com.drugdose.model.FormulaType

class DrugViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DrugRepository(application)
    private val _drugs = MutableLiveData<List<Drug>>()
    val drugs: LiveData<List<Drug>> get() = _drugs
    private val _selectedDrug = MutableLiveData<Drug?>()
    val selectedDrug: LiveData<Drug?> get() = _selectedDrug
    private val _result = MutableLiveData<DoseResult?>()
    val result: LiveData<DoseResult?> get() = _result
    private val _inputError = MutableLiveData<String?>()
    val inputError: LiveData<String?> get() = _inputError

    init { loadDrugs() }

    fun loadDrugs() {
        _drugs.value = repository.loadDrugs()
        if (_drugs.value.isNullOrEmpty()) _inputError.value = "Impossibile caricare il database dei farmaci."
    }

    fun selectDrug(drug: Drug) {
        _selectedDrug.value = drug
        _result.value = null
        _inputError.value = null
    }

    fun calcolaDose(pesoStr: String, altezzaStr: String, etaStr: String) {
        val drug = _selectedDrug.value ?: return erroreInput("Seleziona un farmaco prima di calcolare.")

        val peso = parseDouble(pesoStr)
        if (peso == null || peso <= 0) return erroreInput("Inserisci un peso valido in kg.")

        val altezza = if (drug.tipoFormula == FormulaType.PER_M2) {
            val value = parseDouble(altezzaStr)
            if (value == null || value <= 0) return erroreInput("Inserisci un'altezza valida in cm.")
            value
        } else null

        val eta = if (etaStr.isBlank()) null else etaStr.trim().toIntOrNull()
        if (etaStr.isNotBlank() && (eta == null || eta < 0)) return erroreInput("Inserisci un'età valida in anni.")
        if (drug.etaMinimaAnni != null && eta == null) {
            return erroreInput("Inserisci l'età: questo farmaco richiede il controllo del limite minimo.")
        }

        _inputError.value = null
        _result.value = DoseCalculator.calcola(drug, peso, altezza, eta)
    }

    private fun parseDouble(value: String): Double? = value.trim().replace(',', '.').toDoubleOrNull()

    private fun erroreInput(message: String) {
        _inputError.value = message
        _result.value = null
    }
}
