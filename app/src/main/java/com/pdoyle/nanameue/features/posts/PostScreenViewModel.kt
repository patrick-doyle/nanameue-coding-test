package com.pdoyle.nanameue.features.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostScreenViewModel(private val postScreenNav: PostScreenNav): ViewModel() {

    fun onLogout() {
        postScreenNav.launchLoginActivity()
        postScreenNav.finishHostActivity()
    }

}

@Suppress("UNCHECKED_CAST")
class PostScreenViewModelFactory(
    private val postScreenNav: PostScreenNav,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostScreenViewModel(postScreenNav) as T
    }
}