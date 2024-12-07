package com.example.sflnotifi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sflnotifi.databinding.ActivityRawDataBinding

class RawDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRawDataBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRawDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferencesManager = PreferencesManager(this)
        
        // Set up button click listeners
        binding.greenhouseButton.setOnClickListener {
            startDataDisplayActivity("Greenhouse", "greenhouse")
        }

        binding.cropsButton.setOnClickListener {
            startDataDisplayActivity("Crops", "crops")
        }

        binding.treesButton.setOnClickListener {
            startDataDisplayActivity("Trees", "trees")
        }

        binding.flowersButton.setOnClickListener {
            startDataDisplayActivity("Flowers", "flowers")
        }

        binding.beehivesButton.setOnClickListener {
            startDataDisplayActivity("Beehives", "beehives")
        }

        binding.buildingsButton.setOnClickListener {
            startDataDisplayActivity("Buildings", "buildings")
        }

        binding.mushroomsButton.setOnClickListener {
            startDataDisplayActivity("Mushrooms", "mushrooms")
        }

        binding.henHouseButton.setOnClickListener {
            startDataDisplayActivity("Hen House", "chickens")
        }

        binding.barnButton.setOnClickListener {
            startDataDisplayActivity("Barn", "barn")
        }
    }

    private fun startDataDisplayActivity(title: String, key: String) {
        val intent = Intent(this, DataDisplayActivity::class.java).apply {
            putExtra("title", title)
            putExtra("data", preferencesManager.getRawData(key))
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 