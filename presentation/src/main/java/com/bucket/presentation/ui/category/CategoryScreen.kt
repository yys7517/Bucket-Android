package com.bucket.presentation.ui.category

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.example.domain.model.category.BucketCategory
import androidx.core.graphics.toColorInt

@Composable
fun CategoryRoute(
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CategoryScreen(uiState = uiState)
}

@Composable
fun CategoryScreen(
    uiState: CategoryUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars
                .asPaddingValues()
                .let {
                    PaddingValues(
                        start = 24.dp,
                        top = it.calculateTopPadding() + 34.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryHeader()
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(14.dp))
            }
            if (uiState.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "불러오는 중...",
                        color = Muted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            uiState.errorMessage?.let { message ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = message,
                        color = Color(0xFFE04D5F),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(uiState.categories) { category ->
                CategoryCard(category = category)
            }
        }
    }
}

@Composable
private fun CategoryHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "카테고리",
                color = Ink,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "관심사별로 버킷을 모아보세요",
                color = Muted,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CategoryCard(category: BucketCategory) {
    val colors = category.categoryColors()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.background)
            .padding(22.dp)
    ) {
        DecorativeBubbles(
            color = colors.accent,
            modifier = Modifier.matchParentSize()
        )
        Text(
            text = category.name,
            color = colors.content,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "버킷 ${category.bucketCount}",
                color = colors.content,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            MoveIcon(
                color = colors.content,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DecorativeBubbles(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {
        drawCircle(
            color = color.copy(alpha = 0.48f),
            radius = size.width * 0.28f,
            center = Offset(size.width * 0.91f, size.height * 0.15f)
        )
        drawCircle(
            color = color.copy(alpha = 0.62f),
            radius = size.width * 0.13f,
            center = Offset(size.width * 0.94f, size.height * 0.38f)
        )
    }
}

@Composable
private fun PencilIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.26f, size.height * 0.74f),
            end = Offset(size.width * 0.75f, size.height * 0.25f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.63f, size.height * 0.18f),
            end = Offset(size.width * 0.82f, size.height * 0.37f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.22f, size.height * 0.78f),
            end = Offset(size.width * 0.18f, size.height * 0.92f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.18f, size.height * 0.92f),
            end = Offset(size.width * 0.32f, size.height * 0.88f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MoveIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.28f),
            end = Offset(size.width * 0.64f, size.height * 0.50f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.64f, size.height * 0.50f),
            end = Offset(size.width * 0.42f, size.height * 0.72f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}

private data class CategoryColors(
    val background: Color,
    val content: Color,
    val accent: Color
)

private fun BucketCategory.categoryColors(): CategoryColors {
    val serverColor = categoryColor.toComposeColorOrNull() ?: Color(0xFF6C657B)
    return CategoryColors(
        background = serverColor.copy(alpha = 0.16f),
        content = serverColor,
        accent = serverColor
    )
}

private fun String.toComposeColorOrNull(): Color? =
    runCatching {
        val normalized = if (startsWith("#")) this else "#$this"
        Color(normalized.toColorInt())
    }.getOrNull()

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun CategoryScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        CategoryScreen(
            uiState = CategoryUiState(
                categories = listOf(
                    BucketCategory(1, "여행", 142, "#22668B"),
                    BucketCategory(2, "운동", 87, "#1F7049"),
                    BucketCategory(3, "취미", 56, "#6E348B")
                )
            )
        )
    }
}
