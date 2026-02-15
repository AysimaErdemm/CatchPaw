package com.catchpaw.ui.component

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
import com.catchpaw.domain.model.GameConfig
import com.catchpaw.domain.model.Mouse

@Composable
fun MouseItem(mouse: Mouse, onClick: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(mouse.id) {
        alpha.animateTo(1f, tween(150))
    }
    LaunchedEffect(mouse.id) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    LaunchedEffect(mouse.isDying) {
        if (mouse.isDying) {
            scale.animateTo(0.3f, tween(GameConfig.MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }
    LaunchedEffect(mouse.isDying) {
        if (mouse.isDying) {
            alpha.animateTo(0f, tween(GameConfig.MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }

    val density = LocalDensity.current
    val offsetX = with(density) { mouse.x.toDp() }
    val offsetY = with(density) { mouse.y.toDp() }

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
            text = mouse.emoji,
            fontSize = 40.sp
        )
    }
}
