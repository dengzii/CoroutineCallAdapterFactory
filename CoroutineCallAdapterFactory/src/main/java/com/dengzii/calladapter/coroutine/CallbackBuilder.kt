package com.dengzii.calladapter.coroutine

open class CallbackBuilder<T> {

    private var onStartFn: ((Disposable) -> Unit)? = null
    private var onSuccessFn: ((T) -> Unit)? = null
    private var onCancelFn: (() -> Unit)? = null
    private var onFailFn: ((Throwable) -> Unit)? = null
    private var onCompleteFn: (() -> Unit)? = null


    fun onStart(block: ((Disposable) -> Unit)) = apply { onStartFn = block }

    fun onSuccess(block: (T) -> Unit) = apply { onSuccessFn = block }

    fun onCancel(block: () -> Unit) = apply { onCancelFn = block }

    fun onFail(block: (Throwable) -> Unit) = apply { onFailFn = block }

    fun onComplete(block: () -> Unit) = apply { onCompleteFn = block }

    internal fun build(): ResponseCallback<T> {
        return ResponseCallbackInternal()
    }

    private inner class ResponseCallbackInternal : ResponseCallback<T> {

        override fun onStart(disposable: Disposable) {
            if (checkAvailable()) {
                onStartFn?.invoke(disposable)
            }
        }

        override fun onSuccess(response: T) {
            if (checkAvailable()) {
                onSuccessFn?.invoke(response)
            }
        }

        override fun onCancel() {
            if (checkAvailable()) {
                onCancelFn?.invoke()
            }
        }

        override fun onFail(throwable: Throwable) {
            onFailFn?.invoke(throwable)
        }

        override fun onComplete() {
            onCompleteFn?.invoke()
        }

        private fun checkAvailable(): Boolean {
            return true
        }
    }
}