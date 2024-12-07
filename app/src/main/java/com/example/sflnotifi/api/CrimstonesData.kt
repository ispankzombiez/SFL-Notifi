package com.example.sflnotifi.api

typealias CrimstonesData = Map<String, CrimstonePlot>

data class CrimstonePlot(
    val createdAt: Long,
    val height: Int,
    val width: Int,
    val x: Int,
    val y: Int,
    val stone: CrimstoneInfo,
    val minesLeft: Int
)

data class CrimstoneInfo(
    val minedAt: Long,
    val amount: Double
) 