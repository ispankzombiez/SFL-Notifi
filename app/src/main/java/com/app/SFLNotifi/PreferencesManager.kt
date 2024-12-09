package com.app.SFLNotifi

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SFLNotifi", Context.MODE_PRIVATE)
    private val VIEW_MODE_KEY = "view_mode"

    fun saveFarmId(farmId: String) {
        prefs.edit().putString("farm_id", farmId).apply()
    }

    fun getFarmId(): String {
        return prefs.getString("farm_id", "") ?: ""
    }

    fun saveGreenhouseData(data: String) {
        prefs.edit().putString("greenhouse_data", data).apply()
    }

    fun getGreenhouseData(): String {
        return prefs.getString("greenhouse_data", "") ?: ""
    }

    fun saveCropsData(data: String) {
        prefs.edit().putString("crops_data", data).apply()
    }

    fun getCropsData(): String {
        return prefs.getString("crops_data", "") ?: ""
    }

    fun saveTreesData(data: String) {
        prefs.edit().putString("trees_data", data).apply()
    }

    fun getTreesData(): String {
        return prefs.getString("trees_data", "") ?: ""
    }

    fun saveStonesData(data: String) {
        prefs.edit().putString("stones_data", data).apply()
    }

    fun getStonesData(): String {
        return prefs.getString("stones_data", "") ?: ""
    }

    fun saveIronData(data: String) {
        prefs.edit().putString("iron_data", data).apply()
    }

    fun getIronData(): String {
        return prefs.getString("iron_data", "") ?: ""
    }

    fun saveGoldData(data: String) {
        prefs.edit().putString("gold_data", data).apply()
    }

    fun getGoldData(): String {
        return prefs.getString("gold_data", "") ?: ""
    }

    fun saveCrimstonesData(data: String) {
        prefs.edit().putString("crimstones_data", data).apply()
    }

    fun getCrimstonesData(): String {
        return prefs.getString("crimstones_data", "") ?: ""
    }

    fun saveOilReservesData(data: String) {
        prefs.edit().putString("oil_reserves_data", data).apply()
    }

    fun getOilReservesData(): String {
        return prefs.getString("oil_reserves_data", "") ?: ""
    }

    fun saveSunstonesData(data: String) {
        prefs.edit().putString("sunstones_data", data).apply()
    }

    fun getSunstonesData(): String {
        return prefs.getString("sunstones_data", "") ?: ""
    }

    fun saveFruitPatchesData(data: String) {
        prefs.edit().putString("fruit_patches_data", data).apply()
    }

    fun getFruitPatchesData(): String {
        return prefs.getString("fruit_patches_data", "") ?: ""
    }

    fun saveFlowersData(data: String) {
        prefs.edit().putString("flowers_data", data).apply()
    }

    fun getFlowersData(): String {
        return prefs.getString("flowers_data", "") ?: ""
    }

    fun saveBeehivesData(data: String) {
        prefs.edit().putString("beehives_data", data).apply()
    }

    fun getBeehivesData(): String {
        return prefs.getString("beehives_data", "") ?: ""
    }

    fun saveBuildingsData(data: String) {
        prefs.edit().putString("buildings_data", data).apply()
    }

    fun getBuildingsData(): String {
        return prefs.getString("buildings_data", "") ?: ""
    }

    fun saveMushroomsData(data: String) {
        prefs.edit().putString("mushrooms_data", data).apply()
    }

    fun getMushroomsData(): String {
        return prefs.getString("mushrooms_data", "") ?: ""
    }

    fun saveUsernameData(data: String) {
        prefs.edit().putString("username_data", data).apply()
    }

    fun getUsernameData(): String {
        return prefs.getString("username_data", "") ?: ""
    }

    fun saveHenHouseData(data: String) {
        prefs.edit().putString("hen_house_data", data).apply()
    }

    fun getHenHouseData(): String {
        return prefs.getString("hen_house_data", "") ?: ""
    }

    fun saveBarnData(data: String) {
        prefs.edit().putString("barn_data", data).apply()
    }

    fun getBarnData(): String {
        return prefs.getString("barn_data", "") ?: ""
    }

    fun saveCraftingBoxData(data: String) {
        prefs.edit().putString("crafting_box_data", data).apply()
    }

    fun getCraftingBoxData(): String {
        return prefs.getString("crafting_box_data", "") ?: ""
    }

    fun saveViewMode(isSequential: Boolean) {
        prefs.edit().putBoolean(VIEW_MODE_KEY, isSequential).apply()
    }

    fun isSequentialMode(): Boolean {
        return prefs.getBoolean(VIEW_MODE_KEY, false) // false = categorized (default)
    }

    fun saveRefreshInterval(hours: Int, minutes: Int) {
        prefs.edit()
            .putInt("refresh_interval_hours", hours)
            .putInt("refresh_interval_minutes", minutes)
            .apply()
    }

    fun getRefreshIntervalHours(): Int {
        return try {
            prefs.getInt("refresh_interval_hours", 0)
        } catch (e: ClassCastException) {
            val stringValue = prefs.getString("refresh_interval_hours", "0")
            try {
                stringValue?.toInt() ?: 0
            } catch (e: NumberFormatException) {
                0
            }
        }
    }

    fun getRefreshIntervalMinutes(): Int {
        return try {
            prefs.getInt("refresh_interval_minutes", 30)
        } catch (e: ClassCastException) {
            val stringValue = prefs.getString("refresh_interval_minutes", "30")
            try {
                stringValue?.toInt() ?: 30
            } catch (e: NumberFormatException) {
                30
            }
        }
    }

    fun getRefreshIntervalDays(): Int {
        return try {
            prefs.getInt("refresh_interval_days", 1)
        } catch (e: ClassCastException) {
            val stringValue = prefs.getString("refresh_interval_days", "1")
            try {
                stringValue?.toInt() ?: 1
            } catch (e: NumberFormatException) {
                1
            }
        }
    }

    fun saveRefreshIntervalDays(days: Int) {
        prefs.edit()
            .putInt("refresh_interval_days", days)
            .apply()
    }

    fun saveNotificationTypeEnabled(type: String, enabled: Boolean) {
        prefs.edit().putBoolean("notification_type_$type", enabled).apply()
    }

    fun isNotificationTypeEnabled(type: String): Boolean {
        return prefs.getBoolean("notification_type_$type", true) // Default to true
    }

    fun saveLastUpdateTime(time: Long) {
        prefs.edit().putLong("last_update_time", time).apply()
    }

    fun getLastUpdateTime(): Long {
        return prefs.getLong("last_update_time", 0)
    }

    fun saveSequentialData(data: String) {
        prefs.edit().putString("sequential_data", data).apply()
    }

    fun getSequentialData(): String {
        return prefs.getString("sequential_data", "") ?: ""
    }

    fun saveCategorizedData(data: String) {
        prefs.edit().putString("categorized_data", data).apply()
    }

    fun getCategorizedData(): String {
        return prefs.getString("categorized_data", "") ?: ""
    }

    fun saveLastViewUpdateTime(time: Long) {
        prefs.edit().putLong("last_view_update_time", time).apply()
    }

    fun getLastViewUpdateTime(): Long {
        return prefs.getLong("last_view_update_time", 0)
    }

    fun saveRawData(key: String, data: String) {
        prefs.edit().putString("raw_data_$key", data).apply()
    }

    fun getRawData(key: String): String {
        return prefs.getString("raw_data_$key", "") ?: ""
    }

    companion object {
        private const val DEFAULT_REFRESH_HOURS = 0
        private const val DEFAULT_REFRESH_MINUTES = 30
    }
} 