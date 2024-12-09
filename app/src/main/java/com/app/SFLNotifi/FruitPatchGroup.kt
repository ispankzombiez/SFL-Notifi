package com.app.SFLNotifi

import com.app.SFLNotifi.api.FruitPatchPlot

data class FruitPatchGroup(
    val fruitName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<FruitPatchPlot>,
    var isExpanded: Boolean = false
) 