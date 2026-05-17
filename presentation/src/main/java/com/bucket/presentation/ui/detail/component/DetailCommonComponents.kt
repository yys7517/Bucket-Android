package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.R
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
internal fun DetailTopBar(onBackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        CircleIconButton(onClick = onBackClick) { BackIcon(Modifier.size(22.dp)) }
    }
}

@Composable
internal fun CircleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, CircleShape),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ─── Shared composables ───────────────────────────────────────────────────────

@Composable
internal fun CategoryChip(category: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(accentColor))
        Spacer(Modifier.width(7.dp))
        Text(text = category, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
internal fun LikeButton(isLiked: Boolean, likeCount: Int, onLikeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(50.dp))
            .clickable { onLikeClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            colorFilter = if (isLiked) null else ColorFilter.tint(Color(0xFF6E687D))
        )
        Text(text = likeCount.toString(), color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
internal fun InfoPill(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(text = label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(5.dp))
        Text(text = value, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth().height(9.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Purple.copy(alpha = 0.14f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f)).height(9.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Purple)
        )
    }
}

// ─── Icon composables ─────────────────────────────────────────────────────────

@Composable
internal fun BackIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(Ink, Offset(size.width * 0.62f, size.height * 0.2f), Offset(size.width * 0.34f, size.height * 0.5f), stroke, StrokeCap.Round)
        drawLine(Ink, Offset(size.width * 0.34f, size.height * 0.5f), Offset(size.width * 0.62f, size.height * 0.8f), stroke, StrokeCap.Round)
    }
}

@Composable
internal fun CheckIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        drawLine(color, Offset(size.width * 0.20f, size.height * 0.52f), Offset(size.width * 0.42f, size.height * 0.73f), 2.6.dp.toPx(), StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.42f, size.height * 0.73f), Offset(size.width * 0.82f, size.height * 0.28f), 2.6.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
internal fun CloseIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.22f), Offset(size.width * 0.78f, size.height * 0.78f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.78f, size.height * 0.22f), Offset(size.width * 0.22f, size.height * 0.78f), stroke, StrokeCap.Round)
    }
}

/** 휴지통 아이콘 (Canvas) */
@Composable
internal fun TrashIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val stroke = 2.0.dp.toPx()
        val cap = StrokeCap.Round
        // 뚜껑
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.28f), Offset(size.width * 0.82f, size.height * 0.28f), stroke, cap)
        // 손잡이
        drawLine(color, Offset(size.width * 0.38f, size.height * 0.18f), Offset(size.width * 0.62f, size.height * 0.18f), stroke, cap)
        // 몸통 왼쪽
        drawLine(color, Offset(size.width * 0.26f, size.height * 0.28f), Offset(size.width * 0.30f, size.height * 0.82f), stroke, cap)
        // 몸통 오른쪽
        drawLine(color, Offset(size.width * 0.74f, size.height * 0.28f), Offset(size.width * 0.70f, size.height * 0.82f), stroke, cap)
        // 바닥
        drawLine(color, Offset(size.width * 0.30f, size.height * 0.82f), Offset(size.width * 0.70f, size.height * 0.82f), stroke, cap)
        // 내부 선 왼쪽
        drawLine(color, Offset(size.width * 0.42f, size.height * 0.38f), Offset(size.width * 0.42f, size.height * 0.72f), stroke, cap)
        // 내부 선 오른쪽
        drawLine(color, Offset(size.width * 0.58f, size.height * 0.38f), Offset(size.width * 0.58f, size.height * 0.72f), stroke, cap)
    }
}
