package com.bucket.presentation.ui.detail

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.R
import com.bucket.presentation.theme.BucketappTheme
import com.example.domain.model.user.Author
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.home.component.UserAvatar
import com.bucket.presentation.ui.home.component.categoryAccent
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import androidx.core.graphics.toColorInt

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

// ─── Route ────────────────────────────────────────────────────────────────────

@Composable
fun BucketDetailRoute(
    bucketId: Long,
    author: Author,
    onBackClick: () -> Unit,
    viewModel: BucketDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(bucketId) { viewModel.loadBucketDetail(bucketId, author) }
    val uiState by viewModel.uiState.collectAsState()
    BucketDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLikeClick = { viewModel.toggleLike(bucketId) },
        onSelectMandalaCell = viewModel::selectMandalaCell,
        onDrillDown = viewModel::drillDown,
        onExitDrillDown = viewModel::exitDrillDown,
        onAddPlan = { content, color, sortOrder -> viewModel.addPlan(content, color, sortOrder) },
        onAddSmallGoal = { planId, content, color, isComplete, position -> viewModel.addSmallGoal(planId, content, color, isComplete, position) },
        onUpdateSmallGoal = viewModel::updateSmallGoal,
        onDeleteSmallGoal = viewModel::deleteSmallGoal,
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun BucketDetailScreen(
    uiState: BucketDetailUiState,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit = {},
    onSelectMandalaCell: (SmallGoal) -> Unit = {},
    onDrillDown: (SmallGoal) -> Unit = {},
    onExitDrillDown: () -> Unit = {},
    onAddPlan: (content: String, color: String, sortOrder: Int) -> Unit = { _, _, _ -> },
    onAddSmallGoal: (planId: Long, content: String, color: String, isComplete: Boolean, position: Int) -> Unit = { _, _, _, _, _ -> },
    onUpdateSmallGoal: (planId: Long, goalId: Long, content: String, color: String, isComplete: Boolean) -> Unit = { _, _, _, _, _ -> },
    onDeleteSmallGoal: (planId: Long, goalId: Long) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val bucket = uiState.bucketDetail

    var isEditing by rememberSaveable(bucket?.id, bucket?.isMine) {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val selectedCell = uiState.selectedMandalaCell
    LaunchedEffect(selectedCell) {
        if (selectedCell != null) {
            listState.animateScrollBy(with(density) { 120.dp.toPx() })
        }
    }
    val drillDownPlan = uiState.drillDownPlan
    LaunchedEffect(drillDownPlan) {
        if (drillDownPlan != null) {
            listState.animateScrollBy(with(density) { 160.dp.toPx() })
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = HomeBackground) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars.asPaddingValues().let {
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
                DetailTopBar(onBackClick = onBackClick)
            }
            when {
                uiState.isLoading -> item {
                    Text(text = "불러오는 중...", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                uiState.errorMessage != null -> item {
                    Text(text = uiState.errorMessage, color = Color(0xFFE04D5F), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                bucket != null -> {
                    val accentColor = categoryAccent(bucket.category, bucket.categoryColor)
                    val completedCount = bucket.smallGoals.count { it.isComplete }
                    val progress = if (bucket.smallGoals.isEmpty()) 0f else completedCount.toFloat() / bucket.smallGoals.size

                    item {
                        BucketHeaderCard(
                            bucket = bucket,
                            accentColor = accentColor,
                            completedCount = completedCount,
                            progress = progress,
                            likeCount = uiState.likeCount,
                            isMine = bucket.isMine,
                            isLiked = uiState.isLiked,
                            isEditing = isEditing && bucket.isMine,
                            onLikeClick = onLikeClick,
                            onEditClick = { isEditing = isEditing.not() },
                        )
                    }

                    item {
                        val drillDownPlan = uiState.drillDownPlan
                        if (drillDownPlan != null) {
                            MandalaDetailView(
                                bucketTitle = bucket.title,
                                plan = drillDownPlan,
                                accentColor = accentColor,
                                isMine = bucket.isMine,
                                onExitDrillDown = onExitDrillDown,
                                onAddSmallGoal = { content, color, isComplete, position ->
                                    onAddSmallGoal(drillDownPlan.id, content, color, isComplete, position)
                                },
                                onUpdateSmallGoal = { goalId, content, color, isComplete ->
                                    onUpdateSmallGoal(drillDownPlan.id, goalId, content, color, isComplete)
                                },
                                onDeleteSmallGoal = { goalId ->
                                    onDeleteSmallGoal(drillDownPlan.id, goalId)
                                },
                            )
                        } else {
                            MandalaSection(
                                bucket = bucket,
                                accentColor = accentColor,
                                isMine = bucket.isMine,
                                selectedCell = uiState.selectedMandalaCell,
                                onSelectCell = onSelectMandalaCell,
                                onDrillDown = onDrillDown,
                                onAddPlan = onAddPlan,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(onBackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        CircleIconButton(onClick = onBackClick) { BackIcon(Modifier.size(22.dp)) }
    }
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
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

// ─── Header Card ──────────────────────────────────────────────────────────────

@Composable
private fun BucketHeaderCard(
    bucket: BucketPostDetail,
    accentColor: Color,
    completedCount: Int,
    progress: Float,
    likeCount: Int,
    isMine: Boolean,
    isLiked: Boolean,
    isEditing: Boolean,
    onLikeClick: () -> Unit,
    onEditClick: () -> Unit,
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
            CategoryChip(category = bucket.category, accentColor = accentColor)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMine) {
                    CircleIconButton(onClick = onEditClick) {
                        if (isEditing) {
                            CheckIcon(modifier = Modifier.size(20.dp), color = Color(0xFF2F9B68))
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF6E687D))
                            )
                        }
                    }
                }
                LikeButton(isLiked = isLiked, likeCount = likeCount, onLikeClick = onLikeClick)
            }
        }
        Spacer(Modifier.height(18.dp))
        if (isEditing) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(), singleLine = false,
                textStyle = TextStyle(color = Ink, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold)
            )
        } else {
            Text(text = title, color = Ink, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(14.dp))
        if (isEditing) {
            OutlinedTextField(
                value = memo, onValueChange = { memo = it },
                modifier = Modifier.fillMaxWidth(), minLines = 3,
                textStyle = TextStyle(color = Color(0xFF635E72), fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
            )
        } else {
            Text(text = memo, color = Color(0xFF635E72), fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoPill(label = "시작일", value = bucket.startDate.toKoreanDateText(), modifier = Modifier.weight(1f))
            InfoPill(label = "달성률", value = "$completedCount/${bucket.smallGoals.size}", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        ProgressBar(progress = progress)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                profileImageUrl = bucket.author.profileImgUrl,
                username = bucket.author.username,
                color = accentColor.copy(alpha = 0.18f),
                textColor = accentColor,
                size = 30
            )
            Spacer(Modifier.width(10.dp))
            Text(text = bucket.author.username, color = Color(0xFF6F687E), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Mandala Grid ─────────────────────────────────────────────────────────────
//
// 3×3 배치 (index 기준):
//   [0] [1] [2]      plans[0] plans[1] plans[2]
//   [3] [4] [5]  →   plans[3]  CENTER  plans[4]
//   [6] [7] [8]      plans[5] plans[6] plans[7]
//
// isMine=true  → 셀 탭 → 선택(하단 '만다라트 만들기' 버튼) → 버튼 탭 → 드릴다운
// isMine=false → 셀 탭 → 바로 드릴다운 (읽기 전용)

@Composable
private fun MandalaSection(
    bucket: BucketPostDetail,
    accentColor: Color,
    isMine: Boolean,
    selectedCell: SmallGoal?,
    onSelectCell: (SmallGoal) -> Unit,
    onDrillDown: (SmallGoal) -> Unit,
    onAddPlan: (content: String, color: String, sortOrder: Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "간다라트", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.width(8.dp))
            Text(text = "${bucket.smallGoals.size}/8", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        MandalaGridView(
            bucket = bucket,
            accentColor = accentColor,
            isMine = isMine,
            selectedCell = selectedCell,
            onSelectCell = onSelectCell,
            onDrillDown = onDrillDown,
            onAddPlan = onAddPlan,
        )
    }
}

@Composable
private fun MandalaGridView(
    bucket: BucketPostDetail,
    accentColor: Color,
    isMine: Boolean,
    selectedCell: SmallGoal?,
    onSelectCell: (SmallGoal) -> Unit,
    onDrillDown: (SmallGoal) -> Unit,
    onAddPlan: (content: String, color: String, sortOrder: Int) -> Unit,
) {
    var showAddPlanSheet by remember { mutableStateOf(false) }
    var pendingSortOrder by remember { mutableStateOf(1) }
    val plans = bucket.smallGoals.sortedBy { it.sortOrder }

    // index 0..3 = plan 0..3 / index 4 = center / index 5..8 = plan 4..7
    val cells: List<SmallGoal?> = buildList {
        repeat(4) { add(plans.getOrNull(it)) }
        add(null) // center
        repeat(4) { add(plans.getOrNull(it + 4)) }
    }

    // 그리드 카드 + 선택 바를 하나의 흰색 카드로 통합 (연결된 느낌)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(20.dp))
    ) {
        // 3×3 그리드
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 3) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (col in 0 until 3) {
                        val idx = row * 3 + col
                        val plan = cells[idx]
                        when {
                            idx == 4 -> MandalaCenter(
                                title = bucket.title,
                                label = "큰 목표",
                                accentColor = accentColor,
                                modifier = Modifier.weight(1f)
                            )
                            plan != null -> MandalaPlanCell(
                                plan = plan,
                                isSelected = selectedCell?.id == plan.id,
                                onClick = {
                                    when {
                                        !isMine -> onDrillDown(plan)
                                        plan.todos.isNotEmpty() -> onDrillDown(plan)
                                        else -> onSelectCell(plan)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            else -> {
                                val targetIndex = if (idx < 4) idx else idx - 1
                                MandalaEmptyPlanCell(
                                    isMine = isMine,
                                    onAdd = { pendingSortOrder = targetIndex; showAddPlanSheet = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // isMine=true이고 셀이 선택됐을 때: 구분선 + 선택 표시 + 만다라트 만들기 버튼
        if (isMine && selectedCell != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SoftLine)
            )
            MakeMandalaBar(
                selectedPlan = selectedCell,
                onMakeMandala = { onDrillDown(selectedCell) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            )
        }
    }

    if (showAddPlanSheet) {
        PlanBottomSheet(
            onDismiss = { showAddPlanSheet = false },
            onSave = { content, color ->
                onAddPlan(content, color, pendingSortOrder)
                showAddPlanSheet = false
            }
        )
    }
}

/** 선택된 계획 표시 + 만다라트 만들기 버튼 (카드 내부에 배치) */
@Composable
private fun MakeMandalaBar(
    selectedPlan: SmallGoal,
    onMakeMandala: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellColor = selectedPlan.color.toComposeColor()
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(cellColor))
            Text(
                text = selectedPlan.content,
                color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(12.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Purple)
                .clickable { onMakeMandala() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("⊞", color = Color.White, fontSize = 13.sp)
            Text("만다라트 만들기", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** 만다라트 그리드 중앙 셀 — 큰 목표 */
@Composable
private fun MandalaCenter(title: String, label: String, accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(accentColor)
            .border(2.dp, accentColor, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.28f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(text = label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(
                text = title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center, maxLines = 3, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** 만다라트 핵심 목표 셀 — 선택 시 강조 테두리 */
@Composable
private fun MandalaPlanCell(
    plan: SmallGoal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellColor = plan.color.toComposeColor()
    val borderColor = if (isSelected) cellColor else cellColor.copy(alpha = 0.35f)
    val borderWidth = if (isSelected) 2.dp else 1.5.dp
    val bgColor = if (isSelected) cellColor.copy(alpha = 0.18f) else cellColor.copy(alpha = 0.10f)

    Box(
        modifier = modifier
            .alpha(if (plan.isComplete) 0.45f else 1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(cellColor))
        }
        Text(
            text = plan.content,
            color = Ink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (plan.isComplete) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.align(Alignment.Center).fillMaxWidth()
        )
    }
}

/** 빈 만다라트 슬롯 — 대시 테두리 + 원형 + 버튼 */
@Composable
private fun MandalaEmptyPlanCell(isMine: Boolean, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    MandalaEmptyCell(onClick = if (isMine) onAdd else null, showPlus = isMine, modifier = modifier)
}

// ─── Mandala Drill-Down ───────────────────────────────────────────────────────
//
// 3×3 배치 (index 기준):
//   [0] [1] [2]      goals[0] goals[1] goals[2]
//   [3] [4] [5]  →   goals[3]  CENTER  goals[4]
//   [6] [7] [8]      goals[5] goals[6] goals[7]
//
// isMine=true  → 빈 셀 탭 → 소목표 추가 BottomSheet / 기존 셀 탭 → 편집 BottomSheet
// isMine=false → 읽기 전용, BottomSheet 없음

@Composable
private fun MandalaDetailView(
    bucketTitle: String,
    plan: SmallGoal,
    accentColor: Color,
    isMine: Boolean,
    onExitDrillDown: () -> Unit,
    onAddSmallGoal: (content: String, color: String, isComplete: Boolean, position: Int) -> Unit,
    onUpdateSmallGoal: (goalId: Long, content: String, color: String, isComplete: Boolean) -> Unit,
    onDeleteSmallGoal: (goalId: Long) -> Unit,
) {
    var editTarget by remember { mutableStateOf<Todo?>(null) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var pendingPosition by remember { mutableStateOf(0) }

    val smallGoals = plan.todos.take(8)
    val doneCount = plan.todos.count { it.isComplete }

    // index 0..3 = goal 0..3 / index 4 = center(plan) / index 5..8 = goal 4..7
    val cells: List<Todo?> = buildList {
        repeat(4) { add(smallGoals.getOrNull(it)) }
        add(null) // center
        repeat(4) { add(smallGoals.getOrNull(it + 4)) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 브레드크럼
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = bucketTitle, color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(" › ", color = Muted, fontSize = 13.sp)
            Text(
                text = plan.content, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }

        // 제목 + 달성률
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "'${plan.content}'의 만다라트",
                color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$doneCount/${plan.todos.size}",
                color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
        }

        // 3×3 그리드
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, SoftLine, RoundedCornerShape(20.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 3) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (col in 0 until 3) {
                        val idx = row * 3 + col
                        val goal = cells[idx]
                        when {
                            idx == 4 -> MandalaCenter(
                                title = plan.content,
                                label = "작은 목표",
                                accentColor = accentColor,
                                modifier = Modifier.weight(1f)
                            )
                            goal != null -> MandalaSmallGoalCell(
                                goal = goal,
                                onClick = {
                                    if (isMine) {
                                        editTarget = goal
                                        showEditSheet = true
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            else -> {
                                val position = if (idx < 4) idx else idx - 1
                                MandalaEmptySmallGoalCell(
                                    onClick = { if (isMine) { pendingPosition = position; showAddSheet = true } },
                                    isMine = isMine,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 큰 목표로 돌아가기
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, SoftLine, RoundedCornerShape(16.dp))
                .clickable { onExitDrillDown() }
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                MiniMandalaIcon(accentColor = accentColor)
                Column {
                    Text(text = "큰 목표로 돌아가기", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = bucketTitle, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(text = "›", color = Muted, fontSize = 20.sp)
        }
    }

    // 기존 소목표 편집 (isMine=true만 열림)
    if (showEditSheet && editTarget != null) {
        SmallGoalBottomSheet(
            existingGoal = editTarget,
            onDismiss = { showEditSheet = false; editTarget = null },
            onSave = { content, color, isComplete ->
                onUpdateSmallGoal(editTarget!!.id, content, color, isComplete)
                showEditSheet = false; editTarget = null
            },
            onDelete = {
                onDeleteSmallGoal(editTarget!!.id)
                showEditSheet = false; editTarget = null
            }
        )
    }

    // 새 소목표 추가 (isMine=true만 열림)
    if (showAddSheet) {
        SmallGoalBottomSheet(
            existingGoal = null,
            onDismiss = { showAddSheet = false },
            onSave = { content, color, isComplete ->
                onAddSmallGoal(content, color, isComplete, pendingPosition)
                showAddSheet = false
            },
            onDelete = {}
        )
    }
}

@Composable
private fun MandalaSmallGoalCell(goal: Todo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val cellColor = goal.color.toComposeColor()
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(cellColor.copy(alpha = 0.10f))
            .border(1.5.dp, cellColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(cellColor))
            if (goal.isComplete) {
                Box(
                    modifier = Modifier.size(14.dp).clip(CircleShape).background(cellColor),
                    contentAlignment = Alignment.Center
                ) { Text("✓", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Text(
            text = goal.content, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, maxLines = 3, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        )
    }
}

/** 빈 소목표 슬롯 — isMine=true면 탭 가능, false면 비활성 */
@Composable
private fun MandalaEmptySmallGoalCell(onClick: () -> Unit, isMine: Boolean, modifier: Modifier = Modifier) {
    MandalaEmptyCell(
        onClick = if (isMine) onClick else null,
        modifier = modifier
    )
}

/**
 * 공용 빈 셀 — 대시 테두리 + 원형 + 버튼
 * onClick=null 이면 비클릭/비활성 스타일
 */
@Composable
private fun MandalaEmptyCell(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    showPlus: Boolean = onClick != null,
) {
    val isActive = onClick != null
    val activeOnClick = onClick
    val dashColor = if (isActive) Color(0xFFD8CEF2) else Color(0xFFE2DDEA)
    val contentColor = if (isActive) Color(0xFF8F899D) else Color(0xFFC7C1D2)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .then(if (activeOnClick != null) Modifier.clickable(onClick = activeOnClick) else Modifier)
            .background(Color(0xFFFCFAFF))
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    size = size,
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(7.dp.toPx(), 5.dp.toPx())
                        )
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (showPlus) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, dashColor.copy(alpha = 0.72f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = contentColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

/** 큰 목표로 돌아가기 카드의 미니 9칸 아이콘 */
@Composable
private fun MiniMandalaIcon(accentColor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (r in 0 until 3) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (c in 0 until 3) {
                        val isCenter = r == 1 && c == 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isCenter) accentColor else accentColor.copy(alpha = 0.25f))
                        )
                    }
                }
            }
        }
    }
}

// ─── Plan BottomSheet ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanBottomSheet(
    onDismiss: () -> Unit,
    onSave: (content: String, color: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    val canSave = name.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "목표 추가",
                color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
            Column(verticalArrangement = Arrangement.spacedBy(66.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("제목", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicTextField(
                        value = name,
                        onValueChange = { if (it.length <= 15) name = it },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                                if (name.isEmpty()) {
                                    Text(
                                        text = "목표를 입력해 주세요",
                                        color = Color(0xFFB0AABC),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF4A4652)))
                    Text(
                        text = "${name.length} / 16",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = Color(0xFF4A4652),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (canSave) Purple else Color(0xFFD7DAE0))
                    .clickable(enabled = canSave) { onSave(name.trim(), MANDALA_COLORS[0]) },
                contentAlignment = Alignment.Center
            ) {
                Text("저장", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// ─── Small Goal BottomSheet ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallGoalBottomSheet(
    existingGoal: Todo?,
    onDismiss: () -> Unit,
    onSave: (content: String, color: String, isComplete: Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(existingGoal?.content ?: "") }
    val selectedColor = existingGoal?.color ?: MANDALA_COLORS[0]
    var isComplete by remember { mutableStateOf(existingGoal?.isComplete ?: false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val canSave = name.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "계획",
                color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )

            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(66.dp)) {
                    Text("이름", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicTextField(
                            value = name,
                            onValueChange = { if (it.length <= 20) name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Ink,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (name.isEmpty()) {
                                        Text(
                                            text = "작은 목표를 입력해 주세요",
                                            color = Color(0xFFB0AABC),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF4A4652))
                        )
                        Text(
                            text = "${name.length} / 20",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = Color(0xFF4A4652),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("달성 상태", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isComplete) "달성" else "미달성",
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isComplete,
                            onCheckedChange = { isComplete = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Purple,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD7DAE0),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (canSave) Purple else Color(0xFFD7DAE0))
                    .clickable(enabled = canSave) { onSave(name.trim(), selectedColor, isComplete) },
                contentAlignment = Alignment.Center
            ) {
                Text("저장", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            if (existingGoal != null) {
                Text(
                    text = "삭제",
                    color = Color(0xFFE04D5F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDeleteConfirm = true }
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(10.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("목표 삭제", fontWeight = FontWeight.ExtraBold) },
            text = { Text("이 목표를 삭제할까요? 삭제 후 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("삭제", color = Color(0xFFE04D5F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("취소", color = Ink)
                }
            }
        )
    }
}

// ─── Shared composables ───────────────────────────────────────────────────────

@Composable
private fun CategoryChip(category: String, accentColor: Color) {
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
private fun LikeButton(isLiked: Boolean, likeCount: Int, onLikeClick: () -> Unit) {
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
private fun InfoPill(label: String, value: String, modifier: Modifier = Modifier) {
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
private fun ProgressBar(progress: Float) {
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
private fun BackIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(Ink, Offset(size.width * 0.62f, size.height * 0.2f), Offset(size.width * 0.34f, size.height * 0.5f), stroke, StrokeCap.Round)
        drawLine(Ink, Offset(size.width * 0.34f, size.height * 0.5f), Offset(size.width * 0.62f, size.height * 0.8f), stroke, StrokeCap.Round)
    }
}

@Composable
private fun CheckIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        drawLine(color, Offset(size.width * 0.20f, size.height * 0.52f), Offset(size.width * 0.42f, size.height * 0.73f), 2.6.dp.toPx(), StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.42f, size.height * 0.73f), Offset(size.width * 0.82f, size.height * 0.28f), 2.6.dp.toPx(), StrokeCap.Round)
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun String.toKoreanDateText(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    val month = parts[1].toIntOrNull() ?: return this
    val day = parts[2].toIntOrNull() ?: return this
    return "${parts[0]}년 ${month}월 ${day}일"
}

@SuppressLint("UseKtx")
fun String.toComposeColor(): Color = try {
    Color(this.toColorInt())
} catch (e: Exception) {
    Color(0xFF8D6BE8)
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun MandalaGridPreview() {
    val plans = listOf(
        SmallGoal(id = 1, sortOrder = 1, content = "면접 준비", isComplete = true, color = "#8D6BE8",
            todos = listOf(Todo(11, "모의면접", "#8D6BE8", true))),
        SmallGoal(id = 2, sortOrder = 2, content = "자기소개서", isComplete = false, color = "#E8736B"),
        SmallGoal(id = 3, sortOrder = 3, content = "포트폴리오", isComplete = false, color = "#E8A06B"),
        SmallGoal(id = 4, sortOrder = 4, content = "인적성", isComplete = false, color = "#6BE88D"),
        SmallGoal(id = 5, sortOrder = 5, content = "CS스터디", isComplete = true, color = "#E8C96B"),
        SmallGoal(id = 6, sortOrder = 6, content = "체력관리", isComplete = false, color = "#6BB8E8"),
        SmallGoal(id = 7, sortOrder = 7, content = "어학성적", isComplete = false, color = "#6BE8D4"),
        SmallGoal(id = 8, sortOrder = 8, content = "코딩테스트", isComplete = false, color = "#E86BB8"),
    )
    BucketappTheme(dynamicColor = false) {
        BucketDetailScreen(
            uiState = BucketDetailUiState(
                bucketDetail = BucketPostDetail(
                    id = 13, title = "대기업 입사",
                    memo = "내년 상반기 공채 합격이 목표.",
                    category = "학습", categoryColor = "#8D6BE8",
                    likeCount = 243, startDate = "2026-05-13",
                    author = Author(userId = 2, username = "jiwon", profileImgUrl = ""),
                    smallGoals = plans, isLiked = true, isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun MandalaGridSelectedPreview() {
    val plans = listOf(
        SmallGoal(id = 1, sortOrder = 1, content = "면접 준비", isComplete = true, color = "#8D6BE8",
            todos = listOf(Todo(11, "모의면접", "#8D6BE8", true))),
        SmallGoal(id = 2, sortOrder = 2, content = "자기소개서", isComplete = false, color = "#E8736B"),
        SmallGoal(id = 3, sortOrder = 3, content = "포트폴리오", isComplete = false, color = "#E8A06B"),
    )
    BucketappTheme(dynamicColor = false) {
        BucketDetailScreen(
            uiState = BucketDetailUiState(
                selectedMandalaCell = plans[0],
                bucketDetail = BucketPostDetail(
                    id = 13, title = "대기업 입사", memo = "내년 상반기 공채 합격이 목표.",
                    category = "학습", categoryColor = "#8D6BE8",
                    likeCount = 243, startDate = "2026-05-13",
                    author = Author(userId = 2, username = "jiwon", profileImgUrl = ""),
                    smallGoals = plans, isLiked = true, isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun MandalaDetailPreview() {
    val plan = SmallGoal(
        id = 1, sortOrder = 1, content = "면접 준비", isComplete = true, color = "#8D6BE8",
        todos = listOf(
            Todo(11, "모의면접", "#E8736B", true),
            Todo(12, "자기 PR 정리", "#8D6BE8", true),
            Todo(13, "꼬리질문 대비", "#6BE88D", false),
            Todo(14, "AI 면접", "#6BB8E8", false),
            Todo(15, "면접 복장", "#E8A06B", false),
        )
    )
    BucketappTheme(dynamicColor = false) {
        BucketDetailScreen(
            uiState = BucketDetailUiState(
                drillDownPlan = plan,
                bucketDetail = BucketPostDetail(
                    id = 13, title = "대기업 입사", memo = "",
                    category = "학습", categoryColor = "#8D6BE8",
                    likeCount = 243, startDate = "2026-05-13",
                    author = Author(userId = 2, username = "jiwon", profileImgUrl = ""),
                    smallGoals = listOf(plan), isLiked = true, isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}
