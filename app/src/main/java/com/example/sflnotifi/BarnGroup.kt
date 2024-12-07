package com.example.sflnotifi

import com.example.sflnotifi.api.BarnAnimal

data class BarnGroup(
    val animalType: String,  // "Cow" or "Sheep"
    val wakeTime: Long,
    val animals: List<BarnAnimal>,
    var isExpanded: Boolean = false
) 