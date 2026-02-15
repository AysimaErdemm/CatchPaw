package com.catchpaw.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.catchpaw.domain.model.PawEffect
import kotlinx.coroutines.delay

@Composable
fun PawCatchEffect(paw: PawEffect) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(paw.id) {
        scale.animateTo(
            1.5f,
            animationSpec = tween(350, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(paw.id) {
        delay(80)
        alpha.animateTo(0f, animationSpec = tween(320, easing = FastOutSlowInEasing))
    }

    val density = LocalDensity.current
    val offsetX = with(density) { paw.x.toDp() }
    val offsetY = with(density) { paw.y.toDp() }

    Text(
        text = "🐾",
        fontSize = 36.sp,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.value)
            .alpha(alpha.value)
    )
}
