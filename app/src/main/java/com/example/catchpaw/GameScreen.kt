package com.example.catchpaw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catchpaw.ui.theme.GrassGreen
import com.example.catchpaw.ui.theme.PawOrange
import com.example.catchpaw.ui.theme.ScoreGold
import com.example.catchpaw.ui.theme.SkyBlue
import com.example.catchpaw.ui.theme.TimerRed
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

data class Mouse(
    val id: Int,
    val x: Float,
    val y: Float,
    val emoji: String = listOf("🐭", "🐁").random()
)

data class PawEffect(
    val id: Int,
    val x: Float,
    val y: Float
)

enum class GameState {
    START, PLAYING, GAME_OVER
}

private const val GAME_DURATION_MS = 30_000L
private const val MOUSE_LIFETIME_MS = 1_500L
private const val MOUSE_SPAWN_INTERVAL_MS = 800L
private const val MAX_MICE = 4

@Composable
fun CatchPawGame() {
    var gameState by remember { mutableStateOf(GameState.START) }
    var score by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }
    var missedCount by remember { mutableIntStateOf(0) }

    when (gameState) {
        GameState.START -> StartScreen(
            bestScore = bestScore,
            onStartGame = {
                score = 0
                missedCount = 0
                gameState = GameState.PLAYING
            }
        )
        GameState.PLAYING -> PlayingScreen(
            score = score,
            onScoreChange = { score = it },
            onMissed = { missedCount++ },
            onGameOver = {
                if (score > bestScore) bestScore = score
                gameState = GameState.GAME_OVER
            }
        )
        GameState.GAME_OVER -> GameOverScreen(
            score = score,
            bestScore = bestScore,
            missedCount = missedCount,
            onPlayAgain = {
                score = 0
                missedCount = 0
                gameState = GameState.PLAYING
            },
            onMainMenu = {
                gameState = GameState.START
            }
        )
    }
}

@Composable
private fun StartScreen(bestScore: Int, onStartGame: () -> Unit) {
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
            verticalArrangement = Arrangement.Center
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
private fun PlayingScreen(
    score: Int,
    onScoreChange: (Int) -> Unit,
    onMissed: () -> Unit,
    onGameOver: () -> Unit
) {
    var timeLeftMs by remember { mutableLongStateOf(GAME_DURATION_MS) }
    val mice = remember { mutableStateListOf<Mouse>() }
    val pawEffects = remember { mutableStateListOf<PawEffect>() }
    var mouseIdCounter by remember { mutableIntStateOf(0) }
    var pawIdCounter by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var lastCatchTime by remember { mutableLongStateOf(0L) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

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

    // Remove old mice
    LaunchedEffect(Unit) {
        while (true) {
            delay(MOUSE_LIFETIME_MS)
            if (mice.isNotEmpty()) {
                val removed = mice.removeFirstOrNull()
                if (removed != null) {
                    onMissed()
                    combo = 0
                }
            }
        }
    }

    // Remove paw effects
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            if (pawEffects.isNotEmpty()) {
                pawEffects.removeFirstOrNull()
            }
        }
    }

    val progress = timeLeftMs.toFloat() / GAME_DURATION_MS
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(100),
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

        // Grass at the bottom
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
        ) {
            drawGrass(size.width, size.height)
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
                        Text(
                            text = "$score",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreGold
                        )
                        if (combo > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "x$combo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PawOrange
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${(timeLeftMs / 1000)}s",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timeLeftMs < 5000) TimerRed else Color(0xFF5D4037)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (timeLeftMs < 5000) TimerRed else GrassGreen,
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
                    mice.remove(mouse)
                    val now = System.currentTimeMillis()
                    combo = if (now - lastCatchTime < 1200) combo + 1 else 1
                    lastCatchTime = now
                    val points = if (combo > 1) combo else 1
                    onScoreChange(score + points)
                    pawEffects.add(PawEffect(id = pawIdCounter++, x = mouse.x, y = mouse.y))
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
private fun MouseItem(mouse: Mouse, onClick: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(mouse.id) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    val density = LocalDensity.current
    val offsetX = with(density) { mouse.x.toDp() }
    val offsetY = with(density) { mouse.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.value)
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
        scale.animateTo(1.5f, animationSpec = tween(300, easing = LinearEasing))
    }
    LaunchedEffect(paw.id) {
        delay(100)
        alpha.animateTo(0f, animationSpec = tween(300))
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
    )
}

@Composable
private fun GameOverScreen(
    score: Int,
    bestScore: Int,
    missedCount: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val isNewBest = score == bestScore && score > 0

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
                .fillMaxWidth(),
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

private fun DrawScope.drawGrass(width: Float, height: Float) {
    val grassColor = GrassGreen
    val darkGrass = Color(0xFF558B2F)
    for (i in 0..width.toInt() step 12) {
        val h = Random.nextFloat() * height * 0.6f + height * 0.3f
        val color = if (Random.nextBoolean()) grassColor else darkGrass
        drawLine(
            color = color,
            start = Offset(i.toFloat(), height),
            end = Offset(i.toFloat() + Random.nextFloat() * 6 - 3, height - h),
            strokeWidth = 3f
        )
    }
}
