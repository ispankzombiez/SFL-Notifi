package com.example.sflnotifi.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SunflowerApi {
    @GET("visit/{farmId}")
    suspend fun visitFarm(@Path("farmId") farmId: String): Response<JsonObject>
} 