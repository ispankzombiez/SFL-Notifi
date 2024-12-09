package com.app.SFLNotifi

import com.app.SFLNotifi.api.FlowerBed

data class FlowerGroup(
    val flowerName: String,
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<FlowerBed>,
    var isExpanded: Boolean = false
) 