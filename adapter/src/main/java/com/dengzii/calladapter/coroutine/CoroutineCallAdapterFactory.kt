package com.dengzii.calladapter.coroutine

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapterFactory private constructor() : CallAdapter.Factory() {

    companion object {
        fun create(): CoroutineCallAdapterFactory {
            return CoroutineCallAdapterFactory()
        }
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != CoroutineCall::class.java) {
            return null
        }
        require(returnType is ParameterizedType) {
            "Call return type must be parameterized as CoroutineCall<Foo> or CoroutineCall<? extends Foo>"
        }
        val responseType = getParameterUpperBound(0, returnType)

        retrofit.callbackExecutor()
        return CoroutineCallAdapter(responseType)
    }

    private class CoroutineCallAdapter(
        private val responseType: Type
    ) : CallAdapter<Any, CoroutineCall<*>> {

        override fun responseType(): Type = responseType

        override fun adapt(call: Call<Any>): CoroutineCall<*> {
            return CoroutineCall(call)
        }
    }

}