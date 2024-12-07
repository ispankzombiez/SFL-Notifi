package com.example.sflnotifi

object GrowTimes {
    // Basic Crops (all values in seconds)
    const val SUNFLOWER = 60        // 1 min
    const val POTATO = 300          // 5 min
    const val PUMPKIN = 1800        // 30 min
    const val CARROT = 3600         // 1 hour
    const val CABBAGE = 7200        // 2 hours
    const val SOYBEAN = 10800       // 3 hours
    const val BEETROOT = 14400      // 4 hours
    const val CAULIFLOWER = 28800   // 8 hours
    const val PARSNIP = 43200       // 12 hours
    const val EGGPLANT = 57600      // 16 hours
    const val CORN = 72000          // 20 hours
    const val RADISH = 86400        // 24 hours
    const val WHEAT = 86400         // 24 hours
    const val KALE = 129600         // 36 hours
    const val BARLEY = 172800       // 48 hours

    // Special Crops (in seconds)
    const val RICE = 115200         // 32 hours
    const val OLIVE = 158400        // 44 hours

    // Fruit Trees (in seconds)
    const val FRUIT_TOMATO = 7200      // 2 hours
    const val FRUIT_LEMON = 14400      // 4 hours
    const val FRUIT_BLUEBERRY = 21600  // 6 hours
    const val FRUIT_ORANGE = 28800     // 8 hours
    const val FRUIT_APPLE = 43200      // 12 hours
    const val FRUIT_BANANA = 43200     // 12 hours
    const val GRAPE = 43200         // 12 hours

    // Tree regrowth time (in seconds)
    const val TREE = 7200  // 2 hours

    // Resource regrowth times (in seconds)
    const val STONE = 14400  // 4 hours
    const val IRON = 28800  // 8 hours
    const val GOLD = 86400  // 24 hours
    const val CRIMSTONE = 86400  // 24 hours
    const val OIL = 72000  // 20 hours
    const val SUNSTONE = 259200  // 72 hours

    // Fruit patch regrowth times (in seconds)
    const val FRUIT_PATCH = 7200  // 2 hours

    // Flower regrowth times (in seconds)
    const val PANSY_RED = 86400      // 24 hours
    const val PANSY_YELLOW = 86400   // 24 hours
    const val PANSY_PURPLE = 86400   // 24 hours
    const val PANSY_WHITE = 86400    // 24 hours
    const val PANSY_BLUE = 86400     // 24 hours

    const val COSMOS_RED = 86400     // 24 hours
    const val COSMOS_YELLOW = 86400  // 24 hours
    const val COSMOS_PURPLE = 86400  // 24 hours
    const val COSMOS_WHITE = 86400   // 24 hours
    const val COSMOS_BLUE = 86400    // 24 hours
    const val PRISM_PETAL = 86400    // 24 hours

    const val BALLOON_RED = 172800    // 48 hours
    const val BALLOON_YELLOW = 172800 // 48 hours
    const val BALLOON_PURPLE = 172800 // 48 hours
    const val BALLOON_WHITE = 172800  // 48 hours
    const val BALLOON_BLUE = 172800   // 48 hours

    const val DAFFODIL_RED = 172800    // 48 hours
    const val DAFFODIL_YELLOW = 172800 // 48 hours
    const val DAFFODIL_PURPLE = 172800 // 48 hours
    const val DAFFODIL_WHITE = 172800  // 48 hours
    const val DAFFODIL_BLUE = 172800   // 48 hours
    const val CELESTIAL_FROSTBLOOM = 172800 // 48 hours

    const val CARNATION_RED = 432000    // 120 hours (5 days)
    const val CARNATION_YELLOW = 432000 // 120 hours (5 days)
    const val CARNATION_PURPLE = 432000 // 120 hours (5 days)
    const val CARNATION_WHITE = 432000  // 120 hours (5 days)
    const val CARNATION_BLUE = 432000   // 120 hours (5 days)

    const val LOTUS_RED = 432000    // 120 hours (5 days)
    const val LOTUS_YELLOW = 432000 // 120 hours (5 days)
    const val LOTUS_PURPLE = 432000 // 120 hours (5 days)
    const val LOTUS_WHITE = 432000  // 120 hours (5 days)
    const val LOTUS_BLUE = 432000   // 120 hours (5 days)
    const val PRIMULA_ENIGMA = 432000 // 120 hours (5 days)

    // Mushroom refresh times (in seconds)
    const val MUSHROOM_REGULAR = 43200    // 12 hours
    const val MUSHROOM_MAGIC = 86400      // 24 hours
}

object CookingTimes {
    // All cooking times in seconds
    const val MASHED_POTATO = 30                // 30 sec
    const val PUMPKIN_SOUP = 180               // 3 min
    const val REINDEER_CARROT = 300            // 5 min
    const val MUSHROOM_SOUP = 600              // 10 min
    const val POPCORN = 720                    // 12 min
    const val BUMPKIN_BROTH = 1200             // 20 min
    const val CABBERS_N_MASH = 2400            // 40 min
    const val BOILED_EGGS = 3600               // 1 hour
    const val KALE_STEW = 7200                 // 2 hours
    const val KALE_OMELETTE = 12600            // 3.5 hours
    const val GUMBO = 14400                    // 4 hours
    const val RAPID_ROAST = 10                 // 10 sec
    const val FRIED_TOFU = 5400                // 90 min
    const val RICE_BUN = 18000                 // 300 min
    const val ANTIPASTO = 10800                // 180 min
    const val PIZZA_MARGHERITA = 72000         // 20 hours

