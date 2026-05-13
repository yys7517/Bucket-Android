package com.bucket.presentation.ui.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bucket.presentation.theme.Ink

@Composable
fun SectionHeader(
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
