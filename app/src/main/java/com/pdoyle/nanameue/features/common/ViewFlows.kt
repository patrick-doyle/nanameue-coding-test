package com.pdoyle.nanameue.features.common

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> {
    val view = this
    return callbackFlow {
        view.setOnClickListener {
            this.trySend(Unit)
        }
        this.awaitClose { view.setOnClickListener(null) }
    }
}