package com.app.SFLNotifi.api

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SunflowerApiClient private constructor() {
    private val lastCallTime = AtomicLong(0)
    private val mutex = Mutex()
    private val api: SunflowerApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d(TAG, "Making API call to: ${request.url}")
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://api.sunflower-land.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SunflowerApi::class.java)
    }

    suspend fun getFarmData(farmId: String): JsonObject {
        mutex.withLock {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastCall = currentTime - lastCallTime.get()
            if (timeSinceLastCall < MIN_INTERVAL_MS) {
                delay(MIN_INTERVAL_MS - timeSinceLastCall)
            }
        }

        val response = api.visitFarm(farmId)

        if (!response.isSuccessful) {
            if (response.code() == 429) {
                throw RateLimitException()
            }
            throw Exception("API call failed with code: ${response.code()}")
        }

        val responseBody = response.body()
        if (responseBody == null) {
            throw Exception("Response body was null")
        }

        lastCallTime.set(System.currentTimeMillis())
        
        return responseBody
    }

    companion object {
        private const val TAG = "SunflowerApiClient"
        private const val MIN_INTERVAL_MS = 30_000 // 30 seconds minimum between calls
        
        @Volatile
        private var instance: SunflowerApiClient? = null

        fun getInstance(): SunflowerApiClient {
            return instance ?: synchronized(this) {
                instance ?: SunflowerApiClient().also { instance = it }
            }
        }
    }

    class RateLimitException : Exception("Rate limit exceeded")
} 