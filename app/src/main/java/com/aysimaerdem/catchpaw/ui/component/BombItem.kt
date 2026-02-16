package com.aysimaerdem.catchpaw.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aysimaerdem.catchpaw.domain.model.Bomb
import com.aysimaerdem.catchpaw.domain.model.GameConfig

@Composable
fun BombItem(bomb: Bomb, onClick: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(bomb.id) {
        alpha.animateTo(1f, tween(150))
    }
    LaunchedEffect(bomb.id) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    LaunchedEffect(bomb.isDying) {
        if (bomb.isDying) {
            scale.animateTo(0.3f, tween(GameConfig.MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }
    LaunchedEffect(bomb.isDying) {
        if (bomb.isDying) {
            alpha.animateTo(0f, tween(GameConfig.MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }

    val density = LocalDensity.current
    val offsetX = with(density) { bomb.x.toDp() }
    val offsetY = with(density) { bomb.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.value)
            .alpha(alpha.value)
            .size(60.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "\uD83D\uDCA3",
            fontSize = 40.sp
        )
    }
}
