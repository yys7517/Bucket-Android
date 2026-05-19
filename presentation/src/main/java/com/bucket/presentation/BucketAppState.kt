package com.bucket.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bucket.presentation.navigation.BucketRoute
import com.example.domain.model.user.Author
import kotlinx.coroutines.CoroutineScope

@Stable
data class BucketAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope
) {
    fun navigateToBottomBarRoute(route: BucketRoute) {
        navController.navigate(route.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToBucketDetail(bucketId: Long, author: Author) {
        navController.navigate(BucketRoute.BucketDetail.createRoute(bucketId, author))
    }

    fun navigateToOtherProfile(userId: Long) {
        navController.navigate(BucketRoute.OtherProfile.createRoute(userId))
    }

    fun navigateToMyProfile() {
        navigateToBottomBarRoute(BucketRoute.Profile)
    }

    fun navigateToProfileEdit(
        username: String,
        email: String,
        introduction: String,
        profileImageUrl: String
    ) {
        navController.navigate(BucketRoute.ProfileEdit.createRoute(username, email, introduction, profileImageUrl))
    }

    fun navigateBackWithProfileUpdate(
        username: String,
        email: String,
        introduction: String,
    ) {
        navController.previousBackStackEntry?.savedStateHandle?.apply {
            set(BucketRoute.ProfileEdit.RESULT_USERNAME, username)
            set(BucketRoute.ProfileEdit.RESULT_EMAIL, email)
            set(BucketRoute.ProfileEdit.RESULT_INTRODUCTION, introduction)
        }
        navigateBack()
    }

    fun navigateToHomeAfterLogin() {
        navController.navigate(BucketRoute.Home.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToLoginAfterAutoLoginFailure() {
        navController.navigate(BucketRoute.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToLoginAfterLogout() {
        navController.navigate(BucketRoute.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberBucketAppState(
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): BucketAppState = remember(navHostController, coroutineScope) {
    BucketAppState(navHostController, coroutineScope)
}
