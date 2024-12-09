package com.app.SFLNotifi

import com.app.SFLNotifi.api.StonePlot

data class StoneGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<StonePlot>,
    var isExpanded: Boolean = false
) 