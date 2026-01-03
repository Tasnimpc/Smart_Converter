package com.example.smartconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class StorageActivity : BaseActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var btnSelectFrom: Button
    private lateinit var btnSelectTo: Button

    private lateinit var storageUnits: Array<String>

    private var unitFrom = "Byte"
    private var unitTo = "Kilobyte (KB)"

    // Conversion factors to BYTES (defined once)
    private val toBytes = mapOf(
        "Bit" to BigDecimal("0.125"),
        "Byte" to BigDecimal("1"),
        "Kilobyte (KB)" to BigDecimal("1024"),
        "Megabyte (MB)" to BigDecimal("1048576"),
        "Gigabyte (GB)" to BigDecimal("1073741824"),
        "Terabyte (TB)" to BigDecimal("1099511627776"),
        "Petabyte (PB)" to BigDecimal("1125899906842624")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.digital_storage)

        etFrom = findViewById(R.id.et_from)
        etTo = findViewById(R.id.et_to)
        btnConvert = findViewById(R.id.btn_convert)
        tvResult = findViewById(R.id.tv_result)
        btnSelectFrom = findViewById(R.id.btn_select_from)
        btnSelectTo = findViewById(R.id.btn_select_to)

        storageUnits = resources.getStringArray(R.array.storage_units)

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

            val result = convertStorage(value, unitFrom, unitTo)

            etTo.setText(format(result))
            tvResult.text = "${format(value)} $unitFrom = ${format(result)} $unitTo"
        }
    }

    private fun showUnitDialog(isFrom: Boolean) {
        AlertDialog.Builder(this)
            .setTitle("Select Unit")
            .setItems(storageUnits) { _, position ->
                if (isFrom) {
                    unitFrom = storageUnits[position]
                    btnSelectFrom.text = "From: $unitFrom ▾"
                } else {
                    unitTo = storageUnits[position]
                    btnSelectTo.text = "To: $unitTo ▾"
                }
            }
            .show()
    }

    private fun convertStorage(
        value: BigDecimal,
        from: String,
        to: String
    ): BigDecimal {

        val fromFactor = toBytes[from]
            ?: throw IllegalArgumentException("Unknown unit: $from")

        val toFactor = toBytes[to]
            ?: throw IllegalArgumentException("Unknown unit: $to")

        val valueInBytes = value.multiply(fromFactor)
        return valueInBytes.divide(toFactor, 12, RoundingMode.HALF_UP)
    }


    private fun format(value: BigDecimal): String {
        val decimals = SettingsManager.getSigFigures(this) // treat as decimal places
        return value
            .setScale(decimals, RoundingMode.HALF_UP)   // fixed decimal places
            .stripTrailingZeros()
            .toPlainString()
    }

}
