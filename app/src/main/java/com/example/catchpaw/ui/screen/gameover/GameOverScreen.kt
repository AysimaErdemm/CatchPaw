package com.example.catchpaw.ui.screen.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catchpaw.R
import com.example.catchpaw.ui.component.BannerAd
import com.example.catchpaw.ui.theme.LocalCatchPawColors

@Composable
fun GameOverScreen(
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    viewModel: GameOverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalCatchPawColors.current

    val cardScale = remember { Animatable(0.8f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        cardAlpha.animateTo(1f, tween(400))
    }
    LaunchedEffect(Unit) {
        cardScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.gradientTop,
                        colors.gradientMid,
                        colors.gradientBottom
                    )
                )
            )
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale.value
                    scaleY = cardScale.value
                    this.alpha = cardAlpha.value
                },
            colors = CardDefaults.cardColors(
                containerColor = colors.cardBackground
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isNewBest) "🏆" else "🐱",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (uiState.isNewBest) stringResource(R.string.gameover_new_record) else stringResource(R.string.gameover_time_up),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isNewBest) colors.scoreGold else colors.textPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🐾", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.gameover_score, uiState.score),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.scoreGold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.gameover_missed, uiState.missedCount),
                    fontSize = 16.sp,
                    color = colors.textSecondary
                )

                if (uiState.maxCombo > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.gameover_best_combo, uiState.maxCombo),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.pawOrange
                    )
                }

                if (!uiState.isNewBest && uiState.bestScore > 0) {
                    Text(
                        text = stringResource(R.string.gameover_best_score, uiState.bestScore),
                        fontSize = 16.sp,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.pawOrange),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = stringResource(R.string.gameover_play_again),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onMainMenu,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.gameover_main_menu),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                }
            }
        }

        BannerAd(
            modifier = Modifier.align(Alignment.BottomCenter),
            adaptive = true
        )
    }
}
