package com.bucket.presentation.ui.postcreate

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.detail.component.WheelDatePicker
import com.bucket.presentation.ui.detail.extension.displayKoreanDate
import com.bucket.presentation.ui.detail.extension.parseIsoDateOrNull
import com.bucket.presentation.ui.detail.extension.todayDateParts
import com.example.domain.model.post.PostDetail

data class PostCreateInput(
    val title: String,
    val memo: String,
    val startDate: String?,
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,
)

private data class CreateCategory(
    val id: Long,
    val name: String,
    val colorHex: String,
    val color: Color,
)

private val createCategories = listOf(
    CreateCategory(id = 1, name = "여행", colorHex = "#3B82F6", color = Color(0xFF3B82F6)),
    CreateCategory(id = 2, name = "학습", colorHex = "#10B981", color = Color(0xFF10B981)),
    CreateCategory(id = 3, name = "건강", colorHex = "#F97316", color = Color(0xFFF97316)),
    CreateCategory(id = 4, name = "취미", colorHex = "#A855F7", color = Color(0xFFA855F7)),
    CreateCategory(id = 5, name = "운동", colorHex = "#EF4444", color = Color(0xFFEF4444)),
    CreateCategory(id = 6, name = "재테크", colorHex = "#F59E0B", color = Color(0xFFF59E0B)),
    CreateCategory(id = 7, name = "독서", colorHex = "#14B8A6", color = Color(0xFF14B8A6)),
)

@Composable
fun PostCreateRoute(
    onBackClick: () -> Unit,
    onPostCreated: (PostDetail) -> Unit,
    viewModel: PostCreateViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PostCreateEvent.Created -> onPostCreated(event.post)
                is PostCreateEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    PostCreateScreen(
        isCreating = uiState.isCreating,
        onBackClick = onBackClick,
        onCreateClick = viewModel::createPost,
    )
}

