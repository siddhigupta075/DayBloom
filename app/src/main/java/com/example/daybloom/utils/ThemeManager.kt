package com.example.daybloom.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF = "theme_pref"
    private const val KEY = "dark_mode"

    fun applyTheme(context: Context) {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val isDark = pref.getBoolean(KEY, false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun toggleTheme(context: Context, enableDark: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY, enableDark)
            .apply()

        applyTheme(context)
    }

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)
    }
}
