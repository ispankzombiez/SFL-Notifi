package com.example.sflnotifi.api

typealias FruitPatchesData = Map<String, FruitPatchPlot>

data class FruitPatchPlot(
    val height: Int,
    val width: Int,
    val x: Int,
    val y: Int,
    val createdAt: Long,
    val fruit: FruitInfo
)

data class FruitInfo(
    val name: String,
    val plantedAt: Long,
    val amount: Double,
    val harvestedAt: Long,
    val harvestsLeft: Int
) 