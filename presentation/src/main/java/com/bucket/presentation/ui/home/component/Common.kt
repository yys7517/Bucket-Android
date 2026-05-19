package com.bucket.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.theme.Purple

@Composable
fun UserAvatar(
    profileImageUrl: String,
    username: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    size: Int = 28
) {
    var showInitialBadge by remember(profileImageUrl) {
        mutableStateOf(profileImageUrl.isBlank())
    }

    if (showInitialBadge) {
        InitialBadge(
            text = username.initial(),
            color = color,
            textColor = textColor,
            modifier = modifier,
            size = size
        )
    } else {
        AsyncImage(
            model = profileImageUrl,
            contentDescription = "$username 프로필 이미지",
            contentScale = ContentScale.Crop,
            onError = { showInitialBadge = true },
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, SoftLine, CircleShape)
        )
    }
}

@Composable
fun InitialBadge(
    text: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    size: Int = 28
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (size < 28) 11.sp else 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

fun String.initial(): String = trim().take(1).ifEmpty { "?" }

fun categoryGradient(category: String, categoryColor: String): List<Color> {
    val accent = categoryAccent(category, categoryColor)
    return listOf(accent.copy(alpha = 0.24f), accent.copy(alpha = 0.08f))
}

fun categoryAccent(category: String, categoryColor: String): Color =
    categoryColor.toComposeColorOrNull() ?: when (category) {
        "여행" -> Color(0xFF2C8BAA)
        "학습" -> Color(0xFF7B5DD6)
        "건강" -> Color(0xFF2F9B68)
        "운동" -> Color(0xFF2F9B68)
        "취미" -> Color(0xFFD57931)
        else -> Purple
    }

fun String.toComposeColorOrNull(): Color? =
    runCatching {
        val normalized = if (startsWith("#")) this else "#$this"
        Color(android.graphics.Color.parseColor(normalized))
    }.getOrNull()
