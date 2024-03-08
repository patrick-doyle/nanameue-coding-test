package com.pdoyle.nanameue.app.users

import com.pdoyle.nanameue.app.AppScope
import javax.inject.Inject

@AppScope
class UsersRepository @Inject constructor(private val usersApi: UsersApi) {

    suspend fun createUserInStore(user: User) {
        return usersApi.createUserInStore(user)
    }

}