package com.example.sflnotifi.api

data class GreenhouseData(
    val pots: Map<String, Pot>
)

data class Pot(
    val plant: Plant
)

data class Plant(
    val amount: Double,
    val name: String,
    val plantedAt: Long
) 