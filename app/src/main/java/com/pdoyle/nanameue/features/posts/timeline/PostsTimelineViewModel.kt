package com.pdoyle.nanameue.features.posts.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pdoyle.nanameue.app.login.LoginRepository
import com.pdoyle.nanameue.features.posts.PostScreenNav
import com.pdoyle.nanameue.features.posts.PostsUseCase
import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsTimelineViewModel(
    private val postsUseCase: PostsUseCase,
    private val postScreenNav: PostScreenNav,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostTimelineViewState())
    val uiState: StateFlow<PostTimelineViewState> = _uiState.asStateFlow()

    init {
        _uiState.value = PostTimelineViewState()
        loadPosts()
        listenForNewPosts()
    }

    private fun listenForNewPosts() {
        postsUseCase.listenForNewPosts()
            .onEach { newPosts ->
                _uiState.update { it.copy(posts = newPosts) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val posts = withContext(appDispatchers.io()) {
                postsUseCase.getPosts()
            }
            _uiState.update { it.copy(posts = posts, loading = false) }
        }
    }

    fun onFabClicked() {
        postScreenNav.showNewPostScreen()
    }

    fun refresh() {
        loadPosts()
    }
}

@Suppress("UNCHECKED_CAST")
class PostsTimelineViewModelFactory(
    private val postsUseCase: PostsUseCase,
    private val postScreenNav: PostScreenNav,
    private val appDispatchers: AppDispatchers
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostsTimelineViewModel(postsUseCase, postScreenNav, appDispatchers) as T
    }
}