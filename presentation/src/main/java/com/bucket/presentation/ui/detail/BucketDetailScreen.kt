package com.bucket.presentation.ui.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.R
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.home.component.UserAvatar
import com.bucket.presentation.ui.home.component.categoryAccent
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.model.post.PostPlan

@Composable
fun BucketDetailRoute(
    bucketId: Long,
    onBackClick: () -> Unit,
    viewModel: BucketDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(bucketId) {
        viewModel.loadBucketDetail(bucketId)
    }
    val uiState by viewModel.uiState.collectAsState()

    BucketDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick
    )
}

@Composable
fun BucketDetailScreen(
    uiState: BucketDetailUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bucket = uiState.bucketDetail
    var isLiked by rememberSaveable(bucket?.id, bucket?.isLiked) {
        mutableStateOf(bucket?.isLiked ?: false)
    }
    var isEditing by rememberSaveable(bucket?.id, bucket?.isMine) {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars
                .asPaddingValues()
                .let {
                    PaddingValues(
                        start = 24.dp,
                        top = it.calculateTopPadding() + 18.dp,
                        end = 24.dp,
                        bottom = 34.dp
                    )
                },
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                DetailTopBar(
                    isLiked = isLiked,
                    isMine = bucket?.isMine == true,
                    isEditing = isEditing,
                    onBackClick = onBackClick,
                    onLikeClick = { isLiked = isLiked.not() },
                    onEditClick = { isEditing = isEditing.not() }
                )
            }
            when {
                uiState.isLoading -> {
                    item {
                        Text(
                            text = "불러오는 중...",
                            color = Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        Text(
                            text = uiState.errorMessage,
                            color = Color(0xFFE04D5F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                bucket != null -> {
                    val accentColor = categoryAccent(bucket.category, bucket.categoryColor)
                    val completedCount = bucket.plans.count { it.isComplete }
                    val progress = if (bucket.plans.isEmpty()) 0f else completedCount.toFloat() / bucket.plans.size

                    item {
                        BucketHeaderCard(
                            bucket = bucket,
                            accentColor = accentColor,
                            completedCount = completedCount,
                            progress = progress,
                            likeCount = bucket.adjustedLikeCount(isLiked),
                            isEditing = isEditing && bucket.isMine
                        )
                    }
                    item {
                        PlanSection(
                            plans = bucket.plans,
                            accentColor = accentColor,
                            isEditing = isEditing && bucket.isMine
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    isLiked: Boolean,
    isMine: Boolean,
    isEditing: Boolean,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            onClick = onBackClick,
            content = { BackIcon(Modifier.size(22.dp)) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (isMine) {
                CircleIconButton(
                    onClick = onEditClick,
                    content = {
                        if (isEditing) {
                            CheckIcon(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF2F9B68)
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF6E687D))
                            )
                        }
                    }
                )
            }
            CircleIconButton(
                onClick = onLikeClick,
                content = {
                    HeartIcon(
                        modifier = Modifier.size(22.dp),
                        color = if (isLiked) Color(0xFFFF5D65) else Color(0xFF6E687D)
                    )
                }
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun BucketHeaderCard(
    bucket: BucketPostDetail,
    accentColor: Color,
    completedCount: Int,
    progress: Float,
    likeCount: Int,
    isEditing: Boolean
) {
    var title by rememberSaveable(bucket.id, "title") { mutableStateOf(bucket.title) }
    var memo by rememberSaveable(bucket.id, "memo") { mutableStateOf(bucket.memo) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(24.dp))
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryChip(
                category = bucket.category,
                accentColor = accentColor
            )
            LikeCount(likeCount = likeCount)
        }
        Spacer(Modifier.height(18.dp))
        if (isEditing) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Ink,
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        } else {
            Text(
                text = title,
                color = Ink,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.height(14.dp))
        if (isEditing) {
            OutlinedTextField(
                value = memo,
                onValueChange = { memo = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF635E72),
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        } else {
            Text(
                text = memo,
                color = Color(0xFF635E72),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoPill(
                label = "시작일",
                value = bucket.startDate.toKoreanDateText(),
                modifier = Modifier.weight(1f)
            )
            InfoPill(
                label = "달성률",
                value = "$completedCount/${bucket.plans.size}",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(18.dp))
        ProgressBar(
            progress = progress,
            accentColor = accentColor
        )
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                profileImageUrl = bucket.profileImage,
                username = bucket.username,
                color = accentColor.copy(alpha = 0.18f),
                textColor = accentColor,
                size = 30
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = bucket.username,
                color = Color(0xFF6F687E),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = category,
            color = accentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LikeCount(likeCount: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFFFEEF1))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("♥", color = Color(0xFFFF5D65), fontSize = 13.sp)
        Spacer(Modifier.width(5.dp))
        Text(
            text = likeCount.toString(),
            color = Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun InfoPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = value,
            color = Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = 0.14f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(9.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(accentColor)
        )
    }
}

@Composable
private fun PlanSection(
    plans: List<PostPlan>,
    accentColor: Color,
    isEditing: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(
            title = "세부 계획",
            count = plans.size
        )
        plans.sortedBy { it.sortOrder }.forEach { plan ->
            PlanChecklistItem(
                plan = plan,
                accentColor = accentColor,
                isEditing = isEditing
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Ink,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = count.toString(),
            color = Muted,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun PlanChecklistItem(
    plan: PostPlan,
    accentColor: Color,
    isEditing: Boolean
) {
    var checked by rememberSaveable(plan.id) { mutableStateOf(plan.isComplete) }
    var content by rememberSaveable(plan.id, "content") { mutableStateOf(plan.content) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = isEditing) { checked = checked.not() }
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = if (isEditing) {
                { checked = it }
            } else {
                null
            },
            enabled = isEditing,
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                uncheckedColor = Color(0xFFB7B0C5),
                checkmarkColor = Color.White
            )
        )
        Spacer(Modifier.width(8.dp))
        if (isEditing) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = if (checked) Color(0xFF8C8697) else Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            )
        } else {
            Text(
                text = content,
                color = if (checked) Color(0xFF8C8697) else Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = "%02d".format(plan.sortOrder),
            color = accentColor.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun BackIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.62f, size.height * 0.2f),
            end = Offset(size.width * 0.34f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Ink,
            start = Offset(size.width * 0.34f, size.height * 0.5f),
            end = Offset(size.width * 0.62f, size.height * 0.8f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CheckIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.20f, size.height * 0.52f),
            end = Offset(size.width * 0.42f, size.height * 0.73f),
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.73f),
            end = Offset(size.width * 0.82f, size.height * 0.28f),
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun HeartIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.1.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = color,
            startAngle = 140f,
            sweepAngle = 230f,
            useCenter = false,
            topLeft = Offset(size.width * 0.05f, size.height * 0.13f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.45f, size.height * 0.45f),
            style = stroke
        )
        drawArc(
            color = color,
            startAngle = 170f,
            sweepAngle = 230f,
            useCenter = false,
            topLeft = Offset(size.width * 0.50f, size.height * 0.13f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.45f, size.height * 0.45f),
            style = stroke
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.12f, size.height * 0.52f),
            end = Offset(size.width * 0.50f, size.height * 0.86f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.88f, size.height * 0.52f),
            end = Offset(size.width * 0.50f, size.height * 0.86f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}

private fun BucketPostDetail.adjustedLikeCount(isLiked: Boolean): Int =
    when {
        isLiked && !this.isLiked -> likeCount + 1
        !isLiked && this.isLiked -> (likeCount - 1).coerceAtLeast(0)
        else -> likeCount
    }

private fun String.toKoreanDateText(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    val month = parts[1].toIntOrNull() ?: return this
    val day = parts[2].toIntOrNull() ?: return this
    return "${parts[0]}년 ${month}월 ${day}일"
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun BucketDetailScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        BucketDetailScreen(
            uiState = BucketDetailUiState(
                bucketDetail = BucketPostDetail(
                    id = 13,
                    title = "마라톤 풀코스 완주하기",
                    memo = "춘천 마라톤에서 서브4 달성하는 그 날까지!",
                    category = "운동",
                    categoryColor = "#2F9B68",
                    likeCount = 243,
                    startDate = "2026-09-01",
                    userId = 2,
                    username = "testuser",
                    profileImage = "https://...",
                    plans = listOf(
                        PostPlan(
                            id = 101,
                            sortOrder = 1,
                            content = "하프 마라톤 1회 완주",
                            isComplete = false
                        ),
                        PostPlan(
                            id = 102,
                            sortOrder = 2,
                            content = "런닝크루 가입",
                            isComplete = true
                        )
                    ),
                    isLiked = true,
                    isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}
