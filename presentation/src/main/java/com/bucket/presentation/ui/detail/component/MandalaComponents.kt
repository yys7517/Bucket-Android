package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.R
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.detail.extension.asOuterMandalaPositionOrFallback
import com.bucket.presentation.ui.detail.extension.mandalaGridPosition
import com.bucket.presentation.ui.detail.extension.toComposeColor
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo

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
internal fun MandalaSection(
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
internal fun MandalaModal(
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
        // 좌상단 뱃지: 완료면 체크 배지, 미완료면 색상 점
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            if (goal.isComplete) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(cellColor),
                    contentAlignment = Alignment.Center
                ) {
                    CheckIcon(modifier = Modifier.size(10.dp), color = Color.White)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(cellColor)
                )
            }
        }
        // 텍스트: 셀 정중앙
        Text(
            text = goal.content,
            color = Ink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
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
