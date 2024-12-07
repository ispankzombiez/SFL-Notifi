package com.example.sflnotifi

import com.example.sflnotifi.api.Pot

data class GreenhouseGroup(
    val plantName: String,
    val harvestTime: Long,
    val totalAmount: Double,
    val pots: List<Pot>,
    var isExpanded: Boolean = false
) 