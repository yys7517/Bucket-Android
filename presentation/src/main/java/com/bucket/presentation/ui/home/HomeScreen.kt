package com.bucket.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.BucketappTheme
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.ui.home.component.HomeHeader
import com.bucket.presentation.ui.home.component.PopularBucketCard
import com.bucket.presentation.ui.home.component.RecentBucketCard
import com.bucket.presentation.ui.home.component.SearchBar
import com.bucket.presentation.ui.home.component.SectionHeader
import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.RecentBucket

@Composable
fun HomeRoute(
    onBucketClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onBucketClick = onBucketClick
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBucketClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = HomeBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars
                .asPaddingValues()
                .let {
                    PaddingValues(
                        start = 24.dp,
                        top = it.calculateTopPadding() + 26.dp,
                        end = 0.dp,
                        bottom = 24.dp
                    )
                },
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            item { HomeHeader() }
            item { SearchBar(Modifier.padding(end = 24.dp)) }
            item {
                SectionHeader(
                    title = "인기 버킷",
                    leading = "\uD83D\uDD25",
                    modifier = Modifier.padding(end = 24.dp)
                )
                Spacer(Modifier.height(16.dp))
                PopularBucketRow(
                    popularBuckets = uiState.popularBuckets,
                    onBucketClick = onBucketClick
                )
            }
            item {
                SectionHeader(
                    title = "최근 올라온",
                    modifier = Modifier.padding(end = 24.dp)
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.padding(end = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    uiState.recentBuckets.forEach { bucket ->
                        RecentBucketCard(
                            bucket = bucket,
                            onClick = { onBucketClick(bucket.id) }
                        )
                    }
                    if (uiState.isLoading) {
                        Text(
                            text = "불러오는 중...",
                            color = Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color(0xFFE04D5F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularBucketRow(
    popularBuckets: List<PopularBucket>,
    onBucketClick: (Long) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(end = 24.dp)
    ) {
        items(popularBuckets) { bucket ->
            PopularBucketCard(
                bucket = bucket,
                onClick = { onBucketClick(bucket.id) }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun HomeScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        HomeScreen(
            uiState = HomeUiState(
                popularBuckets = listOf(
                    PopularBucket(
                        id = 1,
                        category = "여행",
                        categoryColor = "",
                        title = "한라산 백록담 등반하기",
                        userName = "test-user",
                        profileImageUrl = "https://example.com/test-user.png",
                        likeCount = 1,
                        isLiked = false,
                        progress = 66
                    )
                ),
                recentBuckets = listOf(
                    RecentBucket(
                        id = 3,
                        category = "건강",
                        categoryColor = "",
                        title = "매주 3회 러닝 루틴 만들기",
                        userName = "test-user",
                        profileImageUrl = "https://example.com/test-user.png",
                        startDate = "2026-05-13"
                    )
                )
            )
        )
    }
}
