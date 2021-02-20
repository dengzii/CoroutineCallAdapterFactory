package com.dengzii.calladapter.coroutine

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

open class CallbackBuilder<T> {

    private var onStartFn: ((Disposable) -> Unit)? = null
    private var onSuccessFn: ((T) -> Unit)? = null
    private var onCancelFn: (() -> Unit)? = null
    private var onFailFn: ((Throwable) -> Unit)? = null
    private var onCompleteFn: (() -> Unit)? = null
    private var lifecycleOwner: LifecycleOwner? = null

    fun onStart(block: ((Disposable) -> Unit)) = apply { onStartFn = block }

    fun onSuccess(block: (T) -> Unit) = apply { onSuccessFn = block }

    fun onCancel(block: () -> Unit) = apply { onCancelFn = block }

    fun onFail(block: (Throwable) -> Unit) = apply { onFailFn = block }

    fun onComplete(block: () -> Unit) = apply { onCompleteFn = block }

    internal fun lifecycle(lifecycleOwner: LifecycleOwner?) = apply {
        this.lifecycleOwner = lifecycleOwner
    }

    internal fun build(): ResponseCallback<T> {

        return ResponseCallbackInternal()
    }

    private inner class ResponseCallbackInternal : ResponseCallback<T>, LifecycleEventObserver {

        private var currentOwnerState: Lifecycle.State = Lifecycle.State.INITIALIZED

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            currentOwnerState = source.lifecycle.currentState
        }

        override fun onStart(disposable: Disposable) {
            if (currentOwnerState.isAtLeast(Lifecycle.State.CREATED)) {
                onStartFn?.invoke(disposable)
            }
        }

        override fun onSuccess(response: T) {
            if (currentOwnerState.isAtLeast(Lifecycle.State.CREATED)) {
                onSuccessFn?.invoke(response)
            }
        }

        override fun onCancel() {
            if (currentOwnerState.isAtLeast(Lifecycle.State.CREATED)) {
                onCancelFn?.invoke()
            }
        }

        override fun onFail(throwable: Throwable) {
            onFailFn?.invoke(throwable)
        }

        override fun onComplete() {
            onCompleteFn?.invoke()
        }

        private fun isOwnerDestroyed(): Boolean {
            return currentOwnerState.isAtLeast(Lifecycle.State.DESTROYED)
        }
    }
}