package com.pdoyle.nanameue

import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestAppDispatchers : AppDispatchers {

    private val dispatcher = StandardTestDispatcher()
    override fun io(): CoroutineDispatcher = dispatcher

    override fun main(): CoroutineDispatcher = dispatcher

    fun getTestDispatcher() = dispatcher
}