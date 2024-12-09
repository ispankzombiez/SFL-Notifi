package com.app.SFLNotifi

import com.app.SFLNotifi.api.GoldPlot

data class GoldGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<GoldPlot>,
    var isExpanded: Boolean = false
) 