package com.example.sflnotifi

import com.example.sflnotifi.api.FlowerBed

data class FlowerGroup(
    val flowerName: String,
    val harvestTime: Long,
    val totalAmount: Int,
    val plots: List<FlowerBed>,
    var isExpanded: Boolean = false
) 