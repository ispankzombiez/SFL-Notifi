package com.example.sflnotifi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sflnotifi.databinding.ActivitySettingsBinding
import com.google.gson.Gson
import com.example.sflnotifi.api.UsernameData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast
import android.content.Intent
import android.net.Uri

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper
    private val farmDataRepository by lazy { FarmDataRepository.getInstance(preferencesManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        preferencesManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)
        
        // Load saved farm ID
        binding.farmIdInput.setText(preferencesManager.getFarmId())
        
        // Setup view mode switch
        binding.viewModeSwitch.isChecked = preferencesManager.isSequentialMode()
        binding.viewModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.saveViewMode(isChecked)
            setResult(RESULT_OK)
            // Notify MainActivity to update its view
            val intent = Intent()
            intent.putExtra("VIEW_MODE_CHANGED", true)
            intent.putExtra("IS_SEQUENTIAL", isChecked)
            setResult(RESULT_OK, intent)
            finish()
        }
        
        // Save farm ID when it changes
        binding.farmIdInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                preferencesManager.saveFarmId(binding.farmIdInput.text.toString())
            }
        }

        // Setup notification settings button
        binding.notificationSettingsButton.setOnClickListener {
            try {
                val intent = Intent(this, NotificationSettingsActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening notification settings", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup donate button
        binding.donateButton.setOnClickListener {
            val intent = Intent(this, DonateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        // Save farm ID when leaving the activity
        preferencesManager.saveFarmId(binding.farmIdInput.text.toString())
    }
} 