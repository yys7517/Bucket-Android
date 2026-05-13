package com.bucket.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bucket.presentation.navigation.BucketNavHost
import com.bucket.presentation.navigation.bottomBarRoutes
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.ui.common.BucketBottomBar

@Composable
fun BucketApp(
    appState: BucketAppState
) {
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = HomeBackground,
        bottomBar = {
            BucketBottomBar(
                routes = bottomBarRoutes,
                currentRoute = currentRoute,
                onRouteClick = appState::navigateToBottomBarRoute
            )
        }
    ) { padding ->
        BucketNavHost(
            appState = appState,
            padding = padding
        )
    }
}



