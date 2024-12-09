package com.app.SFLNotifi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.SFLNotifi.databinding.ActivitySavedBinding

class SavedActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferencesManager = PreferencesManager(this)
        
        // Set up button click listeners
        binding.greenhouseButton.setOnClickListener {
            startDataDisplayActivity("Greenhouse", preferencesManager.getGreenhouseData())
        }

        binding.cropsButton.setOnClickListener {
            startDataDisplayActivity("Crops", preferencesManager.getCropsData())
        }

        binding.treesButton.setOnClickListener {
            startDataDisplayActivity("Trees", preferencesManager.getTreesData())
        }

        binding.stonesButton.setOnClickListener {
            startDataDisplayActivity("Stones", preferencesManager.getStonesData())
        }

        binding.ironButton.setOnClickListener {
            startDataDisplayActivity("Iron", preferencesManager.getIronData())
        }

        binding.goldButton.setOnClickListener {
            startDataDisplayActivity("Gold", preferencesManager.getGoldData())
        }

        binding.crimstonesButton.setOnClickListener {
            startDataDisplayActivity("Crimstones", preferencesManager.getCrimstonesData())
        }

        binding.oilReservesButton.setOnClickListener {
            startDataDisplayActivity("Oil Reserves", preferencesManager.getOilReservesData())
        }

        binding.sunstonesButton.setOnClickListener {
            startDataDisplayActivity("Sunstones", preferencesManager.getSunstonesData())
        }

        binding.fruitPatchesButton.setOnClickListener {
            startDataDisplayActivity("Fruit Patches", preferencesManager.getFruitPatchesData())
        }

        binding.flowersButton.setOnClickListener {
            startDataDisplayActivity("Flowers", preferencesManager.getFlowersData())
        }

        binding.beehivesButton.setOnClickListener {
            startDataDisplayActivity("Beehives", preferencesManager.getBeehivesData())
        }

        binding.buildingsButton.setOnClickListener {
            startDataDisplayActivity("Buildings", preferencesManager.getBuildingsData())
        }

        binding.mushroomsButton.setOnClickListener {
            startDataDisplayActivity("Mushrooms", preferencesManager.getMushroomsData())
        }

        binding.usernameButton.setOnClickListener {
            startDataDisplayActivity("Username", preferencesManager.getUsernameData())
        }

        binding.henHouseButton.setOnClickListener {
            startDataDisplayActivity("Hen House", preferencesManager.getHenHouseData())
        }

        binding.barnButton.setOnClickListener {
            startDataDisplayActivity("Barn", preferencesManager.getBarnData())
        }

        binding.craftingBoxButton.setOnClickListener {
            startDataDisplayActivity("Crafting Box", preferencesManager.getCraftingBoxData())
        }
    }

    private fun startDataDisplayActivity(title: String, data: String) {
        val intent = Intent(this, DataDisplayActivity::class.java).apply {
            putExtra("TITLE", title)
            putExtra("DATA", data)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 