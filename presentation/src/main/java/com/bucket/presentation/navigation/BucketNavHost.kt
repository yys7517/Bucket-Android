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
import com.example.domain.model.user.Author
import com.bucket.presentation.theme.Ink
import com.bucket.presentation.ui.category.CategoryRoute
import com.bucket.presentation.ui.detail.PostDetailRoute
import com.bucket.presentation.ui.home.HomeRoute
import com.bucket.presentation.ui.login.LoginRoute
import com.bucket.presentation.ui.splash.SplashRoute

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
        startDestination = BucketRoute.Splash.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable(BucketRoute.Splash.route) {
            SplashRoute(
                onAutoLoginSuccess = appState::navigateToHomeAfterLogin,            // 자동 로그인 성공 시, 홈 화면으로
                onAutoLoginFailure = appState::navigateToLoginAfterAutoLoginFailure // 자동 로그인 실패 시, 로그인 화면으로
            )
        }
        composable(BucketRoute.Login.route) {
            LoginRoute(onLoginSuccess = appState::navigateToHomeAfterLogin)
        }
        composable(BucketRoute.Category.route) {
            CategoryRoute()
        }
        composable(BucketRoute.Home.route) {
            HomeRoute(onBucketClick = { id, author -> appState.navigateToBucketDetail(id, author) })
        }
        composable(
            route = BucketRoute.BucketDetail.route,
            arguments = listOf(
                navArgument(BucketRoute.BucketDetail.ARG_BUCKET_ID) { type = NavType.LongType },
                navArgument(BucketRoute.BucketDetail.ARG_USER_ID) { type = NavType.LongType; defaultValue = 0L },
                navArgument(BucketRoute.BucketDetail.ARG_USERNAME) { type = NavType.StringType; defaultValue = "" },
                navArgument(BucketRoute.BucketDetail.ARG_PROFILE_IMAGE) { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            PostDetailRoute(
                postId = args?.getLong(BucketRoute.BucketDetail.ARG_BUCKET_ID) ?: 0L,
                author = Author(
                    userId = args?.getLong(BucketRoute.BucketDetail.ARG_USER_ID) ?: 0L,
                    username = args?.getString(BucketRoute.BucketDetail.ARG_USERNAME).orEmpty(),
                    profileImgUrl = args?.getString(BucketRoute.BucketDetail.ARG_PROFILE_IMAGE).orEmpty()
                ),
                onBackClick = appState::navigateBack
            )
        }
        composable(BucketRoute.Profile.route) {
            PlaceholderScreen(title = "마이")
        }
    }
}
