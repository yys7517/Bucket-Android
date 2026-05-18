package com.bucket.presentation.navigation

import com.example.domain.model.user.Author

sealed class BucketRoute(
    val route: String,
    val label: String
) {
    data object Splash : BucketRoute("splash", "스플래시")
    data object Login : BucketRoute("login", "로그인")
    data object Category : BucketRoute("category", "카테고리")
    data object Home : BucketRoute("home", "홈")
    data object BucketDetail : BucketRoute("bucket/{bucketId}?userId={userId}&username={username}&profileImage={profileImage}", "버킷 상세") {
        const val ARG_BUCKET_ID = "bucketId"
        const val ARG_USER_ID = "userId"
        const val ARG_USERNAME = "username"
        const val ARG_PROFILE_IMAGE = "profileImage"

        fun createRoute(bucketId: Long, author: Author): String {
            val encodedUsername = android.net.Uri.encode(author.username)
            val encodedProfileImage = android.net.Uri.encode(author.profileImgUrl)
            return "bucket/$bucketId?userId=${author.userId}&username=$encodedUsername&profileImage=$encodedProfileImage"
        }
    }
    data object Profile : BucketRoute("profile", "마이")
    data object ProfileEdit : BucketRoute("profile/edit", "프로필 편집")
    data object OtherProfile : BucketRoute("profile/{userId}", "프로필") {
        const val ARG_USER_ID = "userId"
        fun createRoute(userId: Long) = "profile/$userId"
    }
}

val bottomBarRoutes = listOf(
    BucketRoute.Home,
    BucketRoute.Profile
)
