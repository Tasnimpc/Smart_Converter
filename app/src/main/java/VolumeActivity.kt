package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class VolumeActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var volumeUnits: Array<String>

    private var unitFrom = "Milliliter"
    private var unitTo = "Liter"

    // Conversion factors to LITERS (defined once)
    private val toLiter = mapOf(
        "Milliliter" to BigDecimal("0.001"),
        "Liter" to BigDecimal("1"),
        "Cubic Meter" to BigDecimal("1000"),
        "Cubic Inch" to BigDecimal("0.016387064"),
        "Cubic Foot" to BigDecimal("28.316846592"),
        "Gallon (US)" to BigDecimal("3.785411784"),
        "Quart (US)" to BigDecimal("0.946352946"),
        "Pint (US)" to BigDecimal("0.473176473"),
        "Cup (US)" to BigDecimal("0.2365882365")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        volumeUnits = resources.getStringArray(R.array.volume_units)

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

            val result = convertVolume(value, unitFrom, unitTo)

            etTo.setText(format(result))
            tvResult.text =
                "${format(value)} $unitFrom = ${format(result)} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(volumeUnits) { _, position ->
                if (isFrom) {
                    unitFrom = volumeUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = volumeUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    private fun convertVolume(
        value: BigDecimal,
        from: String,
        to: String
    ): BigDecimal {

        val fromFactor = toLiter[from]
            ?: throw IllegalArgumentException("Unknown unit: $from")

        val toFactor = toLiter[to]
            ?: throw IllegalArgumentException("Unknown unit: $to")

        val valueInLiter = value.multiply(fromFactor)
        return valueInLiter.divide(toFactor, 6, RoundingMode.HALF_UP)
    }

    private fun format(value: BigDecimal): String {
        val decimals = SettingsManager.getSigFigures(this) // treat as decimal places
        return value
            .setScale(decimals, RoundingMode.HALF_UP)   // fixed decimal places
            .stripTrailingZeros()
            .toPlainString()
    }

}
