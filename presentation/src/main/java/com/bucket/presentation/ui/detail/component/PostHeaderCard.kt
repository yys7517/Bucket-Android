package com.bucket.presentation.ui.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.R
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.SoftLine
import com.bucket.presentation.ui.detail.extension.toKoreanDateText
import com.bucket.presentation.ui.home.component.UserAvatar
import com.example.domain.model.post.PostDetail

// ─── Header Card ──────────────────────────────────────────────────────────────

@Composable
internal fun PostHeaderCard(
    post: PostDetail,
    accentColor: Color,
    progress: Float,
    likeCount: Int,
    isMine: Boolean,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val progressPercent = (progress.coerceIn(0f, 1f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(24.dp))
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryChip(category = post.category, accentColor = accentColor)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMine) {
                    CircleIconButton(onClick = onEditClick) {
                        Image(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(Color(0xFF6E687D))
                        )
                    }
                }
                LikeButton(isLiked = isLiked, likeCount = likeCount, onLikeClick = onLikeClick)
                BookmarkButton(isBookmarked = isBookmarked, onBookmarkClick = onBookmarkClick)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = post.title, color = Ink,
            fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = post.memo, color = Color(0xFF635E72),
            fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoPill(label = "시작일", value = post.startDate.toKoreanDateText(), modifier = Modifier.weight(1f))
            InfoPill(label = "달성률", value = "$progressPercent%", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        ProgressBar(progress = progress)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                profileImageUrl = post.author.profileImgUrl,
                username = post.author.username,
                color = accentColor.copy(alpha = 0.18f),
                textColor = accentColor,
                size = 30
            )
            Spacer(Modifier.width(10.dp))
            Text(text = post.author.username, color = Color(0xFF6F687E), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
