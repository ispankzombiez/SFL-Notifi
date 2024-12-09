package com.app.SFLNotifi

import com.app.SFLNotifi.api.Building

data class ComposterGroup(
    val composterType: String,
    val readyAt: Long,
    val buildings: List<Building>,
    var isExpanded: Boolean = false
) 