package com.example.smartconverter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingsManager.applyTheme(this)
        setTheme(getFontTheme())
        super.onCreate(savedInstanceState)
    }

    private fun getFontTheme(): Int {
        val size = SettingsManager.getFontSize(this)
        return when {
            size <= 14 -> R.style.FontSmall
            size <= 18 -> R.style.FontMedium
            else -> R.style.FontLarge
        }
    }
}
