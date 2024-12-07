package com.example.sflnotifi

import com.example.sflnotifi.api.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs
import android.util.Log

class FarmDataRepository private constructor(private val preferencesManager: PreferencesManager) {
    private val _treeGroups = MutableStateFlow<List<TreeGroup>>(emptyList())
    private val _greenhouseGroups = MutableStateFlow<List<GreenhouseGroup>>(emptyList())
    private val _cropGroups = MutableStateFlow<List<CropGroup>>(emptyList())
    private val _stoneGroups = MutableStateFlow<List<StoneGroup>>(emptyList())
    private val _ironGroups = MutableStateFlow<List<IronGroup>>(emptyList())
    private val _goldGroups = MutableStateFlow<List<GoldGroup>>(emptyList())
    private val _crimstoneGroups = MutableStateFlow<List<CrimstoneGroup>>(emptyList())
    private val _oilGroups = MutableStateFlow<List<OilReserveGroup>>(emptyList())
    private val _sunstoneGroups = MutableStateFlow<List<SunstoneGroup>>(emptyList())
    private val _fruitGroups = MutableStateFlow<List<FruitPatchGroup>>(emptyList())
    private val _flowerGroups = MutableStateFlow<List<FlowerGroup>>(emptyList())
    private val _buildingGroups = MutableStateFlow<List<BuildingGroup>>(emptyList())
    private val _composterGroups = MutableStateFlow<List<ComposterGroup>>(emptyList())
    private val _mushroomGroups = MutableStateFlow<List<MushroomGroup>>(emptyList())
    private val _henHouseGroups = MutableStateFlow<List<HenHouseGroup>>(emptyList())
    private val _barnGroups = MutableStateFlow<List<BarnGroup>>(emptyList())
    private val _username = MutableStateFlow("")
    private val _testItem = MutableStateFlow<TestItem?>(null)

    // Public read-only flows
    val treeGroups: StateFlow<List<TreeGroup>> = _treeGroups
    val greenhouseGroups: StateFlow<List<GreenhouseGroup>> = _greenhouseGroups
    val cropGroups: StateFlow<List<CropGroup>> = _cropGroups
    val stoneGroups: StateFlow<List<StoneGroup>> = _stoneGroups
    val ironGroups: StateFlow<List<IronGroup>> = _ironGroups
    val goldGroups: StateFlow<List<GoldGroup>> = _goldGroups
    val crimstoneGroups: StateFlow<List<CrimstoneGroup>> = _crimstoneGroups
    val oilGroups: StateFlow<List<OilReserveGroup>> = _oilGroups
    val sunstoneGroups: StateFlow<List<SunstoneGroup>> = _sunstoneGroups
    val fruitGroups: StateFlow<List<FruitPatchGroup>> = _fruitGroups
    val flowerGroups: StateFlow<List<FlowerGroup>> = _flowerGroups
    val buildingGroups: StateFlow<List<BuildingGroup>> = _buildingGroups
    val composterGroups: StateFlow<List<ComposterGroup>> = _composterGroups
    val mushroomGroups: StateFlow<List<MushroomGroup>> = _mushroomGroups
    val henHouseGroups: StateFlow<List<HenHouseGroup>> = _henHouseGroups
    val barnGroups: StateFlow<List<BarnGroup>> = _barnGroups
    val username: StateFlow<String> = _username

