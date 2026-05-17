package com.bucket.presentation.ui.login

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.LightPurple
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple
import com.bucket.presentation.theme.SoftLine

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        uiState = uiState,
        onKakaoLoginClick = { viewModel.loginWithKakao(context) }
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onKakaoLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    top = topPadding + 20.dp,
                    end = 24.dp,
                    bottom = bottomPadding + 20.dp
                )
        ) {
            // ── 상단 헤더: 로고 + SINCE
            BrandHeader()
            Spacer(Modifier.height(22.dp))

            // ── 라벨
            Text(
                text = "9칸 버킷리스트",
                color = Purple,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(10.dp))

            // ── 메인 타이틀 (형광펜 하이라이트 포함)
            HighlightTitle()

            Spacer(Modifier.height(24.dp))

            // ── 9칸 예시 만다라트 카드
            ExampleMandalaCard(
                modifier = Modifier.fillMaxWidth()
            )

            // ── 남는 공간
            Spacer(Modifier.weight(1f))

            // ── 카카오 버튼
            KakaoLoginButton(
                onClick = onKakaoLoginClick,
                enabled = !uiState.isLoading,
                text = if (uiState.isLoading) "로그인 중..." else "카카오로 내 만다라트 만들기",
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    color = Color(0xFFE04848),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── 상단 헤더 ────────────────────────────────────────────────────────────────

@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiniMandalaLogo(size = 22.dp)
            Text(
                text = "간다라트",
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "SINCE · 2026",
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun MiniMandalaLogo(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
            .padding(2.5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp)
                ) {
                    for (col in 0 until 3) {
                        val isCenter = row == 1 && col == 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(if (isCenter) Purple else LightPurple)
                        )
                    }
                }
            }
        }
    }
}

// ─── 타이틀 (형광펜 하이라이트) ────────────────────────────────────────────────

@Composable
private fun HighlightTitle() {
    val highlight = Color(0xFFFFE99B)
    val annotated = buildAnnotatedString {
        append("81칸 만다라트는 너무 많아서\n")
        withStyle(SpanStyle(background = highlight, color = Ink)) {
            append("딱 9칸")
        }
        append("만 채워봐요.")
    }
    Text(
        text = annotated,
        color = Ink,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

// ─── 9칸 예시 카드 ────────────────────────────────────────────────────────────

private data class MandalaSample(val title: String, val subtitle: String, val isCenter: Boolean = false)

private val sampleCells = listOf(
    MandalaSample("여행", "제주 한달살이"),
    MandalaSample("운동", "주 3회"),
    MandalaSample("독서", "12권"),
    MandalaSample("요리", "코스 5개"),
    MandalaSample("올해의 나", "핵심 목표", isCenter = true),
    MandalaSample("취미", "드로잉"),
    MandalaSample("관계", "편지 6통"),
    MandalaSample("재테크", "월 적금"),
    MandalaSample("건강", "검진 완료"),
)

@Composable
private fun ExampleMandalaCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(22.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 헤더 row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Purple)
                )
                Text(
                    text = "예시 · 영선님의 만다라트",
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "09 / 09",
                color = Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // 3×3 그리드
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val cell = sampleCells[row * 3 + col]
                        ExampleMandalaCell(
                            cell = cell,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExampleMandalaCell(cell: MandalaSample, modifier: Modifier = Modifier) {
    val bg = if (cell.isCenter) Purple else LightPurple
    val titleColor = if (cell.isCenter) Color.White else Ink
    val subtitleColor = if (cell.isCenter) Color.White.copy(alpha = 0.85f) else Muted

    Box(
        modifier = modifier
            .aspectRatio(0.95f)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = cell.title,
                color = titleColor,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = cell.subtitle,
                color = subtitleColor,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── 카카오 버튼 ──────────────────────────────────────────────────────────────

@Composable
private fun KakaoLoginButton(
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(50.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .background(Color(0xFFFEE500))
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        KakaoTalkIcon(Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = Color(0xFF191600),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun KakaoTalkIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val bubble = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = size.width * 0.06f,
                        top = size.height * 0.12f,
                        right = size.width * 0.94f,
                        bottom = size.height * 0.78f
                    ),
                    cornerRadius = CornerRadius(size.width * 0.28f, size.width * 0.28f)
                )
            )
            moveTo(size.width * 0.37f, size.height * 0.74f)
            lineTo(size.width * 0.27f, size.height * 0.92f)
            lineTo(size.width * 0.52f, size.height * 0.77f)
            close()
        }
        drawPath(bubble, Color(0xFF191600))
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun LoginScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        LoginScreen(
            uiState = LoginUiState(),
            onKakaoLoginClick = {}
        )
    }
}
