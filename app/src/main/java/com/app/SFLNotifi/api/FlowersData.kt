package com.app.SFLNotifi.api

data class FlowersData(
    val flowerBeds: Map<String, FlowerBed>,
    val discovered: Map<String, Any>
)

data class FlowerBed(
    val createdAt: Long,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val flower: FlowerInfo
)

data class FlowerInfo(
    val plantedAt: Long,
    val amount: Int,
    val name: String
) 