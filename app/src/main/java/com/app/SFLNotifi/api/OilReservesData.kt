package com.app.SFLNotifi.api

typealias OilReservesData = Map<String, OilReservePlot>

data class OilReservePlot(
    val height: Int,
    val width: Int,
    val x: Int,
    val y: Int,
    val createdAt: Long,
    val drilled: Int,
    val oil: OilInfo
)

data class OilInfo(
    val amount: Int,
    val drilledAt: Long
) 