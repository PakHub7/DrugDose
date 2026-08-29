package com.drugdose

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.drugdose.databinding.ActivityMainBinding
import com.drugdose.model.FormulaType
import com.drugdose.viewmodel.DrugViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DrugViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.drugs.observe(this) { drugs ->
            val nomi = drugs.map { "${it.nome} — ${it.indicazione}" }
            binding.dropdownFarmaco.setAdapter(ArrayAdapter(this, R.layout.item_dropdown_drug, nomi))
        }

        viewModel.selectedDrug.observe(this) { drug ->
            binding.tilAltezza.visibility = if (drug?.tipoFormula == FormulaType.PER_M2) View.VISIBLE else View.GONE
            binding.tvFormulaInfo.text = drug?.let { "Regola: ${it.formulaLabel()} · ${it.frequencyLabel()}" } ?: ""
            
            val etaObbligatoria = drug?.etaMinimaAnni != null
            binding.tilEta.hint = if (etaObbligatoria) "Età (anni) *" else "Età (anni) — opzionale"
            binding.tilEta.helperText = if (etaObbligatoria) "Obbligatoria per questo farmaco; età minima: ${drug?.etaMinimaAnni} anni" else null
            binding.tilEta.error = null
        }

        viewModel.inputError.observe(this) { errore ->
            binding.tvInputError.text = errore
            binding.tvInputError.visibility = if (errore != null) View.VISIBLE else View.GONE
        }

        viewModel.result.observe(this) { result ->
            val card = binding.resultCard
            if (result == null) {
                card.root.visibility = View.GONE
                return@observe
            }
            card.root.visibility = View.VISIBLE
            card.tvFarmacoIndicazione.text = "${result.farmacoNome} · ${result.indicazione}"
            card.tvFormula.text = "Regola applicata: ${viewModel.selectedDrug.value?.formulaLabel() ?: "—"}"
            card.tvFrequenza.text = "Frequenza: ${result.formatFrequency()}"
            card.tvFonte.text = "Fonte: ${result.fonte}"

            if (result.isError) {
                card.tvDoseTotale.text = "—"
                card.layoutDoseTeorica.visibility = View.GONE
                card.layoutLimiteDose.visibility = View.GONE
                card.cardErrore.visibility = View.VISIBLE
                card.tvErrore.text = result.errore
                card.layoutBsa.visibility = View.GONE
                card.layoutCompresse.visibility = View.GONE
                card.layoutDosePerSomm.visibility = View.GONE
                card.layoutAlerts.visibility = View.GONE
                card.layoutControindicazioni.visibility = View.GONE
                return@observe
            }

            card.cardErrore.visibility = View.GONE
            card.layoutDoseTeorica.visibility = View.VISIBLE
            card.tvDoseTeorica.text = result.formatDoseTeorica()

            if (result.limiteMassimoApplicato != null) {
                card.layoutLimiteDose.visibility = View.VISIBLE
                card.tvLimiteDose.text = "${result.limiteMassimoApplicato} ${result.doseUnita.label}"
            } else {
                card.layoutLimiteDose.visibility = View.GONE
            }

            card.tvDoseTotale.text = result.formatDoseTotale()
            card.layoutBsa.visibility = if (result.bsa != null) View.VISIBLE else View.GONE
            result.bsa?.let { card.tvBsa.text = String.format(java.util.Locale.ITALY, "%.2f m²", it) }
            card.layoutDosePerSomm.visibility = if (result.dosePerSomministrazione != null && result.nSomministrazioni > 1) View.VISIBLE else View.GONE
            card.tvDosePerSomm.text = result.formatDosePerSomm()
            card.layoutCompresse.visibility = if (result.nCompresse != null) View.VISIBLE else View.GONE
            result.nCompresse?.let { card.tvCompresse.text = "$it cpr" }
            card.tvNSomministrazioni.text = result.formatFrequency()
            card.layoutAlerts.visibility = if (result.alerts.isNotEmpty()) View.VISIBLE else View.GONE
            card.tvAlerts.text = result.alerts.joinToString("\n")
            card.layoutControindicazioni.visibility = if (result.controindicazioni.isNotEmpty()) View.VISIBLE else View.GONE
            card.tvControindicazioni.text = result.controindicazioni.joinToString("\n") { "• $it" }
        }

        binding.dropdownFarmaco.setOnItemClickListener { _, _, position, _ ->
            viewModel.drugs.value?.getOrNull(position)?.let { drug ->
                viewModel.selectDrug(drug)
                binding.etPeso.text?.clear()
                binding.etAltezza.text?.clear()
                binding.etEta.text?.clear()
            }
        }

        binding.btnCalcola.setOnClickListener {
            viewModel.calcolaDose(
                binding.etPeso.text.toString(),
                binding.etAltezza.text.toString(),
                binding.etEta.text.toString()
            )
        }
    }
}
