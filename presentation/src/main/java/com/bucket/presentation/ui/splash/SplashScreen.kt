package com.bucket.presentation.ui.splash

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay

/** 스플래시 최소 노출 시간(ms) — 디자인 의도상 최소 1초는 보이도록. */
private const val MIN_SPLASH_DURATION_MS = 1000L

@Composable
fun SplashRoute(
    onAutoLoginSuccess: () -> Unit,
    onAutoLoginFailure: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 자동 로그인 결과가 1초 이전에 도착해도, 최소 1초는 스플래시를 노출.
    LaunchedEffect(uiState.hasSavedLogin) {
        val result = uiState.hasSavedLogin ?: return@LaunchedEffect
        delay(MIN_SPLASH_DURATION_MS)
        if (result) onAutoLoginSuccess() else onAutoLoginFailure()
    }

    SplashScreen()
}

@Composable
private fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 중앙: 아이콘 + 타이틀 + 서브카피
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SplashAppIcon(size = 132.dp)
                Spacer(Modifier.height(28.dp))
                Text(
                    text = "간다라트",
                    color = Ink,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "꿈을 채우는 한 칸 한 칸",
                    color = Muted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 하단: 페이지 인디케이터 (3 dots, 중앙 활성)
//            PageIndicator(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .navigationBarsPadding()
//                    .padding(bottom = 36.dp)
//            )
        }
    }
}

/** 9칸 만다라트 모양의 앱 아이콘 — 흰 라운드 카드 + 3×3 보라색 셀. */
@Composable
private fun SplashAppIcon(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 3) {
                        val isCenter = row == 1 && col == 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (isCenter) Purple else LightPurple)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(active = false)
        Dot(active = true)
        Dot(active = false)
    }
}

@Composable
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(if (active) 8.dp else 7.dp)
            .clip(CircleShape)
            .background(if (active) Purple else SoftLine)
    )
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun SplashScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        SplashScreen()
    }
}
