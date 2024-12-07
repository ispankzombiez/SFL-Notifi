package com.example.sflnotifi

import com.example.sflnotifi.api.Building

data class ComposterGroup(
    val composterType: String,
    val readyAt: Long,
    val buildings: List<Building>,
    var isExpanded: Boolean = false
) 