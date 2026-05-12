package com.example.bucket_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bucket_app.ui.theme.BucketappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BucketappTheme(dynamicColor = false) {
                BucketHomeScreen()
            }
        }
    }
}

private val Background = Color(0xFFFAF7FF)
private val Ink = Color(0xFF171421)
private val Muted = Color(0xFF7A748D)
private val SoftLine = Color(0xFFE9E4F1)
private val Purple = Color(0xFF8D6BE8)
private val LightPurple = Color(0xFFE9DBFF)

private data class PopularBucket(
    val category: String,
    val title: String,
    val author: String,
    val initial: String,
    val likes: Int,
    val colors: List<Color>,
    val chipColor: Color
)

private data class RecentBucket(
    val category: String,
    val title: String,
    val author: String,
    val date: String,
    val initial: String,
    val color: Color,
    val textColor: Color
)

private val popularBuckets = listOf(
    PopularBucket(
        category = "운동",
        title = "마라톤 풀코스 완주하기",
        author = "서연",
        initial = "서",
        likes = 248,
        colors = listOf(Color(0xFFD9F4E1), Color(0xFFEAF8EE)),
        chipColor = Color(0xFFEBDDFF)
    ),
    PopularBucket(
        category = "여행",
        title = "제주 한 달 살기",
        author = "준호",
        initial = "준",
        likes = 193,
        colors = listOf(Color(0xFFD9F1FA), Color(0xFFE8F7FF)),
        chipColor = Color(0xFFD8F2FF)
    ),
    PopularBucket(
        category = "취미",
        title = "나만의 사진전 열기",
        author = "하린",
        initial = "하",
        likes = 156,
        colors = listOf(Color(0xFFFFE9D4), Color(0xFFFFF3E7)),
        chipColor = Color(0xFFFFE3C5)
    )
)

private val recentBuckets = listOf(
    RecentBucket("워홀", "호주 워킹홀리데이 다녀오기", "민지", "2026.09.01", "민", Color(0xFFFFD9DC), Color(0xFFA6414A)),
    RecentBucket("학습", "일본어 JLPT N1 따기", "도윤", "2026.01.10", "도", Color(0xFFFFE3C1), Color(0xFF995A14)),
    RecentBucket("취미", "주말마다 필름 사진 찍기", "유나", "2026.04.22", "유", Color(0xFFDCE9FF), Color(0xFF315EA6))
)

