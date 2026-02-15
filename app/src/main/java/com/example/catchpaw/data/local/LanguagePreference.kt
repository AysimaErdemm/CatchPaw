package com.catchpaw.data.local

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
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
    private val localeManager = context.getSystemService(LocaleManager::class.java)

    private val _language = MutableStateFlow(getCurrentLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private fun getCurrentLanguage(): AppLanguage {
        val appLocales = localeManager.applicationLocales
        if (appLocales.isEmpty) return AppLanguage.TURKISH
        val tag = appLocales.get(0)?.language ?: return AppLanguage.TURKISH
        return when (tag) {
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.TURKISH
        }
    }

    fun setLanguage(lang: AppLanguage) {
        localeManager.applicationLocales = LocaleList.forLanguageTags(lang.tag)
        _language.value = lang
    }
}
