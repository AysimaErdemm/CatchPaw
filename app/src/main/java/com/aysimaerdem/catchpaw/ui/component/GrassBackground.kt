package com.aysimaerdem.catchpaw.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.aysimaerdem.catchpaw.domain.model.GrassLine
import com.aysimaerdem.catchpaw.ui.theme.LocalCatchPawColors
import kotlin.random.Random

@Composable
fun GrassBackground(containerWidth: Float, modifier: Modifier = Modifier) {
    val colors = LocalCatchPawColors.current
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
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        drawCachedGrass(grassLines, size.height, colors.warmWheat, colors.warmSand)
    }
}

fun DrawScope.drawCachedGrass(
    grassLines: List<GrassLine>,
    height: Float,
    lightStrand: Color,
    darkStrand: Color
) {
    for (line in grassLines) {
        val h = line.heightRatio * height
        val color = if (line.isDark) darkStrand else lightStrand
        drawLine(
            color = color,
            start = Offset(line.x, height),
            end = Offset(line.x + line.offsetX, height - h),
            strokeWidth = 3f
        )
    }
}
