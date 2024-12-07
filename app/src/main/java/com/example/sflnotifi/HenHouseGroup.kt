package com.example.sflnotifi

import com.example.sflnotifi.api.Animal

data class HenHouseGroup(
    val wakeTime: Long,
    val chickens: List<Animal>,
    var isExpanded: Boolean = false
) 