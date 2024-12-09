package com.app.SFLNotifi.api

typealias BeehivesData = Map<String, BeehivePlot>

data class BeehivePlot(
    val swarm: Boolean,
    val x: Int,
    val y: Int,
    val height: Int,
    val width: Int,
    val honey: HoneyInfo,
    val flowers: List<FlowerAttachment>
)

data class HoneyInfo(
    val updatedAt: Long,
    val produced: Double
)

data class FlowerAttachment(
    val attachedAt: Long,
    val attachedUntil: Long,
    val id: String,
    val rate: Double
) 