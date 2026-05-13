package com.bucket.presentation.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.bucket.presentation.navigation.BucketRoute
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.theme.Purple

@Composable
fun BucketBottomBar(
    routes: List<BucketRoute>,
    currentRoute: String?,
    onRouteClick: (BucketRoute) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp
    ) {
        routes.forEach { route ->
            val selected = currentRoute == route.route
            NavigationBarItem(
                selected = selected,
                onClick = { onRouteClick(route) },
                icon = {
                    BottomBarIcon(
                        route = route,
                        color = if (selected) Purple else Color(0xFF6E687D)
                    )
                },
                label = { Text(route.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Purple,
                    selectedTextColor = Purple,
                    indicatorColor = Color(0xFFE9DBFF),
                    unselectedIconColor = Color(0xFF6E687D),
                    unselectedTextColor = Color(0xFF6E687D)
                )
            )
        }
    }
}

@Composable
private fun BottomBarIcon(
    route: BucketRoute,
    color: Color
) {
    Box(Modifier.size(26.dp)) {
        when (route) {
            BucketRoute.Category -> GridIcon(color = color)
            BucketRoute.Home -> HomeIcon(color = color)
            BucketRoute.Profile -> UserIcon(color = color)
            else -> Unit
        }
    }
}

@Composable
private fun GridIcon(color: Color) {
    Canvas(Modifier.size(26.dp)) {
        val stroke = Stroke(width = 2.3.dp.toPx())
        val cell = size.width * 0.28f
        drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.12f), size = Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.60f, size.height * 0.12f), size = Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.12f, size.height * 0.60f), size = Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
        drawRoundRect(color, topLeft = Offset(size.width * 0.60f, size.height * 0.60f), size = Size(cell, cell), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
    }
}

@Composable
private fun HomeIcon(color: Color = Ink) {
    Canvas(Modifier.size(26.dp)) {
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
        drawRect(Color.White.copy(alpha = 0.82f), topLeft = Offset(size.width * 0.43f, size.height * 0.61f), size = Size(size.width * 0.14f, size.height * 0.23f))
    }
}

@Composable
private fun UserIcon(color: Color) {
    Canvas(Modifier.size(26.dp)) {
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color, radius = size.width * 0.17f, center = Offset(size.width * 0.5f, size.height * 0.28f), style = stroke)
        drawArc(color, 205f, 130f, false, topLeft = Offset(size.width * 0.18f, size.height * 0.48f), size = Size(size.width * 0.64f, size.height * 0.56f), style = stroke)
    }
}
