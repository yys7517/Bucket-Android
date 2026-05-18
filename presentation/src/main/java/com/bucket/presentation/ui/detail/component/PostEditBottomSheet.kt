package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.detail.extension.daysInMonth
import com.bucket.presentation.ui.detail.extension.displayKoreanDate
import com.bucket.presentation.ui.detail.extension.parseIsoDateOrNull
import com.example.domain.model.post.PostDetail

// ─── Post Edit BottomSheet ──────────────────────────────────────────────────
//
// 편집 버튼 클릭 시 노출. 목표 이름(필수), 시작일(휠 선택), 메모, 삭제/완료 버튼.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostEditBottomSheet(
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
                        .background(Color(0xFFFFEEF1))
                        .border(1.dp, Color(0xFFE04D5F).copy(alpha = 0.22f), RoundedCornerShape(50.dp))
                        .clickable { showDeleteConfirm = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("삭제", color = Color(0xFFE04D5F), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
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
