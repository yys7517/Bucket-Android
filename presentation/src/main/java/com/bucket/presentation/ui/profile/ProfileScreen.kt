package com.bucket.presentation.ui.profile

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.LightPurple
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.home.component.UserAvatar
import com.bucket.presentation.ui.home.component.categoryAccent
import com.example.domain.model.home.PostCard
import com.example.domain.model.home.SmallGoalSummary
import com.example.domain.model.profile.ProfilePostStatus
import com.example.domain.model.user.Author

data class ProfileEditResult(
    val username: String,
    val email: String,
    val introduction: String,
)

// ─── Route ────────────────────────────────────────────────────────────────────

/** 내 프로필 */
@Composable
fun ProfileRoute(
    onEditClick: (username: String, email: String, introduction: String, profileImageUrl: String) -> Unit = { _, _, _, _ -> },
    onBucketClick: (Long, Author) -> Unit = { _, _ -> },
    onCreatePostClick: () -> Unit = {},
    profileEditResult: ProfileEditResult? = null,
    onProfileEditResultConsumed: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadProfile(isMine = true) }
    LaunchedEffect(profileEditResult) {
        profileEditResult?.let { result ->
            viewModel.applyProfileUpdate(
                username = result.username,
                email = result.email,
                introduction = result.introduction,
            )
            onProfileEditResultConsumed()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onTabSelected = viewModel::selectTab,
        onStatusSelected = viewModel::selectStatus,
        onFollowClick = viewModel::toggleFollow,
        onBucketClick = onBucketClick,
        onFabClick = onCreatePostClick,
        onEditClick = {
            onEditClick(
                uiState.username,
                uiState.userEmail,
                uiState.introduction,
                uiState.profileImgUrl
            )
        },
    )
}

/** 상대방 프로필 */
@Composable
fun OtherProfileRoute(
    userId: Long,
    onBackClick: () -> Unit,
    onBucketClick: (Long, Author) -> Unit = { _, _ -> },
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) { viewModel.loadProfile(userId = userId, isMine = false) }
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onTabSelected = viewModel::selectTab,
        onStatusSelected = viewModel::selectStatus,
        onFollowClick = viewModel::toggleFollow,
        onBucketClick = onBucketClick,
        onFabClick = {},
        onBackClick = onBackClick,
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onTabSelected: (ProfileTab) -> Unit = {},
    onStatusSelected: (ProfilePostStatus) -> Unit = {},
    onFollowClick: () -> Unit = {},
    onBucketClick: (Long, Author) -> Unit = { _, _ -> },
    onFabClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = HomeBackground,
        floatingActionButton = {
            if (uiState.isMine) {
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = Purple,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(
                            end = 8.dp,
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + 8.dp
                        )
                        .size(56.dp)
                ) {
                    PlusIcon()
                }
            }
        }
    ) { innerPadding ->
        // 탭 목록 결정 (내 프로필: 3개, 상대방: 1개)
        val tabs = if (uiState.isMine) ProfileTab.entries else listOf(ProfileTab.POSTS)
        val selectedTabIndex = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = statusBarPadding + 16.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── 프로필 헤더 (전체 너비)
            item(span = { GridItemSpan(2) }) {
                ProfileHeader(
                    uiState = uiState,
                    onFollowClick = onFollowClick,
                    onEditClick = onEditClick,
                    onBackClick = onBackClick,
                )
            }

            if (uiState.errorMessage != null) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = Color(0xFFE04D5F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // ── 탭 행 (전체 너비)
            item(span = { GridItemSpan(2) }) {
                Column {
                    HorizontalDivider(color = SoftLine, thickness = 1.dp)
                    if (tabs.size > 1) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = HomeBackground,
                            contentColor = Purple,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = Purple,
                                    height = 2.dp
                                )
                            },
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { onTabSelected(tab) },
                                    text = {
                                        Text(
                                            text = tab.label,
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedTabIndex == index)
                                                FontWeight.ExtraBold else FontWeight.SemiBold
                                        )
                                    },
                                    selectedContentColor = Purple,
                                    unselectedContentColor = Muted
                                )
                            }
                        }
                    }
                    ProfileStatusFilter(
                        selectedStatus = uiState.selectedStatus,
                        onStatusSelected = onStatusSelected,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── 버킷 카드 그리드 (2열)
            if (uiState.isLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "불러오는 중...",
                            color = Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else if (uiState.displayedBuckets.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "아직 만다라트가 없어요",
                            color = Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                items(
                    items = uiState.displayedBuckets,
                    key = { it.id }
                ) { bucket ->
                    ProfileBucketCard(
                        post = bucket,
                        onClick = { onBucketClick(bucket.id, bucket.author) },
                    )
                }
            }
        }
    }
}

