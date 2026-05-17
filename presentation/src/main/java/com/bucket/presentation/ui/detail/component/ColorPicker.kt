package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bucket.presentation.ui.detail.extension.toComposeColor

// ─── 만다라트 8가지 선택 색상 ─────────────────────────────────────────────────

val MANDALA_COLORS = listOf(
    "#8D6BE8", // purple
    "#E8736B", // red
    "#E8A06B", // orange
    "#E8C96B", // yellow
    "#6BE88D", // green
    "#6BB8E8", // blue
    "#6BE8D4", // teal
    "#E86BB8", // pink
)

// ─── Color Picker ─────────────────────────────────────────────────────────────
//
// MANDALA_COLORS 8가지를 2×4 그리드로 보여주고, 선택된 색에는 outer ring 표시.
// usedColors 에 포함된 색상에만 체크 표시 (이미 사용된 색상만 체크).

@Composable
internal fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    usedColors: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
    colors: List<String> = MANDALA_COLORS,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 2행 × 4열
        for (rowIdx in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (colIdx in 0 until 4) {
                    val idx = rowIdx * 4 + colIdx
                    val hex = colors.getOrNull(idx)
                    if (hex != null) {
                        ColorSwatch(
                            color = hex,
                            isSelected = hex == selectedColor,
                            isUsed = hex in usedColors,
                            onClick = { onColorSelected(hex) },
                        )
                    } else {
                        Spacer(Modifier.size(54.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: String,
    isSelected: Boolean,
    isUsed: Boolean,
    onClick: () -> Unit,
) {
    val composeColor = color.toComposeColor()
    val outerSize = 54.dp
    val innerSize = if (isSelected) 40.dp else 48.dp

    Box(
        modifier = Modifier
            .size(outerSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.5.dp, composeColor, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(composeColor),
            contentAlignment = Alignment.Center
        ) {
            // 이미 사용된 색상에만 체크 표시
            if (isUsed) {
                CheckIcon(modifier = Modifier.size(20.dp), color = Color.White)
            }
        }
    }
}
