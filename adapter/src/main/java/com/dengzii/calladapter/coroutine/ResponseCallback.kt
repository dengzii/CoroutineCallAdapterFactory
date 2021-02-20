package com.dengzii.calladapter.coroutine

interface ResponseCallback<T> {
    fun onStart(disposable: Disposable)
    fun onSuccess(response:T)
    fun onCancel()
    fun onFail(throwable: Throwable)
    fun onComplete()
}