@Composable
fun BucketHomeScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Background
    ) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = WindowInsets.statusBars
                    .asPaddingValues()
                    .let {
                        androidx.compose.foundation.layout.PaddingValues(
                            start = 24.dp,
                            top = it.calculateTopPadding() + 26.dp,
                            end = 0.dp,
                            bottom = 112.dp
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                item { HomeHeader() }
                item { SearchBar(Modifier.padding(end = 24.dp)) }
                item {
                    SectionHeader(
                        title = "인기 버킷",
                        leading = "♨",
                        modifier = Modifier.padding(end = 24.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    PopularBucketRow()
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
                        recentBuckets.forEach { RecentBucketCard(it) }
                    }
                }
            }
            BottomNavigation(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "안녕하세요, 영선님",
                color = Muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "오늘은 어떤 꿈을 찾아볼까요?",
                color = Ink,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        BellIcon(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(30.dp)
        )
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(32.dp))
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchIcon(Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Text(
            text = "버킷 리스트, 사용자 검색",
            modifier = Modifier.weight(1f),
            color = Color(0xFF9A94A8),
            fontSize = 17.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        TuneIcon(Modifier.size(24.dp))
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                Text(
                    text = leading,
                    color = Color(0xFFFFB600),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = title,
                color = Ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "더보기",
            color = Color(0xFF6C657B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PopularBucketRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(end = 24.dp)
    ) {
        items(popularBuckets) { bucket ->
            PopularBucketCard(bucket)
        }
    }
}

@Composable
private fun PopularBucketCard(bucket: PopularBucket) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .height(282.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(22.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .background(Brush.linearGradient(bucket.colors))
                .padding(20.dp)
        ) {
            Text(
                text = bucket.category,
                color = Color(0xFF1F7049),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("♥", color = Color(0xFFFF5D65), fontSize = 13.sp)
                Spacer(Modifier.width(5.dp))
                Text(
                    text = bucket.likes.toString(),
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = bucket.title,
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                InitialBadge(text = bucket.initial, color = bucket.chipColor, textColor = Purple)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = bucket.author,
                    color = Color(0xFF6F687E),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecentBucketCard(bucket: RecentBucket) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(bucket.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bucket.category,
                color = bucket.textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = bucket.title,
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                InitialBadge(text = bucket.initial, color = bucket.color.copy(alpha = 0.74f), textColor = bucket.textColor, size = 24)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${bucket.author} · ${bucket.date}",
                    color = Color(0xFF8C8697),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        ChevronIcon(Modifier.size(24.dp))
    }
}

@Composable
private fun InitialBadge(
    text: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    size: Int = 28
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (size < 28) 11.sp else 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun BottomNavigation(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 44.dp,
                    top = 16.dp,
                    end = 44.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GridIcon(Modifier.size(28.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(42.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                HomeIcon(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(y = (-1).dp),
                    color = Purple
                )
            }
            UserIcon(Modifier.size(28.dp))
        }
    }
}

@Composable
private fun BellIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        drawLine(Ink, Offset(w * 0.25f, h * 0.72f), Offset(w * 0.75f, h * 0.72f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawArc(Ink, 200f, 140f, false, topLeft = Offset(w * 0.24f, h * 0.20f), size = androidx.compose.ui.geometry.Size(w * 0.52f, h * 0.58f), style = stroke)
        drawLine(Ink, Offset(w * 0.31f, h * 0.69f), Offset(w * 0.31f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawLine(Ink, Offset(w * 0.69f, h * 0.69f), Offset(w * 0.69f, h * 0.50f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawArc(Ink, 30f, 120f, false, topLeft = Offset(w * 0.39f, h * 0.68f), size = androidx.compose.ui.geometry.Size(w * 0.22f, h * 0.18f), style = stroke)
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.4.dp.toPx()
        drawCircle(Muted, radius = size.minDimension * 0.32f, center = Offset(size.width * 0.43f, size.height * 0.42f), style = Stroke(stroke))
        drawLine(Muted, Offset(size.width * 0.64f, size.height * 0.64f), Offset(size.width * 0.84f, size.height * 0.84f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun TuneIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawLine(Muted, Offset(size.width * 0.25f, size.height * 0.33f), Offset(size.width * 0.82f, size.height * 0.33f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Muted, Offset(size.width * 0.42f, size.height * 0.52f), Offset(size.width * 0.75f, size.height * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Muted, Offset(size.width * 0.55f, size.height * 0.71f), Offset(size.width * 0.66f, size.height * 0.71f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun ChevronIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        drawLine(Color(0xFF9A94A8), Offset(size.width * 0.42f, size.height * 0.28f), Offset(size.width * 0.64f, size.height * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Color(0xFF9A94A8), Offset(size.width * 0.64f, size.height * 0.50f), Offset(size.width * 0.42f, size.height * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun GridIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = Stroke(width = 2.3.dp.toPx())
        val color = Color(0xFF6E687D)
        val cell = size.width * 0.28f
        drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.12f), size = androidx.compose.ui.geometry.Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.60f, size.height * 0.12f), size = androidx.compose.ui.geometry.Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.60f), size = androidx.compose.ui.geometry.Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.60f, size.height * 0.60f), size = androidx.compose.ui.geometry.Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
    }
}

@Composable
private fun HomeIcon(modifier: Modifier = Modifier, color: Color = Ink) {
    Canvas(modifier) {
        val roof = Path().apply {
            moveTo(size.width * 0.16f, size.height * 0.48f)
            lineTo(size.width * 0.50f, size.height * 0.18f)
            lineTo(size.width * 0.84f, size.height * 0.48f)
            lineTo(size.width * 0.77f, size.height * 0.56f)
            lineTo(size.width * 0.77f, size.height * 0.84f)
            lineTo(size.width * 0.23f, size.height * 0.84f)
            lineTo(size.width * 0.23f, size.height * 0.56f)
            close()
        }
        drawPath(roof, color)
        drawRect(Color.White.copy(alpha = 0.82f), topLeft = Offset(size.width * 0.43f, size.height * 0.61f), size = androidx.compose.ui.geometry.Size(size.width * 0.14f, size.height * 0.23f))
    }
}

@Composable
private fun UserIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val color = Color(0xFF6E687D)
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color, radius = size.width * 0.17f, center = Offset(size.width * 0.5f, size.height * 0.28f), style = stroke)
        drawArc(color, 205f, 130f, false, topLeft = Offset(size.width * 0.18f, size.height * 0.48f), size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.56f), style = stroke)
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun BucketHomeScreenPreview() {
    BucketappTheme(dynamicColor = false) {
        BucketHomeScreen()
    }
}
