package com.bucket.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bucket.presentation.navigation.BucketRoute
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
}

@Composable
fun rememberBucketAppState(
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): BucketAppState = remember(navHostController, coroutineScope) {
    BucketAppState(navHostController, coroutineScope)
}
