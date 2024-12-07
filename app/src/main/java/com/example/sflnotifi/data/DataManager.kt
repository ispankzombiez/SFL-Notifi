package com.example.sflnotifi.data

import android.util.Log
import com.google.gson.JsonObject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DataManager private constructor() {
    private val mutex = Mutex()
    private var storedData: JsonObject? = null

    suspend fun updateStoredData(data: JsonObject) {
        mutex.withLock {
            storedData = data
        }
    }

    suspend fun getStoredData(): JsonObject? {
        return mutex.withLock {
            storedData
        }
    }

    companion object {
        private const val TAG = "DataManager"
        
        @Volatile
        private var instance: DataManager? = null

        fun getInstance(): DataManager {
            return instance ?: synchronized(this) {
                instance ?: DataManager().also { instance = it }
            }
        }
    }
} 