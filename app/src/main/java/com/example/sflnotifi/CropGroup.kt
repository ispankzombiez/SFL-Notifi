package com.example.sflnotifi

import com.example.sflnotifi.api.CropPlot

data class CropGroup(
    val cropName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<CropPlot>,
    var isExpanded: Boolean = false
) 