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
import com.catchpaw.domain.model.ExplosionEffect
import kotlinx.coroutines.delay

@Composable
fun ExplosionEffect(explosion: ExplosionEffect) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(explosion.id) {
        scale.animateTo(
            2.0f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(explosion.id) {
        delay(80)
        alpha.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    val density = LocalDensity.current
    val offsetX = with(density) { explosion.x.toDp() }
    val offsetY = with(density) { explosion.y.toDp() }

    Text(
        text = "\uD83D\uDCA5",
        fontSize = 44.sp,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.value)
            .alpha(alpha.value)
    )
}
