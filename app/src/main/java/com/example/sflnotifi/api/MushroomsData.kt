package com.example.sflnotifi.api

data class MushroomsData(
    val mushrooms: Map<String, Any>,
    val spawnedAt: Long,
    val magicSpawnedAt: Long
) 