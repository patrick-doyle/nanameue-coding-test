package com.pdoyle.nanameue.features.posts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.features.posts.PostsUseCase
import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCreateViewModel(
    private val postsUseCase: PostsUseCase,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostCreateViewState())
    val uiState: StateFlow<PostCreateViewState> = _uiState.asStateFlow()

    init {
        _uiState.value = PostCreateViewState()
    }

    fun submitForm(postSubmit: PostForm) {
        viewModelScope.launch {
            withContext(appDispatchers.io()) {
                _uiState.value = _uiState.value.copy(loading = true)
                postsUseCase.createPost(postSubmit)
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class PostCreateViewModelFactory(
    private val postsUseCase: PostsUseCase,
    private val appDispatchers: AppDispatchers
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostCreateViewModel(postsUseCase, appDispatchers) as T
    }
}