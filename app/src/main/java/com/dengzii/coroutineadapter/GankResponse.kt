package com.dengzii.coroutineadapter

data class GankResponse(
    val data: List<Data>
)

data class Data(
    val _id: String,
    val desc: String,
    val type: String
)