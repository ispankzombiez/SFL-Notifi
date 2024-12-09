package com.app.SFLNotifi.api

data class HenHouseData(
    val level: Int,
    val animals: Map<String, Animal>
)

data class Animal(
    val id: String,
    val state: String,
    val type: String,
    val createdAt: Long,
    val awakeAt: Long,
    val asleepAt: Long,
    val experience: Int,
    val lovedAt: Long,
    val item: String,
    val healthCheckedAt: Long,
    val multiplier: Int
) 