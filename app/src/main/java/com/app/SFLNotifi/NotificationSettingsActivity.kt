package com.app.SFLNotifi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.app.SFLNotifi.databinding.ActivityNotificationSettingsBinding

class NotificationSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationSettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    private val farmDataRepository by lazy { FarmDataRepository.getInstance(preferencesManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notification Settings"

        // Load saved values
        binding.hoursInput.setText(preferencesManager.getRefreshIntervalHours().toString())
        binding.minutesInput.setText(preferencesManager.getRefreshIntervalMinutes().toString())

        // Add text change listeners
        binding.hoursInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveRefreshInterval()
            }
        })

        binding.minutesInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveRefreshInterval()
            }
        })

        // Setup notification types button
        binding.notificationTypesButton.setOnClickListener {
            startActivity(Intent(this, NotificationTypesActivity::class.java))
        }

        // Setup test notification button
        binding.testNotificationButton.setOnClickListener {
            // Disable the button temporarily
            binding.testNotificationButton.isEnabled = false
            
            // Update repository with test data
            val testTime = System.currentTimeMillis() + 10000 // 10 seconds from now
            farmDataRepository.setTestNotification(1, testTime)
            
            Toast.makeText(this, "Test notification scheduled for 10 seconds", Toast.LENGTH_SHORT).show()
            
            // Re-enable the button after 10 seconds
            lifecycleScope.launch {
                delay(10000)
                binding.testNotificationButton.isEnabled = true
            }
        }
    }

    private fun saveRefreshInterval() {
        try {
            // Handle empty inputs by using empty string checks first
            val hoursText = binding.hoursInput.text?.toString() ?: ""
            val minutesText = binding.minutesInput.text?.toString() ?: ""
            val daysText = binding.daysInput.text?.toString() ?: ""
            
            val hours = if (hoursText.isEmpty()) 0 else hoursText.toIntOrNull() ?: 0
            val minutes = if (minutesText.isEmpty()) 30 else minutesText.toIntOrNull() ?: 30
            val days = if (daysText.isEmpty()) 0 else daysText.toIntOrNull() ?: 0
            
            // Validate input
            if (hours < 0 || minutes < 0 || minutes >= 60 || days < 0) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Save the new values
            preferencesManager.saveRefreshInterval(hours, minutes)
            preferencesManager.saveRefreshIntervalDays(days)
            
            Toast.makeText(this, "Refresh interval updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("NotificationSettings", "Error saving interval", e)
            Toast.makeText(this, "Error: Invalid input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 