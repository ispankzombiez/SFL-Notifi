package com.app.SFLNotifi

data class MushroomGroup(
    val harvestTime: Long,
    val mushroomType: String,
    val amount: Int,
    var isExpanded: Boolean = false
) 