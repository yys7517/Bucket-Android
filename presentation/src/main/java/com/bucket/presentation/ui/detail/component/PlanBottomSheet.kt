package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.example.domain.model.post.SmallGoal

// ─── Plan BottomSheet ─────────────────────────────────────────────────────────
// 작은 목표 추가 / 수정 바텀 시트

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlanBottomSheet(
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
