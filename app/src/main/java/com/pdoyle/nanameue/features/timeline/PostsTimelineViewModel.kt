package com.pdoyle.nanameue.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsTimelineViewModel(
    private val postsUseCase: PostsUseCase,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineViewState())
    val uiState: StateFlow<TimelineViewState> = _uiState.asStateFlow()

    init {
        _uiState.value = TimelineViewState()
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            val posts = withContext(appDispatchers.io()) {
                postsUseCase.getPosts()
            }
            _uiState.update { it.copy(posts = posts) }
        }
    }

    fun onLogout() {

    }

    fun onNewPost() {

    }


}