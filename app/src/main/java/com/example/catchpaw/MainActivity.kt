package com.example.catchpaw

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.catchpaw.data.local.LanguagePreference
import com.catchpaw.data.local.ThemeMode
import com.catchpaw.data.local.ThemePreference
import com.catchpaw.navigation.CatchPawNavHost
import com.catchpaw.ui.theme.CatchPawTheme
import com.catchpaw.ui.theme.LocalLanguagePreference
import com.catchpaw.ui.theme.LocalThemePreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            val statusBarColor = if (isDark) android.graphics.Color.parseColor("#2A2220")
                else android.graphics.Color.parseColor("#FDF9F6")

            SideEffect {
                window.statusBarColor = statusBarColor
            }

            CompositionLocalProvider(
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
