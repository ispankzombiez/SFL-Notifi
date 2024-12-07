package com.example.sflnotifi.api

import com.google.gson.annotations.SerializedName

data class FarmResponse(
    val state: FarmState,
    val bumpkin: Bumpkin?,
    val balance: String?
)

data class FarmState(
    val coins: Double,
    val balance: String,
    val inventory: Map<String, Any>,
    @SerializedName("previousInventory")
    val previousInventory: Map<String, Any>
)

data class Bumpkin(
    val experience: Double,
    val tokenUri: String,
    val id: Int
) 