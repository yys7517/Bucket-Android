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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bucket.presentation.BucketAppState
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.ui.category.CategoryRoute
import com.bucket.presentation.ui.detail.BucketDetailRoute
import com.bucket.presentation.ui.home.HomeRoute
import com.bucket.presentation.ui.login.LoginRoute

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
        startDestination = BucketRoute.Login.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable(BucketRoute.Login.route) {
            LoginRoute(onLoginSuccess = appState::navigateToHomeAfterLogin)
        }
        composable(BucketRoute.Category.route) {
            CategoryRoute()
        }
        composable(BucketRoute.Home.route) {
            HomeRoute(onBucketClick = appState::navigateToBucketDetail)
        }
        composable(
            route = BucketRoute.BucketDetail.route,
            arguments = listOf(
                navArgument(BucketRoute.BucketDetail.ARG_BUCKET_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            BucketDetailRoute(
                bucketId = backStackEntry.arguments?.getLong(BucketRoute.BucketDetail.ARG_BUCKET_ID) ?: 0L,
                onBackClick = appState::navigateBack
            )
        }
        composable(BucketRoute.Profile.route) {
            PlaceholderScreen(title = "마이")
        }
    }
}
