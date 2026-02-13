package com.example.catchpaw

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catchpaw.ui.theme.GrassGreen
import com.example.catchpaw.ui.theme.PawOrange
import com.example.catchpaw.ui.theme.ScoreGold
import com.example.catchpaw.ui.theme.SkyBlue
import com.example.catchpaw.ui.theme.TimerRed
import com.example.catchpaw.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Mouse(
    val id: Int,
    val x: Float,
    val y: Float,
    val emoji: String = listOf("🐭", "🐁").random(),
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
)

private const val MOUSE_FADE_OUT_MS = 700L

data class PawEffect(
    val id: Int,
    val x: Float,
    val y: Float
)

data class GrassLine(
    val x: Float,
    val heightRatio: Float,
    val offsetX: Float,
    val isDark: Boolean
)

private const val GAME_DURATION_MS = 30_000L
private const val MOUSE_LIFETIME_MS = 1_500L
private const val MOUSE_SPAWN_INTERVAL_MS = 800L
private const val MAX_MICE = 4

@Composable
fun StartScreen(bestScore: Int, onStartGame: () -> Unit) {
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
                    colors = listOf(SkyBlue, Color(0xFFA5D6A7), GrassGreen)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
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
                text = "Patileri Yakala!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Farelere dokun, puan topla!",
                fontSize = 16.sp,
                color = Color(0xFF5D4037)
            )
            if (bestScore > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "En Yuksek Skor: $bestScore",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScoreGold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PawOrange
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "🐾  Oyna!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Row {
                Text("🐭", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐭", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🐭", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun PlayingScreen(
    viewModel: GameViewModel,
    onGameOver: () -> Unit
) {
    BackHandler {
        // Block back button during gameplay
    }

    var timeLeftMs by remember { mutableLongStateOf(GAME_DURATION_MS) }
    val mice = remember { mutableStateListOf<Mouse>() }
    val pawEffects = remember { mutableStateListOf<PawEffect>() }
    var mouseIdCounter by remember { mutableIntStateOf(0) }
    var pawIdCounter by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var lastCatchTime by remember { mutableLongStateOf(0L) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    val score = viewModel.score

    // Timer
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (timeLeftMs > 0) {
            delay(50)
            timeLeftMs = (GAME_DURATION_MS - (System.currentTimeMillis() - startTime)).coerceAtLeast(0)
        }
        onGameOver()
    }

    // Spawn mice
    LaunchedEffect(Unit) {
        delay(500)
        while (true) {
            if (mice.size < MAX_MICE && containerWidth > 0 && containerHeight > 0) {
                val mouseSize = 60f
                val topBarHeight = 120f
                val newMouse = Mouse(
                    id = mouseIdCounter++,
                    x = Random.nextFloat() * (containerWidth - mouseSize * 2) + mouseSize / 2,
                    y = topBarHeight + Random.nextFloat() * (containerHeight - topBarHeight - mouseSize * 2)
                )
                mice.add(newMouse)
            }
            val progress = 1f - (timeLeftMs.toFloat() / GAME_DURATION_MS)
            val interval = (MOUSE_SPAWN_INTERVAL_MS * (1f - progress * 0.4f)).toLong()
            delay(interval.coerceAtLeast(400))
        }
    }

    // Mark old mice as dying
    LaunchedEffect(Unit) {
        while (true) {
            delay(MOUSE_LIFETIME_MS)
            val alive = mice.firstOrNull { !it.isDying }
            if (alive != null) {
                val index = mice.indexOf(alive)
                if (index >= 0) {
                    mice[index] = alive.copy(isDying = true, dyingStartTime = System.currentTimeMillis())
                    viewModel.incrementMissed()
                    combo = 0
                }
            }
        }
    }

    // Remove mice after fade-out completes
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            val now = System.currentTimeMillis()
            mice.removeAll { it.isDying && now - it.dyingStartTime > MOUSE_FADE_OUT_MS }
        }
    }

    // Remove paw effects
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            if (pawEffects.isNotEmpty()) {
                pawEffects.removeFirstOrNull()
            }
        }
    }

    val progress = timeLeftMs.toFloat() / GAME_DURATION_MS
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "timer"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyBlue, Color(0xFFC8E6C9), GrassGreen)
                )
            )
    ) {
        val density = LocalDensity.current
        containerWidth = with(density) { maxWidth.toPx() }
        containerHeight = with(density) { maxHeight.toPx() }

        // Grass at the bottom (cached)
        val grassLines = remember(containerWidth) {
            if (containerWidth <= 0) emptyList()
            else buildList {
                for (i in 0..containerWidth.toInt() step 12) {
                    add(
                        GrassLine(
                            x = i.toFloat(),
                            heightRatio = Random.nextFloat() * 0.6f + 0.3f,
                            offsetX = Random.nextFloat() * 6 - 3,
                            isDark = Random.nextBoolean()
                        )
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
        ) {
            drawCachedGrass(grassLines, size.height)
        }

        // Top bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
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
                                color = ScoreGold
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
                                    text = "x$combo",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PawOrange
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val timerColor by animateColorSafe(
                            targetValue = if (timeLeftMs < 5000) TimerRed else Color(0xFF5D4037),
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
                    targetValue = if (timeLeftMs < 5000) TimerRed else GrassGreen,
                    animationSpec = tween(500)
                )
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressBarColor,
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        // Mice
        mice.forEach { mouse ->
            MouseItem(
                mouse = mouse,
                onClick = {
                    if (!mouse.isDying) {
                        mice.remove(mouse)
                        val now = System.currentTimeMillis()
                        combo = if (now - lastCatchTime < 1200) combo + 1 else 1
                        lastCatchTime = now
                        val points = if (combo > 1) combo else 1
                        viewModel.addScore(points)
                        pawEffects.add(PawEffect(id = pawIdCounter++, x = mouse.x, y = mouse.y))
                    }
                }
            )
        }

        // Paw catch effects
        pawEffects.forEach { paw ->
            PawCatchEffect(paw = paw)
        }
    }
}

@Composable
private fun animateColorSafe(
    targetValue: Color,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Color> = tween(300)
): androidx.compose.runtime.State<Color> {
    return androidx.compose.animation.animateColorAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "colorAnim"
    )
}

@Composable
private fun MouseItem(mouse: Mouse, onClick: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // Entrance animation
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

    // Dying fade-out + shrink animation
    LaunchedEffect(mouse.isDying) {
        if (mouse.isDying) {
            scale.animateTo(0.3f, tween(MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
        }
    }
    LaunchedEffect(mouse.isDying) {
        if (mouse.isDying) {
            alpha.animateTo(0f, tween(MOUSE_FADE_OUT_MS.toInt(), easing = FastOutSlowInEasing))
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

@Composable
private fun PawCatchEffect(paw: PawEffect) {
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

@Composable
fun GameOverScreen(
    score: Int,
    bestScore: Int,
    missedCount: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val isNewBest = score == bestScore && score > 0
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
                        Color(0xFFFFE0B2),
                        Color(0xFFFFCC80),
                        PawOrange.copy(alpha = 0.6f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale.value
                    scaleY = cardScale.value
                    this.alpha = cardAlpha.value
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isNewBest) "🏆" else "🐱",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isNewBest) "Yeni Rekor!" else "Süre Doldu!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNewBest) ScoreGold else Color(0xFF4E342E)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Score
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🐾", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$score puan",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreGold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Kacirilan: $missedCount 🐭",
                    fontSize = 16.sp,
                    color = Color(0xFF8D6E63)
                )

                if (!isNewBest && bestScore > 0) {
                    Text(
                        text = "En iyi: $bestScore",
                        fontSize = 16.sp,
                        color = Color(0xFF8D6E63)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = PawOrange),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "🐾  Tekrar Oyna",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onMainMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBCAAA4)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Ana Menü",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCachedGrass(grassLines: List<GrassLine>, height: Float) {
    val grassColor = GrassGreen
    val darkGrass = Color(0xFF558B2F)
    for (line in grassLines) {
        val h = line.heightRatio * height
        val color = if (line.isDark) darkGrass else grassColor
        drawLine(
            color = color,
            start = Offset(line.x, height),
            end = Offset(line.x + line.offsetX, height - h),
            strokeWidth = 3f
        )
    }
}
