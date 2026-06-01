package com.aysimaerdem.catchpaw.ui.screen.start

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aysimaerdem.catchpaw.R
import com.aysimaerdem.catchpaw.ui.component.BannerAd
import com.aysimaerdem.catchpaw.ui.component.LanguageSelector
import com.aysimaerdem.catchpaw.ui.component.ThemeSelector
import com.aysimaerdem.catchpaw.ui.theme.LocalCatchPawColors
import com.aysimaerdem.catchpaw.ui.theme.LocalLanguagePreference
import com.aysimaerdem.catchpaw.ui.theme.LocalThemePreference

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    onStartDailyChallenge: () -> Unit,
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
            Image(
                painter = painterResource(R.drawable.welcome_icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
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
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
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
                colors = ButtonDefaults.buttonColors(containerColor = colors.pawOrange),
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                val dailyBorderColor = if (uiState.isDailyCompleted)
                    colors.divider else colors.pawOrange
                val dailyBorderWidth = if (uiState.isDailyCompleted) 1.5.dp else 2.dp
                val dailyBgColor = if (uiState.isDailyCompleted)
                    colors.cardBackground else Color.Transparent

                ChallengeInfoBox(
                    borderColor = dailyBorderColor,
                    borderWidth = dailyBorderWidth,
                    backgroundColor = dailyBgColor,
                    onClick = if (!uiState.isDailyCompleted) onStartDailyChallenge else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.challenge_daily),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (uiState.isDailyCompleted) {
                        Text(
                            text = "🏆 ${uiState.dailyBestScore}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.textSecondary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.challenge_play_today),
                            fontSize = 12.sp,
                            color = colors.pawOrange
                        )
                    }
                }

                ChallengeInfoBox(
                    borderColor = colors.divider,
                    borderWidth = 2.dp,
                    backgroundColor = colors.cardBackground,
                    onClick = null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.challenge_weekly),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (uiState.weeklyTotalScore > 0) {
                        Text(
                            text = "🐾 ${uiState.weeklyTotalScore}  •  ${uiState.daysPlayedThisWeek}/7",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.scoreGold
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.challenge_weekly_empty),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        BannerAd(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    val parentContext = LocalContext.current

    if (showSettings) {
        Dialog(onDismissRequest = { showSettings = false }) {
            CompositionLocalProvider(LocalContext provides parentContext) {
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
                            onLanguageSelected = { langPref.setLanguage(it) },
                            turkishLabel = parentContext.getString(R.string.lang_turkish),
                            englishLabel = parentContext.getString(R.string.lang_english)
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
}

@Composable
private fun ChallengeInfoBox(
    borderColor: androidx.compose.ui.graphics.Color,
    borderWidth: androidx.compose.ui.unit.Dp,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(72.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            content()
        }
    }
}
