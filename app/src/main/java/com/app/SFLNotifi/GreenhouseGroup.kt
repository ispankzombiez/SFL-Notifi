package com.app.SFLNotifi

import com.app.SFLNotifi.api.Pot

data class GreenhouseGroup(
    val plantName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val pots: List<Pot>,
    var isExpanded: Boolean = false
) 