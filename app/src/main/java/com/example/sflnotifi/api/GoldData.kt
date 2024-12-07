package com.example.sflnotifi.api

typealias GoldData = Map<String, GoldPlot>

data class GoldPlot(
    val stone: GoldInfo
)

data class GoldInfo(
    val minedAt: Long,
    val amount: Double
) 