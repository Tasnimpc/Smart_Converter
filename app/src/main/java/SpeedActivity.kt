package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class SpeedActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var speedUnits: Array<String>

    private var unitFrom = "m/s"
    private var unitTo = "km/h"

    // Conversion factors to meters/second (defined once)
    private val toMetersPerSecond = mapOf(
        "m/s" to 1.0,
        "km/h" to 1.0 / 3.6,
        "mph" to 0.44704,
        "ft/s" to 0.3048,
        "knots" to 0.514444
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        speedUnits = resources.getStringArray(R.array.speed_units)

        btnSelectFrom.setOnClickListener { showUnitDialog(true) }
        btnSelectTo.setOnClickListener { showUnitDialog(false) }

        btnConvert.setOnClickListener {
            val inputText = etFrom.text.toString().trim()

            if (inputText.isEmpty()) {
                etFrom.error = "Enter a value"
                return@setOnClickListener
            }

            val value = inputText.toDoubleOrNull()
            if (value == null) {
                etFrom.error = "Invalid number"
                return@setOnClickListener
            }

            val result = convertSpeed(value, unitFrom, unitTo)

            etTo.setText(format(BigDecimal(result)))
                        tvResult . text = "${format(BigDecimal(value))} $unitFrom = ${format(BigDecimal(result))} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(speedUnits) { _, position ->
                if (isFrom) {
                    unitFrom = speedUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = speedUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    private fun convertSpeed(value: Double, from: String, to: String): Double {
        val fromFactor = toMetersPerSecond[from]
            ?: throw IllegalArgumentException("Unknown unit: $from")

        val toFactor = toMetersPerSecond[to]
            ?: throw IllegalArgumentException("Unknown unit: $to")

        val inMetersPerSecond = value * fromFactor
        return inMetersPerSecond / toFactor
    }

    private fun format(value: BigDecimal): String {
        val sig = SettingsManager.getSigFigures(this)

        return value
            .round(MathContext(sig, RoundingMode.HALF_UP))
            .stripTrailingZeros()
            .toPlainString()
    }
}
