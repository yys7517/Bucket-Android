package com.bucket.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bucket.presentation.navigation.BucketNavHost
import com.bucket.presentation.navigation.BucketRoute
import com.bucket.presentation.navigation.bottomBarRoutes
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.ui.common.BucketBottomBar

@Composable
fun BucketApp(
    appState: BucketAppState,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = bottomBarRoutes.any { it.route == currentRoute }
    val hasSavedLogin by authViewModel.hasSavedLogin.collectAsStateWithLifecycle()

    LaunchedEffect(hasSavedLogin, currentRoute) {
        val isAuthRoute = currentRoute == BucketRoute.Splash.route ||
            currentRoute == BucketRoute.Login.route ||
            currentRoute == null

        if (!hasSavedLogin && !isAuthRoute) {
            appState.navigateToLoginAfterLogout()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = HomeBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BucketBottomBar(
                    routes = bottomBarRoutes,
                    currentRoute = currentRoute,
                    onRouteClick = appState::navigateToBottomBarRoute
                )
            }
        }
    ) { padding ->
        BucketNavHost(
            appState = appState,
            padding = padding
        )
    }
}
