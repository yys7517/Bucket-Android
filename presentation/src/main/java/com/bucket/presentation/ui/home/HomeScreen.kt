package com.bucket.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.home.component.UserAvatar
import com.bucket.presentation.ui.home.component.categoryAccent

import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.SmallGoalSummary
import com.example.domain.model.user.Author

// 홈 화면에 표시할 카테고리 칩 목록
private val HOME_CATEGORIES = listOf("전체", "학습", "여행", "운동", "재테크", "독서", "건강", "취미")

@Composable
fun HomeRoute(
    onBucketClick: (Long, Author) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadHome()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.BookmarkUpdated -> {
                    val message = if (event.isBookmarked) {
                        "북마크에 추가되었습니다."
                    } else {
                        "북마크에서 삭제합니다."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onBucketClick = onBucketClick,
        onCategorySelected = viewModel::selectCategory,
        onBookmarkClick = viewModel::toggleBookmark,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBucketClick: (Long, Author) -> Unit = { _, _ -> },
    onCategorySelected: (String) -> Unit = {},
    onBookmarkClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hasBuckets = uiState.popularBuckets.isNotEmpty()

    Surface(modifier = modifier.fillMaxSize(), color = HomeBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars.asPaddingValues().let {
                PaddingValues(
                    top = it.calculateTopPadding() + 20.dp,
                    bottom = 24.dp
                )
            },
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── 상단 바: 아이콘 + 타이틀 + 벨
            item {
                FeedTopBar(modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(18.dp))
            }

            // ── 카테고리 칩 (가로 스크롤)
            item {
                CategoryChipRow(
                    categories = HOME_CATEGORIES,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                )
                Spacer(Modifier.height(18.dp))
            }

            // ── 피드 카드 목록
            if (uiState.isLoading && !hasBuckets) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("불러오는 중...", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (uiState.errorMessage != null && !hasBuckets) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.errorMessage, color = Color(0xFFE04D5F), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                items(uiState.filteredBuckets, key = { it.id }) { bucket ->
                    FeedCard(
                        bucket = bucket,
                        onClick = { onBucketClick(bucket.id, bucket.author) },
                        onBookmarkClick = { onBookmarkClick(bucket.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                }
                if (uiState.filteredBuckets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "아직 게시물이 없어요",
                                color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── 상단 바 ──────────────────────────────────────────────────────────────────

@Composable
private fun FeedTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 간다라트 3×3 그리드 아이콘
            MiniGridIcon()
            Spacer(Modifier.width(10.dp))
            Text(
                text = "간다라트",
                color = Ink,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        // 알림 아이콘
        /*Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, SoftLine, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            BellIcon(modifier = Modifier.size(20.dp), color = Color(0xFF6E687D))
        }*/
    }
}

// ─── 카테고리 칩 ──────────────────────────────────────────────────────────────

@Composable
private fun CategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isSelected) Purple else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Purple else SoftLine,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color(0xFF6E687D),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── 피드 카드 ────────────────────────────────────────────────────────────────

@Composable
private fun FeedCard(
    bucket: PopularBucket,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = categoryAccent(bucket.category, bucket.categoryColor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 카드 헤더: 아바타 + 유저명 + 날짜 + 카테고리 칩
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserAvatar(
                    profileImageUrl = bucket.author.profileImgUrl,
                    username = bucket.author.username,
                    color = accentColor.copy(alpha = 0.18f),
                    textColor = accentColor,
                    size = 34
                )
                Column {
                    Text(
                        text = bucket.author.username,
                        color = Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = bucket.startDate.toRelativeDateText(),
                        color = Muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            // 카테고리 칩
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = bucket.category,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // ── 미니 만다라트 3×3 그리드 (실제 SmallGoal 데이터)
        MiniMandalaGrid(
            accentColor = accentColor,
            title = bucket.title,
            smallGoals = bucket.smallGoals,
        )

        // ── 게시물 제목 + 진행률
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = bucket.title,
                color = Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // 진행률 바
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("할 일 ", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${bucket.completedCount}",
                        color = Ink, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "/${bucket.totalCount}",
                        color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(accentColor.copy(alpha = 0.14f))
                ) {
                    val fraction = if (bucket.totalCount > 0)
                        bucket.completedCount.toFloat() / bucket.totalCount else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction.coerceIn(0f, 1f))
                            .height(5.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(accentColor)
                    )
                }
                Text(
                    text = "${bucket.progressRate}%",
                    color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── 액션 바: 좋아요 + 댓글 + 북마크
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좋아요
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Canvas(Modifier.size(18.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width * 0.50f, size.height * 0.80f)
                            cubicTo(
                                size.width * 0.10f, size.height * 0.55f,
                                size.width * 0.02f, size.height * 0.22f,
                                size.width * 0.28f, size.height * 0.18f
                            )
                            cubicTo(
                                size.width * 0.38f, size.height * 0.16f,
                                size.width * 0.46f, size.height * 0.22f,
                                size.width * 0.50f, size.height * 0.32f
                            )
                            cubicTo(
                                size.width * 0.54f, size.height * 0.22f,
                                size.width * 0.62f, size.height * 0.16f,
                                size.width * 0.72f, size.height * 0.18f
                            )
                            cubicTo(
                                size.width * 0.98f, size.height * 0.22f,
                                size.width * 0.90f, size.height * 0.55f,
                                size.width * 0.50f, size.height * 0.80f
                            )
                            close()
                        }
                        drawPath(path, Color(0xFFE04D5F).copy(alpha = 0.85f))
                    }
                    Text(
                        text = "${bucket.likeCount}",
                        color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                }
                // 댓글 아이콘
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(5.dp)
//                ) {
//                    Canvas(Modifier.size(18.dp)) {
//                        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
//                        drawRoundRect(
//                            color = Color(0xFF9A94A8),
//                            topLeft = Offset(size.width * 0.10f, size.height * 0.14f),
//                            size = Size(size.width * 0.80f, size.height * 0.60f),
//                            cornerRadius = CornerRadius(4.dp.toPx()),
//                            style = stroke
//                        )
//                        drawLine(
//                            color = Color(0xFF9A94A8),
//                            start = Offset(size.width * 0.28f, size.height * 0.74f),
//                            end = Offset(size.width * 0.22f, size.height * 0.90f),
//                            strokeWidth = 1.8.dp.toPx(),
//                            cap = StrokeCap.Round
//                        )
//                    }
//                    Text(text = "댓글", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
//                }
            }
            // 북마크
            BookmarkIcon(
                isBookmarked = bucket.isBookmarked,
                onClick = onBookmarkClick,
            )
        }
    }
}

// ─── 미니 만다라트 그리드 ──────────────────────────────────────────────────────

@Composable
private fun MiniMandalaGrid(
    accentColor: Color,
    title: String,
    smallGoals: Map<Int, SmallGoalSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(14.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (col in 0 until 3) {
                    val idx = row * 3 + col
                    val position = idx + 1  // sortOrder: 1~9
                    when {
                        idx == 4 -> MiniMandalaCenter(
                            accentColor = accentColor,
                            title = title,
                            modifier = Modifier.weight(1f)
                        )
                        else -> {
                            val goal = smallGoals[position]
                            MiniMandalaCell(
                                accentColor = accentColor,
                                goal = goal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniMandalaCenter(
    accentColor: Color,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(3.dp)
        )
    }
}

@Composable
private fun MiniMandalaCell(
    accentColor: Color,
    goal: SmallGoalSummary?,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        goal == null -> Color.Transparent
        goal.isCompleted -> accentColor.copy(alpha = 0.55f)
        else -> accentColor.copy(alpha = 0.18f)
    }
    val borderColor = if (goal == null) SoftLine else accentColor.copy(alpha = 0.30f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (goal != null) {
            Text(
                text = goal.content,
                color = if (goal.isCompleted) Color.White else accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(3.dp)
            )
        }
    }
}

// ─── 아이콘 ───────────────────────────────────────────────────────────────────

@Composable
private fun MiniGridIcon(modifier: Modifier = Modifier) {
    // 셀별 투명도: 모서리(0.45) → 가장자리(0.70) → 중앙(0.95)
    val alphas = listOf(
        0.45f, 0.70f, 0.45f,
        0.70f, 0.95f, 0.70f,
        0.45f, 0.70f, 0.45f,
    )
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Purple.copy(alpha = 0.13f))
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    for (col in 0 until 3) {
                        val idx = row * 3 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(2.dp))
                                .background(Purple.copy(alpha = alphas[idx]))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BellIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.0.dp.toPx(), cap = StrokeCap.Round)
        // 벨 몸통
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.50f, size.height * 0.10f)
            cubicTo(
                size.width * 0.24f, size.height * 0.10f,
                size.width * 0.18f, size.height * 0.30f,
                size.width * 0.18f, size.height * 0.52f
            )
            lineTo(size.width * 0.12f, size.height * 0.72f)
            lineTo(size.width * 0.88f, size.height * 0.72f)
            lineTo(size.width * 0.82f, size.height * 0.52f)
            cubicTo(
                size.width * 0.82f, size.height * 0.30f,
                size.width * 0.76f, size.height * 0.10f,
                size.width * 0.50f, size.height * 0.10f
            )
            close()
        }
        drawPath(path, color, style = stroke)
        // 손잡이
        drawArc(
            color = color,
            startAngle = 0f, sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.38f, size.height * 0.68f),
            size = Size(size.width * 0.24f, size.height * 0.22f),
            style = stroke
        )
    }
}

@Composable
private fun BookmarkIcon(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.10f)
            lineTo(size.width * 0.78f, size.height * 0.10f)
            lineTo(size.width * 0.78f, size.height * 0.88f)
            lineTo(size.width * 0.50f, size.height * 0.68f)
            lineTo(size.width * 0.22f, size.height * 0.88f)
            close()
        }
        if (isBookmarked) {
            drawPath(path, Purple)
        } else {
            drawPath(
                path = path,
                color = Color(0xFF9A94A8),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

// ─── 날짜 헬퍼 ────────────────────────────────────────────────────────────────

private fun String.toRelativeDateText(): String {
    return try {
        val parts = split("-")
        if (parts.size != 3) return this
        "${parts[0]}년 ${parts[1].toInt()}월 ${parts[2].toInt()}일"
    } catch (e: Exception) {
        this
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun HomeScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        HomeScreen(
            uiState = HomeUiState(
                popularBuckets = listOf(
                    PopularBucket(
                        id = 1,
                        category = "학습",
                        categoryColor = "#7B5DD6",
                        title = "백엔드 인증 구조 완성",
                        author = Author(1L, "윤영선", ""),
                        likeCount = 24,
                        isLiked = false,
                        startDate = "2026-05-15",
                        completedCount = 1,
                        totalCount = 3,
                        progressRate = 33,
                        smallGoals = mapOf(
                            1 to SmallGoalSummary("Spring Security", "#7B5DD6", true),
                            2 to SmallGoalSummary("JWT 토큰", "#7B5DD6", false),
                            3 to SmallGoalSummary("OAuth2", "#7B5DD6", false),
                        )
                    ),
                    PopularBucket(
                        id = 2,
                        category = "여행",
                        categoryColor = "#2C8BAA",
                        title = "제주도 한 달 살기",
                        author = Author(2L, "김민호", ""),
                        likeCount = 12,
                        isLiked = false,
                        startDate = "2026-05-06",
                        completedCount = 3,
                        totalCount = 7,
                        progressRate = 43,
                        smallGoals = emptyMap()
                    )
                )
            )
        )
    }
}
