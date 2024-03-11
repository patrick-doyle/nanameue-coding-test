package com.pdoyle.nanameue.features.posts

import androidx.navigation.NavHostController
import com.pdoyle.nanameue.features.login.LoginActivity

class PostScreenNav(private val activity: PostsActivity) {

    companion object {
        const val POSTS_ROUTE = "posts"
        const val NEW_POSTS_ROUTE = "posts/new"
    }

    private var navController: NavHostController? = null

    fun setNavController(controller: NavHostController) {
        this.navController = controller
    }

    fun finishHostActivity() {
        activity.finish()
    }

    fun launchLoginActivity() {
        LoginActivity.start(activity)
    }

    fun showNewPostScreen() {
        navController?.navigate(NEW_POSTS_ROUTE)
    }

    fun navigateBack() {
        navController?.popBackStack()
    }
}