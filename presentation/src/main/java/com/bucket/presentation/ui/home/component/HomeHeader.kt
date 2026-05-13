package com.bucket.presentation.ui.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted

@Composable
fun HomeHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "안녕하세요, 영선님",
                color = Muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "오늘은 어떤 꿈을 찾아볼까요?",
                color = Ink,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        BellIcon(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(30.dp)
        )
    }
}

@Composable
private fun BellIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        drawLine(Ink, Offset(w * 0.25f, h * 0.72f), Offset(w * 0.75f, h * 0.72f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawArc(Ink, 200f, 140f, false, topLeft = Offset(w * 0.24f, h * 0.20f), size = Size(w * 0.52f, h * 0.58f), style = stroke)
        drawLine(Ink, Offset(w * 0.31f, h * 0.69f), Offset(w * 0.31f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawLine(Ink, Offset(w * 0.69f, h * 0.69f), Offset(w * 0.69f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawArc(Ink, 30f, 120f, false, topLeft = Offset(w * 0.39f, h * 0.68f), size = Size(w * 0.22f, h * 0.18f), style = stroke)
    }
}
