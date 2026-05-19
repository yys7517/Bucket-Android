package com.bucket.presentation.ui.profile

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.home.component.UserAvatar

@Composable
fun ProfileEditRoute(
    username: String,
    email: String,
    introduction: String,
    profileImageUrl: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit = onBackClick
) {
    ProfileEditScreen(
        initialName = username,
        initialEmail = email,
        initialBio = introduction,
        profileImageUrl = profileImageUrl,
        onBackClick = onBackClick,
        onSaveClick = onSaveClick
    )
}

@Composable
fun ProfileEditScreen(
    initialName: String = "",
    initialEmail: String = "",
    initialBio: String = "",
    profileImageUrl: String = "",
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable(initialName) { mutableStateOf(initialName) }
    var email by rememberSaveable(initialEmail) { mutableStateOf(initialEmail) }
    var bio by rememberSaveable(initialBio) { mutableStateOf(initialBio) }
    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = statusPadding,
                bottom = 34.dp
            )
        ) {
            item {
                ProfileEditTopBar(
                    onBackClick = onBackClick,
                    onSaveClick = onSaveClick
                )
            }
            item {
                ProfilePhotoEditor(
                    name = name,
                    profileImageUrl = profileImageUrl
                )
            }
            item {
                EditSectionTitle("기본 정보")
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    UnderlineInput(
                        label = "이름",
                        required = true,
                        value = name,
                        onValueChange = { if (it.length <= 16) name = it },
                        maxLength = 16
                    )
                    UnderlineInput(
                        label = "이메일",
                        value = email,
                        onValueChange = { if (it.length <= 60) email = it },
                        maxLength = 60,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Email
                        )
                    )
                    UnderlineInput(
                        label = "자기소개",
                        value = bio,
                        onValueChange = { if (it.length <= 60) bio = it },
                        maxLength = 60,
                        singleLine = false,
                        minHeight = 72.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileEditTopBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircleTextButton(onClick = onBackClick) {
            BackIcon(color = Ink)
        }
        Text(
            text = "프로필 편집",
            color = Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Purple)
                .clickable(onClick = onSaveClick)
                .padding(horizontal = 17.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("저장", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ProfilePhotoEditor(
    name: String,
    profileImageUrl: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(Purple.copy(alpha = 0.13f))
                .border(3.dp, Purple.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            UserAvatar(
                profileImageUrl = profileImageUrl,
                username = name,
                color = Purple.copy(alpha = 0.13f),
                textColor = Purple,
                size = 128
            )
        }
    }
}

@Composable
private fun EditSectionTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAF6FF))
            .padding(horizontal = 28.dp, vertical = 13.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF716B83),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun UnderlineInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    helper: String? = null,
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = 42.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            if (required) {
                Spacer(Modifier.width(4.dp))
                Text("*", color = Purple, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight),
            singleLine = singleLine,
            textStyle = TextStyle(
                color = Ink,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = helper.orEmpty(),
                color = Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${value.length} / $maxLength",
                color = Muted,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoChangeBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("사진 변경", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("어떻게 사진을 올리시겠어요?", color = Muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            PhotoOptionRow(
                label = "카메라로 찍기",
                icon = { CameraIcon(color = Ink, modifier = Modifier.size(20.dp)) },
                onClick = onDismiss
            )
            PhotoOptionRow(
                label = "앨범에서 선택",
                icon = { GalleryIcon(color = Ink, modifier = Modifier.size(20.dp)) },
                onClick = onDismiss
            )
            PhotoOptionRow(
                label = "기본 이미지로 되돌리기",
                icon = { ResetFaceIcon(color = Ink, modifier = Modifier.size(20.dp)) },
                onClick = onDismiss
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PhotoOptionRow(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Purple.copy(alpha = 0.11f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Text(label, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun CircleTextButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun BackIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawLine(color, Offset(size.width * 0.62f, size.height * 0.20f), Offset(size.width * 0.34f, size.height * 0.50f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.34f, size.height * 0.50f), Offset(size.width * 0.62f, size.height * 0.80f), stroke, StrokeCap.Round)
    }
}

@Composable
private fun CameraIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawRoundRect(color, Offset(size.width * 0.14f, size.height * 0.28f), androidx.compose.ui.geometry.Size(size.width * 0.72f, size.height * 0.54f), androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
        drawRoundRect(color, Offset(size.width * 0.33f, size.height * 0.17f), androidx.compose.ui.geometry.Size(size.width * 0.24f, size.height * 0.16f), androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()))
        drawCircle(color, radius = size.width * 0.14f, center = Offset(size.width * 0.50f, size.height * 0.55f), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
    }
}

@Composable
private fun GalleryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawRoundRect(color, Offset(size.width * 0.14f, size.height * 0.18f), androidx.compose.ui.geometry.Size(size.width * 0.72f, size.height * 0.64f), androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
        drawCircle(color, radius = size.width * 0.08f, center = Offset(size.width * 0.35f, size.height * 0.38f))
        drawLine(color, Offset(size.width * 0.22f, size.height * 0.72f), Offset(size.width * 0.44f, size.height * 0.54f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.44f, size.height * 0.54f), Offset(size.width * 0.62f, size.height * 0.70f), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.62f, size.height * 0.70f), Offset(size.width * 0.78f, size.height * 0.48f), stroke, StrokeCap.Round)
    }
}

@Composable
private fun ResetFaceIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawCircle(color, radius = size.width * 0.34f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
        drawCircle(color, radius = size.width * 0.035f, center = Offset(size.width * 0.39f, size.height * 0.45f))
        drawCircle(color, radius = size.width * 0.035f, center = Offset(size.width * 0.61f, size.height * 0.45f))
        drawArc(color, 35f, 110f, false, Offset(size.width * 0.35f, size.height * 0.43f), androidx.compose.ui.geometry.Size(size.width * 0.30f, size.height * 0.30f), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun ProfileEditScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        ProfileEditScreen(onBackClick = {}, onSaveClick = {})
    }
}