@Composable
fun PostCreateScreen(
    isCreating: Boolean = false,
    onBackClick: () -> Unit,
    onCreateClick: (PostCreateInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var memo by rememberSaveable { mutableStateOf("") }
    var selectedDateIso by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val defaultDate = remember { todayDateParts() }
    val defaultDateIso = remember(defaultDate) {
        "%04d-%02d-%02d".format(defaultDate.first, defaultDate.second, defaultDate.third)
    }

    val selectedCategory = remember(selectedCategoryId) {
        createCategories.firstOrNull { it.id == selectedCategoryId }
    }
    val canCreate = title.isNotBlank() && selectedCategory != null

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 24.dp,
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 18.dp,
                        end = 24.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 118.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                PostCreateTopBar(onBackClick = onBackClick)

                CreateIntroCard()

                FormCard {
                    FieldLabel(text = "목표 이름", required = true)
                    UnderlineCreateField(
                        value = title,
                        onValueChange = { if (it.length <= 24) title = it },
                        placeholder = "예: 제주 한 달 살기",
                        singleLine = true,
                        maxLength = 24
                    )
                }

                FormCard {
                    FieldLabel(text = "카테고리", required = true)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        createCategories.forEach { category ->
                            CategoryChoiceChip(
                                category = category,
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id }
                            )
                        }
                    }
                }

                FormCard {
                    FieldLabel(text = "시작일", required = false)
                    DateSelectRow(
                        selectedDateIso = selectedDateIso,
                        expanded = showDatePicker,
                        onClick = {
                            val opening = !showDatePicker
                            if (opening && selectedDateIso == null) {
                                selectedDateIso = defaultDateIso
                            }
                            showDatePicker = opening
                        },
                        onClear = {
                            selectedDateIso = null
                            showDatePicker = false
                        }
                    )
                    if (showDatePicker) {
                        val parsedDate = parseIsoDateOrNull(selectedDateIso)
                        Spacer(Modifier.height(4.dp))
                        WheelDatePicker(
                            year = parsedDate?.first ?: defaultDate.first,
                            month = parsedDate?.second ?: defaultDate.second,
                            day = parsedDate?.third ?: defaultDate.third,
                            onChange = { year, month, day ->
                                selectedDateIso = "%04d-%02d-%02d".format(year, month, day)
                            },
                            onReset = {
                                selectedDateIso = null
                                showDatePicker = false
                            },
                            onConfirm = { showDatePicker = false },
                        )
                    }
                }

                FormCard {
                    FieldLabel(text = "메모", required = false)
                    UnderlineCreateField(
                        value = memo,
                        onValueChange = { if (it.length <= 120) memo = it },
                        placeholder = "목표를 시작하는 이유나 기준을 적어보세요",
                        singleLine = false,
                        minHeight = 96,
                        maxLength = 120
                    )
                }

                GandartLaterCard(
                    accentColor = selectedCategory?.color ?: Purple
                )
            }

            CreateBottomBar(
                enabled = canCreate && !isCreating,
                onClick = {
                    val category = selectedCategory ?: return@CreateBottomBar
                    onCreateClick(
                        PostCreateInput(
                            title = title.trim(),
                            memo = memo.trim(),
                            startDate = selectedDateIso,
                            categoryId = category.id,
                            categoryName = category.name,
                            categoryColor = category.colorHex,
                        )
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (isCreating) {
        PostCreateProgressDialog()
    }
}

@Composable
private fun PostCreateProgressDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Purple,
                    strokeWidth = 3.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "게시글 작성 중",
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "상세 정보를 준비하고 있어요.",
                        color = Muted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PostCreateTopBar(
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            BackIcon(
                color = Ink,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = "게시글 작성",
            color = Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun CreateIntroCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(24.dp))
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainGoalGandartIcon(accentColor = Purple)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "큰 목표부터 가볍게 시작해요",
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "작성 후 상세 화면에서 8칸 간다라트를 이어서 채울 수 있어요.",
                color = Muted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FormCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(22.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        content = content
    )
}

@Composable
private fun FieldLabel(
    text: String,
    required: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            color = Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.size(4.dp))
        if (required) {
            Text(
                text = "*",
                color = Purple,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        } else {
            Text(
                text = "(선택)",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun UnderlineCreateField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    maxLength: Int,
    modifier: Modifier = Modifier,
    minHeight: Int = 42,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight.dp),
            singleLine = singleLine,
            textStyle = TextStyle(
                color = Ink,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFFB0AABC),
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
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
                .background(if (value.length == maxLength) Purple else SoftLine)
        )
        Text(
            text = "${value.length} / $maxLength",
            modifier = Modifier.fillMaxWidth(),
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun CategoryChoiceChip(
    category: CreateCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) category.color else category.color.copy(alpha = 0.11f))
            .border(
                width = 1.dp,
                color = if (selected) category.color else category.color.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (selected) Color.White else category.color)
        )
        Text(
            text = category.name,
            color = if (selected) Color.White else category.color,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DateSelectRow(
    selectedDateIso: String?,
    expanded: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFAF8FE))
            .border(1.dp, SoftLine, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = selectedDateIso?.let { displayKoreanDate(it) } ?: "시작일을 선택해주세요",
                color = if (selectedDateIso == null) Color(0xFF9A94A8) else Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "선택하지 않아도 나중에 수정할 수 있어요",
                color = Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (selectedDateIso != null) {
            Text(
                text = "초기화",
                color = Purple,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .clickable(onClick = onClear)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        } else {
            Text(
                text = if (expanded) "˅" else "›",
                color = Muted,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun GandartLaterCard(
    accentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubGoalsGandartIcon(accentColor = accentColor)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "간다라트는 상세에서 추가해요",
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "처음부터 8칸을 채우지 않아도 괜찮아요. 큰 목표를 작성 후, 상세화면에서 이어서 작은 목표들을 추가해보세요.",
                color = Color(0xFF6F687E),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MainGoalGandartIcon(
    accentColor: Color,
) {
    GandartGuideIcon(
        accentColor = accentColor,
        highlightCenter = true
    )
}

@Composable
private fun SubGoalsGandartIcon(
    accentColor: Color,
) {
    GandartGuideIcon(
        accentColor = accentColor,
        highlightCenter = false
    )
}

@Composable
private fun GandartGuideIcon(
    accentColor: Color,
    highlightCenter: Boolean,
) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, accentColor.copy(alpha = 0.14f), RoundedCornerShape(24.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(3) { col ->
                        val center = row == 1 && col == 1
                        val highlighted = if (highlightCenter) center else !center
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (highlighted) {
                                        accentColor
                                    } else {
                                        accentColor.copy(alpha = 0.20f)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateBottomBar(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    top = 14.dp,
                    end = 24.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 14.dp
                )
                .height(56.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(if (enabled) Ink else Color(0xFFD7DAE0))
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "게시글 작성",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun BackIcon(
    color: Color,
    modifier: Modifier = Modifier.size(20.dp),
) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.62f, size.height * 0.20f),
            end = Offset(size.width * 0.34f, size.height * 0.50f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.34f, size.height * 0.50f),
            end = Offset(size.width * 0.62f, size.height * 0.80f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun PostCreateScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        PostCreateScreen(
            onBackClick = {},
            onCreateClick = {}
        )
    }
}
