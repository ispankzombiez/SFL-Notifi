package com.example.sflnotifi

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sflnotifi.databinding.ActivityDataDisplayBinding

class DataDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: "Data"
        val data = intent.getStringExtra("data") ?: "null"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title

        // Set text color based on theme
        val textColor = if (isNightMode()) {
            getColor(android.R.color.white)
        } else {
            getColor(android.R.color.black)
        }
        
        binding.dataText.setTextColor(textColor)
        binding.dataText.text = data
    }

    private fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 