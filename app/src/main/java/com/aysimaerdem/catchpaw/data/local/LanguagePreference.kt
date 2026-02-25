package com.aysimaerdem.catchpaw.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage(val tag: String) {
    TURKISH("tr"),
    ENGLISH("en")
}

@Singleton
class LanguagePreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("catchpaw_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(getSavedLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private fun getSavedLanguage(): AppLanguage {
        val tag = prefs.getString("app_language", "tr") ?: "tr"
        return when (tag) {
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.TURKISH
        }
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("app_language", lang.tag).apply()
        _language.value = lang
    }
}
