package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class AreaActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var areaUnits: Array<String>

    private var unitFrom = ""
    private var unitTo = ""

    /** Centralized, immutable conversion factors (m² as base unit) */
    private val areaFactorMap = mapOf(
        "Square millimeter" to 1e-6,
        "Square centimeter" to 1e-4,
        "Square meter" to 1.0,
        "Square kilometer" to 1e6,
        "Hectare" to 10_000.0,
        "Acre" to 4046.8564224,
        "Square mile" to 2_589_988.110336
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area)

        // Bind views
        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        // Load units
        areaUnits = resources.getStringArray(R.array.area_units)

        // Default selections
        unitFrom = areaUnits.firstOrNull() ?: ""
        unitTo = areaUnits.getOrNull(2) ?: unitFrom

        btnSelectFrom.text = "From: $unitFrom ▾"
        btnSelectTo.text = "To: $unitTo ▾"

        // Click listeners
        btnSelectFrom.setOnClickListener { showUnitPicker(true) }
        btnSelectTo.setOnClickListener { showUnitPicker(false) }
        btnConvert.setOnClickListener { performConversion() }
    }

    /** Perform conversion and update UI */
    private fun performConversion() {
        val inputStr = etFrom.text.toString().trim()
        if (inputStr.isEmpty()) {
            etFrom.error = "Enter a value"
            return
        }

        val value = inputStr.toDoubleOrNull()
        if (value == null) {
            etFrom.error = "Invalid number"
            return
        }

        try {
            val result = convertArea(value, unitFrom, unitTo)
            val formattedValue = format(BigDecimal(value))
            val formattedResult = format(BigDecimal(result))

            etTo.setText(formattedResult)
            tvResult.text = "$formattedValue $unitFrom = $formattedResult $unitTo"

        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message ?: "Conversion error", Toast.LENGTH_SHORT).show()
        }
    }

    /** Show dialog to pick unit */
    private fun showUnitPicker(isFrom: Boolean) {
        val currentUnit = if (isFrom) unitFrom else unitTo
        val checkedIndex = areaUnits.indexOf(currentUnit).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setSingleChoiceItems(areaUnits, checkedIndex) { dialog, which ->
                val selected = areaUnits[which]
                if (isFrom) {
                    unitFrom = selected
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = selected
                    btnSelectTo.text = "To: $unitTo ▾"
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /** Convert value from one area unit to another */
    private fun convertArea(value: Double, from: String, to: String): Double {
        require(areaFactorMap.containsKey(from)) { "Unknown unit: $from" }
        require(areaFactorMap.containsKey(to)) { "Unknown unit: $to" }

        val valueInSqMeters = value * areaFactorMap[from]!!
        return valueInSqMeters / areaFactorMap[to]!!
    }

    /** Format BigDecimal using user's significant figures from SettingsManager */
    private fun format(value: BigDecimal): String {
        val sig = SettingsManager.getSigFigures(this)
        return value
            .round(MathContext(sig, RoundingMode.HALF_UP))
            .stripTrailingZeros()
            .toPlainString()
    }
}
