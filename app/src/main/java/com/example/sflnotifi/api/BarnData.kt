package com.example.sflnotifi.api

data class BarnData(
    val level: Int,
    val animals: Map<String, BarnAnimal>
)

data class BarnAnimal(
    val id: String,
    val type: String,
    val state: String,
    val asleepAt: Long,
    val experience: Int,
    val createdAt: Long,
    val item: String,
    val lovedAt: Long,
    val awakeAt: Long,
    val healthCheckedAt: Long,
    val multiplier: Int
) 