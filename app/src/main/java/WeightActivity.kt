package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class WeightActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var weightUnits: Array<String>
    private var unitFrom = "Kilogram"
    private var unitTo = "Gram"

    // Conversion factors in grams
    private val toGram = mapOf(
        "Milligram" to BigDecimal("0.001"),
        "Gram" to BigDecimal("1"),
        "Kilogram" to BigDecimal("1000"),
        "Tonne" to BigDecimal("1000000"),
        "Ounce" to BigDecimal("28.3495"),
        "Pound" to BigDecimal("453.592")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        weightUnits = resources.getStringArray(R.array.weight_units)

        btnSelectFrom.setOnClickListener { showUnitDialog(true) }
        btnSelectTo.setOnClickListener { showUnitDialog(false) }

        btnConvert.setOnClickListener {
            val inputText = etFrom.text.toString().trim()
            if (inputText.isEmpty()) {
                etFrom.error = "Enter a value"
                return@setOnClickListener
            }

            val value = inputText.toBigDecimalOrNull()
            if (value == null) {
                etFrom.error = "Invalid number"
                return@setOnClickListener
            }

            val result = convertWeight(value, unitFrom, unitTo)

            etTo.setText(format(result))
            tvResult.text = "${format(value)} $unitFrom = ${format(result)} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(weightUnits) { _, pos ->
                if (isFrom) {
                    unitFrom = weightUnits[pos]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = weightUnits[pos]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    private fun convertWeight(value: BigDecimal, from: String, to: String): BigDecimal {
        val fromFactor = toGram[from] ?: throw IllegalArgumentException("Unknown unit: $from")
        val toFactor = toGram[to] ?: throw IllegalArgumentException("Unknown unit: $to")
        val valueInGram = value.multiply(fromFactor)
        return valueInGram.divide(toFactor, 6, RoundingMode.HALF_UP)
    }

    private fun format(value: BigDecimal): String {
        val sig = SettingsManager.getSigFigures(this)

        return value
            .round(MathContext(sig, RoundingMode.HALF_UP))
            .stripTrailingZeros()
            .toPlainString()
    }}
