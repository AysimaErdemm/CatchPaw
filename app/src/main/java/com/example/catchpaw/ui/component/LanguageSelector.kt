package com.example.catchpaw.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catchpaw.R
import com.example.catchpaw.data.local.AppLanguage
import com.example.catchpaw.ui.theme.LocalCatchPawColors

@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCatchPawColors.current
    val options = listOf(
        AppLanguage.SYSTEM to stringResource(R.string.lang_system),
        AppLanguage.TURKISH to stringResource(R.string.lang_turkish),
        AppLanguage.ENGLISH to stringResource(R.string.lang_english)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (lang, label) ->
            FilterChip(
                selected = currentLanguage == lang,
                onClick = { onLanguageSelected(lang) },
                label = {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
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
