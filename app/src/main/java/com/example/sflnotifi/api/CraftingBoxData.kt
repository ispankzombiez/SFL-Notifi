package com.example.sflnotifi.api

data class CraftingBoxData(
    val status: String,
    val startedAt: Long,
    val readyAt: Long,
    val recipes: Map<String, Any>
) 