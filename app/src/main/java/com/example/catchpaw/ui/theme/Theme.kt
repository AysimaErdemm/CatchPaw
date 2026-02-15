package com.catchpaw.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class CatchPawColors(
    val gradientTop: Color,
    val gradientMid: Color,
    val gradientBottom: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val pawOrange: Color,
    val scoreGold: Color,
    val timerRed: Color,
    val warmSand: Color,
    val warmWheat: Color,
    val progressTeal: Color,
    val divider: Color,
    val buttonSecondary: Color,
    val topBarBackground: Color
)

val LightCatchPawColors = CatchPawColors(
    gradientTop = Cream50,
    gradientMid = Cream200,
    gradientBottom = Cream300,
    cardBackground = Cream50,
    textPrimary = Color(0xFF4E342E),
    textSecondary = Color(0xFF8D6E63),
    pawOrange = PawOrange,
    scoreGold = ScoreGold,
    timerRed = TimerRed,
    warmSand = WarmSand,
    warmWheat = WarmWheat,
    progressTeal = ProgressTeal,
    divider = Cream300,
    buttonSecondary = Cream400,
    topBarBackground = Cream50.copy(alpha = 0.95f)
)

val DarkCatchPawColors = CatchPawColors(
    gradientTop = Color(0xFF2A2220),
    gradientMid = Color(0xFF3B302A),
    gradientBottom = Color(0xFF4A3D34),
    cardBackground = Color(0xFF3B302A),
    textPrimary = Color(0xFFF5E6D8),
    textSecondary = Color(0xFFBFA48E),
    pawOrange = Color(0xFFE8A84C),
    scoreGold = Color(0xFFDC9A3A),
    timerRed = Color(0xFFE05A3A),
    warmSand = Color(0xFF5C4D3E),
    warmWheat = Color(0xFF7A6650),
    progressTeal = Color(0xFF7A9E96),
    divider = Color(0xFF5C4D3E),
    buttonSecondary = Color(0xFF6B5A4A),
    topBarBackground = Color(0xFF3B302A).copy(alpha = 0.95f)
)

val LocalCatchPawColors = staticCompositionLocalOf { LightCatchPawColors }
val LocalThemePreference = staticCompositionLocalOf<com.catchpaw.data.local.ThemePreference?> { null }
val LocalLanguagePreference = staticCompositionLocalOf<com.catchpaw.data.local.LanguagePreference?> { null }

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,
    secondary = OrangeGrey80,
    tertiary = Warm80,
    background = CreamDark,
    surface = CreamDark2
)

private val LightColorScheme = lightColorScheme(
    primary = Orange40,
    secondary = OrangeGrey40,
    tertiary = Warm40,
    background = Cream50,
    surface = Cream100
)

@Composable
fun CatchPawTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val catchPawColors = if (darkTheme) DarkCatchPawColors else LightCatchPawColors

    CompositionLocalProvider(LocalCatchPawColors provides catchPawColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