// ─── 프로필 헤더 ──────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    onFollowClick: () -> Unit,
    onEditClick: () -> Unit,
    onBackClick: (() -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 타이틀 행
        if (onBackClick != null) {
            // 상대방 프로필: ← 뒤로가기 + 유저명
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onBackClick)
                    .padding(8.dp)
            ) {
                BackArrowIcon(color = Ink)
            }
        } else {
            Text(
                text = "내 프로필",
                color = Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // ── 아바타 + 이름/아이디 + 액션 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserAvatar(
                profileImageUrl = uiState.profileImgUrl,
                username = uiState.username,
                color = Purple.copy(alpha = 0.15f),
                textColor = Purple,
                size = 72
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = uiState.username,
                    color = Ink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (uiState.userEmail.isNotBlank()) {
                    Text(
                        text = uiState.userEmail,
                        color = Muted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (uiState.isMine) {
                PillOutlineButton(label = "편집", onClick = onEditClick)
            } /*else {
                if (uiState.isFollowing) {
                    PillOutlineButton(label = "팔로잉", onClick = onFollowClick)
                } else {
                    PillFilledButton(label = "팔로우", onClick = onFollowClick)
                }
            }*/
        }

        // ── 자기소개
        if (uiState.introduction.isNotBlank()) {
            Text(
                text = uiState.introduction,
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 23.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }

        // ── 통계 카드 (만다라트 | 완료 | 좋아요)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(label = "만다라트", value = uiState.bucketCount.toString(), valueColor = Ink)
            StatDivider()
            StatItem(label = "완료", value = uiState.completedBucketCount.toString(), valueColor = Purple)
            StatDivider()
            StatItem(label = "받은 좋아요", value = uiState.totalLikeCount.toString(), valueColor = Ink)
        }
    }
}

@Composable
private fun ProfileStatusFilter(
    selectedStatus: ProfilePostStatus,
    onStatusSelected: (ProfilePostStatus) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val statuses = listOf(
            ProfilePostStatus.ALL,
            ProfilePostStatus.IN_PROGRESS,
            ProfilePostStatus.COMPLETED,
            ProfilePostStatus.DRAFT,
        )
        statuses.forEach { status ->
            val selected = selectedStatus == status
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (selected) Ink else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (selected) Ink else SoftLine,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable { onStatusSelected(status) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = status.label,
                    color = if (selected) Color.White else Color(0xFF7F788E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color = Ink) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(SoftLine)
    )
}

/** Pill 형태 채워진 버튼 (팔로우) */
@Composable
private fun PillFilledButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Purple)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Pill 형태 외곽선 버튼 (편집 / 팔로잉) */
@Composable
private fun PillOutlineButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .border(1.5.dp, SoftLine, RoundedCornerShape(50.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// ─── 버킷 카드 (2열 그리드용) ─────────────────────────────────────────────────

@Composable
private fun ProfileBucketCard(
    post: PostCard,
    onClick: () -> Unit,
) {
    val accentColor = categoryAccent(post.category, post.categoryColor)
    val status = post.postStatus()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 상태 배지 + 카테고리 칩
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 상태 배지
            StatusBadge(status = status)
            // 카테고리 칩
            ProfileCategoryChip(category = post.category, accentColor = accentColor)
        }

        // 미니 만다라트
        SmallMiniMandala(
            accentColor = accentColor,
            title = post.title,
            smallGoals = post.smallGoals,
        )

        // 제목
        Text(
            text = post.title,
            color = Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 21.sp,
        )

        // 진행률 바
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${post.completedCount}/${post.totalCount}",
                    color = Muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${post.progressRate}%",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(accentColor.copy(alpha = 0.14f))
            ) {
                val fraction = if (post.totalCount > 0)
                    post.completedCount.toFloat() / post.totalCount else 0f
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(accentColor)
                )
            }
        }
    }
}

@Composable
private fun ProfileCategoryChip(category: String, accentColor: Color) {
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
            text = category,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StatusBadge(status: PostStatus) {
    val (bgColor, textColor) = when (status) {
        PostStatus.COMPLETED        -> Color(0xFFE8F7EF) to Color(0xFF2F9B68)
        PostStatus.IN_PROGRESS -> LightPurple to Purple
        PostStatus.DRAFT     -> Color(0xFFF0EEF5) to Muted
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (status == PostStatus.COMPLETED) {
            ProfileCheckIcon(modifier = Modifier.size(10.dp), color = textColor)
        }
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ProfileCheckIcon(modifier: Modifier = Modifier, color: Color) {
    androidx.compose.foundation.Canvas(modifier) {
        val stroke = 1.8.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.54f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.76f),
            strokeWidth = stroke,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.76f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.28f),
            strokeWidth = stroke,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

// ─── 프로필용 소형 만다라트 그리드 ────────────────────────────────────────────

@Composable
private fun SmallMiniMandala(
    accentColor: Color,
    title: String,
    smallGoals: Map<Int, SmallGoalSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(10.dp))
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                for (col in 0 until 3) {
                    val idx = row * 3 + col
                    val position = idx + 1
                    if (idx == 4) {
                        // 중앙 셀 = 타이틀
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    } else {
                        val goal = smallGoals[position]
                        val bgColor = when {
                            goal == null  -> Color.Transparent
                            goal.isCompleted -> accentColor.copy(alpha = 0.55f)
                            else          -> accentColor.copy(alpha = 0.18f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(bgColor)
                                .border(
                                    1.dp,
                                    if (goal == null) SoftLine else accentColor.copy(alpha = 0.25f),
                                    RoundedCornerShape(5.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (goal != null) {
                                Text(
                                    text = goal.content,
                                    color = if (goal.isCompleted) Color.White else accentColor,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 아이콘 ───────────────────────────────────────────────────────────────────

@Composable
private fun PlusIcon() {
    androidx.compose.foundation.Canvas(Modifier.size(24.dp)) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 2.5.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.2f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.8f),
            strokeWidth = stroke.width,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.5f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.5f),
            strokeWidth = stroke.width,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
private fun BackArrowIcon(color: Color = Ink) {
    androidx.compose.foundation.Canvas(Modifier.size(22.dp)) {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 2.2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.60f, size.height * 0.22f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.50f),
            strokeWidth = stroke.width,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.50f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.60f, size.height * 0.78f),
            strokeWidth = stroke.width,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
