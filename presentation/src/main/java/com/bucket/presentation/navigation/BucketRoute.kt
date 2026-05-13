package com.bucket.presentation.navigation

sealed class BucketRoute(
    val route: String,
    val label: String
) {
    data object Category : BucketRoute("category", "카테고리")
    data object Home : BucketRoute("home", "홈")
    data object Profile : BucketRoute("profile", "마이")
}

val bottomBarRoutes = listOf(
    BucketRoute.Category,
    BucketRoute.Home,
    BucketRoute.Profile
)
