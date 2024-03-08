package com.pdoyle.nanameue.app

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.pdoyle.nanameue.features.login.LoginComponent
import com.pdoyle.nanameue.features.login.LoginModule
import com.pdoyle.nanameue.features.posts.PostComponent
import com.pdoyle.nanameue.features.posts.PostsModule
import com.pdoyle.nanameue.util.AppDispatchers
import com.pdoyle.nanameue.util.DefaultAppDispatchers
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
annotation class AppScope

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

    fun loginComponent(module: LoginModule): LoginComponent

    fun postsComponent(module: PostsModule): PostComponent
}

@Module
class AppModule(private val context: Context) {

    @Provides
    fun context() = context

    @Provides
    @AppScope
    fun fireBaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @AppScope
    fun fireStore() = Firebase.firestore

    @Provides
    @AppScope
    fun storage() = Firebase.storage

    @Provides
    @AppScope
    fun contentResolver(): ContentResolver = context.contentResolver

    @Provides
    @AppScope
    fun connectivityManager(): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @AppScope
    fun appDispatchers(): AppDispatchers = DefaultAppDispatchers
}