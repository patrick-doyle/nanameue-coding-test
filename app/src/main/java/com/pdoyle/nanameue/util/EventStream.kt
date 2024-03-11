package com.pdoyle.nanameue.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EventStream<T> {

    private var listener: (T) -> Unit = { }

    fun sendEvent(data: T) {
        listener(data)
    }

    fun listen(callback: (T) -> Unit) {
        this.listener = callback
    }

    fun listenWithScope(scope: CoroutineScope, callback: suspend (T) -> Unit) {
        this.listener = { data ->
            scope.launch {
                callback(data)
            }
        }
    }
}