package com.example.sflnotifi

import com.example.sflnotifi.api.TreePlot

data class TreeGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<TreePlot>,
    var isExpanded: Boolean = false
) 