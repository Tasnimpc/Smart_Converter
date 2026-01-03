package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class LengthActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var lengthUnits: Array<String>

    private var unitFrom = ""
    private var unitTo = ""

     /** Centralized conversion factors (meter as base unit) */
    private val lengthFactorMap = mapOf(
        "Millimeter" to 0.001,
        "Centimeter" to 0.01,
        "Meter" to 1.0,
        "Kilometer" to 1000.0,
        "Inch" to 0.0254,
        "Foot" to 0.3048,
        "Yard" to 0.9144,
        "Mile" to 1609.34
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_length)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        lengthUnits = resources.getStringArray(R.array.length_units)

        // Safe defaults from resources
        unitFrom = lengthUnits.firstOrNull() ?: ""
        unitTo = lengthUnits.getOrNull(3) ?: unitFrom

        btnSelectFrom.text = "From: $unitFrom ▾"
        btnSelectTo.text = "To: $unitTo ▾"

        btnSelectFrom.setOnClickListener { showUnitDialog(true) }
        btnSelectTo.setOnClickListener { showUnitDialog(false) }

        btnConvert.setOnClickListener { performConversion() }
    }

    private fun performConversion() {
        val input = etFrom.text.toString().trim()

        if (input.isEmpty()) {
            etFrom.error = "Enter a value"
            return
        }

        val value = input.toDoubleOrNull()
        if (value == null) {
            etFrom.error = "Invalid number!"
            return
        }

        try {
            val result = convertLength(value, unitFrom, unitTo)
            val formattedResult = format(BigDecimal(result))
            val formattedInput = format(BigDecimal(value))

            etTo.setText(formattedResult)
            tvResult.text = "$formattedInput $unitFrom = $formattedResult $unitTo"
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message ?: "Conversion error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(lengthUnits) { _, position ->
                if (isFrom) {
                    unitFrom = lengthUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = lengthUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    /**
     * Safe and accurate length conversion
     */
    private fun convertLength(value: Double, from: String, to: String): Double {
        require(lengthFactorMap.containsKey(from)) { "Unknown unit: $from" }
        require(lengthFactorMap.containsKey(to)) { "Unknown unit: $to" }

        val meters = value * lengthFactorMap[from]!!
        return meters / lengthFactorMap[to]!!
    }

    private fun format(value: BigDecimal): String {
        val decimals = SettingsManager.getSigFigures(this) // treat as decimal places
        return value
            .setScale(decimals, RoundingMode.HALF_UP)   // fixed decimal places
            .stripTrailingZeros()
            .toPlainString()
    }

}
