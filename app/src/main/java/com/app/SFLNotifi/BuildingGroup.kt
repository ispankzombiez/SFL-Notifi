package com.app.SFLNotifi

import com.app.SFLNotifi.api.Building

data class BuildingGroup(
    val itemName: String,
    val buildingType: String,
    val readyAt: Long,
    val buildings: List<Building>,
    var isExpanded: Boolean = false
) 