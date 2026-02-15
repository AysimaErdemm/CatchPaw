package com.example.catchpaw.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catchpaw.data.local.ThemeMode
import com.example.catchpaw.ui.theme.LocalCatchPawColors

@Composable
fun ThemeSelector(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCatchPawColors.current
    val options = listOf(
        ThemeMode.SYSTEM to "Sistem",
        ThemeMode.LIGHT to "☀️ Acik",
        ThemeMode.DARK to "🌙 Koyu"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (mode, label) ->
            FilterChip(
                selected = currentMode == mode,
                onClick = { onModeSelected(mode) },
                label = {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (currentMode == mode) FontWeight.Bold else FontWeight.Normal
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.pawOrange,
                    selectedLabelColor = colors.gradientTop
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
