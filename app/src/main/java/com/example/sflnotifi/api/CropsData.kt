package com.example.sflnotifi.api

typealias CropsData = Map<String, CropPlot>

data class CropPlot(
    val createdAt: Long,
    val x: Int,
    val y: Int,
    val height: Int,
    val width: Int,
    val crop: CropInfo?
)

data class CropInfo(
    val id: String?,
    val plantedAt: Double?,
    val name: String?,
    val amount: Double?
) 