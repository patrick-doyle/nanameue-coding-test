package com.pdoyle.nanameue.features.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdoyle.nanameue.app.login.LoginRepository

class PostScreenViewModel(
    private val postScreenNav: PostScreenNav,
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun onLogout() {
        loginRepository.logout()
        postScreenNav.launchLoginActivity()
        postScreenNav.finishHostActivity()
    }

}

@Suppress("UNCHECKED_CAST")
class PostScreenViewModelFactory(
    private val postScreenNav: PostScreenNav,
    private val loginRepository: LoginRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostScreenViewModel(postScreenNav, loginRepository) as T
    }
}