package com.bucket.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import com.example.domain.model.user.Author
import com.example.domain.usecase.post.CreateSmallGoalUseCase
import com.example.domain.usecase.post.CreateTodoUseCase
import com.example.domain.usecase.post.DeletePostUseCase
import com.example.domain.usecase.post.DeleteSmallGoalUseCase
import com.example.domain.usecase.post.DeleteTodoUseCase
import com.example.domain.usecase.post.GetPostDetailUseCase
import com.example.domain.usecase.post.ToggleBookmarkUseCase
import com.example.domain.usecase.post.ToggleLikeUseCase
import com.example.domain.usecase.post.UpdatePostUseCase
import com.example.domain.usecase.post.UpdateSmallGoalUseCase
import com.example.domain.usecase.post.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val postDetail: PostDetail? = null,
    val errorMessage: String? = null,
    val isLiked: Boolean = false,
    val likeCount: Int = 0,
    val isBookmarked: Boolean = false,
    /** 탭된 외곽 셀 — non-null이면 만다라트 모달 표시 */
    val selectedMandalaCell: SmallGoal? = null,
    /** 편집/삭제 진행 중 표시 (바텀시트 버튼 비활성화 등에 사용) */
    val isMutating: Boolean = false,
)

/** ViewModel → UI 일회성 이벤트 */
sealed interface PostDetailEvent {
    data object PostUpdated : PostDetailEvent
    data object PostDeleted : PostDetailEvent
    data object PlanUpdated : PostDetailEvent
    data class BookmarkUpdated(val isBookmarked: Boolean) : PostDetailEvent
    data class Error(val message: String) : PostDetailEvent
}

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val createSmallGoalUseCase: CreateSmallGoalUseCase,
    private val updateSmallGoalUseCase: UpdateSmallGoalUseCase,
    private val deleteSmallGoalUseCase: DeleteSmallGoalUseCase,
    private val createTodoUseCase: CreateTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<PostDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var savedPostId: Long = 0L
    private var savedAuthor: Author? = null

    fun showCreatedPostDetail(post: PostDetail) {
        savedPostId = post.id
        savedAuthor = post.author
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                postDetail = post,
                isLiked = post.isLiked,
                likeCount = post.likeCount,
                isBookmarked = post.isBookmarked,
            )
        }
    }

    fun loadPostDetail(postId: Long, author: Author) {
        if (postId <= 0L) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "잘못된 게시글입니다.") }
            return
        }
        savedPostId = postId
        savedAuthor = author
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPostDetailUseCase(postId, author)
                .onSuccess { post ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            postDetail = post,
                            isLiked = post.isLiked,
                            likeCount = post.likeCount,
                            isBookmarked = post.isBookmarked,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message ?: "게시글을 불러오지 못했습니다.")
                    }
                }
        }
    }

    private fun refreshSilently() {
        val postId = savedPostId.takeIf { it > 0L } ?: return
        val author = savedAuthor ?: return
        viewModelScope.launch {
            getPostDetailUseCase(postId, author)
                .onSuccess { post ->
                    _uiState.update { state ->
                        val freshSelected = state.selectedMandalaCell?.let { current ->
                            post.smallGoals.firstOrNull { it.id == current.id }
                        }
                        state.copy(
                            postDetail = post,
                            isLiked = post.isLiked,
                            likeCount = post.likeCount,
                            isBookmarked = post.isBookmarked,
                            selectedMandalaCell = freshSelected,
                        )
                    }
                }
        }
    }

    /** 셀 탭 → 모달 열기. 같은 셀 재탭 시 해제. */
    fun selectMandalaCell(plan: SmallGoal) {
        val isOpening = _uiState.value.selectedMandalaCell?.id != plan.id
        _uiState.update { state ->
            val next = if (state.selectedMandalaCell?.id == plan.id) null else plan
            state.copy(selectedMandalaCell = next)
        }
        if (isOpening) refreshSilently()
    }

    /** 모달 닫기 → 선택 해제 + 백그라운드 재조회 */
    fun dismissMandalaModal() {
        _uiState.update { it.copy(selectedMandalaCell = null) }
        refreshSilently()
    }

    fun addSmallGoal(planId: Long, content: String, color: String, isComplete: Boolean, position: Int) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val tempId = -System.currentTimeMillis()
        val newTodo = Todo(id = tempId, content = content, color = color, isComplete = isComplete, position = position)

        _uiState.update { s ->
            val p = s.postDetail ?: return@update s
            val updatedSmallGoals = p.smallGoals.map { plan ->
                if (plan.id == planId) plan.copy(todos = plan.todos + newTodo) else plan
            }
            val updatedSelected = s.selectedMandalaCell?.let { cell ->
                if (cell.id == planId) cell.copy(todos = cell.todos + newTodo) else cell
            }
            s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
        }

        viewModelScope.launch {
            createTodoUseCase(
                postId = post.id,
                smallGoalId = planId,
                content = content,
                color = color,
                isComplete = isComplete,
                sortOrder = position,
            )
                .onSuccess { serverTodo ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val updatedSmallGoals = p.smallGoals.map { plan ->
                            if (plan.id == planId) plan.copy(todos = plan.todos.map { if (it.id == tempId) serverTodo else it }) else plan
                        }
                        val updatedSelected = s.selectedMandalaCell?.let { cell ->
                            if (cell.id == planId) cell.copy(todos = cell.todos.map { if (it.id == tempId) serverTodo else it }) else cell
                        }
                        s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
                    }
                    refreshSilently()
                }
                .onFailure { throwable ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val updatedSmallGoals = p.smallGoals.map { plan ->
                            if (plan.id == planId) plan.copy(todos = plan.todos.filter { it.id != tempId }) else plan
                        }
                        val updatedSelected = s.selectedMandalaCell?.let { cell ->
                            if (cell.id == planId) cell.copy(todos = cell.todos.filter { it.id != tempId }) else cell
                        }
                        s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
                    }
                    _events.trySend(PostDetailEvent.Error(throwable.message ?: "할 일 추가에 실패했습니다."))
                }
        }
    }

    fun updateSmallGoal(planId: Long, goalId: Long, content: String, color: String, isComplete: Boolean) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val previous = post.smallGoals.firstOrNull { it.id == planId }
            ?.todos?.firstOrNull { it.id == goalId } ?: return

        _uiState.update { s ->
            val p = s.postDetail ?: return@update s
            val updatedSmallGoals = p.smallGoals.map { plan ->
                if (plan.id == planId) {
                    plan.copy(todos = plan.todos.map { g ->
                        if (g.id == goalId) g.copy(content = content, color = color, isComplete = isComplete) else g
                    })
                } else plan
            }
            val updatedSelected = s.selectedMandalaCell?.let { cell ->
                if (cell.id == planId) {
                    cell.copy(todos = cell.todos.map { g ->
                        if (g.id == goalId) g.copy(content = content, color = color, isComplete = isComplete) else g
                    })
                } else cell
            }
            s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
        }

        viewModelScope.launch {
            updateTodoUseCase(
                postId = post.id,
                smallGoalId = planId,
                todoId = goalId,
                content = content,
                color = color,
                isComplete = isComplete,
                sortOrder = previous.position,
            )
                .onSuccess { serverTodo ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val updatedSmallGoals = p.smallGoals.map { plan ->
                            if (plan.id == planId) plan.copy(todos = plan.todos.map { if (it.id == goalId) serverTodo else it }) else plan
                        }
                        val updatedSelected = s.selectedMandalaCell?.let { cell ->
                            if (cell.id == planId) cell.copy(todos = cell.todos.map { if (it.id == goalId) serverTodo else it }) else cell
                        }
                        s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
                    }
                    refreshSilently()
                }
                .onFailure { throwable ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val updatedSmallGoals = p.smallGoals.map { plan ->
                            if (plan.id == planId) plan.copy(todos = plan.todos.map { if (it.id == goalId) previous else it }) else plan
                        }
                        val updatedSelected = s.selectedMandalaCell?.let { cell ->
                            if (cell.id == planId) cell.copy(todos = cell.todos.map { if (it.id == goalId) previous else it }) else cell
                        }
                        s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
                    }
                    _events.trySend(PostDetailEvent.Error(throwable.message ?: "할 일 수정에 실패했습니다."))
                }
        }
    }

    /**
     * 만다라트 외곽 셀(SmallGoal/Plan) 추가.
     * sortOrder = 3x3 그리드 위치(1-9), 가운데 5는 Goal 자리라 외곽 셀은 1,2,3,4,6,7,8,9를 사용한다.
     * 낙관적으로 임시 ID로 삽입하고 서버 응답으로 교체. 실패 시 롤백.
     */
    fun addPlan(content: String, color: String, isComplete: Boolean, sortOrder: Int) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val tempId = -System.currentTimeMillis()
        val newPlan = SmallGoal(
            id = tempId,
            sortOrder = sortOrder,
            content = content,
            color = color,
            isComplete = isComplete,
            todos = emptyList(),
        )

        _uiState.update { it.copy(postDetail = post.copy(smallGoals = post.smallGoals + newPlan)) }

        viewModelScope.launch {
            createSmallGoalUseCase(
                postId = post.id,
                content = content,
                color = color,
                isComplete = isComplete,
                sortOrder = sortOrder,
            )
                .onSuccess { serverPlan ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        s.copy(
                            postDetail = p.copy(
                                smallGoals = p.smallGoals.map { plan ->
                                    if (plan.id == tempId) serverPlan else plan
                                }
                            )
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        s.copy(postDetail = p.copy(smallGoals = p.smallGoals.filter { it.id != tempId }))
                    }
                    _events.trySend(
                        PostDetailEvent.Error(throwable.message ?: "셀 추가에 실패했습니다.")
                    )
                }
        }
    }

    /**
     * 만다라트 외곽 셀(SmallGoal/Plan) 수정.
     * 낙관적 업데이트 후 서버 응답으로 reconcile. 실패 시 이전 값으로 롤백.
     */
    fun updatePlan(
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
    ) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val previous = post.smallGoals.firstOrNull { it.id == smallGoalId } ?: return

        // 낙관적 업데이트 (postDetail + selectedMandalaCell 동시 반영)
        _uiState.update { s ->
            val p = s.postDetail ?: return@update s
            val updatedPlan = { plan: SmallGoal ->
                if (plan.id == smallGoalId) plan.copy(content = content, color = color, isComplete = isComplete) else plan
            }
            s.copy(
                postDetail = p.copy(smallGoals = p.smallGoals.map(updatedPlan)),
                selectedMandalaCell = s.selectedMandalaCell?.let(updatedPlan),
            )
        }

        viewModelScope.launch {
            updateSmallGoalUseCase(
                postId = post.id,
                smallGoalId = smallGoalId,
                content = content,
                color = color,
                isComplete = isComplete,
                sortOrder = previous.sortOrder,
            )
                .onSuccess { serverPlan ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val reconciled = { plan: SmallGoal ->
                            if (plan.id == smallGoalId) serverPlan.copy(todos = plan.todos) else plan
                        }
                        s.copy(
                            postDetail = p.copy(smallGoals = p.smallGoals.map(reconciled)),
                            selectedMandalaCell = s.selectedMandalaCell?.let(reconciled),
                        )
                    }
                    _events.trySend(PostDetailEvent.PlanUpdated)
                    refreshSilently()
                }
                .onFailure { throwable ->
                    // 롤백
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        s.copy(
                            postDetail = p.copy(
                                smallGoals = p.smallGoals.map { plan ->
                                    if (plan.id == smallGoalId) previous else plan
                                }
                            ),
                            selectedMandalaCell = if (s.selectedMandalaCell?.id == smallGoalId) previous else s.selectedMandalaCell,
                        )
                    }
                    _events.trySend(PostDetailEvent.Error(throwable.message ?: "셀 수정에 실패했습니다."))
                }
        }
    }

    /**
     * 만다라트 외곽 셀(SmallGoal/Plan) 삭제.
     * 낙관적으로 즉시 제거하고, 실패 시 원래 위치에 복원.
     */
    fun deletePlan(smallGoalId: Long) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val previous = post.smallGoals.firstOrNull { it.id == smallGoalId } ?: return

        // 낙관적 업데이트: 삭제한 슬롯만 비우고, 다른 셀의 sortOrder는 유지한다.
        _uiState.update { s ->
            val p = s.postDetail ?: return@update s
            s.copy(
                postDetail = p.copy(
                    smallGoals = p.smallGoals.filter { it.id != smallGoalId }
                )
            )
        }

        viewModelScope.launch {
            deleteSmallGoalUseCase(postId = post.id, smallGoalId = smallGoalId)
                .onSuccess {
                    // 모달 닫기 + 재조회
                    _uiState.update { it.copy(selectedMandalaCell = null) }
                    refreshSilently()
                }
                .onFailure { throwable ->
                    // 롤백: 이전 sortOrder 그대로 복원
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val restored = (p.smallGoals + previous).sortedBy { it.sortOrder }
                        s.copy(postDetail = p.copy(smallGoals = restored))
                    }
                    _events.trySend(PostDetailEvent.Error(throwable.message ?: "셀 삭제에 실패했습니다."))
                }
        }
    }

    fun toggleLike(postId: Long) {
        val current = _uiState.value
        val optimisticIsLiked = !current.isLiked
        val optimisticCount = if (optimisticIsLiked) current.likeCount + 1 else (current.likeCount - 1).coerceAtLeast(0)
        _uiState.update { it.copy(isLiked = optimisticIsLiked, likeCount = optimisticCount) }
        viewModelScope.launch {
            toggleLikeUseCase(postId)
                .onSuccess { result -> _uiState.update { it.copy(isLiked = result.isLiked, likeCount = result.likeCount) } }
                .onFailure { _uiState.update { it.copy(isLiked = current.isLiked, likeCount = current.likeCount) } }
        }
    }

    fun toggleBookmark(postId: Long) {
        val current = _uiState.value
        val optimisticIsBookmarked = !current.isBookmarked
        _uiState.update {
            it.copy(
                isBookmarked = optimisticIsBookmarked,
                postDetail = it.postDetail?.copy(isBookmarked = optimisticIsBookmarked),
            )
        }
        viewModelScope.launch {
            toggleBookmarkUseCase(postId)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isBookmarked = result.isBookmarked,
                            postDetail = it.postDetail?.copy(isBookmarked = result.isBookmarked),
                        )
                    }
                    _events.trySend(PostDetailEvent.BookmarkUpdated(result.isBookmarked))
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isBookmarked = current.isBookmarked,
                            postDetail = current.postDetail,
                        )
                    }
                }
        }
    }

    /**
     * 게시글(태스크) 본문 수정 — 목표 이름 / 시작일 / 메모.
     * 낙관적 업데이트 후 서버 응답으로 reconcile. 실패 시 이전 값으로 롤백.
     */
    fun updatePost(title: String, startDate: String, memo: String) {
        val current = _uiState.value
        val post = current.postDetail ?: return
        val trimmedTitle = title.trim()

        // 낙관적 업데이트
        _uiState.update {
            it.copy(
                isMutating = true,
                postDetail = post.copy(
                    title = trimmedTitle,
                    startDate = startDate,
                    memo = memo,
                )
            )
        }

        viewModelScope.launch {
            updatePostUseCase(
                postId = post.id,
                title = trimmedTitle,
                memo = memo,
                startDate = startDate,
            )
                .onSuccess { updated ->
                    _uiState.update { state ->
                        val cur = state.postDetail ?: return@update state.copy(isMutating = false)
                        state.copy(
                            isMutating = false,
                            postDetail = cur.copy(
                                title = updated.title,
                                memo = updated.memo,
                                startDate = updated.startDate,
                            )
                        )
                    }
                    _events.trySend(PostDetailEvent.PostUpdated)
                }
                .onFailure { throwable ->
                    // 롤백
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            postDetail = post,
                        )
                    }
                    _events.trySend(
                        PostDetailEvent.Error(throwable.message ?: "수정에 실패했습니다.")
                    )
                }
        }
    }

    /**
     * 게시글(태스크) 삭제. 성공 시 PostDeleted 이벤트 발행 → UI에서 화면 닫기.
     */
    fun deletePost() {
        val post = _uiState.value.postDetail ?: return
        _uiState.update { it.copy(isMutating = true) }

        viewModelScope.launch {
            deletePostUseCase(post.id)
                .onSuccess {
                    _uiState.update { it.copy(isMutating = false) }
                    _events.trySend(PostDetailEvent.PostDeleted)
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isMutating = false) }
                    _events.trySend(
                        PostDetailEvent.Error(throwable.message ?: "삭제에 실패했습니다.")
                    )
                }
        }
    }

    fun deleteSmallGoal(planId: Long, goalId: Long) {
        val state = _uiState.value
        val post = state.postDetail ?: return
        val previous = post.smallGoals.firstOrNull { it.id == planId }
            ?.todos?.firstOrNull { it.id == goalId } ?: return

        _uiState.update { s ->
            val p = s.postDetail ?: return@update s
            val updatedSmallGoals = p.smallGoals.map { plan ->
                if (plan.id == planId) plan.copy(todos = plan.todos.filter { it.id != goalId }) else plan
            }
            val updatedSelected = s.selectedMandalaCell?.let { cell ->
                if (cell.id == planId) cell.copy(todos = cell.todos.filter { it.id != goalId }) else cell
            }
            s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
        }

        viewModelScope.launch {
            deleteTodoUseCase(postId = post.id, smallGoalId = planId, todoId = goalId)
                .onSuccess {
                    refreshSilently()
                }
                .onFailure { throwable ->
                    _uiState.update { s ->
                        val p = s.postDetail ?: return@update s
                        val updatedSmallGoals = p.smallGoals.map { plan ->
                            if (plan.id == planId) plan.copy(todos = (plan.todos + previous).sortedBy { it.position }) else plan
                        }
                        val updatedSelected = s.selectedMandalaCell?.let { cell ->
                            if (cell.id == planId) cell.copy(todos = (cell.todos + previous).sortedBy { it.position }) else cell
                        }
                        s.copy(postDetail = p.copy(smallGoals = updatedSmallGoals), selectedMandalaCell = updatedSelected)
                    }
                    _events.trySend(PostDetailEvent.Error(throwable.message ?: "할 일 삭제에 실패했습니다."))
                }
        }
    }
}