    // Kitchen Recipes (in seconds)
    const val SUNFLOWER_CRUNCH = 600           // 10 min
    const val MUSHROOM_JACKET_POTATOES = 600   // 10 min
    const val FRUIT_SALAD = 1800               // 30 min
    const val PANCAKES = 3600                  // 1 hour
    const val ROAST_VEGGIES = 7200             // 2 hours
    const val CAULIFLOWER_BURGER = 10800       // 3 hours
    const val CLUB_SANDWICH = 10800            // 3 hours
    const val BUMPKIN_SALAD = 12600            // 3.5 hours
    const val BUMPKIN_GANOUSH = 18000          // 5 hours
    const val GOBLINS_TREAT = 21600            // 6 hours
    const val CHOWDER = 28800                  // 8 hours
    const val BUMPKIN_ROAST = 43200            // 12 hours
    const val GOBLIN_BRUNCH = 43200            // 12 hours
    const val BEETROOT_BLAZE = 30              // 30 sec
    const val STEAMED_RED_RICE = 14400         // 4 hours
    const val TOFU_SCRAMBLE = 10800            // 3 hours
    const val FRIED_CALAMARI = 18000           // 5 hours
    const val FISH_BURGER = 7200               // 2 hours
    const val FISH_OMELETTE = 18000            // 5 hours
    const val OCEANS_OLIVE = 7200              // 2 hours
    const val SEAFOOD_BASKET = 18000           // 5 hours
    const val FISH_N_CHIPS = 14400             // 4 hours
    const val SUSHI_ROLL = 3600                // 1 hour
    const val CAPRESE_SALAD = 10800            // 3 hours
    const val SPAGHETTI_AL_LIMONE = 54000      // 15 hours

    // Bakery Recipes (in seconds)
    const val APPLE_PIE = 14400                // 4 hours
    const val ORANGE_CAKE = 14400              // 4 hours
    const val KALE_MUSHROOM_PIE = 14400        // 4 hours
    const val SUNFLOWER_CAKE = 23400           // 6.5 hours
    const val HONEY_CAKE = 28800               // 8 hours
    const val POTATO_CAKE = 37800              // 10.5 hours
    const val PUMPKIN_CAKE = 37800             // 10.5 hours
    const val CORNBREAD = 43200                // 12 hours
    const val CARROT_CAKE = 46800              // 13 hours
    const val CABBAGE_CAKE = 54000             // 15 hours
    const val BEETROOT_CAKE = 79200            // 22 hours
    const val CAULIFLOWER_CAKE = 79200         // 22 hours
    const val PARSNIP_CAKE = 86400             // 24 hours
    const val EGGPLANT_CAKE = 86400            // 24 hours
    const val RADISH_CAKE = 86400              // 24 hours
    const val WHEAT_CAKE = 86400               // 24 hours
    const val LEMON_CHEESECAKE = 108000        // 30 hours

    // Deli Items (in seconds)
    const val BLUEBERRY_JAM = 43200            // 12 hours
    const val FERMENTED_CARROTS = 86400        // 24 hours
    const val SAUERKRAUT = 86400               // 24 hours
    const val FANCY_FRIES = 86400              // 24 hours
    const val FERMENTED_FISH = 86400           // 24 hours
    const val SHROOM_SYRUP = 10                // 10 sec
    const val CHEESE = 1200                    // 20 min
    const val BLUE_CHEESE = 10800              // 3 hours
    const val HONEY_CHEDDAR = 43200            // 12 hours

    // Smoothie Shack (in seconds)
    const val PURPLE_SMOOTHIE = 1800           // 30 min
    const val ORANGE_JUICE = 2700              // 45 min
    const val APPLE_JUICE = 3600               // 1 hour
    const val POWER_SMOOTHIE = 5400            // 1.5 hours
    const val BUMPKIN_DETOX = 7200             // 2 hours
    const val BANANA_BLAST = 10800             // 3 hours
    const val GRAPE_JUICE = 10800              // 3 hours
    const val THE_LOT = 12600                  // 3.5 hours
    const val CARROT_JUICE = 3600              // 1 hour
    const val QUICK_JUICE = 1800               // 30 min
    const val SLOW_JUICE = 86400               // 24 hours
    const val SOUR_SHAKE = 3600                // 1 hour
}

object ComposterTimes {
    // Base Times (in seconds)
    const val COMPOST_BIN = 21600              // 6 hours
    const val TURBO_COMPOSTER = 28800          // 8 hours
    const val PREMIUM_COMPOSTER = 43200        // 12 hours

    // Egg Boost Times (in seconds)
    const val COMPOST_BIN_EGG_BOOST = 7200     // 2 hours
    const val TURBO_COMPOSTER_EGG_BOOST = 10800 // 3 hours
    const val PREMIUM_COMPOSTER_EGG_BOOST = 14400 // 4 hours
} 