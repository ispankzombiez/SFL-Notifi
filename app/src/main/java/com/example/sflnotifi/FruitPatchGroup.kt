package com.example.sflnotifi

import com.example.sflnotifi.api.FruitPatchPlot

data class FruitPatchGroup(
    val fruitName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<FruitPatchPlot>,
    var isExpanded: Boolean = false
) 