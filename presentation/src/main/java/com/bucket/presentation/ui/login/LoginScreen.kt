package com.bucket.presentation.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.theme.Purple

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
                    start = 28.dp,
                    top = topPadding + 78.dp,
                    end = 28.dp,
                    bottom = bottomPadding + 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BucketSearchLogo(Modifier.size(188.dp))
            Spacer(Modifier.height(26.dp))
            Text(
                text = "Bucket Search",
                color = Ink,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "버킷 서치",
                color = Muted,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.weight(1f))
            KakaoLoginButton(
                onClick = onKakaoLoginClick,
                enabled = !uiState.isLoading,
                text = if (uiState.isLoading) "로그인 중..." else "카카오로 시작하기",
                modifier = Modifier.fillMaxWidth()
            )
            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    color = Color(0xFFE04848),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BucketSearchLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White,
                        Color(0xFFEFE8FF),
                        Color(0xFFDDEEFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(132.dp)) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFE9DBFF),
                        Color(0xFFD7ECFF)
                    )
                ),
                topLeft = Offset(size.width * 0.20f, size.height * 0.16f),
                size = Size(size.width * 0.52f, size.height * 0.64f),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
            )
            drawRoundRect(
                color = Purple.copy(alpha = 0.62f),
                topLeft = Offset(size.width * 0.20f, size.height * 0.16f),
                size = Size(size.width * 0.52f, size.height * 0.64f),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
            )
            listOf(0.31f, 0.46f, 0.61f).forEachIndexed { index, y ->
                val checkColor = if (index == 0) Color(0xFF4A90F2) else Purple.copy(alpha = 0.7f)
                val lineColor = if (index == 0) Ink else Color(0xFF8C8697)
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.72f),
                    topLeft = Offset(size.width * 0.29f, size.height * (y - 0.035f)),
                    size = Size(size.width * 0.07f, size.width * 0.07f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawLine(
                    color = checkColor,
                    start = Offset(size.width * 0.305f, size.height * y),
                    end = Offset(size.width * 0.325f, size.height * (y + 0.022f)),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = checkColor,
                    start = Offset(size.width * 0.325f, size.height * (y + 0.022f)),
                    end = Offset(size.width * 0.36f, size.height * (y - 0.025f)),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = lineColor,
                    start = Offset(size.width * 0.42f, size.height * y),
                    end = Offset(size.width * 0.61f, size.height * y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            drawCircle(
                color = Color(0xFF4A90F2),
                radius = size.width * 0.13f,
                center = Offset(size.width * 0.60f, size.height * 0.57f),
                style = Stroke(width = 4.dp.toPx())
            )
            drawLine(
                color = Color(0xFF4A90F2),
                start = Offset(size.width * 0.70f, size.height * 0.67f),
                end = Offset(size.width * 0.83f, size.height * 0.80f),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFFFD95E),
                radius = size.width * 0.038f,
                center = Offset(size.width * 0.74f, size.height * 0.22f)
            )
        }
    }
}

@Composable
private fun KakaoLoginButton(
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .background(Color(0xFFFEE500))
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        KakaoTalkIcon(Modifier.size(24.dp))
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
