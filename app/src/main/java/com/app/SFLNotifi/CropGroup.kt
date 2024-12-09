package com.app.SFLNotifi

import com.app.SFLNotifi.api.CropPlot

data class CropGroup(
    val cropName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val plots: List<CropPlot>,
    var isExpanded: Boolean = false
) 