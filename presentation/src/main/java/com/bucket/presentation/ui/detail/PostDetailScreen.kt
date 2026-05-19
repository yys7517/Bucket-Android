package com.bucket.presentation.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bucket.presentation.theme.HomeBackground
import com.bucket.presentation.theme.Muted
import com.bucket.presentation.ui.detail.component.DetailTopBar
import com.bucket.presentation.ui.detail.component.MandalaModal
import com.bucket.presentation.ui.detail.component.MandalaSection
import com.bucket.presentation.ui.detail.component.PostEditBottomSheet
import com.bucket.presentation.ui.detail.component.PostHeaderCard
import com.bucket.presentation.ui.detail.extension.toComposeColor
import com.bucket.presentation.ui.home.component.categoryAccent
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.user.Author

// ─── Route ────────────────────────────────────────────────────────────────────

@Composable
fun PostDetailRoute(
    postId: Long,
    author: Author,
    onBackClick: () -> Unit,
    onAuthorClick: (Long) -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(postId) { viewModel.loadPostDetail(postId, author) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PostDetailEvent.PostUpdated -> {
                    Toast.makeText(context, "수정되었습니다", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.PostDeleted -> {
                    Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
                is PostDetailEvent.PlanUpdated -> {
                    Toast.makeText(context, "수정되었습니다", Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.BookmarkUpdated -> {
                    val message = if (event.isBookmarked) {
                        "북마크에 추가되었습니다."
                    } else {
                        "북마크에서 삭제합니다."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                is PostDetailEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    PostDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLikeClick = { viewModel.toggleLike(postId) },
        onBookmarkClick = { viewModel.toggleBookmark(postId) },
        onAuthorClick = onAuthorClick,
        onSelectMandalaCell = viewModel::selectMandalaCell,
        onDismissMandalaModal = viewModel::dismissMandalaModal,
        onAddPlan = { content, color, isComplete, sortOrder -> viewModel.addPlan(content, color, isComplete, sortOrder) },
        onUpdatePlan = { smallGoalId, content, color, isComplete -> viewModel.updatePlan(smallGoalId, content, color, isComplete) },
        onDeletePlan = { smallGoalId -> viewModel.deletePlan(smallGoalId) },
        onAddSmallGoal = { planId, content, color, isComplete, position -> viewModel.addSmallGoal(planId, content, color, isComplete, position) },
        onUpdateSmallGoal = viewModel::updateSmallGoal,
        onDeleteSmallGoal = viewModel::deleteSmallGoal,
        onUpdatePost = { title, startDate, memo -> viewModel.updatePost(title, startDate, memo) },
        onDeletePost = { viewModel.deletePost() },
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onAuthorClick: (Long) -> Unit = {},
    onSelectMandalaCell: (SmallGoal) -> Unit = {},
    onDismissMandalaModal: () -> Unit = {},
    onAddPlan: (content: String, color: String, isComplete: Boolean, sortOrder: Int) -> Unit = { _, _, _, _ -> },
    onUpdatePlan: (smallGoalId: Long, content: String, color: String, isComplete: Boolean) -> Unit = { _, _, _, _ -> },
    onDeletePlan: (smallGoalId: Long) -> Unit = {},
    onAddSmallGoal: (planId: Long, content: String, color: String, isComplete: Boolean, position: Int) -> Unit = { _, _, _, _, _ -> },
    onUpdateSmallGoal: (planId: Long, goalId: Long, content: String, color: String, isComplete: Boolean) -> Unit = { _, _, _, _, _ -> },
    onDeleteSmallGoal: (planId: Long, goalId: Long) -> Unit = { _, _ -> },
    onUpdatePost: (title: String, startDate: String, memo: String) -> Unit = { _, _, _ -> },
    onDeletePost: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val post = uiState.postDetail
    var showEditSheet by rememberSaveable(post?.id, post?.isMine) { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize(), color = HomeBackground) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.statusBars.asPaddingValues().let {
                PaddingValues(
                    start = 24.dp,
                    top = it.calculateTopPadding() + 18.dp,
                    end = 24.dp,
                    bottom = 34.dp
                )
            },
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { DetailTopBar(onBackClick = onBackClick) }
            when {
                uiState.isLoading -> item {
                    Text(text = "불러오는 중...", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                uiState.errorMessage != null -> item {
                    Text(text = uiState.errorMessage, color = Color(0xFFE04D5F), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                post != null -> {
                    val accentColor = categoryAccent(post.category, post.categoryColor)
                    val completedCount = post.smallGoals.count { it.isComplete }
                    val progress = if (post.smallGoals.isEmpty()) 0f else completedCount.toFloat() / post.smallGoals.size

                    item {
                        PostHeaderCard(
                            post = post,
                            accentColor = accentColor,
                            progress = progress,
                            likeCount = uiState.likeCount,
                            isMine = post.isMine,
                            isLiked = uiState.isLiked,
                            isBookmarked = uiState.isBookmarked,
                            onLikeClick = onLikeClick,
                            onBookmarkClick = onBookmarkClick,
                            onEditClick = { showEditSheet = true },
                            onAuthorClick = onAuthorClick,
                        )
                    }
                    item {
                        MandalaSection(
                            post = post,
                            accentColor = accentColor,
                            isMine = post.isMine,
                            onSelectCell = onSelectMandalaCell,
                            onAddPlan = onAddPlan,
                        )
                    }
                }
            }
        }
    }

    // ── 만다라트 모달 (셀 탭 시)
    val selectedCell = uiState.selectedMandalaCell
    if (selectedCell != null && post != null) {
        MandalaModal(
            plan = selectedCell,
            postTitle = post.title,
            accentColor = selectedCell.color.toComposeColor(),
            isMine = post.isMine,
            usedSmallGoalColors = post.smallGoals.map { it.color }.toSet(),
            onDismiss = onDismissMandalaModal,
            onUpdatePlan = { content, color, isComplete ->
                onUpdatePlan(selectedCell.id, content, color, isComplete)
            },
            onDeletePlan = { onDeletePlan(selectedCell.id) },
            onAddTodo = { content, color, isComplete, position ->
                onAddSmallGoal(selectedCell.id, content, color, isComplete, position)
            },
            onUpdateTodo = { goalId, content, color, isComplete ->
                onUpdateSmallGoal(selectedCell.id, goalId, content, color, isComplete)
            },
            onDeleteTodo = { goalId -> onDeleteSmallGoal(selectedCell.id, goalId) },
        )
    }

    // ── 게시글 편집 바텀시트 (isMine=true 전용)
    if (showEditSheet && post != null && post.isMine) {
        PostEditBottomSheet(
            post = post,
            onDismiss = { showEditSheet = false },
            onSave = { title, startDate, memo ->
                onUpdatePost(title, startDate, memo)
                showEditSheet = false
            },
            onDelete = {
                onDeletePost()
                showEditSheet = false
            },
        )
    }
}
