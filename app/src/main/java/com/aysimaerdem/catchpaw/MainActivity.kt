package com.aysimaerdem.catchpaw

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aysimaerdem.catchpaw.data.local.LanguagePreference
import com.aysimaerdem.catchpaw.data.local.ThemeMode
import com.aysimaerdem.catchpaw.data.local.ThemePreference
import com.aysimaerdem.catchpaw.navigation.CatchPawNavHost
import com.aysimaerdem.catchpaw.ui.theme.CatchPawTheme
import com.aysimaerdem.catchpaw.ui.theme.LocalLanguagePreference
import com.aysimaerdem.catchpaw.ui.theme.LocalThemePreference
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

private class LocaleContextWrapper(
    base: Context,
    private val localizedResources: Resources
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedResources
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreference: ThemePreference

    @Inject
    lateinit var languagePreference: LanguagePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themePreference.themeMode.collectAsStateWithLifecycle()
            val currentLanguage by languagePreference.language.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            val statusBarColor = if (isDark) android.graphics.Color.parseColor("#2A2220")
                else android.graphics.Color.parseColor("#FDF9F6")

            SideEffect {
                window.statusBarColor = statusBarColor
            }

            val localizedContext = remember(currentLanguage) {
                val locale = Locale(currentLanguage.tag)
                val config = Configuration(resources.configuration).apply {
                    setLocale(locale)
                }
                val locResources = createConfigurationContext(config).resources
                LocaleContextWrapper(this@MainActivity, locResources)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalThemePreference provides themePreference,
                LocalLanguagePreference provides languagePreference
            ) {
                CatchPawTheme(darkTheme = isDark) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        val navController = rememberNavController()
                        CatchPawNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
