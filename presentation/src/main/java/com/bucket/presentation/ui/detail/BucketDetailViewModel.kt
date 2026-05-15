package com.bucket.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import com.example.domain.model.user.Author
import com.example.domain.usecase.post.GetPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BucketDetailUiState(
    val isLoading: Boolean = false,
    val bucketDetail: BucketPostDetail? = null,
    val errorMessage: String? = null,
    val drillDownPlan: SmallGoal? = null,
    /** 만다라트 그리드에서 현재 선택된 계획 셀 (isMine=true 전용) */
    val selectedMandalaCell: SmallGoal? = null,
)

@HiltViewModel
class BucketDetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BucketDetailUiState())
    val uiState: StateFlow<BucketDetailUiState> = _uiState.asStateFlow()

    fun loadBucketDetail(bucketId: Long, author: Author) {
        if (bucketId <= 0L) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "잘못된 게시글입니다.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPostDetailUseCase(bucketId, author)
                .onSuccess { bucket ->
                    _uiState.update { it.copy(isLoading = false, bucketDetail = bucket) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message ?: "게시글을 불러오지 못했습니다.")
                    }
                }
        }
    }

    /** 만다라트 그리드에서 셀 선택/해제 토글 (isMine=true 전용) */
    fun selectMandalaCell(plan: SmallGoal) {
        _uiState.update { state ->
            val next = if (state.selectedMandalaCell?.id == plan.id) null else plan
            state.copy(selectedMandalaCell = next)
        }
    }

    fun drillDown(plan: SmallGoal) {
        _uiState.update { it.copy(drillDownPlan = plan, selectedMandalaCell = null) }
    }

    fun exitDrillDown() {
        _uiState.update { it.copy(drillDownPlan = null, selectedMandalaCell = null) }
    }

    fun addSmallGoal(planId: Long, content: String, color: String, isComplete: Boolean, position: Int) {
        _uiState.update { state ->
            val bucket = state.bucketDetail ?: return@update state
            val newTodo = Todo(id = System.currentTimeMillis(), content = content, color = color, isComplete = isComplete)
            val updatedSmallGoals = bucket.smallGoals.map { plan ->
                if (plan.id == planId) {
                    val todos = plan.todos.toMutableList()
                    todos.add(position.coerceIn(0, todos.size), newTodo)
                    plan.copy(todos = todos)
                } else plan
            }
            val updatedDrillDown = state.drillDownPlan?.let { drill ->
                if (drill.id == planId) {
                    val todos = drill.todos.toMutableList()
                    todos.add(position.coerceIn(0, todos.size), newTodo)
                    drill.copy(todos = todos)
                } else drill
            }
            state.copy(bucketDetail = bucket.copy(smallGoals = updatedSmallGoals), drillDownPlan = updatedDrillDown)
        }
    }

    fun updateSmallGoal(planId: Long, goalId: Long, content: String, color: String, isComplete: Boolean) {
        _uiState.update { state ->
            val bucket = state.bucketDetail ?: return@update state
            val updatedSmallGoals = bucket.smallGoals.map { plan ->
                if (plan.id == planId) {
                    plan.copy(todos = plan.todos.map { g ->
                        if (g.id == goalId) g.copy(content = content, color = color, isComplete = isComplete) else g
                    })
                } else plan
            }
            val updatedDrillDown = state.drillDownPlan?.let { drill ->
                if (drill.id == planId) {
                    drill.copy(todos = drill.todos.map { g ->
                        if (g.id == goalId) g.copy(content = content, color = color, isComplete = isComplete) else g
                    })
                } else drill
            }
            state.copy(bucketDetail = bucket.copy(smallGoals = updatedSmallGoals), drillDownPlan = updatedDrillDown)
        }
    }

    fun addPlan(content: String, color: String, targetIndex: Int) {
        _uiState.update { state ->
            val bucket = state.bucketDetail ?: return@update state
            val newPlan = SmallGoal(
                id = System.currentTimeMillis(),
                sortOrder = 0,
                content = content,
                color = color,
                isComplete = false,
                todos = emptyList()
            )
            val current = bucket.smallGoals.sortedBy { it.sortOrder }.toMutableList()
            current.add(targetIndex.coerceIn(0, current.size), newPlan)
            val reordered = current.mapIndexed { i, plan -> plan.copy(sortOrder = i + 1) }
            state.copy(bucketDetail = bucket.copy(smallGoals = reordered))
        }
    }

    fun deleteSmallGoal(planId: Long, goalId: Long) {
        _uiState.update { state ->
            val bucket = state.bucketDetail ?: return@update state
            val updatedSmallGoals = bucket.smallGoals.map { plan ->
                if (plan.id == planId) plan.copy(todos = plan.todos.filter { it.id != goalId }) else plan
            }
            val updatedDrillDown = state.drillDownPlan?.let { drill ->
                if (drill.id == planId) drill.copy(todos = drill.todos.filter { it.id != goalId }) else drill
            }
            state.copy(bucketDetail = bucket.copy(smallGoals = updatedSmallGoals), drillDownPlan = updatedDrillDown)
        }
    }
}
