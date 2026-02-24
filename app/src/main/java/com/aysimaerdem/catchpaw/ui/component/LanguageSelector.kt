package com.aysimaerdem.catchpaw.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aysimaerdem.catchpaw.R
import com.aysimaerdem.catchpaw.data.local.AppLanguage
import com.aysimaerdem.catchpaw.ui.theme.LocalCatchPawColors

@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCatchPawColors.current
    val options = listOf(
        AppLanguage.TURKISH to stringResource(R.string.lang_turkish),
        AppLanguage.ENGLISH to stringResource(R.string.lang_english)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (lang, label) ->
            val isSelected = currentLanguage == lang
            OutlinedButton(
                onClick = { onLanguageSelected(lang) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) colors.pawOrange else colors.divider
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) colors.pawOrange.copy(alpha = 0.12f) else colors.cardBackground
                ),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) colors.pawOrange else colors.textSecondary
                )
            }
        }
    }
}
