package com.dengzii.calladapter.coroutine

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class LifecycleCoroutineContext(
    private val coroutineContext: CoroutineContext
) : LifecycleEventObserver, CoroutineContext by coroutineContext {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source.lifecycle.currentState.isAtLeast(Lifecycle.State.DESTROYED)) {
            source.lifecycle.removeObserver(this)
            cancel()
        }
    }
}