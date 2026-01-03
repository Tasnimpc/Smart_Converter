package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class TimeActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var timeUnits: Array<String>

    private var unitFrom = "Second"
    private var unitTo = "Minute"

    // Conversion factors to SECONDS (defined once)
    // Month and Year are FIXED approximations (as labeled)
    private val toSeconds = mapOf(
        "Second" to BigDecimal("1"),
        "Minute" to BigDecimal("60"),
        "Hour" to BigDecimal("3600"),
        "Day" to BigDecimal("86400"),
        "Week" to BigDecimal("604800"),
        "Month (30d)" to BigDecimal("2592000"),
        "Year (365d)" to BigDecimal("31536000")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        timeUnits = resources.getStringArray(R.array.time_units)

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

            val result = convertTime(value, unitFrom, unitTo)

            etTo.setText(format(result))
            tvResult.text =
                "${format(value)} $unitFrom = ${format(result)} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(timeUnits) { _, position ->
                if (isFrom) {
                    unitFrom = timeUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = timeUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    private fun convertTime(
        value: BigDecimal,
        from: String,
        to: String
    ): BigDecimal {

        val fromFactor = toSeconds[from]
            ?: throw IllegalArgumentException("Unknown unit: $from")

        val toFactor = toSeconds[to]
            ?: throw IllegalArgumentException("Unknown unit: $to")

        val valueInSeconds = value.multiply(fromFactor)
        return valueInSeconds.divide(toFactor, 6, RoundingMode.HALF_UP)
    }

    private fun format(value: BigDecimal): String {
        val decimals = SettingsManager.getSigFigures(this) // treat as decimal places
        return value
            .setScale(decimals, RoundingMode.HALF_UP)   // fixed decimal places
            .stripTrailingZeros()
            .toPlainString()
    }

}
