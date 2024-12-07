package com.example.sflnotifi

import com.example.sflnotifi.api.IronPlot

data class IronGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<IronPlot>,
    var isExpanded: Boolean = false
) 