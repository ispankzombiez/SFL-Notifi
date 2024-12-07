package com.example.sflnotifi

import com.example.sflnotifi.api.OilReservePlot

data class OilReserveGroup(
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<OilReservePlot>,
    var isExpanded: Boolean = false
) 