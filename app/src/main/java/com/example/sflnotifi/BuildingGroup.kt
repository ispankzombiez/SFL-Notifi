package com.example.sflnotifi

import com.example.sflnotifi.api.Building

data class BuildingGroup(
    val itemName: String,
    val buildingType: String,
    val readyAt: Long,
    val buildings: List<Building>,
    var isExpanded: Boolean = false
) 