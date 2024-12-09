package com.app.SFLNotifi

import com.app.SFLNotifi.api.IronPlot

data class IronGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<IronPlot>,
    var isExpanded: Boolean = false
) 