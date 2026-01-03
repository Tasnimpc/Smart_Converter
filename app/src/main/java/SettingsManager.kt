package com.example.smartconverter

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object SettingsManager {

    private const val PREFS = "smart_converter_settings"

    private const val KEY_DARK = "dark_theme"
    private const val KEY_FONT = "font_size"
    private const val KEY_SIG = "sig_fig"

    /* ---------- THEME ---------- */

    fun applyTheme(context: Context) {
        val dark = isDarkTheme(context)
        AppCompatDelegate.setDefaultNightMode(
            if (dark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun setTheme(context: Context, dark: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK, dark).apply()
    }

    fun isDarkTheme(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK, false)

    /* ---------- FONT SIZE ---------- */

    fun setFontSize(context: Context, size: Float) {
        prefs(context).edit().putFloat(KEY_FONT, size).apply()
    }

    fun getFontSize(context: Context): Float =
        prefs(context).getFloat(KEY_FONT, 16f)

    /* ---------- SIGNIFICANT FIGURES ---------- */

    fun setSigFigures(context: Context, value: Int) {
        prefs(context).edit().putInt(KEY_SIG, value.coerceAtLeast(1)).apply()
    }

    fun getSigFigures(context: Context): Int =
        prefs(context).getInt(KEY_SIG, 4)

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
