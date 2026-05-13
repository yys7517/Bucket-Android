package com.bucket.presentation.ui.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.SoftLine
import com.example.domain.model.home.RecentBucket

@Composable
fun RecentBucketCard(
    bucket: RecentBucket,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val accentColor = categoryAccent(bucket.category, bucket.categoryColor)
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(accentColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bucket.category,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = bucket.title,
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                InitialBadge(
                    text = bucket.userName.initial(),
                    color = accentColor.copy(alpha = 0.18f),
                    textColor = accentColor,
                    size = 24
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${bucket.userName} · ${bucket.startDate}",
                    color = Color(0xFF8C8697),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        ChevronIcon(Modifier.size(24.dp))
    }
}

@Composable
private fun ChevronIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawLine(Color(0xFF9A94A8), Offset(size.width * 0.42f, size.height * 0.28f), Offset(size.width * 0.64f, size.height * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Color(0xFF9A94A8), Offset(size.width * 0.64f, size.height * 0.50f), Offset(size.width * 0.42f, size.height * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}
