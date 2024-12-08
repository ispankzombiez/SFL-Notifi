package com.example.sflnotifi

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sflnotifi.api.*
import com.example.sflnotifi.databinding.ActivityMainBinding
import com.example.sflnotifi.databinding.ActivityMainSequentialBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs
import java.util.concurrent.TimeUnit
import android.os.PowerManager
import android.content.Context.POWER_SERVICE
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var _bindingCategorized: ActivityMainBinding? = null
    private var _bindingSequential: ActivityMainSequentialBinding? = null
    private val bindingCategorized get() = _bindingCategorized!!
    private val bindingSequential get() = _bindingSequential!!
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var cropAdapter: CropGroupAdapter
    private lateinit var greenhouseAdapter: GreenhouseGroupAdapter
    private lateinit var treeAdapter: TreeGroupAdapter
    private lateinit var stoneAdapter: StoneGroupAdapter
    private lateinit var ironAdapter: IronGroupAdapter
    private lateinit var goldAdapter: GoldGroupAdapter
    private lateinit var crimstoneAdapter: CrimstoneGroupAdapter
    private lateinit var oilReserveAdapter: OilReserveGroupAdapter
    private lateinit var sunstoneAdapter: SunstoneGroupAdapter
    private lateinit var fruitPatchAdapter: FruitPatchGroupAdapter
    private lateinit var flowerAdapter: FlowerGroupAdapter
    private lateinit var buildingAdapter: BuildingGroupAdapter
    private lateinit var composterAdapter: ComposterGroupAdapter
    private lateinit var mushroomAdapter: MushroomGroupAdapter
    private lateinit var henHouseAdapter: HenHouseGroupAdapter
    private lateinit var barnAdapter: BarnGroupAdapter
    private var updateJob: Job? = null
    private lateinit var sequentialAdapter: SequentialItemAdapter
    private var currentViewMode: Boolean = false // false = categorized, true = sequential
    private val farmDataRepository by lazy { FarmDataRepository.getInstance(preferencesManager) }
    private lateinit var notificationHelper: NotificationHelper
    private val notifiedItems = mutableSetOf<String>() // Track items that have been notified
    private val itemsToRemove = mutableSetOf<String>() // Track items that need to be removed
    private val notificationThresholdMs = 5000 // 5 seconds
    private val removalDelayMs = 10000 // 10 seconds
    private var refreshReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        currentViewMode = preferencesManager.isSequentialMode()
        
        // Set the content view based on the current mode
        if (currentViewMode) {
            _bindingSequential = ActivityMainSequentialBinding.inflate(layoutInflater)
            setContentView(_bindingSequential!!.root)
            setSupportActionBar(_bindingSequential!!.toolbar)
        } else {
            _bindingCategorized = ActivityMainBinding.inflate(layoutInflater)
            setContentView(_bindingCategorized!!.root)
            setSupportActionBar(_bindingCategorized!!.toolbar)
        }

        setupRecyclerViews()
        setupFlowCollectors()
        startPeriodicUpdates()
        notificationHelper = NotificationHelper(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }

        checkAndRequestPermissions()

        // Start the background service
        Intent(this, BackgroundService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        // Initialize and register the receiver
        refreshReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.example.sflnotifi.REFRESH_VIEW") {
                    val updateType = intent.getStringExtra("updateType")
                    val isEnabled = intent.getBooleanExtra("isEnabled", true)
                    
                    lifecycleScope.launch {
                        _bindingCategorized?.let { binding ->
                            when (updateType) {
                                "crops" -> {
                                    binding.cropsHeader.visibility = if (!isEnabled) View.GONE else {
                                        if (farmDataRepository.cropGroups.value.isNotEmpty()) View.VISIBLE else View.GONE
                                    }
                                    binding.cropsList.visibility = binding.cropsHeader.visibility
                                    if (isEnabled) cropAdapter.updateGroups(farmDataRepository.cropGroups.value)
                                }
                                "greenhouse" -> {
                                    binding.greenhouseHeader.visibility = if (!isEnabled) View.GONE else {
                                        if (farmDataRepository.greenhouseGroups.value.isNotEmpty()) View.VISIBLE else View.GONE
                                    }
                                    binding.greenhouseList.visibility = binding.greenhouseHeader.visibility
                                    if (isEnabled) greenhouseAdapter.updateGroups(farmDataRepository.greenhouseGroups.value)
                                }
                                // ... Add cases for all other types following the same pattern
                            }
                        }
                        // Always update sequential view to maintain consistency
                        updateSequentialView()
                    }
                }
            }
        }

        // Register the receiver with the intent filter and specify exported status
        val filter = IntentFilter("com.example.sflnotifi.REFRESH_VIEW")
        registerReceiver(refreshReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    private fun setupRecyclerViews() {
        // Initialize all adapters
        sequentialAdapter = SequentialItemAdapter()
        treeAdapter = TreeGroupAdapter()
        greenhouseAdapter = GreenhouseGroupAdapter()
        cropAdapter = CropGroupAdapter()
        stoneAdapter = StoneGroupAdapter()
        ironAdapter = IronGroupAdapter()
        goldAdapter = GoldGroupAdapter()
        crimstoneAdapter = CrimstoneGroupAdapter()
        oilReserveAdapter = OilReserveGroupAdapter()
        sunstoneAdapter = SunstoneGroupAdapter()
        fruitPatchAdapter = FruitPatchGroupAdapter()
        flowerAdapter = FlowerGroupAdapter()
        buildingAdapter = BuildingGroupAdapter()
        composterAdapter = ComposterGroupAdapter()
        mushroomAdapter = MushroomGroupAdapter()
        henHouseAdapter = HenHouseGroupAdapter()
        barnAdapter = BarnGroupAdapter()

        // Setup both views but only show the current one
        setupSequentialView()
        setupCategorizedView()
        updateViewVisibility()
    }

    private fun setupSequentialView() {
        _bindingSequential?.sequentialList?.apply {
            adapter = sequentialAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupCategorizedView() {
        _bindingCategorized?.apply {
            // Setup all recycler views with their adapters
            treesList.apply {
                adapter = treeAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            greenhouseList.apply {
                adapter = greenhouseAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            cropsList.apply {
                adapter = cropAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            stonesList.apply {
                adapter = stoneAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            ironList.apply {
                adapter = ironAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            goldList.apply {
                adapter = goldAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            crimstonesList.apply {
                adapter = crimstoneAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            oilReservesList.apply {
                adapter = oilReserveAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            sunstonesList.apply {
                adapter = sunstoneAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            fruitPatchesList.apply {
                adapter = fruitPatchAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            flowersList.apply {
                adapter = flowerAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            buildingsList.apply {
                adapter = buildingAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            compostersList.apply {
                adapter = composterAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            mushroomsList.apply {
                adapter = mushroomAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            henHouseList.apply {
                adapter = henHouseAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
            barnList.apply {
                adapter = barnAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun updateViewVisibility() {
        if (currentViewMode) {
            // Sequential mode
            _bindingSequential?.root?.visibility = View.VISIBLE
            _bindingCategorized?.root?.visibility = View.GONE
        } else {
            // Categorized mode
            _bindingSequential?.root?.visibility = View.GONE
            _bindingCategorized?.root?.visibility = View.VISIBLE
        }
    }

    // Update the view mode change handler
    fun onViewModeChanged(isSequential: Boolean) {
        currentViewMode = isSequential
        preferencesManager.saveViewMode(isSequential)
        
        // Recreate the activity to properly switch views
        recreate()
    }

    private fun startPeriodicUpdates() {
        updateJob?.cancel()
        updateJob = lifecycleScope.launch {
            while (isActive) {
                // Update time displays based on current view mode
                if (currentViewMode) {  // true = sequential
                    sequentialAdapter.updateTimeDisplays()
                } else {  // false = categorized
                    cropAdapter.updateTimeDisplays()
                    greenhouseAdapter.updateTimeDisplays()
                    treeAdapter.updateTimeDisplays()
                    stoneAdapter.updateTimeDisplays()
                    ironAdapter.updateTimeDisplays()
                    goldAdapter.updateTimeDisplays()
                    crimstoneAdapter.updateTimeDisplays()
                    oilReserveAdapter.updateTimeDisplays()
                    sunstoneAdapter.updateTimeDisplays()
                    fruitPatchAdapter.updateTimeDisplays()
                    flowerAdapter.updateTimeDisplays()
                    buildingAdapter.updateTimeDisplays()
                    composterAdapter.updateTimeDisplays()
                    mushroomAdapter.updateTimeDisplays()
                    henHouseAdapter.updateTimeDisplays()
                    barnAdapter.updateTimeDisplays()
                }
                delay(1000)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivityForResult(
                    Intent(this, SettingsActivity::class.java),
                    SETTINGS_REQUEST_CODE
                )
                true
            }
            R.id.action_refresh -> {
                // Request immediate refresh from BackgroundService
                Intent(this, BackgroundService::class.java).also { intent ->
                    intent.action = "REFRESH_NOW"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Safely unregister the receiver
        refreshReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error unregistering receiver", e)
            }
        }
        updateJob?.cancel()
        _bindingCategorized = null
        _bindingSequential = null
    }

    private fun updateSequentialView() {
        lifecycleScope.launch {
            val allItems = mutableListOf<SequentialItem>()
            val currentTime = System.currentTimeMillis()
            val removalDelayMs = 10000 // 10 seconds

            // Remove test item section since it's now handled internally
            farmDataRepository.apply {
                // Crops
                if (preferencesManager.isNotificationTypeEnabled("crops")) {
                    cropGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.cropName,
                                category = "Crops",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount,
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.mapNotNull { it.crop?.amount }
                            ))
                        }
                    }
                }

                // Trees
                if (preferencesManager.isNotificationTypeEnabled("trees")) {
                    treeGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Trees",
                                category = "Trees",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.wood.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Greenhouse
                if (preferencesManager.isNotificationTypeEnabled("greenhouse")) {
                    greenhouseGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.plantName,
                                category = "Greenhouse",
                                nodeCount = group.pots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.pots.map { it.plant.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Stones
                if (preferencesManager.isNotificationTypeEnabled("stones")) {
                    stoneGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Stone",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.stone.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Iron
                if (preferencesManager.isNotificationTypeEnabled("iron")) {
                    ironGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Iron",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.stone.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Gold
                if (preferencesManager.isNotificationTypeEnabled("gold")) {
                    goldGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Gold",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.stone.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Crimstone
                if (preferencesManager.isNotificationTypeEnabled("crimstone")) {
                    crimstoneGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Crimstone",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.stone.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Oil
                if (preferencesManager.isNotificationTypeEnabled("oil")) {
                    oilGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Oil",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.oil.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Sunstone
                if (preferencesManager.isNotificationTypeEnabled("sunstone")) {
                    sunstoneGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Sunstone",
                                category = "Resources",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.stone.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Fruit
                if (preferencesManager.isNotificationTypeEnabled("fruit")) {
                    fruitGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.fruitName,
                                category = "Fruit",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.fruit.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Flowers
                if (preferencesManager.isNotificationTypeEnabled("flowers")) {
                    flowerGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.flowerName,
                                category = "Flowers",
                                nodeCount = group.plots.size,
                                totalAmount = group.totalAmount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = group.plots.map { it.flower.amount.toDouble() }
                            ))
                        }
                    }
                }

                // Buildings (Cooking)
                if (preferencesManager.isNotificationTypeEnabled("buildings")) {
                    buildingGroups.value.forEach { group ->
                        if (group.readyAt > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.itemName,
                                category = "Cooking",
                                nodeCount = group.buildings.size,
                                totalAmount = group.buildings.size.toDouble(),
                                completionTime = group.readyAt,
                                individualAmounts = group.buildings.map { 1.0 }
                            ))
                        }
                    }
                }

                // Composters
                if (preferencesManager.isNotificationTypeEnabled("composters")) {
                    composterGroups.value.forEach { group ->
                        if (group.readyAt > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.composterType,
                                category = "Composters",
                                nodeCount = group.buildings.size,
                                totalAmount = group.buildings.size.toDouble(),
                                completionTime = group.readyAt,
                                individualAmounts = group.buildings.map { 1.0 }
                            ))
                        }
                    }
                }

                // Mushrooms
                if (preferencesManager.isNotificationTypeEnabled("mushrooms")) {
                    mushroomGroups.value.forEach { group ->
                        if (group.harvestTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.mushroomType,
                                category = "Mushrooms",
                                nodeCount = 1,
                                totalAmount = group.amount.toDouble(),
                                completionTime = group.harvestTime,
                                individualAmounts = listOf(group.amount.toDouble())
                            ))
                        }
                    }
                }

                // Chickens
                if (preferencesManager.isNotificationTypeEnabled("chickens")) {
                    henHouseGroups.value.forEach { group ->
                        if (group.wakeTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = "Chickens",
                                category = "Animals",
                                nodeCount = group.chickens.size,
                                totalAmount = group.chickens.size.toDouble(),
                                completionTime = group.wakeTime,
                                individualAmounts = group.chickens.map { 1.0 }
                            ))
                        }
                    }
                }

                // Barn Animals
                barnGroups.value.forEach { group ->
                    val type = when(group.animalType) {
                        "Cow" -> "cows"
                        "Sheep" -> "sheep"
                        else -> group.animalType.lowercase()
                    }
                    if (preferencesManager.isNotificationTypeEnabled(type)) {
                        if (group.wakeTime > currentTime - removalDelayMs) {
                            allItems.add(SequentialItem(
                                name = group.animalType,
                                category = "Animals",
                                nodeCount = group.animals.size,
                                totalAmount = group.animals.size.toDouble(),
                                completionTime = group.wakeTime,
                                individualAmounts = group.animals.map { 1.0 }
                            ))
                        }
                    }
                }
            }

            // Sort and update adapter on the main thread
            withContext(Dispatchers.Main) {
                sequentialAdapter.updateItems(allItems.sortedBy { it.completionTime })
            }
        }
    }

    // Override onActivityResult to handle settings changes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let {
                if (it.getBooleanExtra("VIEW_MODE_CHANGED", false)) {
                    val isSequential = it.getBooleanExtra("IS_SEQUENTIAL", false)
                    currentViewMode = isSequential
                    preferencesManager.saveViewMode(isSequential)
                    recreate() // This will recreate the activity with the new view
                }
            }
        }
    }

    companion object {
        private const val SETTINGS_REQUEST_CODE = 100
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }

    private fun setupFlowCollectors() {
        // Remove test item collector since it's now handled internally
        lifecycleScope.launch {
            farmDataRepository.username.collect { username ->
                withContext(Dispatchers.Main) {
                    val displayText = username.takeIf { it.isNotEmpty() } ?: "No username"
                    Log.d("MainActivity", "Username flow received: '$username', displaying: '$displayText'")
                    
                    if (currentViewMode) {
                        _bindingSequential?.usernameText?.apply {
                            text = displayText
                            visibility = View.VISIBLE
                            Log.d("MainActivity", "Updated sequential view username to: $displayText")
                        }
                    } else {
                        _bindingCategorized?.usernameText?.apply {
                            text = displayText
                            visibility = View.VISIBLE
                            Log.d("MainActivity", "Updated categorized view username to: $displayText")
                        }
                    }
                }
            }
        }
        
        // Collect trees data
        lifecycleScope.launch {
            farmDataRepository.treeGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("trees") && groups.isNotEmpty()
                            binding.treesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.treesList.visibility = binding.treesHeader.visibility
                            if (shouldShow) {
                                treeAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect greenhouse data
        lifecycleScope.launch {
            farmDataRepository.greenhouseGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("greenhouse") && groups.isNotEmpty()
                            binding.greenhouseHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.greenhouseList.visibility = binding.greenhouseHeader.visibility
                            if (shouldShow) {
                                greenhouseAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect crops data
        lifecycleScope.launch {
            farmDataRepository.cropGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("crops") && groups.isNotEmpty()
                            binding.cropsHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.cropsList.visibility = binding.cropsHeader.visibility
                            if (shouldShow) {
                                cropAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect stones data
        lifecycleScope.launch {
            farmDataRepository.stoneGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("stones") && groups.isNotEmpty()
                            binding.stonesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.stonesList.visibility = binding.stonesHeader.visibility
                            if (shouldShow) {
                                stoneAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect iron data
        lifecycleScope.launch {
            farmDataRepository.ironGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("iron") && groups.isNotEmpty()
                            binding.ironHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.ironList.visibility = binding.ironHeader.visibility
                            if (shouldShow) {
                                ironAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect gold data
        lifecycleScope.launch {
            farmDataRepository.goldGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("gold") && groups.isNotEmpty()
                            binding.goldHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.goldList.visibility = binding.goldHeader.visibility
                            if (shouldShow) {
                                goldAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect crimstone data
        lifecycleScope.launch {
            farmDataRepository.crimstoneGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("crimstone") && groups.isNotEmpty()
                            binding.crimstonesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.crimstonesList.visibility = binding.crimstonesHeader.visibility
                            if (shouldShow) {
                                crimstoneAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect oil data
        lifecycleScope.launch {
            farmDataRepository.oilGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("oil") && groups.isNotEmpty()
                            binding.oilReservesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.oilReservesList.visibility = binding.oilReservesHeader.visibility
                            if (shouldShow) {
                                oilReserveAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect sunstone data
        lifecycleScope.launch {
            farmDataRepository.sunstoneGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("sunstone") && groups.isNotEmpty()
                            binding.sunstonesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.sunstonesList.visibility = binding.sunstonesHeader.visibility
                            if (shouldShow) {
                                sunstoneAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect fruit data
        lifecycleScope.launch {
            farmDataRepository.fruitGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("fruit") && groups.isNotEmpty()
                            binding.fruitPatchesHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.fruitPatchesList.visibility = binding.fruitPatchesHeader.visibility
                            if (shouldShow) {
                                fruitPatchAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect flower data
        lifecycleScope.launch {
            farmDataRepository.flowerGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("flowers") && groups.isNotEmpty()
                            binding.flowersHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.flowersList.visibility = binding.flowersHeader.visibility
                            if (shouldShow) {
                                flowerAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect building data
        lifecycleScope.launch {
            farmDataRepository.buildingGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("buildings") && groups.isNotEmpty()
                            binding.buildingsHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.buildingsList.visibility = binding.buildingsHeader.visibility
                            if (shouldShow) {
                                buildingAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect composter data
        lifecycleScope.launch {
            farmDataRepository.composterGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("composters") && groups.isNotEmpty()
                            binding.compostersHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.compostersList.visibility = binding.compostersHeader.visibility
                            if (shouldShow) {
                                composterAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect mushroom data
        lifecycleScope.launch {
            farmDataRepository.mushroomGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("mushrooms") && groups.isNotEmpty()
                            binding.mushroomsHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.mushroomsList.visibility = binding.mushroomsHeader.visibility
                            if (shouldShow) {
                                mushroomAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect hen house data
        lifecycleScope.launch {
            farmDataRepository.henHouseGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("chickens") && groups.isNotEmpty()
                            binding.henHouseHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.henHouseList.visibility = binding.henHouseHeader.visibility
                            if (shouldShow) {
                                henHouseAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }

        // Collect barn data
        lifecycleScope.launch {
            farmDataRepository.barnGroups.collect { groups ->
                when (currentViewMode) {
                    true -> updateSequentialView()
                    false -> {
                        _bindingCategorized?.let { binding ->
                            val shouldShow = preferencesManager.isNotificationTypeEnabled("barn") && groups.isNotEmpty()
                            binding.barnHeader.visibility = if (shouldShow) View.VISIBLE else View.GONE
                            binding.barnList.visibility = binding.barnHeader.visibility
                            if (shouldShow) {
                                barnAdapter.updateGroups(groups)
                            }
                        }
                        updateSequentialView()
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        // Request to disable battery optimization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val packageName = packageName
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:$packageName")
                }
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.e("MainActivity", "Battery optimization settings not available", e)
                    applicationContext?.let { context ->
                        Toast.makeText(
                            context,
                            "Please manually disable battery optimization for this app in settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    applicationContext?.let { context ->
                        Toast.makeText(
                            context,
                            "Notifications permission is required for timer alerts",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        
        // Update username from preferences
        val savedUsername = preferencesManager.getUsernameData()
        val displayText = savedUsername.takeIf { it.isNotEmpty() } ?: "No username"
        if (currentViewMode) {
            _bindingSequential?.usernameText?.apply {
                text = displayText
                visibility = View.VISIBLE
            }
        } else {
            _bindingCategorized?.usernameText?.apply {
                text = displayText
                visibility = View.VISIBLE
            }
        }
        
        if (currentViewMode) {
            updateSequentialView()
        } else {
            // Update categorized view
            _bindingCategorized?.let { binding ->
                // Handle all types
                val types = listOf(
                    "crops" to Pair(binding.cropsHeader, binding.cropsList),
                    "greenhouse" to Pair(binding.greenhouseHeader, binding.greenhouseList),
                    "trees" to Pair(binding.treesHeader, binding.treesList),
                    "stones" to Pair(binding.stonesHeader, binding.stonesList),
                    "iron" to Pair(binding.ironHeader, binding.ironList),
                    "gold" to Pair(binding.goldHeader, binding.goldList),
                    "crimstone" to Pair(binding.crimstonesHeader, binding.crimstonesList),
                    "oil" to Pair(binding.oilReservesHeader, binding.oilReservesList),
                    "sunstone" to Pair(binding.sunstonesHeader, binding.sunstonesList),
                    "fruit" to Pair(binding.fruitPatchesHeader, binding.fruitPatchesList),
                    "flowers" to Pair(binding.flowersHeader, binding.flowersList),
                    "buildings" to Pair(binding.buildingsHeader, binding.buildingsList),
                    "composters" to Pair(binding.compostersHeader, binding.compostersList),
                    "mushrooms" to Pair(binding.mushroomsHeader, binding.mushroomsList),
                    "chickens" to Pair(binding.henHouseHeader, binding.henHouseList),
                    "barn" to Pair(binding.barnHeader, binding.barnList)
                )

                // Update visibility and data for each type
                types.forEach { (type, views) ->
                    val isEnabled = preferencesManager.isNotificationTypeEnabled(type)
                    val hasItems = when (type) {
                        "crops" -> farmDataRepository.cropGroups.value.isNotEmpty()
                        "greenhouse" -> farmDataRepository.greenhouseGroups.value.isNotEmpty()
                        "trees" -> farmDataRepository.treeGroups.value.isNotEmpty()
                        "stones" -> farmDataRepository.stoneGroups.value.isNotEmpty()
                        "iron" -> farmDataRepository.ironGroups.value.isNotEmpty()
                        "gold" -> farmDataRepository.goldGroups.value.isNotEmpty()
                        "crimstone" -> farmDataRepository.crimstoneGroups.value.isNotEmpty()
                        "oil" -> farmDataRepository.oilGroups.value.isNotEmpty()
                        "sunstone" -> farmDataRepository.sunstoneGroups.value.isNotEmpty()
                        "fruit" -> farmDataRepository.fruitGroups.value.isNotEmpty()
                        "flowers" -> farmDataRepository.flowerGroups.value.isNotEmpty()
                        "buildings" -> farmDataRepository.buildingGroups.value.isNotEmpty()
                        "composters" -> farmDataRepository.composterGroups.value.isNotEmpty()
                        "mushrooms" -> farmDataRepository.mushroomGroups.value.isNotEmpty()
                        "chickens" -> farmDataRepository.henHouseGroups.value.isNotEmpty()
                        "barn" -> farmDataRepository.barnGroups.value.isNotEmpty()
                        else -> false
                    }

                    val visibility = if (!isEnabled || !hasItems) View.GONE else View.VISIBLE
                    views.first.visibility = visibility
                    views.second.visibility = visibility

                    // Update adapter if enabled and has items
                    if (isEnabled && hasItems) {
                        when (type) {
                            "crops" -> cropAdapter.updateGroups(farmDataRepository.cropGroups.value)
                            "greenhouse" -> greenhouseAdapter.updateGroups(farmDataRepository.greenhouseGroups.value)
                            "trees" -> treeAdapter.updateGroups(farmDataRepository.treeGroups.value)
                            "stones" -> stoneAdapter.updateGroups(farmDataRepository.stoneGroups.value)
                            "iron" -> ironAdapter.updateGroups(farmDataRepository.ironGroups.value)
                            "gold" -> goldAdapter.updateGroups(farmDataRepository.goldGroups.value)
                            "crimstone" -> crimstoneAdapter.updateGroups(farmDataRepository.crimstoneGroups.value)
                            "oil" -> oilReserveAdapter.updateGroups(farmDataRepository.oilGroups.value)
                            "sunstone" -> sunstoneAdapter.updateGroups(farmDataRepository.sunstoneGroups.value)
                            "fruit" -> fruitPatchAdapter.updateGroups(farmDataRepository.fruitGroups.value)
                            "flowers" -> flowerAdapter.updateGroups(farmDataRepository.flowerGroups.value)
                            "buildings" -> buildingAdapter.updateGroups(farmDataRepository.buildingGroups.value)
                            "composters" -> composterAdapter.updateGroups(farmDataRepository.composterGroups.value)
                            "mushrooms" -> mushroomAdapter.updateGroups(farmDataRepository.mushroomGroups.value)
                            "chickens" -> henHouseAdapter.updateGroups(farmDataRepository.henHouseGroups.value)
                            "barn" -> barnAdapter.updateGroups(farmDataRepository.barnGroups.value)
                        }
                    }
                }
            }
        }
        
        // Make sure sequential view is also updated
        updateSequentialView()
    }

    // Add new data class to store categorized view data
    private data class CategorizedData(
        val crops: List<CropGroup> = emptyList(),
        val greenhouse: List<GreenhouseGroup> = emptyList(),
        val trees: List<TreeGroup> = emptyList(),
        val stones: List<StoneGroup> = emptyList(),
        val iron: List<IronGroup> = emptyList(),
        val gold: List<GoldGroup> = emptyList(),
        val crimstone: List<CrimstoneGroup> = emptyList(),
        val oil: List<OilReserveGroup> = emptyList(),
        val sunstone: List<SunstoneGroup> = emptyList(),
        val fruit: List<FruitPatchGroup> = emptyList(),
        val flowers: List<FlowerGroup> = emptyList(),
        val buildings: List<BuildingGroup> = emptyList(),
        val composters: List<ComposterGroup> = emptyList(),
        val mushrooms: List<MushroomGroup> = emptyList(),
        val henHouse: List<HenHouseGroup> = emptyList(),
        val barn: List<BarnGroup> = emptyList()
    )

    // Add method to save current view data
    private fun saveCurrentViewData() {
        if (currentViewMode) {
            val sequentialData = Gson().toJson(sequentialAdapter.getItems())
            preferencesManager.saveSequentialData(sequentialData)
        } else {
            val categorizedData = CategorizedData(
                crops = cropAdapter.getGroups(),
                greenhouse = greenhouseAdapter.getGroups(),
                trees = treeAdapter.getGroups(),
                stones = stoneAdapter.getGroups(),
                iron = ironAdapter.getGroups(),
                gold = goldAdapter.getGroups(),
                crimstone = crimstoneAdapter.getGroups(),
                oil = oilReserveAdapter.getGroups(),
                sunstone = sunstoneAdapter.getGroups(),
                fruit = fruitPatchAdapter.getGroups(),
                flowers = flowerAdapter.getGroups(),
                buildings = buildingAdapter.getGroups(),
                composters = composterAdapter.getGroups(),
                mushrooms = mushroomAdapter.getGroups(),
                henHouse = henHouseAdapter.getGroups(),
                barn = barnAdapter.getGroups()
            )
            preferencesManager.saveCategorizedData(Gson().toJson(categorizedData))
        }
        preferencesManager.saveLastViewUpdateTime(System.currentTimeMillis())
    }

    override fun onPause() {
        super.onPause()
        saveCurrentViewData()
    }

    // Add this new method
    private fun refreshData() {
        if (currentViewMode) {
            // Sequential mode
            updateSequentialView()
        } else {
            // Categorized mode
            _bindingCategorized?.let { binding ->
                // Update all categorized views
                if (preferencesManager.isNotificationTypeEnabled("crops")) {
                    cropAdapter.updateGroups(farmDataRepository.cropGroups.value)
                }
                if (preferencesManager.isNotificationTypeEnabled("greenhouse")) {
                    greenhouseAdapter.updateGroups(farmDataRepository.greenhouseGroups.value)
                }
                if (preferencesManager.isNotificationTypeEnabled("trees")) {
                    treeAdapter.updateGroups(farmDataRepository.treeGroups.value)
                }
                // ... continue for other adapters
            }
        }
    }
} 