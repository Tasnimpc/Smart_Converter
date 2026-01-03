package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class TemperatureActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private val tempUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin")

    private var unitFrom = "Celsius"
    private var unitTo = "Fahrenheit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

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

            val result = convertTemperature(value, unitFrom, unitTo)

            etTo.setText(format(result))
            tvResult.text =
                "${format(value)} $unitFrom = ${format(result)} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(tempUnits) { _, position ->
                if (isFrom) {
                    unitFrom = tempUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = tempUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    /**
     * Strategy:
     * 1) Convert FROM -> Celsius
     * 2) Convert Celsius -> TO
     */
    private fun convertTemperature(
        value: BigDecimal,
        from: String,
        to: String
    ): BigDecimal {

        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> value.subtract(BigDecimal("32"))
                .multiply(BigDecimal("5"))
                .divide(BigDecimal("9"), 10, RoundingMode.HALF_UP)

            "Kelvin" -> value.subtract(BigDecimal("273.15"))
            else -> throw IllegalArgumentException("Unknown unit: $from")
        }

        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> celsius
                .multiply(BigDecimal("9"))
                .divide(BigDecimal("5"), 10, RoundingMode.HALF_UP)
                .add(BigDecimal("32"))

            "Kelvin" -> celsius.add(BigDecimal("273.15"))
            else -> throw IllegalArgumentException("Unknown unit: $to")
        }
    }

    private fun format(value: BigDecimal): String {
        val sig = SettingsManager.getSigFigures(this)

        return value
            .round(MathContext(sig, RoundingMode.HALF_UP))
            .stripTrailingZeros()
            .toPlainString()
    }
}
