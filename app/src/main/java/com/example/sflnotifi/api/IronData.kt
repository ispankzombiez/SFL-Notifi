package com.example.sflnotifi.api

typealias IronData = Map<String, IronPlot>

data class IronPlot(
    val stone: IronInfo
)

data class IronInfo(
    val minedAt: Long,
    val amount: Double
) 