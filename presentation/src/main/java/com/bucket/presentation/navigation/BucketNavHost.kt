package com.bucket.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bucket.presentation.BucketAppState
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.ui.home.HomeRoute

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun BucketNavHost(
    appState: BucketAppState,
    padding: PaddingValues
) {
    NavHost(
        navController = appState.navController,
        startDestination = BucketRoute.Home.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable(BucketRoute.Category.route) {
            PlaceholderScreen(title = "버킷")
        }
        composable(BucketRoute.Home.route) {
            HomeRoute()
        }
        composable(BucketRoute.Profile.route) {
            PlaceholderScreen(title = "마이")
        }
    }
}