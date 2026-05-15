package com.bucket.presentation.ui.home.component

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.SoftLine
import com.example.domain.model.home.PopularBucket

@Composable
fun PopularBucketCard(
    bucket: PopularBucket,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .height(282.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(22.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .background(Brush.linearGradient(categoryGradient(bucket.category, bucket.categoryColor)))
                .padding(20.dp)
        ) {
            Text(
                text = bucket.category,
                color = categoryAccent(bucket.category, bucket.categoryColor),
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
                    text = bucket.likeCount.toString(),
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
                val accentColor = categoryAccent(bucket.category, bucket.categoryColor)
                UserAvatar(
                    profileImageUrl = bucket.profileImageUrl,
                    username = bucket.userName,
                    color = accentColor.copy(alpha = 0.18f),
                    textColor = accentColor
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = bucket.userName,
                    color = Color(0xFF6F687E),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
