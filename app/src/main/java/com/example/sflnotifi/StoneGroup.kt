package com.example.sflnotifi

import com.example.sflnotifi.api.StonePlot

data class StoneGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<StonePlot>,
    var isExpanded: Boolean = false
) 