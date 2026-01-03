package com.example.smartconverter

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitch = findViewById<Switch>(R.id.switch_theme)
        val fontSeek = findViewById<SeekBar>(R.id.seek_font)
        val sigSeek = findViewById<SeekBar>(R.id.seek_sig)
        val fontLabel = findViewById<TextView>(R.id.tv_font)
        val sigLabel = findViewById<TextView>(R.id.tv_sig)

        /* Theme */
        themeSwitch.isChecked = SettingsManager.isDarkTheme(this)
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            SettingsManager.setTheme(this, checked)
            recreate()
        }

        /* Font Size */
        fontSeek.progress = SettingsManager.getFontSize(this).toInt()
        fontLabel.text = "Font Size: ${fontSeek.progress}"

        fontSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                SettingsManager.setFontSize(this@SettingsActivity, value.toFloat())
                fontLabel.text = "Font Size: $value"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {
                recreate()
            }
        })

        /* Significant Figures */
        sigSeek.progress = SettingsManager.getSigFigures(this)
        sigLabel.text = "Significant Figures: ${sigSeek.progress}"

        sigSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                SettingsManager.setSigFigures(this@SettingsActivity, value)
                sigLabel.text = "Significant Figures: $value"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}
