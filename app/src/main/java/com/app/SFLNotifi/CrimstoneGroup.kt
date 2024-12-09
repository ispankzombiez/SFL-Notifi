package com.app.SFLNotifi

import com.app.SFLNotifi.api.CrimstonePlot

data class CrimstoneGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<CrimstonePlot>,
    var isExpanded: Boolean = false
) 