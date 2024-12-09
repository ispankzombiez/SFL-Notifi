package com.app.SFLNotifi.api

typealias TreesData = Map<String, TreePlot>

data class TreePlot(
    val wood: WoodInfo
)

data class WoodInfo(
    val choppedAt: Long,
    val amount: Double
) 