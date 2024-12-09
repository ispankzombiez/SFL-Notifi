package com.app.SFLNotifi

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import com.app.SFLNotifi.data.DataManager
import com.google.gson.JsonObject

class SFLNotifiApplication : Application() {
    lateinit var preferencesManager: PreferencesManager
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _farmData = MutableStateFlow<JsonObject?>(null)
    val farmData: StateFlow<JsonObject?> = _farmData

    companion object {
        private var instance: SFLNotifiApplication? = null
        private const val TAG = "SFLNotifiApplication"

        fun getInstance(): SFLNotifiApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesManager = PreferencesManager(this)
        
        // Start background service instead
        Intent(this, BackgroundService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    // This now only updates the UI state from stored data
    fun updateFarmDataFromStorage() {
        val dataManager = DataManager.getInstance()
        applicationScope.launch {
            _farmData.value = dataManager.getStoredData()
        }
    }
} 