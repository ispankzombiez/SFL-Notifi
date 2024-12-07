package com.example.sflnotifi.api

typealias SunstonesData = Map<String, SunstonePlot>

data class SunstonePlot(
    val height: Int,
    val width: Int,
    val x: Int,
    val y: Int,
    val stone: SunstoneInfo,
    val minesLeft: Int,
    val createdAt: Long
)

data class SunstoneInfo(
    val minedAt: Long,
    val amount: Int
) 