package com.app.SFLNotifi

import com.app.SFLNotifi.api.Animal

data class HenHouseGroup(
    val wakeTime: Long,
    val chickens: List<Animal>,
    var isExpanded: Boolean = false
) 