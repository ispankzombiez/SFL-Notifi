package com.app.SFLNotifi

import com.app.SFLNotifi.api.SunstonePlot

data class SunstoneGroup(
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<SunstonePlot>,
    var isExpanded: Boolean = false
) 