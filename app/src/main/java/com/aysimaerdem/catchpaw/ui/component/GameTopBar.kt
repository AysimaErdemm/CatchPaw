package com.aysimaerdem.catchpaw.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aysimaerdem.catchpaw.R
import com.aysimaerdem.catchpaw.domain.model.GameConfig
import com.aysimaerdem.catchpaw.ui.theme.LocalCatchPawColors

@Composable
fun GameTopBar(
    score: Int,
    combo: Int,
    timeLeftMs: Long,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCatchPawColors.current
    val progress = timeLeftMs.toFloat() / GameConfig.GAME_DURATION_MS
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "timer"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.topBarBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🐾", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedContent(
                        targetState = score,
                        transitionSpec = {
                            (slideInVertically { -it } + fadeIn(tween(200)))
                                .togetherWith(slideOutVertically { it } + fadeOut(tween(150)))
                        },
                        label = "scoreAnim"
                    ) { targetScore ->
                        Text(
                            text = "$targetScore",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.scoreGold
                        )
                    }
                    AnimatedVisibility(
                        visible = combo > 1,
                        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                        exit = scaleOut(tween(150)) + fadeOut(tween(150))
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.combo_label, combo),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.pawOrange
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    val timerColor by animateColorSafe(
                        targetValue = if (timeLeftMs < 5000) colors.timerRed else colors.textPrimary,
                        animationSpec = tween(500)
                    )
                    Text(
                        text = "${(timeLeftMs / 1000)}s",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = timerColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val progressBarColor by animateColorSafe(
                targetValue = if (timeLeftMs < 5000) colors.timerRed else colors.progressTeal,
                animationSpec = tween(500)
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressBarColor,
                trackColor = colors.divider,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun animateColorSafe(
    targetValue: Color,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Color> = tween(300)
): androidx.compose.runtime.State<Color> {
    return androidx.compose.animation.animateColorAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "colorAnim"
    )
}
