package com.pdoyle.nanameue.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface AppDispatchers {

    fun io(): CoroutineDispatcher

    fun main(): CoroutineDispatcher
}

object DefaultAppDispatchers: AppDispatchers {
    override fun io() = Dispatchers.IO

    override fun main() = Dispatchers.Main

}