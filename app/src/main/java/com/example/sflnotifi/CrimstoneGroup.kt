package com.example.sflnotifi

import com.example.sflnotifi.api.CrimstonePlot

data class CrimstoneGroup(
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<CrimstonePlot>,
    var isExpanded: Boolean = false
) 