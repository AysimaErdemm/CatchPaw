package com.aysimaerdem.catchpaw.ui.screen.playing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aysimaerdem.catchpaw.R
import com.aysimaerdem.catchpaw.domain.model.GameResult
import com.aysimaerdem.catchpaw.ui.component.GameTopBar
import com.aysimaerdem.catchpaw.ui.component.GrassBackground
import com.aysimaerdem.catchpaw.ui.component.BombItem
import com.aysimaerdem.catchpaw.ui.component.ExplosionEffect
import com.aysimaerdem.catchpaw.ui.component.MouseItem
import com.aysimaerdem.catchpaw.ui.component.PawCatchEffect
import com.aysimaerdem.catchpaw.ui.component.LanguageSelector
import com.aysimaerdem.catchpaw.ui.component.ThemeSelector
import com.aysimaerdem.catchpaw.ui.theme.LocalCatchPawColors
import com.aysimaerdem.catchpaw.ui.theme.LocalLanguagePreference
import com.aysimaerdem.catchpaw.ui.theme.LocalThemePreference

@Composable
fun PlayingScreen(
    onGameOver: (GameResult) -> Unit,
    onMainMenu: () -> Unit,
    viewModel: PlayingViewModel = hiltViewModel()
) {
    BackHandler {
        viewModel.onAction(PlayingAction.TogglePause)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalCatchPawColors.current
    val themePref = LocalThemePreference.current
    val langPref = LocalLanguagePreference.current
    val currentThemeMode by themePref!!.themeMode.collectAsStateWithLifecycle()
    val currentLanguage by langPref!!.language.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PlayingUiEvent.GameOver -> onGameOver(event.result)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.gradientTop, colors.gradientMid, colors.warmSand)
                    )
                )
        ) {
            val density = LocalDensity.current
            val widthPx = with(density) { maxWidth.toPx() }
            val heightPx = with(density) { maxHeight.toPx() }
            val topBarHeight = remember { mutableFloatStateOf(0f) }

            SideEffect {
                viewModel.onAction(PlayingAction.SetContainerSize(widthPx, heightPx, topBarHeight.floatValue))
            }

            GrassBackground(
                containerWidth = widthPx,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            GameTopBar(
                score = uiState.score,
                combo = uiState.combo,
                timeLeftMs = uiState.timeLeftMs,
                onSettingsClick = { viewModel.onAction(PlayingAction.TogglePause) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        topBarHeight.floatValue = coordinates.size.height.toFloat()
                    }
            )

            uiState.mice.forEach { mouse ->
                MouseItem(
                    mouse = mouse,
                    onClick = {
                        viewModel.onAction(PlayingAction.MouseClicked(mouse.id))
                    }
                )
            }

            uiState.bombs.forEach { bomb ->
                BombItem(
                    bomb = bomb,
                    onClick = {
                        viewModel.onAction(PlayingAction.BombClicked(bomb.id))
                    }
                )
            }

            uiState.pawEffects.forEach { paw ->
                PawCatchEffect(paw = paw)
            }

            uiState.explosionEffects.forEach { explosion ->
                ExplosionEffect(explosion = explosion)
            }
        }

        // Pause overlay
        AnimatedVisibility(
            visible = uiState.isPaused,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = uiState.isPaused,
                    enter = scaleIn(
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(40.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.welcome_icon),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.pause_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.pause_score, uiState.score),
                                fontSize = 16.sp,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = colors.divider, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.settings_theme),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ThemeSelector(
                                currentMode = currentThemeMode,
                                onModeSelected = { themePref.setThemeMode(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.settings_language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LanguageSelector(
                                currentLanguage = currentLanguage,
                                onLanguageSelected = { langPref.setLanguage(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.onAction(PlayingAction.TogglePause) },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.pawOrange),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.pause_resume),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { viewModel.onAction(PlayingAction.Restart) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.pause_restart),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onMainMenu,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.pause_main_menu),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
