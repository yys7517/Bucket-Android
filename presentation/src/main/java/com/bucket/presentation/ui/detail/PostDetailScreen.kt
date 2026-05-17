package com.bucket.presentation.ui.detail

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.unit.Dp
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
import com.example.domain.model.post.PostDetail
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
fun PostDetailRoute(
    postId: Long,
    author: Author,
    onBackClick: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(postId) { viewModel.loadPostDetail(postId, author) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PostDetailEvent.PostUpdated -> {
                    Toast.makeText(context, "수정되었습니다", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.PostDeleted -> {
                    Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
                is PostDetailEvent.PlanUpdated -> {
                    Toast.makeText(context, "수정되었습니다", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    PostDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLikeClick = { viewModel.toggleLike(postId) },
        onSelectMandalaCell = viewModel::selectMandalaCell,
        onDismissMandalaModal = viewModel::dismissMandalaModal,
        onAddPlan = { content, color, isComplete, sortOrder -> viewModel.addPlan(content, color, isComplete, sortOrder) },
        onUpdatePlan = { smallGoalId, content, color, isComplete -> viewModel.updatePlan(smallGoalId, content, color, isComplete) },
        onDeletePlan = { smallGoalId -> viewModel.deletePlan(smallGoalId) },
        onAddSmallGoal = { planId, content, color, isComplete, position -> viewModel.addSmallGoal(planId, content, color, isComplete, position) },
        onUpdateSmallGoal = viewModel::updateSmallGoal,
        onDeleteSmallGoal = viewModel::deleteSmallGoal,
        onUpdatePost = { title, startDate, memo -> viewModel.updatePost(title, startDate, memo) },
        onDeletePost = { viewModel.deletePost() },
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit = {},
    onSelectMandalaCell: (SmallGoal) -> Unit = {},
    onDismissMandalaModal: () -> Unit = {},
    onAddPlan: (content: String, color: String, isComplete: Boolean, sortOrder: Int) -> Unit = { _, _, _, _ -> },
    onUpdatePlan: (smallGoalId: Long, content: String, color: String, isComplete: Boolean) -> Unit = { _, _, _, _ -> },
    onDeletePlan: (smallGoalId: Long) -> Unit = {},
    onAddSmallGoal: (planId: Long, content: String, color: String, isComplete: Boolean, position: Int) -> Unit = { _, _, _, _, _ -> },
    onUpdateSmallGoal: (planId: Long, goalId: Long, content: String, color: String, isComplete: Boolean) -> Unit = { _, _, _, _, _ -> },
    onDeleteSmallGoal: (planId: Long, goalId: Long) -> Unit = { _, _ -> },
    onUpdatePost: (title: String, startDate: String, memo: String) -> Unit = { _, _, _ -> },
    onDeletePost: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val post = uiState.postDetail
    var showEditSheet by rememberSaveable(post?.id, post?.isMine) { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize(), color = HomeBackground) {
        LazyColumn(
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
            item { DetailTopBar(onBackClick = onBackClick) }
            when {
                uiState.isLoading -> item {
                    Text(text = "불러오는 중...", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                uiState.errorMessage != null -> item {
                    Text(text = uiState.errorMessage, color = Color(0xFFE04D5F), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                post != null -> {
                    val accentColor = categoryAccent(post.category, post.categoryColor)
                    val completedCount = post.smallGoals.count { it.isComplete }
                    val progress = if (post.smallGoals.isEmpty()) 0f else completedCount.toFloat() / post.smallGoals.size

                    item {
                        PostHeaderCard(
                            post = post,
                            accentColor = accentColor,
                            completedCount = completedCount,
                            progress = progress,
                            likeCount = uiState.likeCount,
                            isMine = post.isMine,
                            isLiked = uiState.isLiked,
                            onLikeClick = onLikeClick,
                            onEditClick = { showEditSheet = true },
                        )
                    }
                    item {
                        MandalaSection(
                            post = post,
                            accentColor = accentColor,
                            isMine = post.isMine,
                            onSelectCell = onSelectMandalaCell,
                            onAddPlan = onAddPlan,
                        )
                    }
                }
            }
        }
    }

    // ── 만다라트 모달 (셀 탭 시)
    val selectedCell = uiState.selectedMandalaCell
    if (selectedCell != null && post != null) {
        MandalaModal(
            plan = selectedCell,
            postTitle = post.title,
            accentColor = categoryAccent(post.category, post.categoryColor),
            isMine = post.isMine,
            usedSmallGoalColors = post.smallGoals.map { it.color }.toSet(),
            onDismiss = onDismissMandalaModal,
            onUpdatePlan = { content, color, isComplete ->
                onUpdatePlan(selectedCell.id, content, color, isComplete)
            },
            onDeletePlan = { onDeletePlan(selectedCell.id) },
            onAddTodo = { content, color, isComplete, position ->
                onAddSmallGoal(selectedCell.id, content, color, isComplete, position)
            },
            onUpdateTodo = { goalId, content, color, isComplete ->
                onUpdateSmallGoal(selectedCell.id, goalId, content, color, isComplete)
            },
            onDeleteTodo = { goalId -> onDeleteSmallGoal(selectedCell.id, goalId) },
        )
    }

    // ── 게시글 편집 바텀시트 (isMine=true 전용)
    if (showEditSheet && post != null && post.isMine) {
        PostEditBottomSheet(
            post = post,
            onDismiss = { showEditSheet = false },
            onSave = { title, startDate, memo ->
                onUpdatePost(title, startDate, memo)
                showEditSheet = false
            },
            onDelete = {
                onDeletePost()
                showEditSheet = false
            },
        )
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
private fun PostHeaderCard(
    post: PostDetail,
    accentColor: Color,
    completedCount: Int,
    progress: Float,
    likeCount: Int,
    isMine: Boolean,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onEditClick: () -> Unit,
) {
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
            CategoryChip(category = post.category, accentColor = accentColor)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMine) {
                    CircleIconButton(onClick = onEditClick) {
                        Image(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(Color(0xFF6E687D))
                        )
                    }
                }
                LikeButton(isLiked = isLiked, likeCount = likeCount, onLikeClick = onLikeClick)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = post.title, color = Ink,
            fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = post.memo, color = Color(0xFF635E72),
            fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoPill(label = "시작일", value = post.startDate.toKoreanDateText(), modifier = Modifier.weight(1f))
            InfoPill(label = "달성률", value = "$completedCount/${post.smallGoals.size}", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        ProgressBar(progress = progress)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                profileImageUrl = post.author.profileImgUrl,
                username = post.author.username,
                color = accentColor.copy(alpha = 0.18f),
                textColor = accentColor,
                size = 30
            )
            Spacer(Modifier.width(10.dp))
            Text(text = post.author.username, color = Color(0xFF6F687E), fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
    post: PostDetail,
    accentColor: Color,
    isMine: Boolean,
    onSelectCell: (SmallGoal) -> Unit,
    onAddPlan: (content: String, color: String, isComplete: Boolean, sortOrder: Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "간다라트", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.width(8.dp))
            Text(text = "${post.smallGoals.size}/8", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        MandalaGridView(
            post = post,
            accentColor = accentColor,
            isMine = isMine,
            onSelectCell = onSelectCell,
            onAddPlan = onAddPlan,
        )
    }
}

@Composable
private fun MandalaGridView(
    post: PostDetail,
    accentColor: Color,
    isMine: Boolean,
    onSelectCell: (SmallGoal) -> Unit,
    onAddPlan: (content: String, color: String, isComplete: Boolean, sortOrder: Int) -> Unit,
) {
    var showAddPlanSheet by remember { mutableStateOf(false) }
    var pendingSortOrder by remember { mutableStateOf(1) }
    val plansByPosition = post.smallGoals
        .sortedBy { it.sortOrder }
        .mapIndexed { index, plan ->
            val slot = plan.sortOrder.asOuterMandalaPositionOrFallback(index)
            slot to plan
        }
        .toMap()

    // 이미 사용된 색상 목록 (작은 목표들의 색상)
    val usedPlanColors = remember(post.smallGoals) {
        post.smallGoals.map { it.color }.toSet()
    }

    val cells: List<SmallGoal?> = buildList {
        repeat(9) { idx ->
            add(mandalaGridPosition(idx)?.let { plansByPosition[it] })
        }
    }

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
                    val plan = cells[idx]
                    when {
                        idx == 4 -> MandalaCenter(
                            title = post.title,
                            label = "큰 목표",
                            accentColor = accentColor,
                            modifier = Modifier.weight(1f)
                        )
                        plan != null -> MandalaPlanCell(
                            plan = plan,
                            isSelected = false,
                            onClick = { onSelectCell(plan) },
                            modifier = Modifier.weight(1f)
                        )
                        else -> {
                            val targetSortOrder = mandalaGridPosition(idx) ?: 1
                            MandalaEmptyPlanCell(
                                isMine = isMine,
                                onAdd = { pendingSortOrder = targetSortOrder; showAddPlanSheet = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddPlanSheet) {
        PlanBottomSheet(
            existingPlan = null,
            usedColors = usedPlanColors,
            onDismiss = { showAddPlanSheet = false },
            onSave = { content, color, isComplete ->
                onAddPlan(content, color, isComplete, pendingSortOrder)
                showAddPlanSheet = false
            },
            onDelete = {},
        )
    }
}

private fun mandalaGridPosition(index: Int): Int? = when (index) {
    in 0..8 -> index + 1
    else -> null
}?.takeUnless { it == 5 }

private fun Int.asOuterMandalaPositionOrFallback(fallbackIndex: Int): Int =
    takeIf { it in 1..9 && it != 5 } ?: fallbackIndex.toOuterMandalaPosition()

private fun Int.toOuterMandalaPosition(): Int = if (this < 4) this + 1 else this + 2


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

// ─── Mandala Modal ────────────────────────────────────────────────────────────
//
// 셀 탭 시 ModalBottomSheet로 해당 SmallGoal의 만다라트(3×3 todo 그리드)를 표시.
// isMine=true → 셀 편집/삭제 + todo 추가/편집/삭제 가능.
// isMine=false → 읽기 전용.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MandalaModal(
    plan: SmallGoal,
    postTitle: String,
    accentColor: Color,
    isMine: Boolean,
    usedSmallGoalColors: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onUpdatePlan: (content: String, color: String, isComplete: Boolean) -> Unit,
    onDeletePlan: () -> Unit,
    onAddTodo: (content: String, color: String, isComplete: Boolean, position: Int) -> Unit,
    onUpdateTodo: (goalId: Long, content: String, color: String, isComplete: Boolean) -> Unit,
    onDeleteTodo: (goalId: Long) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editTarget by remember { mutableStateOf<Todo?>(null) }
    var showTodoSheet by remember { mutableStateOf(false) }
    var pendingPosition by remember { mutableStateOf(0) }
    var showEditPlanSheet by remember { mutableStateOf(false) }
    var showDeletePlanConfirm by remember { mutableStateOf(false) }

    val todosByPosition = plan.todos
        .sortedBy { it.position }
        .mapIndexed { index, todo ->
            val slot = todo.position.asOuterMandalaPositionOrFallback(index)
            slot to todo
        }
        .toMap()
    val doneCount = plan.todos.count { it.isComplete }
    val totalCount = plan.todos.size
    val progressFraction = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f
    val progressPercent = (progressFraction * 100).toInt()

    // 이미 사용된 색상 목록 (할 일들의 색상)
    val usedTodoColors = remember(plan.todos) {
        plan.todos.map { it.color }.toSet()
    }

    val cells: List<Todo?> = buildList {
        repeat(9) { idx ->
            add(mandalaGridPosition(idx)?.let { todosByPosition[it] })
        }
    }

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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── 헤더 행: 작은 목표 chip + 편집/삭제/닫기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 작은 목표 칩
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(accentColor.copy(alpha = 0.13f))
                        .border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(accentColor))
                    Spacer(Modifier.width(6.dp))
                    Text(text = "작은 목표", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                // 버튼 그룹
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isMine) {
                        CircleIconButton(onClick = { showEditPlanSheet = true }) {
                            Image(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "편집",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF6E687D))
                            )
                        }
                        CircleIconButton(onClick = { showDeletePlanConfirm = true }) {
                            // 휴지통 아이콘 (Canvas로 그리기)
                            TrashIcon(modifier = Modifier.size(18.dp), color = Color(0xFFE04D5F))
                        }
                    }
                    // 닫기 버튼 (항상 표시)
                    CircleIconButton(onClick = onDismiss) {
                        CloseIcon(modifier = Modifier.size(16.dp), color = Color(0xFF6E687D))
                    }
                }
            }

            // ── 모달 제목
            Text(
                text = "'${plan.content}'의 만다라트",
                color = Ink, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )

            // ── 진행 바 행: "할 일 X/Y [bar] X%"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("할 일 ", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("$doneCount", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    Text("/$totalCount", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(accentColor.copy(alpha = 0.14f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                            .height(6.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(accentColor)
                    )
                }
                Text(
                    text = "$progressPercent%",
                    color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
            }

            // ── 3×3 todo 그리드
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFAF8FE))
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
                                        if (isMine) { editTarget = goal; showTodoSheet = true }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                else -> {
                                    val position = mandalaGridPosition(idx) ?: 1
                                    MandalaEmptySmallGoalCell(
                                        onClick = { if (isMine) { pendingPosition = position; editTarget = null; showTodoSheet = true } },
                                        isMine = isMine,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── 힌트 텍스트 (isMine일 때만)
            if (isMine) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+ 빈 칸을 눌러 할 일을 추가할 수 있어요",
                        color = Muted.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }

    // todo 추가/편집 시트
    if (showTodoSheet) {
        SmallGoalBottomSheet(
            existingGoal = editTarget,
            planName = plan.content,
            planColor = plan.color,
            usedColors = if (editTarget != null) {
                usedTodoColors - editTarget!!.color
            } else {
                usedTodoColors
            },
            onDismiss = { showTodoSheet = false; editTarget = null },
            onSave = { content, color, isComplete ->
                val target = editTarget
                if (target != null) onUpdateTodo(target.id, content, color, isComplete)
                else onAddTodo(content, color, isComplete, pendingPosition)
                showTodoSheet = false; editTarget = null
            },
            onDelete = {
                editTarget?.let { onDeleteTodo(it.id) }
                showTodoSheet = false; editTarget = null
            }
        )
    }

    // SmallGoal(외곽 셀) 편집 시트
    if (showEditPlanSheet) {
        PlanBottomSheet(
            existingPlan = plan,
            usedColors = usedSmallGoalColors - plan.color, // 현재 목표의 색상 제외
            onDismiss = { showEditPlanSheet = false },
            onSave = { content, color, isComplete ->
                onUpdatePlan(content, color, isComplete)
                showEditPlanSheet = false
            },
            onDelete = { showDeletePlanConfirm = true; showEditPlanSheet = false },
        )
    }

    // SmallGoal 삭제 확인 다이얼로그
    if (showDeletePlanConfirm) {
        AlertDialog(
            onDismissRequest = { showDeletePlanConfirm = false },
            title = { Text("목표 삭제", fontWeight = FontWeight.ExtraBold) },
            text = { Text("'${plan.content}'을 삭제할까요? 삭제 후 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = { showDeletePlanConfirm = false; onDeletePlan(); onDismiss() }) {
                    Text("삭제", color = Color(0xFFE04D5F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlanConfirm = false }) {
                    Text("취소", color = Ink)
                }
            }
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


// ─── Color Picker ─────────────────────────────────────────────────────────────
//
// MANDALA_COLORS 8가지를 2×4 그리드로 보여주고, 선택된 색에는 outer ring 표시.
// usedColors 에 포함된 색상에만 체크 표시 (이미 사용된 색상만 체크).

@Composable
private fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    usedColors: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
    colors: List<String> = MANDALA_COLORS,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 2행 × 4열
        for (rowIdx in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (colIdx in 0 until 4) {
                    val idx = rowIdx * 4 + colIdx
                    val hex = colors.getOrNull(idx)
                    if (hex != null) {
                        ColorSwatch(
                            color = hex,
                            isSelected = hex == selectedColor,
                            isUsed = hex in usedColors,
                            onClick = { onColorSelected(hex) },
                        )
                    } else {
                        Spacer(Modifier.size(54.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: String,
    isSelected: Boolean,
    isUsed: Boolean,
    onClick: () -> Unit,
) {
    val composeColor = color.toComposeColor()
    val outerSize = 54.dp
    val innerSize = if (isSelected) 40.dp else 48.dp

    Box(
        modifier = Modifier
            .size(outerSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.5.dp, composeColor, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(composeColor),
            contentAlignment = Alignment.Center
        ) {
            // 이미 사용된 색상에만 체크 표시
            if (isUsed) {
                CheckIcon(modifier = Modifier.size(20.dp), color = Color.White)
            }
        }
    }
}

// ─── Plan BottomSheet ─────────────────────────────────────────────────────────
// 작은 목표 추가 / 수정 바텀 시트

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanBottomSheet(
    existingPlan: SmallGoal?,
    usedColors: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onSave: (content: String, color: String, isComplete: Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(existingPlan?.content ?: "") }
    var selectedColor by remember { mutableStateOf(existingPlan?.color ?: MANDALA_COLORS[0]) }
    var isComplete by remember { mutableStateOf(existingPlan?.isComplete ?: false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val canSave = name.isNotBlank()
    val maxLength = 20

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
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // ── 타이틀
            Text(
                text = if (existingPlan != null) "작은 목표 수정" else "작은 목표 추가",
                color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )

            // ── 이름 입력
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("이름", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                BasicTextField(
                    value = name,
                    onValueChange = { if (it.length <= maxLength) name = it },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            if (name.isEmpty()) {
                                Text(
                                    text = "이름을 입력해 주세요",
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
                        .background(Purple.copy(alpha = 0.4f))
                )
                Text(
                    text = "${name.length} / $maxLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    color = Muted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── 색상 선택
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("색상", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it },
                    usedColors = usedColors,
                )
            }

            // ── 완성 여부
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFAF8FE))
                    .border(1.dp, SoftLine, RoundedCornerShape(14.dp))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("완성 여부", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = if (isComplete) "달성률에 반영되었어요" else "완료했으면 켜주세요",
                        color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                    )
                }
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

            Spacer(Modifier.height(4.dp))

            // ── 하단 버튼: [삭제] [완료]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (existingPlan != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFE4E2EB))
                            .clickable { showDeleteConfirm = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("삭제", color = Color(0xFF6E687D), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(if (existingPlan != null) 1.6f else 1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (canSave) Purple else Color(0xFFD7DAE0))
                        .clickable(enabled = canSave) { onSave(name.trim(), selectedColor, isComplete) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("완료", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
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

// ─── Small Goal BottomSheet ───────────────────────────────────────────────────
// 할 일 추가 / 수정 바텀 시트
// planName, planColor: 상단 칩에 표시할 작은 목표 이름/색상
// usedColors: 이미 사용된 색상 집합 (해당 색에만 체크 표시)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallGoalBottomSheet(
    existingGoal: Todo?,
    planName: String = "",
    planColor: String = MANDALA_COLORS[0],
    usedColors: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onSave: (content: String, color: String, isComplete: Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(existingGoal?.content ?: "") }
    var selectedColor by remember { mutableStateOf(existingGoal?.color ?: MANDALA_COLORS[0]) }
    var isComplete by remember { mutableStateOf(existingGoal?.isComplete ?: false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val canSave = name.isNotBlank()
    val maxLength = 20
    val planAccentColor = planColor.toComposeColor()

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
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // ── 헤더: 작은 목표 칩 (좌) + 타이틀 (중앙)
            Box(modifier = Modifier.fillMaxWidth()) {
                // 작은 목표 이름 칩
                if (planName.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(50.dp))
                            .background(planAccentColor.copy(alpha = 0.13f))
                            .border(1.dp, planAccentColor.copy(alpha = 0.18f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(planAccentColor))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = planName,
                            color = planAccentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // 타이틀
                Text(
                    text = if (existingGoal != null) "할 일 수정" else "할 일 추가",
                    color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── 이름 입력
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("이름", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                BasicTextField(
                    value = name,
                    onValueChange = { if (it.length <= maxLength) name = it },
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
                                    text = "이름을 입력해 주세요",
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
                        .background(Purple.copy(alpha = 0.4f))
                )
                Text(
                    text = "${name.length} / $maxLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    color = Muted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── 색상 선택
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("색상", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it },
                    usedColors = usedColors,
                )
            }

            // ── 완성 여부
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFAF8FE))
                    .border(1.dp, SoftLine, RoundedCornerShape(14.dp))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("완성 여부", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = if (isComplete) "달성률에 반영되었어요" else "완료했으면 켜주세요",
                        color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                    )
                }
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

            Spacer(Modifier.height(4.dp))

            // ── 하단 버튼: [삭제] [입력]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (existingGoal != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFE4E2EB))
                            .clickable { showDeleteConfirm = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("삭제", color = Color(0xFF6E687D), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(if (existingGoal != null) 1.6f else 1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (canSave) Purple else Color(0xFFD7DAE0))
                        .clickable(enabled = canSave) { onSave(name.trim(), selectedColor, isComplete) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("입력", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("할 일 삭제", fontWeight = FontWeight.ExtraBold) },
            text = { Text("이 할 일을 삭제할까요? 삭제 후 복구할 수 없습니다.") },
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

// ─── Post Edit BottomSheet ──────────────────────────────────────────────────
//
// 편집 버튼 클릭 시 노출. 목표 이름(필수), 시작일(휠 선택), 메모, 삭제/완료 버튼.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostEditBottomSheet(
    post: PostDetail,
    onDismiss: () -> Unit,
    onSave: (title: String, startDate: String, memo: String) -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(post.title) }
    var memo by remember { mutableStateOf(post.memo) }
    val parsedInitial = parseIsoDateOrNull(post.startDate)
    var pickedYear by remember { mutableStateOf(parsedInitial?.first) }
    var pickedMonth by remember { mutableStateOf(parsedInitial?.second) }
    var pickedDay by remember { mutableStateOf(parsedInitial?.third) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val canSave = title.isNotBlank()

    val composedDate: String? = remember(pickedYear, pickedMonth, pickedDay) {
        val y = pickedYear; val m = pickedMonth; val d = pickedDay
        if (y != null && m != null && d != null) {
            "%04d-%02d-%02d".format(y, m, d)
        } else null
    }

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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 제목
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "모 수정",
                    color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Center)
                )
                CloseIcon(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.CenterEnd)
                        .clickable { onDismiss() },
                    color = Ink
                )
            }

            // 목표 이름 (필수)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "목표 이름 (필수)",
                    color = Muted, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold
                )
                BasicTextField(
                    value = title,
                    onValueChange = { if (it.length <= 20) title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            if (title.isEmpty()) {
                                Text(
                                    text = "목표를 입력해 주세요",
                                    color = Color(0xFFB0AABC),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SoftLine))
            }

            // 시작일 (선택)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "시작일 (선택)",
                    color = Muted, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = !showDatePicker }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = composedDate?.let { displayKoreanDate(it) } ?: "시작일을 선택해주세요",
                        color = if (composedDate == null) Color(0xFFB0AABC) else Ink,
                        fontSize = 17.sp,
                        fontWeight = if (composedDate == null) FontWeight.SemiBold else FontWeight.Bold
                    )
                    Text(text = if (showDatePicker) "˅" else "›", color = Muted, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SoftLine))

                if (showDatePicker) {
                    Spacer(Modifier.height(4.dp))
                    WheelDatePicker(
                        year = pickedYear ?: 2026,
                        month = pickedMonth ?: 5,
                        day = pickedDay ?: 16,
                        onChange = { y, m, d ->
                            pickedYear = y; pickedMonth = m; pickedDay = d
                        },
                        onReset = {
                            pickedYear = null; pickedMonth = null; pickedDay = null
                        },
                        onConfirm = { showDatePicker = false },
                    )
                }
            }

            // 메모 (선택)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "메모 (선택)",
                    color = Muted, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold
                )
                BasicTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    singleLine = false,
                    textStyle = TextStyle(color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            if (memo.isEmpty()) {
                                Text(
                                    text = "메모를 입력해주세요",
                                    color = Color(0xFFB0AABC),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SoftLine))
            }

            Spacer(Modifier.height(8.dp))

            // 삭제 / 완료
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFFE4E2EB))
                        .clickable { showDeleteConfirm = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("삭제", color = Color(0xFF6E687D), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(
                    modifier = Modifier
                        .weight(1.6f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (canSave) Ink else Color(0xFFD7DAE0))
                        .clickable(enabled = canSave) {
                            onSave(title.trim(), composedDate ?: post.startDate, memo)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("완료", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("태스크 삭제", fontWeight = FontWeight.ExtraBold) },
            text = { Text("이 태스크를 삭제할까요? 삭제 후 복구할 수 없습니다.") },
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

// ─── Wheel Date Picker ────────────────────────────────────────────────────────

@Composable
private fun WheelDatePicker(
    year: Int,
    month: Int,
    day: Int,
    onChange: (year: Int, month: Int, day: Int) -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
) {
    val years = remember { (1970..2100).toList() }
    val months = remember { (1..12).toList() }
    val days = remember(year, month) { (1..daysInMonth(year, month)).toList() }

    val rowHeight = 40.dp
    val visibleCount = 5

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 초기화 / 완료
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "초기화",
                color = Muted, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onReset() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "완료",
                color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onConfirm() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * visibleCount),
            contentAlignment = Alignment.Center
        ) {
            // 중앙 선택 표시 pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDEAF6))
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WheelColumn(
                    items = years,
                    selected = year,
                    label = { "${it}년" },
                    onSelectedChange = { onChange(it, month, day) },
                    rowHeight = rowHeight,
                    visibleCount = visibleCount,
                    modifier = Modifier.weight(1f)
                )
                WheelColumn(
                    items = months,
                    selected = month,
                    label = { "${it}월" },
                    onSelectedChange = { onChange(year, it, day.coerceAtMost(daysInMonth(year, it))) },
                    rowHeight = rowHeight,
                    visibleCount = visibleCount,
                    modifier = Modifier.weight(1f)
                )
                WheelColumn(
                    items = days,
                    selected = day.coerceAtMost(daysInMonth(year, month)),
                    label = { "${it}일" },
                    onSelectedChange = { onChange(year, month, it) },
                    rowHeight = rowHeight,
                    visibleCount = visibleCount,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelColumn(
    items: List<Int>,
    selected: Int,
    label: (Int) -> String,
    onSelectedChange: (Int) -> Unit,
    rowHeight: Dp,
    visibleCount: Int,
    modifier: Modifier = Modifier,
) {
    val initialIndex = items.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val padding = rowHeight * (visibleCount / 2)

    // 스크롤이 멈췄을 때 중앙(=firstVisibleItemIndex) 값을 외부로 전파.
    LaunchedEffect(listState) {
        snapshotFlow {
            !listState.isScrollInProgress to listState.firstVisibleItemIndex
        }.collect { (settled, idx) ->
            if (settled && idx in items.indices) {
                val newVal = items[idx]
                if (newVal != selected) onSelectedChange(newVal)
            }
        }
    }

    // 외부에서 selected가 바뀌면 (예: month 변경으로 day clamp) 휠 위치도 맞춰줌.
    LaunchedEffect(selected, items) {
        val targetIndex = items.indexOf(selected)
        if (targetIndex >= 0 && targetIndex != listState.firstVisibleItemIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

    val centerIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = padding),
        modifier = modifier
    ) {
        items(items) { value ->
            val idx = items.indexOf(value)
            val isCenter = idx == centerIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(value),
                    color = if (isCenter) Ink else Color(0xFFB0AABC),
                    fontSize = if (isCenter) 17.sp else 15.sp,
                    fontWeight = if (isCenter) FontWeight.ExtraBold else FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── Date Helpers ─────────────────────────────────────────────────────────────

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 31
    }
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

/** "YYYY-MM-DD" → Triple(year, month, day). 파싱 실패 시 null. */
private fun parseIsoDateOrNull(date: String?): Triple<Int, Int, Int>? {
    if (date.isNullOrBlank()) return null
    val parts = date.split("-")
    if (parts.size != 3) return null
    val y = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    val d = parts[2].toIntOrNull() ?: return null
    return Triple(y, m, d)
}

private fun displayKoreanDate(iso: String): String {
    val (y, m, d) = parseIsoDateOrNull(iso) ?: return iso
    return "${y}년 ${m}월 ${d}일"
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

@Composable
private fun CloseIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.22f), Offset(size.width * 0.78f, size.height * 0.78f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.78f, size.height * 0.22f), Offset(size.width * 0.22f, size.height * 0.78f), stroke, StrokeCap.Round)
    }
}

/** 휴지통 아이콘 (Canvas) */
@Composable
private fun TrashIcon(modifier: Modifier = Modifier, color: Color) {
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
        PostDetailScreen(
            uiState = PostDetailUiState(
                postDetail = PostDetail(
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
private fun MandalaModalPreview() {
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
        PostDetailScreen(
            uiState = PostDetailUiState(
                selectedMandalaCell = plan,
                postDetail = PostDetail(
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
