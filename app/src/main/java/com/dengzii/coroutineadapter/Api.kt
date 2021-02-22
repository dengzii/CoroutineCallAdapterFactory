package com.dengzii.coroutineadapter

import com.dengzii.calladapter.coroutine.CoroutineCall
import retrofit2.http.GET

interface Api {

    @GET("https://gank.io/api/v2/categories/GanHuo")
    fun gank():CoroutineCall<GankResponse>
}