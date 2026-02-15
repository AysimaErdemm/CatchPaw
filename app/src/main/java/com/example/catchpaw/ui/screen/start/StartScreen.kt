package com.example.catchpaw.ui.screen.start

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catchpaw.R
import com.example.catchpaw.ui.component.BannerAd
import com.example.catchpaw.ui.component.LanguageSelector
import com.example.catchpaw.ui.component.ThemeSelector
import com.example.catchpaw.ui.theme.LocalCatchPawColors
import com.example.catchpaw.ui.theme.LocalLanguagePreference
import com.example.catchpaw.ui.theme.LocalThemePreference

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    viewModel: StartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalCatchPawColors.current
    val themePref = LocalThemePreference.current
    val langPref = LocalLanguagePreference.current
    val currentThemeMode by themePref!!.themeMode.collectAsStateWithLifecycle()
    val currentLanguage by langPref!!.language.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }

    val enterAlpha = remember { Animatable(0f) }
    val enterOffset = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        enterAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        enterOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.gradientTop, colors.gradientMid, colors.gradientBottom)
                )
            )
    ) {
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(44.dp)
        ) {
            Text(text = "⚙️", fontSize = 26.sp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .graphicsLayer {
                    alpha = enterAlpha.value
                    translationY = enterOffset.value
                }
        ) {
            Text(
                text = "🐱",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.start_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.start_subtitle),
                fontSize = 16.sp,
                color = colors.textSecondary
            )
            if (uiState.bestScore > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.start_best_score, uiState.bestScore),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.scoreGold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.pawOrange
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_play),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Row {
                Text("🐁", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐁", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐁", fontSize = 24.sp)
            }
        }

        BannerAd(
            modifier = Modifier.align(Alignment.BottomCenter),
            adaptive = true
        )
    }

    if (showSettings) {
        Dialog(onDismissRequest = { showSettings = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.settings_theme),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ThemeSelector(
                        currentMode = currentThemeMode,
                        onModeSelected = { themePref.setThemeMode(it) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.settings_language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LanguageSelector(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { langPref.setLanguage(it) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showSettings = false },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.pawOrange),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
