package com.pdoyle.nanameue.app.users

import com.pdoyle.nanameue.app.AppScope
import javax.inject.Inject

@AppScope
class UsersRepository @Inject constructor() {

    fun getUsers(): List<User> {
        return emptyList()
    }

}