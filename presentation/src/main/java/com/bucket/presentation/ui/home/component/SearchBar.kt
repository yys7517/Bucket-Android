package com.bucket.presentation.ui.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.SoftLine

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(32.dp))
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchIcon(Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Text(
            text = "버킷 리스트, 사용자 검색",
            modifier = Modifier.weight(1f),
            color = Color(0xFF9A94A8),
            fontSize = 17.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        TuneIcon(Modifier.size(24.dp))
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawCircle(Muted, radius = size.minDimension * 0.32f, center = Offset(size.width * 0.43f, size.height * 0.42f), style = Stroke(stroke))
        drawLine(Muted, Offset(size.width * 0.64f, size.height * 0.64f), Offset(size.width * 0.84f, size.height * 0.84f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun TuneIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawLine(Muted, Offset(size.width * 0.25f, size.height * 0.33f), Offset(size.width * 0.82f, size.height * 0.33f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Muted, Offset(size.width * 0.42f, size.height * 0.52f), Offset(size.width * 0.75f, size.height * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Muted, Offset(size.width * 0.55f, size.height * 0.71f), Offset(size.width * 0.66f, size.height * 0.71f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}
