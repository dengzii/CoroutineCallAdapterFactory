package com.dengzii.calladapter.coroutine

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class CoroutineCall<T>(private val call: Call<T>) {

    fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        lifecycleOwner: LifecycleOwner? = null,
        builder: CallbackBuilder<T>.() -> Unit
    ) {

        val callback = CallbackBuilder<T>().run {

            if (lifecycleOwner != null) {
                lifecycle(lifecycleOwner)
            }

            apply(builder)
            build()
        }
        GlobalScope.launch(context, start) {
            val deferResponse = async(Dispatchers.IO) {
                try {
                    handleResponse(call.execute())
                } catch (e: Throwable) {
                    callback.onFail(e)
                    null
                }
            }
            callback.onStart(object : Disposable {
                override fun dispose() {
                    if (deferResponse.isActive) {
                        deferResponse.cancel()
                    }
                    if (isActive) {
                        cancel()
                    }
                    callback.onCancel()
                }
            })

            deferResponse.invokeOnCompletion {
                if (deferResponse.isCancelled) {
                    call.cancel()
                    callback.onCancel()
                }
            }

            deferResponse.await()?.let {
                callback.onSuccess(it)
            }
            callback.onComplete()
        }
    }

    fun launch(builder: CallbackBuilder<T>.() -> Unit) {
        launch(Dispatchers.Main, CoroutineStart.DEFAULT, null, builder)
    }

    fun launch(lifecycleOwner: LifecycleOwner, builder: CallbackBuilder<T>.() -> Unit) {
        launch(Dispatchers.Main, CoroutineStart.DEFAULT, lifecycleOwner, builder)
    }

    @Throws(IOException::class)
    private fun <T> handleResponse(response: Response<T>?): T {
        if (response == null) {
            throw IOException("empty response.")
        } else {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return body
                } else {
                    throw Exception("empty response body.")
                }
            } else {
                throw HttpException(response)
            }
        }
    }
}