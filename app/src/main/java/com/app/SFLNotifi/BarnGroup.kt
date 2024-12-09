package com.app.SFLNotifi

import com.app.SFLNotifi.api.BarnAnimal

data class BarnGroup(
    val animalType: String,  // "Cow" or "Sheep"
    val wakeTime: Long,
    val animals: List<BarnAnimal>,
    var isExpanded: Boolean = false
) 