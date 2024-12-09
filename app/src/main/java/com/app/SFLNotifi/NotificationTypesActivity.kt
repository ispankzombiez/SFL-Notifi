package com.app.SFLNotifi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.SFLNotifi.databinding.ActivityNotificationTypesBinding
import androidx.core.app.NotificationManagerCompat

class NotificationTypesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationTypesBinding
    private lateinit var preferencesManager: PreferencesManager
    private val farmDataRepository by lazy { FarmDataRepository.getInstance(preferencesManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        binding = ActivityNotificationTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notification Types"

        // Load saved states and setup listeners
        setupSwitch(binding.cropsSwitch, "crops")
        setupSwitch(binding.greenhouseSwitch, "greenhouse")
        setupSwitch(binding.treesSwitch, "trees")
        setupSwitch(binding.stonesSwitch, "stones")
        setupSwitch(binding.ironSwitch, "iron")
        setupSwitch(binding.goldSwitch, "gold")
        setupSwitch(binding.crimstoneSwitch, "crimstone")
        setupSwitch(binding.oilSwitch, "oil")
        setupSwitch(binding.sunstoneSwitch, "sunstone")
        setupSwitch(binding.fruitSwitch, "fruit")
        setupSwitch(binding.flowersSwitch, "flowers")
        setupSwitch(binding.buildingsSwitch, "buildings")
        setupSwitch(binding.compostersSwitch, "composters")
        setupSwitch(binding.mushroomsSwitch, "mushrooms")
        setupSwitch(binding.chickensSwitch, "chickens")
        setupSwitch(binding.barnSwitch, "barn")
    }

    private fun setupSwitch(switch: androidx.appcompat.widget.SwitchCompat, type: String) {
        // Load saved state
        val wasEnabled = preferencesManager.isNotificationTypeEnabled(type)
        switch.isChecked = wasEnabled
        
        // Setup listener
        switch.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.saveNotificationTypeEnabled(type, isChecked)
            
            if (!isChecked) {
                // Clear existing notifications based on type
                when (type) {
                    "crops" -> {
                        farmDataRepository.cropGroups.value.forEach { group ->
                            val notificationId = "crop-${group.cropName}-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "greenhouse" -> {
                        farmDataRepository.greenhouseGroups.value.forEach { group ->
                            val notificationId = "greenhouse-${group.plantName}-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "trees" -> {
                        farmDataRepository.treeGroups.value.forEach { group ->
                            val notificationId = "trees-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "stones" -> {
                        farmDataRepository.stoneGroups.value.forEach { group ->
                            val notificationId = "stones-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "iron" -> {
                        farmDataRepository.ironGroups.value.forEach { group ->
                            val notificationId = "iron-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "gold" -> {
                        farmDataRepository.goldGroups.value.forEach { group ->
                            val notificationId = "gold-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "crimstone" -> {
                        farmDataRepository.crimstoneGroups.value.forEach { group ->
                            val notificationId = "crimstone-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "oil" -> {
                        farmDataRepository.oilGroups.value.forEach { group ->
                            val notificationId = "oil-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "sunstone" -> {
                        farmDataRepository.sunstoneGroups.value.forEach { group ->
                            val notificationId = "sunstone-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "fruit" -> {
                        farmDataRepository.fruitGroups.value.forEach { group ->
                            val notificationId = "fruit-${group.fruitName}-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "flowers" -> {
                        farmDataRepository.flowerGroups.value.forEach { group ->
                            val notificationId = "flower-${group.flowerName}-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "buildings" -> {
                        farmDataRepository.buildingGroups.value.forEach { group ->
                            val notificationId = "building-${group.itemName}-${group.readyAt}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "composters" -> {
                        farmDataRepository.composterGroups.value.forEach { group ->
                            val notificationId = "composter-${group.readyAt}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "mushrooms" -> {
                        farmDataRepository.mushroomGroups.value.forEach { group ->
                            val notificationId = "mushroom-${group.mushroomType}-${group.harvestTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "chickens" -> {
                        farmDataRepository.henHouseGroups.value.forEach { group ->
                            val notificationId = "chickens-${group.wakeTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                    "barn" -> {
                        farmDataRepository.barnGroups.value.forEach { group ->
                            val notificationId = "barn-${group.animalType}-${group.wakeTime}"
                            NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                        }
                    }
                }
            }
            
            // Force refresh both views in MainActivity
            val refreshIntent = Intent("com.app.SFLNotifi.REFRESH_VIEW").apply {
                putExtra("updateType", type)
                putExtra("isEnabled", isChecked)
            }
            sendBroadcast(refreshIntent)
        }

        // Initial state handling
        if (!wasEnabled) {
            // Clear existing notifications based on type
            when (type) {
                "crops" -> {
                    farmDataRepository.cropGroups.value.forEach { group ->
                        val notificationId = "crop-${group.cropName}-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "greenhouse" -> {
                    farmDataRepository.greenhouseGroups.value.forEach { group ->
                        val notificationId = "greenhouse-${group.plantName}-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "trees" -> {
                    farmDataRepository.treeGroups.value.forEach { group ->
                        val notificationId = "trees-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "stones" -> {
                    farmDataRepository.stoneGroups.value.forEach { group ->
                        val notificationId = "stones-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "iron" -> {
                    farmDataRepository.ironGroups.value.forEach { group ->
                        val notificationId = "iron-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "gold" -> {
                    farmDataRepository.goldGroups.value.forEach { group ->
                        val notificationId = "gold-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "crimstone" -> {
                    farmDataRepository.crimstoneGroups.value.forEach { group ->
                        val notificationId = "crimstone-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "oil" -> {
                    farmDataRepository.oilGroups.value.forEach { group ->
                        val notificationId = "oil-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "sunstone" -> {
                    farmDataRepository.sunstoneGroups.value.forEach { group ->
                        val notificationId = "sunstone-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "fruit" -> {
                    farmDataRepository.fruitGroups.value.forEach { group ->
                        val notificationId = "fruit-${group.fruitName}-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "flowers" -> {
                    farmDataRepository.flowerGroups.value.forEach { group ->
                        val notificationId = "flower-${group.flowerName}-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "buildings" -> {
                    farmDataRepository.buildingGroups.value.forEach { group ->
                        val notificationId = "building-${group.itemName}-${group.readyAt}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "composters" -> {
                    farmDataRepository.composterGroups.value.forEach { group ->
                        val notificationId = "composter-${group.readyAt}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "mushrooms" -> {
                    farmDataRepository.mushroomGroups.value.forEach { group ->
                        val notificationId = "mushroom-${group.mushroomType}-${group.harvestTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "chickens" -> {
                    farmDataRepository.henHouseGroups.value.forEach { group ->
                        val notificationId = "chickens-${group.wakeTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
                "barn" -> {
                    farmDataRepository.barnGroups.value.forEach { group ->
                        val notificationId = "barn-${group.animalType}-${group.wakeTime}"
                        NotificationManagerCompat.from(this).cancel(notificationId.hashCode())
                    }
                }
            }
            
            // Force refresh both views in MainActivity
            val refreshIntent = Intent("com.app.SFLNotifi.REFRESH_VIEW").apply {
                putExtra("updateType", type)
                putExtra("isEnabled", false)
            }
            sendBroadcast(refreshIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 