    fun updateData(jsonObject: JsonObject, gson: Gson) {
        // Update username
        try {
            // Get username directly from the state object
            jsonObject.getAsJsonObject("state")?.get("username")?.let { usernameElement ->
                if (!usernameElement.isJsonNull) {
                    val newUsername = usernameElement.asString
                    Log.d("FarmDataRepository", "Setting username in repository: '$newUsername'")
                    _username.value = newUsername
                    preferencesManager.saveUsernameData(newUsername)
                    Log.d("FarmDataRepository", "Username updated successfully to: '${_username.value}'")
                }
            }
        } catch (e: Exception) {
            Log.e("FarmDataRepository", "Error updating username", e)
        }

        val stateObject = jsonObject.getAsJsonObject("state")

        // Process trees
        stateObject.get("trees")?.let { trees ->
            val treesJson = gson.toJson(trees)
            val treesData: Map<String, TreePlot> = gson.fromJson(treesJson, 
                object : TypeToken<Map<String, TreePlot>>() {}.type)
            
            // Group trees by harvest time
            val groupedTrees = mutableMapOf<Long, MutableList<TreePlot>>()
            treesData.values.forEach { plot ->
                val harvestTime = plot.wood.choppedAt + (GrowTimes.TREE * 1000L)
                val groupTime = groupedTrees.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedTrees.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            // Convert to TreeGroups and update StateFlow
            _treeGroups.value = groupedTrees.map { (harvestTime, plots) ->
                TreeGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.wood.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process greenhouse
        stateObject.get("greenhouse")?.let { greenhouse ->
            val greenhouseJson = gson.toJson(greenhouse)
            val greenhouseData: GreenhouseData = gson.fromJson(greenhouseJson, GreenhouseData::class.java)
            
            // Group pots by harvest time and plant name
            val groupedPots = mutableMapOf<Pair<Long, String>, MutableList<Pot>>()
            
            greenhouseData.pots.values.forEach { pot ->
                val plantedTime = pot.plant.plantedAt
                val plantName = pot.plant.name
                
                val growthTime = when (plantName.lowercase()) {
                    "rice" -> GrowTimes.RICE * 1000L
                    "olive" -> GrowTimes.OLIVE * 1000L
                    "grape" -> GrowTimes.GRAPE * 1000L
                    else -> 0L
                }
                
                val harvestTime = plantedTime + growthTime
                
                // Find a group within 2 minutes with same plant name
                val groupKey = groupedPots.keys.firstOrNull { (existingTime, existingName) ->
                    abs(existingTime - harvestTime) <= 120000 && existingName == plantName
                } ?: (harvestTime to plantName)
                
                groupedPots.getOrPut(groupKey) { mutableListOf() }.add(pot)
            }
            
            // Convert to GreenhouseGroups
            _greenhouseGroups.value = groupedPots.map { (key, pots) ->
                val (harvestTime, plantName) = key
                GreenhouseGroup(
                    plantName = plantName,
                    harvestTime = harvestTime,
                    totalAmount = pots.sumOf { it.plant.amount },
                    pots = pots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process crops
        stateObject.get("crops")?.let { crops ->
            val cropsJson = gson.toJson(crops)
            val cropsData: Map<String, CropPlot> = gson.fromJson(cropsJson, 
                object : TypeToken<Map<String, CropPlot>>() {}.type)
            
            // Group crops by harvest time and crop name
            val groupedCrops = mutableMapOf<Pair<Long, String>, MutableList<CropPlot>>()
            
            cropsData.values
                .filter { plot -> plot.crop?.plantedAt != null && plot.crop.name != null }
                .forEach { plot ->
                    val plantedTime = plot.crop?.plantedAt?.toLong() ?: return@forEach
                    val cropName = plot.crop.name ?: return@forEach
                    
                    val growthTime = when (cropName.lowercase()) {
                        "sunflower" -> GrowTimes.SUNFLOWER * 1000L
                        "potato" -> GrowTimes.POTATO * 1000L
                        "pumpkin" -> GrowTimes.PUMPKIN * 1000L
                        "carrot" -> GrowTimes.CARROT * 1000L
                        "cabbage" -> GrowTimes.CABBAGE * 1000L
                        "soybean" -> GrowTimes.SOYBEAN * 1000L
                        "beetroot" -> GrowTimes.BEETROOT * 1000L
                        "cauliflower" -> GrowTimes.CAULIFLOWER * 1000L
                        "parsnip" -> GrowTimes.PARSNIP * 1000L
                        "eggplant" -> GrowTimes.EGGPLANT * 1000L
                        "corn" -> GrowTimes.CORN * 1000L
                        "radish" -> GrowTimes.RADISH * 1000L
                        "wheat" -> GrowTimes.WHEAT * 1000L
                        "kale" -> GrowTimes.KALE * 1000L
                        "barley" -> GrowTimes.BARLEY * 1000L
                        else -> 0L
                    }
                    
                    val harvestTime = plantedTime + growthTime
                    
                    // Find a group within 2 minutes with same crop name
                    val groupKey = groupedCrops.keys.firstOrNull { (existingTime, existingName) ->
                        abs(existingTime - harvestTime) <= 120000 && existingName == cropName
                    } ?: (harvestTime to cropName)
                    
                    groupedCrops.getOrPut(groupKey) { mutableListOf() }.add(plot)
                }
            
            // Convert to CropGroups
            _cropGroups.value = groupedCrops.map { (key, plots) ->
                val (harvestTime, cropName) = key
                CropGroup(
                    cropName = cropName,
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.crop?.amount ?: 0.0 },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process stones
        stateObject.get("stones")?.let { stones ->
            val stonesJson = gson.toJson(stones)
            val stonesData: Map<String, StonePlot> = gson.fromJson(stonesJson, 
                object : TypeToken<Map<String, StonePlot>>() {}.type)
            
            val groupedStones = mutableMapOf<Long, MutableList<StonePlot>>()
            stonesData.values.forEach { plot ->
                val harvestTime = plot.stone.minedAt + (GrowTimes.STONE * 1000L)
                val groupTime = groupedStones.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedStones.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _stoneGroups.value = groupedStones.map { (harvestTime, plots) ->
                StoneGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.stone.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process iron
        stateObject.get("iron")?.let { iron ->
            val ironJson = gson.toJson(iron)
            val ironData: Map<String, IronPlot> = gson.fromJson(ironJson, 
                object : TypeToken<Map<String, IronPlot>>() {}.type)
            
            val groupedIron = mutableMapOf<Long, MutableList<IronPlot>>()
            ironData.values.forEach { plot ->
                val harvestTime = plot.stone.minedAt + (GrowTimes.IRON * 1000L)
                val groupTime = groupedIron.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedIron.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _ironGroups.value = groupedIron.map { (harvestTime, plots) ->
                IronGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.stone.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process gold
        stateObject.get("gold")?.let { gold ->
            val goldJson = gson.toJson(gold)
            val goldData: Map<String, GoldPlot> = gson.fromJson(goldJson, 
                object : TypeToken<Map<String, GoldPlot>>() {}.type)
            
            val groupedGold = mutableMapOf<Long, MutableList<GoldPlot>>()
            goldData.values.forEach { plot ->
                val harvestTime = plot.stone.minedAt + (GrowTimes.GOLD * 1000L)
                val groupTime = groupedGold.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedGold.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _goldGroups.value = groupedGold.map { (harvestTime, plots) ->
                GoldGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.stone.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process crimstones
        stateObject.get("crimstones")?.let { crimstones ->
            val crimstonesJson = gson.toJson(crimstones)
            val crimstonesData: Map<String, CrimstonePlot> = gson.fromJson(crimstonesJson, 
                object : TypeToken<Map<String, CrimstonePlot>>() {}.type)
            
            val groupedCrimstones = mutableMapOf<Long, MutableList<CrimstonePlot>>()
            crimstonesData.values.forEach { plot ->
                val harvestTime = plot.stone.minedAt + (GrowTimes.CRIMSTONE * 1000L)
                val groupTime = groupedCrimstones.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedCrimstones.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _crimstoneGroups.value = groupedCrimstones.map { (harvestTime, plots) ->
                CrimstoneGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.stone.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process oil reserves
        stateObject.get("oilReserves")?.let { oilReserves ->
            val oilJson = gson.toJson(oilReserves)
            val oilData: Map<String, OilReservePlot> = gson.fromJson(oilJson, 
                object : TypeToken<Map<String, OilReservePlot>>() {}.type)
            
            val groupedOil = mutableMapOf<Long, MutableList<OilReservePlot>>()
            oilData.values.forEach { plot ->
                val harvestTime = plot.oil.drilledAt + (GrowTimes.OIL * 1000L)
                val groupTime = groupedOil.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedOil.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _oilGroups.value = groupedOil.map { (harvestTime, plots) ->
                OilReserveGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.oil.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process sunstones
        stateObject.get("sunstones")?.let { sunstones ->
            val sunstonesJson = gson.toJson(sunstones)
            val sunstonesData: Map<String, SunstonePlot> = gson.fromJson(sunstonesJson, 
                object : TypeToken<Map<String, SunstonePlot>>() {}.type)
            
            val groupedSunstones = mutableMapOf<Long, MutableList<SunstonePlot>>()
            sunstonesData.values.forEach { plot ->
                val harvestTime = plot.stone.minedAt + (GrowTimes.SUNSTONE * 1000L)
                val groupTime = groupedSunstones.keys.firstOrNull { existingTime ->
                    abs(existingTime - harvestTime) <= 120000
                } ?: harvestTime
                groupedSunstones.getOrPut(groupTime) { mutableListOf() }.add(plot)
            }
            
            _sunstoneGroups.value = groupedSunstones.map { (harvestTime, plots) ->
                SunstoneGroup(
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.stone.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process fruit patches
        stateObject.get("fruitPatches")?.let { fruitPatches ->
            val fruitPatchesJson = gson.toJson(fruitPatches)
            val fruitPatchesData: Map<String, FruitPatchPlot> = gson.fromJson(fruitPatchesJson, 
                object : TypeToken<Map<String, FruitPatchPlot>>() {}.type)
            
            val groupedFruitPatches = mutableMapOf<Pair<Long, String>, MutableList<FruitPatchPlot>>()
            
            fruitPatchesData.values.forEach { plot ->
                val growthTime = when (plot.fruit.name.lowercase()) {
                    "tomato" -> GrowTimes.FRUIT_TOMATO * 1000L
                    "lemon" -> GrowTimes.FRUIT_LEMON * 1000L
                    "blueberry" -> GrowTimes.FRUIT_BLUEBERRY * 1000L
                    "orange" -> GrowTimes.FRUIT_ORANGE * 1000L
                    "apple" -> GrowTimes.FRUIT_APPLE * 1000L
                    "banana" -> GrowTimes.FRUIT_BANANA * 1000L
                    else -> GrowTimes.FRUIT_TOMATO * 1000L
                }
                
                val harvestTime = if (plot.fruit.harvestedAt != 0L) {
                    plot.fruit.harvestedAt + growthTime
                } else {
                    plot.fruit.plantedAt + growthTime
                }
                
                val fruitName = plot.fruit.name
                
                val groupKey = groupedFruitPatches.keys.firstOrNull { (existingTime, existingName) ->
                    abs(existingTime - harvestTime) <= 120000 && existingName == fruitName
                } ?: (harvestTime to fruitName)
                
                groupedFruitPatches.getOrPut(groupKey) { mutableListOf() }.add(plot)
            }
            
            _fruitGroups.value = groupedFruitPatches.map { (key, plots) ->
                val (harvestTime, fruitName) = key
                FruitPatchGroup(
                    fruitName = fruitName,
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.fruit.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process flowers
        stateObject.get("flowers")?.let { flowers ->
            val flowersJson = gson.toJson(flowers)
            val flowersData: FlowersData = gson.fromJson(flowersJson, FlowersData::class.java)
            
            val groupedFlowers = mutableMapOf<Pair<Long, String>, MutableList<FlowerBed>>()
            
            flowersData.flowerBeds.values.forEach { flowerBed ->
                val growthTime = when (flowerBed.flower.name.lowercase()) {
                    "red pansy", "yellow pansy", "purple pansy", "white pansy", "blue pansy",
                    "red cosmos", "yellow cosmos", "purple cosmos", "white cosmos", "blue cosmos",
                    "prism petal" -> GrowTimes.PANSY_RED * 1000L
                    
                    "red balloon flower", "yellow balloon flower", "purple balloon flower",
                    "white balloon flower", "blue balloon flower",
                    "red daffodil", "yellow daffodil", "purple daffodil",
                    "white daffodil", "blue daffodil",
                    "celestial frostbloom" -> GrowTimes.BALLOON_RED * 1000L
                    
                    "red carnation", "yellow carnation", "purple carnation",
                    "white carnation", "blue carnation",
                    "red lotus", "yellow lotus", "purple lotus",
                    "white lotus", "blue lotus",
                    "primula enigma" -> GrowTimes.CARNATION_RED * 1000L
                    
                    else -> GrowTimes.PANSY_RED * 1000L
                }
                
                val harvestTime = flowerBed.flower.plantedAt + growthTime
                val flowerName = flowerBed.flower.name
                
                val groupKey = groupedFlowers.keys.firstOrNull { (existingTime, existingName) ->
                    abs(existingTime - harvestTime) <= 120000 && existingName == flowerName
                } ?: (harvestTime to flowerName)
                
                groupedFlowers.getOrPut(groupKey) { mutableListOf() }.add(flowerBed)
            }
            
            _flowerGroups.value = groupedFlowers.map { (key, plots) ->
                val (harvestTime, flowerName) = key
                FlowerGroup(
                    flowerName = flowerName,
                    harvestTime = harvestTime,
                    totalAmount = plots.sumOf { it.flower.amount },
                    plots = plots
                )
            }.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process buildings (food buildings)
        stateObject.get("buildings")?.let { buildings ->
            val buildingsJson = gson.toJson(buildings)
            val buildingsData: Map<String, List<Building>> = gson.fromJson(buildingsJson, 
                object : TypeToken<Map<String, List<Building>>>() {}.type)
            
            val foodBuildingTypes = setOf(
                "Fire Pit", "Kitchen", "Deli", "Smoothie Shack", "Bakery"
            )
            
            val groupedBuildings = mutableMapOf<Pair<String, Long>, MutableList<Building>>()
            
            foodBuildingTypes.forEach { buildingType ->
                buildingsData[buildingType]?.forEach { building ->
                    if (building.crafting != null && building.crafting.readyAt > System.currentTimeMillis()) {
                        val itemName = building.crafting.name
                        val readyAt = building.crafting.readyAt
                        
                        val groupKey = groupedBuildings.keys.firstOrNull { (existingName, existingTime) ->
                            abs(existingTime - readyAt) <= 120000 && existingName == itemName
                        } ?: (itemName to readyAt)
                        
                        groupedBuildings.getOrPut(groupKey) { mutableListOf() }.add(building)
                    }
                }
            }
            
            _buildingGroups.value = groupedBuildings.map { (key, buildings) ->
                val (itemName, readyAt) = key
                BuildingGroup(
                    itemName = itemName,
                    buildingType = buildings.first().id,
                    readyAt = readyAt,
                    buildings = buildings
                )
            }.filter { 
                it.readyAt > System.currentTimeMillis() - 10000 
            }.sortedBy { it.readyAt }
        }

        // Process composters
        stateObject.get("buildings")?.let { buildings ->
            val buildingsJson = gson.toJson(buildings)
            val buildingsData: Map<String, List<Building>> = gson.fromJson(buildingsJson, 
                object : TypeToken<Map<String, List<Building>>>() {}.type)
            
            val composterTypes = setOf(
                "Compost Bin", "Turbo Composter", "Premium Composter"
            )
            
            val groupedComposters = mutableMapOf<Pair<String, Long>, MutableList<Building>>()
            
            composterTypes.forEach { composterType ->
                buildingsData[composterType]?.forEach { building ->
                    if (building.producing != null && building.producing.readyAt > System.currentTimeMillis()) {
                        val readyAt = building.producing.readyAt
                        
                        val groupKey = groupedComposters.keys.firstOrNull { (existingType, existingTime) ->
                            abs(existingTime - readyAt) <= 120000 && existingType == composterType
                        } ?: (composterType to readyAt)
                        
                        groupedComposters.getOrPut(groupKey) { mutableListOf() }.add(building)
                    }
                }
            }
            
            _composterGroups.value = groupedComposters.map { (key, buildings) ->
                val (composterType, readyAt) = key
                ComposterGroup(
                    composterType = composterType,
                    readyAt = readyAt,
                    buildings = buildings
                )
            }.filter { 
                it.readyAt > System.currentTimeMillis() - 10000 
            }.sortedBy { it.readyAt }
        }

        // Process mushrooms
        stateObject.get("mushrooms")?.let { mushrooms ->
            val mushroomsJson = gson.toJson(mushrooms)
            val mushroomsData: MushroomsData = gson.fromJson(mushroomsJson, MushroomsData::class.java)
            
            val mushroomGroups = mutableListOf<MushroomGroup>()
            
            // Add regular mushrooms
            val regularHarvestTime = mushroomsData.spawnedAt + (GrowTimes.MUSHROOM_REGULAR * 1000L)
            if (regularHarvestTime > System.currentTimeMillis()) {
                mushroomGroups.add(
                    MushroomGroup(
                        harvestTime = regularHarvestTime,
                        mushroomType = "Regular Mushroom",
                        amount = 1
                    )
                )
            }
            
            // Add magic mushrooms
            val magicHarvestTime = mushroomsData.magicSpawnedAt + (GrowTimes.MUSHROOM_MAGIC * 1000L)
            if (magicHarvestTime > System.currentTimeMillis()) {
                mushroomGroups.add(
                    MushroomGroup(
                        harvestTime = magicHarvestTime,
                        mushroomType = "Magic Mushroom",
                        amount = 1
                    )
                )
            }
            
            _mushroomGroups.value = mushroomGroups.filter { 
                it.harvestTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.harvestTime }
        }

        // Process hen house
        stateObject.get("henHouse")?.let { henHouse ->
            val henHouseJson = gson.toJson(henHouse)
            val henHouseData: HenHouseData = gson.fromJson(henHouseJson, HenHouseData::class.java)
            
            val groupedChickens = mutableMapOf<Long, MutableList<Animal>>()
            
            henHouseData.animals.values.forEach { animal ->
                val groupTime = groupedChickens.keys.firstOrNull { existingTime ->
                    abs(existingTime - animal.awakeAt) <= 120000
                } ?: animal.awakeAt
                
                groupedChickens.getOrPut(groupTime) { mutableListOf() }.add(animal)
            }
            
            _henHouseGroups.value = groupedChickens.map { (wakeTime, chickens) ->
                HenHouseGroup(
                    wakeTime = wakeTime,
                    chickens = chickens
                )
            }.filter { 
                it.wakeTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.wakeTime }
        }

        // Process barn
        stateObject.get("barn")?.let { barn ->
            val barnJson = gson.toJson(barn)
            val barnData: BarnData = gson.fromJson(barnJson, BarnData::class.java)
            
            val groupedAnimals = mutableMapOf<Triple<String, Long, String>, MutableList<BarnAnimal>>()
            
            barnData.animals.values.forEach { animal ->
                val groupKey = groupedAnimals.keys.firstOrNull { (existingType, existingTime, existingState) ->
                    abs(existingTime - animal.awakeAt) <= 120000 && 
                    existingType == animal.type &&
                    existingState == animal.state
                } ?: Triple(animal.type, animal.awakeAt, animal.state)
                
                groupedAnimals.getOrPut(groupKey) { mutableListOf() }.add(animal)
            }
            
            _barnGroups.value = groupedAnimals.map { (key, animals) ->
                val (animalType, wakeTime, state) = key
                BarnGroup(
                    animalType = "$animalType ($state)",
                    wakeTime = wakeTime,
                    animals = animals
                )
            }.filter { 
                it.wakeTime > System.currentTimeMillis() - 10000 
            }.sortedBy { it.wakeTime }
        }
    }

    internal fun getTestItem(): TestItem? {
        return _testItem.value
    }

    fun clearTestNotification() {
        _testItem.value = null
        Log.d("FarmDataRepository", "Test notification cleared")
    }

    fun setTestNotification(amount: Int, completionTime: Long) {
        _testItem.value = TestItem(amount, completionTime)
    }

    companion object {
        @Volatile
        private var instance: FarmDataRepository? = null

        fun getInstance(preferencesManager: PreferencesManager): FarmDataRepository {
            return instance ?: synchronized(this) {
                instance ?: FarmDataRepository(preferencesManager).also { instance = it }
            }
        }
    }
} 