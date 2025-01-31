package com.sofascoreacademy.minisofa.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

const val BASE_URL = "https://academy-backend.sofascore.dev/"

// checked with a few requests
const val ITEMS_PER_PAGE = 10

object Network {
    @Volatile
    private var INSTANCE: ApiService? = null

    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else ...
        })
        .connectTimeout(10.seconds.toJavaDuration()).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()



    fun getInstance(): ApiService {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: retrofit.create(ApiService::class.java).also { INSTANCE = it }
        }
    }
}