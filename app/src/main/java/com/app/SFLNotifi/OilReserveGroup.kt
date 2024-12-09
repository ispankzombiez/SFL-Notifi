package com.app.SFLNotifi

import com.app.SFLNotifi.api.OilReservePlot

data class OilReserveGroup(
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<OilReservePlot>,
    var isExpanded: Boolean = false
) 