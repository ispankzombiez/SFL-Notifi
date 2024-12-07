package com.example.sflnotifi

import com.example.sflnotifi.api.GoldPlot

data class GoldGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<GoldPlot>,
    var isExpanded: Boolean = false
) 