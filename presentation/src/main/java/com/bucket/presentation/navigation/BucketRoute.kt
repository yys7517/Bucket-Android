package com.bucket.presentation.navigation

sealed class BucketRoute(
    val route: String,
    val label: String
) {
    data object Login : BucketRoute("login", "로그인")
    data object Category : BucketRoute("category", "카테고리")
    data object Home : BucketRoute("home", "홈")
    data object BucketDetail : BucketRoute("bucket/{bucketId}", "버킷 상세") {
        const val ARG_BUCKET_ID = "bucketId"

        fun createRoute(bucketId: Long): String = "bucket/$bucketId"
    }
    data object Profile : BucketRoute("profile", "마이")
}

val bottomBarRoutes = listOf(
    BucketRoute.Category,
    BucketRoute.Home,
    BucketRoute.Profile
)
