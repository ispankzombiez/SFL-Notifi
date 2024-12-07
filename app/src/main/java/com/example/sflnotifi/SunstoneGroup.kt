package com.example.sflnotifi

import com.example.sflnotifi.api.SunstonePlot

data class SunstoneGroup(
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<SunstonePlot>,
    var isExpanded: Boolean = false
) 