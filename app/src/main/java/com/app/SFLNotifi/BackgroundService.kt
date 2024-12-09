package com.app.SFLNotifi

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import android.util.Log
import com.app.SFLNotifi.api.SunflowerApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BackgroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var farmDataRepository: FarmDataRepository
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var api: SunflowerApi
    private val notifiedItems = mutableSetOf<String>()
    private val itemsToRemove = mutableSetOf<String>()
    private val notificationThresholdMs = 5000 // 5 seconds
    private var isRunning = false

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "SFLNotifi_Service_Channel"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        farmDataRepository = FarmDataRepository.getInstance(preferencesManager)
        notificationHelper = NotificationHelper(this)
        setupApi()
        startForeground()
        startPeriodicUpdates()
        isRunning = true
    }

    private fun setupApi() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://api.sunflower-land.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SunflowerApi::class.java)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "SFL Notifi Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("SFL Notifi")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun startPeriodicUpdates() {
        serviceScope.launch {
            while (isActive) {
                try {
                    // Get refresh interval from preferences
                    val days = preferencesManager.getRefreshIntervalDays()
                    val hours = preferencesManager.getRefreshIntervalHours()
                    val minutes = preferencesManager.getRefreshIntervalMinutes()
                    val refreshIntervalMs = ((days * 24L * 60L) + (hours * 60L) + minutes) * 60L * 1000L
                    
                    val lastUpdateTime = preferencesManager.getLastUpdateTime()
                    val currentTime = System.currentTimeMillis()
                    
                    // Only make API call if enough time has passed
                    if ((currentTime - lastUpdateTime) >= refreshIntervalMs) {
                        val farmId = preferencesManager.getFarmId()
                        if (farmId.isNotEmpty()) {
                            val response = api.visitFarm(farmId)
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()!!
                                Log.d("BackgroundService", "Received API response with data")
                                
                                // Store the raw data
                                data.getAsJsonObject("state")?.let { state ->
                                    // Store individual sections
                                    state.getAsJsonObject("greenhouse")?.let { greenhouse ->
                                        preferencesManager.saveRawData("greenhouse", greenhouse.toString())
                                    } ?: preferencesManager.saveRawData("greenhouse", "null")

                                    state.getAsJsonObject("inventory")?.let { inventory ->
                                        preferencesManager.saveRawData("inventory", inventory.toString())
                                    } ?: preferencesManager.saveRawData("inventory", "null")

                                    state.getAsJsonObject("crops")?.let { crops ->
                                        preferencesManager.saveRawData("crops", crops.toString())
                                    } ?: preferencesManager.saveRawData("crops", "null")

                                    state.getAsJsonObject("trees")?.let { trees ->
                                        preferencesManager.saveRawData("trees", trees.toString())
                                    } ?: preferencesManager.saveRawData("trees", "null")

                                    state.getAsJsonObject("chickens")?.let { chickens ->
                                        preferencesManager.saveRawData("chickens", chickens.toString())
                                    } ?: preferencesManager.saveRawData("chickens", "null")

                                    state.getAsJsonObject("barn")?.let { barn ->
                                        preferencesManager.saveRawData("barn", barn.toString())
                                    } ?: preferencesManager.saveRawData("barn", "null")

                                    state.getAsJsonObject("flowers")?.let { flowers ->
                                        preferencesManager.saveRawData("flowers", flowers.toString())
                                    } ?: preferencesManager.saveRawData("flowers", "null")

                                    state.getAsJsonObject("beehives")?.let { beehives ->
                                        preferencesManager.saveRawData("beehives", beehives.toString())
                                    } ?: preferencesManager.saveRawData("beehives", "null")

                                    state.getAsJsonObject("buildings")?.let { buildings ->
                                        preferencesManager.saveRawData("buildings", buildings.toString())
                                    } ?: preferencesManager.saveRawData("buildings", "null")

                                    state.getAsJsonObject("mushrooms")?.let { mushrooms ->
                                        preferencesManager.saveRawData("mushrooms", mushrooms.toString())
                                    } ?: preferencesManager.saveRawData("mushrooms", "null")
                                }
                                
                                // Continue with existing update logic
                                farmDataRepository.updateData(data, Gson())
                                preferencesManager.saveLastUpdateTime(currentTime)
                                
                                // Notify app to update UI
                                val app = applicationContext as SFLNotifiApplication
                                app.updateFarmDataFromStorage()
                            }
                        }
                    }
                    
                    // Check notifications
                    checkTimersAndNotify()
                    delay(1000) // Check every second
                } catch (e: Exception) {
                    Log.e("BackgroundService", "Error in periodic updates", e)
                    delay(1000)
                }
            }
        }
    }

    private fun checkTimersAndNotify() {
        val currentTime = System.currentTimeMillis()
        val removalDelayMs = 10000 // 10 seconds
        
        fun checkItem(name: String, amount: Int, completionTime: Long, type: String) {
            // First check if this notification type is enabled
            if (!preferencesManager.isNotificationTypeEnabled(type)) {
                return
            }

            val timeLeft = completionTime - currentTime
            val itemKey = "$name-$amount-$completionTime"

            when {
                timeLeft in 0..notificationThresholdMs && !notifiedItems.contains(itemKey) -> {
                    Log.d("BackgroundService", "Sending notification for: $itemKey")
                    notificationHelper.showNotification(
                        "$name is ready ($amount)",
                        ""
                    )
                    notifiedItems.add(itemKey)
                }
                timeLeft < -removalDelayMs -> {
                    Log.d("BackgroundService", "Marking for removal: $itemKey")
                    itemsToRemove.add(itemKey)
                    // Keep test item clearing
                    if (name == "Test Item") {
                        farmDataRepository.clearTestNotification()
                    }
                }
            }
        }

        // Check test item first
        farmDataRepository.getTestItem()?.let { testItem ->
            checkItem("Test Item", testItem.amount, testItem.completionTime, "test")
        }

        // Check all other types with proper type identifiers
        farmDataRepository.apply {
            cropGroups.value.forEach { group ->
                checkItem(group.cropName, group.totalAmount.toInt(), group.harvestTime, "crops")
            }
            
            treeGroups.value.forEach { group ->
                checkItem("Trees", group.totalAmount.toInt(), group.harvestTime, "trees")
            }
            
            greenhouseGroups.value.forEach { group ->
                checkItem(group.plantName, group.totalAmount.toInt(), group.harvestTime, "greenhouse")
            }
            
            stoneGroups.value.forEach { group ->
                checkItem("Stone", group.totalAmount.toInt(), group.harvestTime, "stones")
            }
            
            ironGroups.value.forEach { group ->
                checkItem("Iron", group.totalAmount.toInt(), group.harvestTime, "iron")
            }
            
            goldGroups.value.forEach { group ->
                checkItem("Gold", group.totalAmount.toInt(), group.harvestTime, "gold")
            }
            
            crimstoneGroups.value.forEach { group ->
                checkItem("Crimstone", group.totalAmount.toInt(), group.harvestTime, "crimstone")
            }
            
            oilGroups.value.forEach { group ->
                checkItem("Oil", group.totalAmount.toInt(), group.harvestTime, "oil")
            }
            
            sunstoneGroups.value.forEach { group ->
                checkItem("Sunstone", group.totalAmount.toInt(), group.harvestTime, "sunstone")
            }
            
            fruitGroups.value.forEach { group ->
                checkItem(group.fruitName, group.totalAmount.toInt(), group.harvestTime, "fruit")
            }
            
            flowerGroups.value.forEach { group ->
                checkItem(group.flowerName, group.totalAmount.toInt(), group.harvestTime, "flowers")
            }
            
            buildingGroups.value.forEach { group ->
                checkItem(group.itemName, group.buildings.size, group.readyAt, "buildings")
            }
            
            composterGroups.value.forEach { group ->
                checkItem(group.composterType, group.buildings.size, group.readyAt, "composters")
            }
            
            mushroomGroups.value.forEach { group ->
                checkItem(group.mushroomType, group.amount.toInt(), group.harvestTime, "mushrooms")
            }
            
            henHouseGroups.value.forEach { group ->
                checkItem("Chickens", group.chickens.size, group.wakeTime, "chickens")
            }
            
            barnGroups.value.forEach { group ->
                checkItem(group.animalType, group.animals.size, group.wakeTime, when(group.animalType) {
                    "Cow" -> "cows"
                    "Sheep" -> "sheep"
                    else -> group.animalType.lowercase()
                })
            }
        }

        notifiedItems.removeAll { itemKey -> itemsToRemove.contains(itemKey) }
        itemsToRemove.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        if (intent?.action == "REFRESH_NOW") {
            serviceScope.launch {
                try {
                    val farmId = preferencesManager.getFarmId()
                    if (farmId.isNotEmpty()) {
                        val response = api.visitFarm(farmId)
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            farmDataRepository.updateData(data, Gson())
                            preferencesManager.saveLastUpdateTime(System.currentTimeMillis())
                            
                            // Notify app to update UI
                            val app = applicationContext as SFLNotifiApplication
                            app.updateFarmDataFromStorage()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BackgroundService", "Error in manual refresh", e)
                }
            }
        }
        
        return START_STICKY
    }
} 