package com.example.sflnotifi.api

typealias BuildingsData = Map<String, List<Building>>

data class Building(
    val id: String,
    val readyAt: Long,
    val coordinates: Coordinates,
    val createdAt: Long,
    val oil: Int? = null,
    val crafting: Crafting? = null,
    val requires: Map<String, Int>? = null,
    val producing: Production? = null,
    val queue: List<CropMachineItem>? = null,
    val unallocatedOilTime: Long? = null
)

data class Coordinates(
    val x: Int,
    val y: Int
)

data class Crafting(
    val name: String,
    val boost: Map<String, Int>,
    val amount: Int,
    val readyAt: Long
)

data class Production(
    val items: Map<String, Any>,
    val startedAt: Long,
    val readyAt: Long
)

data class CropMachineItem(
    val seeds: Int,
    val amount: Double,
    val crop: String,
    val growTimeRemaining: Long,
    val totalGrowTime: Long,
    val startTime: Long,
    val readyAt: Long
) 