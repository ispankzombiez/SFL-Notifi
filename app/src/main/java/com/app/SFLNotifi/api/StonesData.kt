package com.app.SFLNotifi.api

typealias StonesData = Map<String, StonePlot>

data class StonePlot(
    val stone: StoneInfo
)

data class StoneInfo(
    val minedAt: Long,
    val amount: Double
) 