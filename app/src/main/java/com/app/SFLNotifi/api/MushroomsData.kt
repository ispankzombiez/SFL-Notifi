package com.app.SFLNotifi.api

data class MushroomsData(
    val mushrooms: Map<String, Any>,
    val spawnedAt: Long,
    val magicSpawnedAt: Long
) 