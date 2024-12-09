package com.app.SFLNotifi

import com.app.SFLNotifi.api.TreePlot

data class TreeGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<TreePlot>,
    var isExpanded: Boolean = false
) 