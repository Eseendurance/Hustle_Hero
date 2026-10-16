package com.example.data

import com.example.model.ClubbingVenue
import com.example.model.DailyHustleTask
import com.example.model.JobRole
import com.example.model.MealCuisine
import com.example.model.MealItem
import com.example.model.PartyEvent

object DailyLifeAndMealData {

    val cities = listOf(
        "Lagos",
        "Abuja",
        "Port Harcourt",
        "Ibadan",
        "Kano",
        "Enugu",
        "Calabar"
    )

    fun getDefaultDailyHustles(): List<DailyHustleTask> = listOf(
        DailyHustleTask(
            id = "dh_alaba_rush",
            title = "Alaba Tech Rush Delivery",
            description = "A wholesale customer urgently needs 15 smartphone fast-chargers at Mile 2 bus stop before traffic builds up.",
            category = "Delivery Grind",
            iconEmoji = "📦",
            rewardCash = 35_000L,
            reputationReward = 4,
            stressCost = 3,
            energyCost = 15,
            difficulty = "Medium",
            clientName = "Chief Chinedu",
            location = "Alaba / Mile 2"
        ),
        DailyHustleTask(
            id = "dh_phone_screen",
            title = "Express Screen Replacement",
            description = "A banker dropped their OLED display outside Ikeja City Mall. Repair it on the spot within 25 minutes.",
            category = "Technical Gig",
            iconEmoji = "📱",
            rewardCash = 28_000L,
            reputationReward = 5,
            stressCost = 2,
            energyCost = 12,
            difficulty = "Easy",
            clientName = "Femi (Banker)",
            location = "Ikeja GRA"
        ),
        DailyHustleTask(
            id = "dh_flash_sale",
            title = "WhatsApp Status Flash Promo",
            description = "Bundle slow-moving stock with a 10% discount and blast 20 promotional broadcast messages to loyal buyers.",
            category = "Digital Hustle",
            iconEmoji = "📢",
            rewardCash = 42_000L,
            reputationReward = 3,
            stressCost = 1,
            energyCost = 10,
            difficulty = "Easy",
            clientName = "Campus Buyers Club",
            location = "Yaba Tech Hub"
        ),
        DailyHustleTask(
            id = "dh_supplier_bargain",
            title = "Balogun Bulk Price Haggle",
            description = "Meet Mama Sikira in Balogun Market to negotiate ₦100k off wholesale inventory ahead of the weekend spike.",
            category = "Negotiation",
            iconEmoji = "🤝",
            rewardCash = 50_000L,
            reputationReward = 6,
            stressCost = 4,
            energyCost = 20,
            difficulty = "Hard",
            clientName = "Mama Sikira Textiles",
            location = "Balogun Market"
        ),
        DailyHustleTask(
            id = "dh_pos_float",
            title = "Evening POS Cash Liquidity Bailout",
            description = "Neighbourhood merchants ran out of cash for POS withdrawals. Transfer ₦200k emergency float for a quick fee margin.",
            category = "Fintech Float",
            iconEmoji = "🏧",
            rewardCash = 30_000L,
            reputationReward = 3,
            stressCost = 2,
            energyCost = 10,
            difficulty = "Easy",
            clientName = "Madam Kemi Kiosk",
            location = "Surulere"
        ),
        DailyHustleTask(
            id = "dh_catering_rush",
            title = "Corporate Lunchbox Overtime",
            description = "A law firm on Marina ordered 25 extra packs of hot Jollof Rice & Asun meat with 45 minutes notice.",
            category = "Food Hustle",
            iconEmoji = "🥘",
            rewardCash = 65_000L,
            reputationReward = 7,
            stressCost = 5,
            energyCost = 25,
            difficulty = "Hard",
            clientName = "Aluko & Partners Law",
            location = "Marina, Lagos Island"
        )
    )

    fun getAvailableJobs(): List<JobRole> = listOf(
        JobRole(
            id = "job_software_eng",
            title = "Senior Android / Fintech Engineer",
            company = "PayVibe Africa",
            industry = "Technology",
            weeklySalary = 160_000L,
            iconEmoji = "💻",
            workHours = "09:00 AM - 05:00 PM (Hybrid)",
            description = "Build resilient payment checkout SDKs, fix payment switch timeouts, and collect reliable weekly payroll.",
            prestige = 85
        ),
        JobRole(
            id = "job_bank_officer",
            title = "Commercial Banking Operations Specialist",
            company = "Apex Trust Bank",
            industry = "Finance",
            weeklySalary = 125_000L,
            iconEmoji = "🏦",
            workHours = "08:00 AM - 05:30 PM",
            description = "Reconcile corporate accounts, manage SME loan reviews, and enjoy stable institutional health benefits.",
            prestige = 80
        ),
        JobRole(
            id = "job_marketing_lead",
            title = "Digital Brand & Growth Manager",
            company = "Pulse Media Agency",
            industry = "Creative Media",
            weeklySalary = 110_000L,
            iconEmoji = "🎨",
            workHours = "09:30 AM - 06:00 PM",
            description = "Execute viral influencer campaigns, manage corporate social spots, and drive customer acquisition.",
            prestige = 75
        ),
        JobRole(
            id = "job_nursing_officer",
            title = "Clinical Nursing Officer",
            company = "Evercare Medical Center",
            industry = "Healthcare",
            weeklySalary = 95_000L,
            iconEmoji = "🩺",
            workHours = "Shift Roster (Day / Night)",
            description = "Care for recovering patients, administer medications with empathy, and earn high community respect.",
            prestige = 88
        ),
        JobRole(
            id = "job_sales_director",
            title = "Enterprise B2B Sales Executive",
            company = "Oando Energy & Logistics",
            industry = "Corporate Commerce",
            weeklySalary = 145_000L,
            iconEmoji = "📈",
            workHours = "08:30 AM - 05:00 PM",
            description = "Close high-value distribution deals across West Africa with attractive weekly commission bonuses.",
            prestige = 82
        ),
        JobRole(
            id = "job_school_teacher",
            title = "Senior STEM Teacher & Tutor",
            company = "Corona International Schools",
            industry = "Education",
            weeklySalary = 80_000L,
            iconEmoji = "📚",
            workHours = "07:30 AM - 03:00 PM",
            description = "Educate the next generation in Mathematics & Physics, with ample early afternoon free time.",
            prestige = 78
        )
    )

    fun getAllMeals(): List<MealItem> = listOf(
        // --- 🇳🇬 NIGERIAN MEALS ---
        MealItem(
            id = "ng_jollof_dodo",
            name = "Party Jollof Rice & Fried Dodo",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🍛",
            tagline = "Smoky firewood-aroma rice with sweet fried golden plantain",
            description = "The undisputed king of West African cuisine. Long-grain parboiled rice simmered in a rich tomato, tatashe, and scotch bonnet broth with grilled chicken quarter and crispy dodo.",
            takeoutPrice = 6_500L,
            cookingCost = 2_800L,
            cookingTimeMinutes = 45,
            ingredients = listOf(
                "4 cups Long grain parboiled rice",
                "6 fresh Roma tomatoes & 4 red bell peppers (Tatashe)",
                "3 Scotch bonnet peppers (Ata rodo)",
                "2 large ripe plantains (dodo)",
                "Grilled seasoned chicken quarters",
                "Thyme, bay leaves, curry powder & chicken bouillon"
            ),
            cookingInstructions = listOf(
                "Blend tomatoes, tatashe, ata rodo and onions into a thick puree and boil until excess water reduces.",
                "Heat vegetable oil, fry chopped onions until fragrant, then fry the tomato stew paste until oil floats to the top.",
                "Season with bay leaves, thyme, curry and chicken broth. Pour in washed rice and stir thoroughly.",
                "Cover tightly with foil and a lid; steam on low heat so smoke infuses the grains without burning.",
                "Slice ripe plantains into rounds, fry in hot oil until golden-brown and caramelised. Serve hot!"
            ),
            flavorProfile = "Deeply savoury, aromatic smoke, gentle peppery heat and sweet caramelized plantain.",
            stressRelief = 12,
            energyBoost = 35
        ),
        MealItem(
            id = "ng_egusi_pounded_yam",
            name = "Rich Egusi Soup & Pounded Yam",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🍲",
            tagline = "Ground melon seed stew loaded with stockfish, shaki and bitterleaf",
            description = "Fluffy elastic pounded yam served alongside thick egusi cooked with hand-peeled melon seeds, smoked dry catfish, tender shaki, and fresh bitterleaf simmered in red palm oil.",
            takeoutPrice = 8_000L,
            cookingCost = 3_500L,
            cookingTimeMinutes = 50,
            ingredients = listOf(
                "2 cups ground melon seeds (Egusi)",
                "Fresh white yam tubers (or Iyan flour)",
                "Smoked catfish & pre-boiled beef shaki",
                "Pure bleached red palm oil",
                "Fresh bitterleaf (washed) or ugu leaves",
                "Ground crayfish & seasoning cubes"
            ),
            cookingInstructions = listOf(
                "Boil yam chunks until knife-tender, pound vigorously in a wooden mortar until stretchy and smooth.",
                "Mix ground egusi with warm water and diced onions to form thick paste balls.",
                "Heat palm oil in a pot, drop in egusi balls and fry gently to form thick golden curds.",
                "Add rich meat stock, smoked catfish and cooked beef shaki. Simmer on medium heat for 15 minutes.",
                "Stir in washed bitterleaf and ground crayfish. Cook for 5 more minutes and serve steaming hot."
            ),
            flavorProfile = "Nutty, rich unctuous broth, aromatic dried fish and smooth pillowy swallow.",
            stressRelief = 15,
            energyBoost = 40
        ),
        MealItem(
            id = "ng_suya_pepper_beef",
            name = "Charcoal Suya & Spicy Pepper Beef",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🍢",
            tagline = "Smoky open-flame beef skewers dusted with authentic Northern Yaji spice",
            description = "Tender beef fillets skewered and grilled over coconut charcoal, thoroughly coated in kuli-kuli peanut spice, served with sliced red onions and juicy tomatoes.",
            takeoutPrice = 5_500L,
            cookingCost = 2_200L,
            cookingTimeMinutes = 30,
            ingredients = listOf(
                "500g Thinly sliced beef steak",
                "Authentic Yaji spice (ground kuli-kuli peanut cake, ginger, garlic, chili)",
                "Vegetable oil spray",
                "1 Large red onion (sliced into rings)",
                "2 Fresh firm tomatoes",
                "Coarse sea salt"
            ),
            cookingInstructions = listOf(
                "Slice beef across the grain into ribbons no thicker than 3mm.",
                "Thread beef ribbons tightly onto soaked bamboo skewers.",
                "Brush both sides generously with vegetable oil and rub with heavy coating of Yaji spice blend.",
                "Grill over hot coals or high oven broiler for 6 minutes per side until charred on edges.",
                "Dust with extra fresh Yaji and wrap in newspaper foil with crunchy onions."
            ),
            flavorProfile = "Fiery heat, nutty ginger aromatics, savory crunch and juicy charred beef.",
            stressRelief = 14,
            energyBoost = 30
        ),
        MealItem(
            id = "ng_efo_riro",
            name = "Efo Riro Elegusi with Assorted Meat",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🥬",
            tagline = "Yoruba native rich vegetable pot packed with locust beans (iru) and prawns",
            description = "Fresh green shoko spinach wilted in coarse red pepper sauce, dried prawns, cow foot, and aromatic fermented locust beans.",
            takeoutPrice = 7_500L,
            cookingCost = 3_200L,
            cookingTimeMinutes = 40,
            ingredients = listOf(
                "3 Bunches fresh Shoko or Ugu spinach",
                "Fermented locust beans (Iru woro)",
                "Smoked prawns & deboned panla fish",
                "Boiled assorted meat (kpomo, beef, shaki)",
                "Coarsely blended scotch bonnets and bell peppers",
                "Pure palm oil"
            ),
            cookingInstructions = listOf(
                "Blanch fresh spinach in boiling water for 90 seconds, plunge into cold water and squeeze dry.",
                "Bleach palm oil slightly and fry whole iru until your kitchen fills with earthy aroma.",
                "Add coarse pepper blend and fry until moisture evaporates completely.",
                "Add assorted meats, smoked prawns and panla fish; simmer in the thick sauce for 10 mins.",
                "Fold in drained greens, cook for 2 minutes to retain emerald vibrancy, and take off the flame."
            ),
            flavorProfile = "Pungent earthy iru, spicy pepper base, juicy tender greens and oceanic prawn sweetness.",
            stressRelief = 10,
            energyBoost = 35
        ),
        MealItem(
            id = "ng_banga_starch",
            name = "Delta Banga Soup & Yellow Starch",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🍲",
            tagline = "Aromatic oil-palm fruit nectar with native spices and fresh river catfish",
            description = "Traditional Urhobo/Itsekiri delicacy made from fresh palm nut fruit concentrate, beletiete and oburunbebe stick, served with glossy yellow starch.",
            takeoutPrice = 8_500L,
            cookingCost = 3_800L,
            cookingTimeMinutes = 55,
            ingredients = listOf(
                "Fresh palm fruit extract (Oghwo amiedi)",
                "Native Banga spice blend (Aidan, beletiete, rohojie)",
                "Fresh river Catfish & dried bushmeat",
                "Cassava starch & a dash of palm oil for color",
                "Dried bitterleaf & crayfish"
            ),
            cookingInstructions = listOf(
                "Boil palm nut extract until it thickens into a glossy orange-red soup base.",
                "Drop in banga spice stick, crushed rohojie, and dried fish; simmer until flavours meld.",
                "Gently place seasoned fresh catfish chunks into the soup and cook on low heat for 12 minutes.",
                "Dissolve starch in cold water with half spoon of palm oil, heat in a pot while stirring rapidly until it turns into a shiny solid mound.",
                "Serve in a traditional clay pot alongside the steaming yellow starch."
            ),
            flavorProfile = "Herbaceous woody spice, rich palm fruit sweetness and tender melting catfish.",
            stressRelief = 16,
            energyBoost = 42
        ),
        MealItem(
            id = "ng_akara_pap",
            name = "Golden Akara & Warm Ogi (Pap)",
            cuisine = MealCuisine.NIGERIAN,
            iconEmoji = "🥞",
            tagline = "Crisp fluffy bean fritters with warm fermented ginger corn pap",
            description = "The classic Nigerian weekend breakfast. Peeled honey beans whipped with onions and habanero, deep-fried into cloud-light golden puffs, served with creamy sweetened pap.",
            takeoutPrice = 3_800L,
            cookingCost = 1_500L,
            cookingTimeMinutes = 25,
            ingredients = listOf(
                "2 cups peeled brown honey beans",
                "1 large purple onion & 2 ata rodo",
                "Fermented yellow/white corn paste (Ogi/Akamu)",
                "Pure groundnut oil for deep frying",
                "Evaporated peak milk & honey or sugar to taste",
                "Pinch of sea salt"
            ),
            cookingInstructions = listOf(
                "Blend soaked beans with minimal water into a very thick, smooth batter.",
                "Whisk batter with a wooden spoon or hand mixer for 8 minutes to trap air and create lightness.",
                "Fold in finely chopped onions, scotch bonnet, and salt just before frying.",
                "Scoop batter into medium-hot oil; fritters will puff up and turn deep golden brown.",
                "Dissolve ogi in cold water, pour rapid boiling water until it sets into velvety custard; drizzle cold milk!"
            ),
            flavorProfile = "Crispy exterior, airy bean center, comforting sweet-tart milky warmth.",
            stressRelief = 11,
            energyBoost = 28
        ),

        // --- 🇹🇭 THAI MEALS ---
        MealItem(
            id = "th_pad_thai",
            name = "Pad Thai Kung (Stir-Fried Rice Noodles)",
            cuisine = MealCuisine.THAI,
            iconEmoji = "🍜",
            tagline = "Wok-seared rice noodles with succulent prawns, tofu and tamarind glaze",
            description = "Thailand's iconic street noodle. Thin flat rice noodles tossed at blazing wok heat with jumbo tiger prawns, pressed yellow tofu, sweet preserved radish, egg, tamarind pulp, and crunchy crushed peanuts.",
            takeoutPrice = 9_500L,
            cookingCost = 4_200L,
            cookingTimeMinutes = 25,
            ingredients = listOf(
                "200g Dried flat rice stick noodles",
                "8 Fresh jumbo black tiger prawns",
                "100g Firm pressed yellow tofu (cubed)",
                "Tamarind concentrate, fish sauce & palm sugar",
                "2 Fresh farm eggs",
                "Bean sprouts, garlic chives, roasted crushed peanuts, lime wedges"
            ),
            cookingInstructions = listOf(
                "Soak noodles in warm water for 25 minutes until pliable but still firm.",
                "Whisk tamarind paste, fish sauce, and palm sugar in a saucepan until dissolved and glossy.",
                "Sear prawns in smoking hot wok for 90 seconds and set aside. Fry tofu and garlic until crisp.",
                "Add noodles and tamarind sauce, tossing vigorously until noodles absorb the sauce.",
                "Push noodles aside, crack eggs into wok, scramble lightly and toss with prawns, bean sprouts, and crushed peanuts!"
            ),
            flavorProfile = "Tangy tamarind tartness, sweet palm sugar, savoury umami wok-breath and nutty peanut crunch.",
            stressRelief = 15,
            energyBoost = 35
        ),
        MealItem(
            id = "th_tom_yum_goong",
            name = "Tom Yum Goong (Spicy Sour Prawn Soup)",
            cuisine = MealCuisine.THAI,
            iconEmoji = "🍲",
            tagline = "Invigorating clear broth scented with lemongrass, galangal and kaffir lime",
            description = "A fiery, soul-cleansing broth infused with bruised fresh lemongrass stalks, galangal slices, torn kaffir lime leaves, fresh birds eye chilies, juicy prawns, and oyster mushrooms.",
            takeoutPrice = 8_800L,
            cookingCost = 3_800L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "300g Tiger prawns with heads attached",
                "3 Stalks fresh lemongrass (bruised)",
                "5 Slices aromatic galangal root",
                "6 Fresh kaffir lime leaves (torn)",
                "Fresh birds eye chilies (crushed)",
                "Straw or oyster mushrooms, fish sauce, lime juice, dash of evaporated milk"
            ),
            cookingInstructions = listOf(
                "Simmer prawn heads and shells in water for 10 minutes to extract rich seafood stock.",
                "Strain stock and bring to a rolling boil. Add bruised lemongrass, galangal, and kaffir lime leaves.",
                "Toss in mushrooms and fresh raw prawns; cook until prawns curl and turn opaque coral pink.",
                "Turn off heat immediately. Stir in fish sauce, fresh lime juice, crushed chilies, and fresh coriander.",
                "Serve piping hot to clear your sinuses and energize the body."
            ),
            flavorProfile = "Electric sour lime, lemongrass floral bouquet, tingling chili warmth and sweet seafood broth.",
            stressRelief = 18,
            energyBoost = 30
        ),
        MealItem(
            id = "th_green_curry",
            name = "Thai Green Curry & Jasmine Rice",
            cuisine = MealCuisine.THAI,
            iconEmoji = "🍛",
            tagline = "Creamy coconut milk stew with green chili paste and sweet Thai basil",
            description = "Rich aromatic green curry with tender chicken strips, tender round Thai eggplants, bamboo shoot ribbons, and sweet basil leaves simmered in freshly pressed coconut milk.",
            takeoutPrice = 9_000L,
            cookingCost = 4_000L,
            cookingTimeMinutes = 35,
            ingredients = listOf(
                "3 tbsp Authentic Thai green curry paste",
                "400ml Full-fat coconut milk & cream",
                "300g Chicken breast (thinly sliced)",
                "Small Thai round eggplants & bamboo shoots",
                "Fresh Thai sweet basil (Horapha)",
                "Fragrant steamed Jasmine rice"
            ),
            cookingInstructions = listOf(
                "Heat 3 tablespoons of coconut cream in a pan until the oil splits and bubbles.",
                "Add green curry paste and fry until deeply fragrant and oil turns jade green.",
                "Add sliced chicken, coating each piece in the curry paste.",
                "Pour in remaining coconut milk, add bamboo shoots and eggplants; simmer for 15 minutes.",
                "Turn off the heat and scatter fresh sweet basil leaves. Serve with fluffy jasmine rice."
            ),
            flavorProfile = "Velvety coconut creaminess, spicy green chili bite, anise-like basil fragrance.",
            stressRelief = 14,
            energyBoost = 38
        ),
        MealItem(
            id = "th_som_tum",
            name = "Som Tum (Spicy Green Papaya Salad)",
            cuisine = MealCuisine.THAI,
            iconEmoji = "🥗",
            tagline = "Mortar-pounded shredded green papaya with lime, chili and roasted peanuts",
            description = "Crunchy raw green papaya pounded in a traditional wooden mortar with garlic, bird's eye chilies, green beans, dried shrimp, and sweet-sour tamarind dressing.",
            takeoutPrice = 5_800L,
            cookingCost = 2_200L,
            cookingTimeMinutes = 15,
            ingredients = listOf(
                "1 Medium unripe green papaya (peeled and julienned)",
                "3 Fresh Thai birds eye chilies & 2 garlic cloves",
                "Long yard beans & cherry tomatoes",
                "Palm sugar, fish sauce, fresh lime juice",
                "Roasted salted peanuts & dried mini shrimp"
            ),
            cookingInstructions = listOf(
                "Pound garlic and chilies lightly in a pestle and mortar.",
                "Add palm sugar, lime juice, and fish sauce; stir with pestle until dissolved.",
                "Toss in cut long beans and cherry tomatoes, crushing gently to release juices.",
                "Add shredded crisp green papaya and pound gently while tossing with a spoon.",
                "Transfer to a plate, sprinkle roasted peanuts and serve crisp and cool."
            ),
            flavorProfile = "Crisp refreshing crunch, explosive lime acidity, spicy kick and savory umami.",
            stressRelief = 8,
            energyBoost = 22
        ),

        // --- 🇨🇳 CHINESE MEALS ---
        MealItem(
            id = "cn_dim_sum",
            name = "Dim Sum Pork & Shrimp Dumplings (Siu Mai)",
            cuisine = MealCuisine.CHINESE,
            iconEmoji = "🥟",
            tagline = "Handcrafted bamboo basket dumplings with sweet chili soy vinegar dip",
            description = "Delicate open-topped steamed dumplings filled with minced Berkshire pork, diced succulent prawns, crunchy water chestnuts, and fragrant toasted sesame oil.",
            takeoutPrice = 8_200L,
            cookingCost = 3_600L,
            cookingTimeMinutes = 35,
            ingredients = listOf(
                "Round wonton dumpling wrappers",
                "250g Minced pork shoulder & 150g chopped tiger prawns",
                "Finely diced water chestnuts & shiitake mushrooms",
                "Shaoxing wine, light soy sauce, sesame oil, white pepper",
                "Grated ginger and finely chopped spring onions",
                "Chili crisp oil and Chinkiang black vinegar dip"
            ),
            cookingInstructions = listOf(
                "Combine minced pork, chopped prawns, water chestnuts, soy sauce, and sesame oil; stir vigorously in one direction until sticky.",
                "Place a spoonful of filling in the center of each wrapper, gather edges upwards into an open cup.",
                "Garnish the top with tiny dots of grated carrot or tobiko.",
                "Arrange in bamboo steaming baskets lined with parchment paper.",
                "Steam over rapidly boiling water for 8 minutes until juicy and firm. Serve immediately with vinegar dip."
            ),
            flavorProfile = "Juicy savory pork, plump sweet shrimp, crisp water chestnuts and spicy vinegary kick.",
            stressRelief = 13,
            energyBoost = 32
        ),
        MealItem(
            id = "cn_kung_pao",
            name = "Kung Pao Chicken & Steamed Rice",
            cuisine = MealCuisine.CHINESE,
            iconEmoji = "🍗",
            tagline = "Sichuan wok-fried diced chicken with tingly peppercorns and roasted peanuts",
            description = "Diced chicken breast wok-seared with whole dried red chilies, numbing Sichuan peppercorns, scallion batons, and crunchy peanuts in a balanced sweet, sour, and savoury sauce.",
            takeoutPrice = 7_800L,
            cookingCost = 3_400L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "350g Boneless chicken thighs (diced into cubes)",
                "Whole dried Sichuan red chilies & Sichuan peppercorns",
                "Roasted unsalted peanuts & sliced scallions",
                "Dark soy sauce, Chinkiang vinegar, sugar, cornstarch",
                "Fresh ginger, garlic, and hot wok oil"
            ),
            cookingInstructions = listOf(
                "Marinate diced chicken in soy sauce, Shaoxing wine, and cornstarch for 15 minutes.",
                "Whisk vinegar, soy sauce, sugar, chicken stock, and cornstarch for the sauce.",
                "Heat oil in wok; fry dried chilies and peppercorns until fragrant and darkened.",
                "Toss chicken in high heat wok until seared; add ginger, garlic, and scallions.",
                "Pour in sauce, toss until glossy and clinging to chicken, stir in peanuts and serve!"
            ),
            flavorProfile = "Spicy tingly Mala sensation, crunchy nutty peanuts and sweet-sour umami glaze.",
            stressRelief = 12,
            energyBoost = 36
        ),
        MealItem(
            id = "cn_fried_rice",
            name = "Yangzhou Classic Golden Fried Rice",
            cuisine = MealCuisine.CHINESE,
            iconEmoji = "🍚",
            tagline = "Wok-tossed day-old rice with char siu BBQ pork, baby shrimp and sweet peas",
            description = "The gold standard of Chinese fried rice. Every grain coated in golden egg yolk, tossed over blazing wok heat with Chinese sweet sausages, baby shrimp, tender peas, and spring onions.",
            takeoutPrice = 6_800L,
            cookingCost = 2_900L,
            cookingTimeMinutes = 18,
            ingredients = listOf(
                "3 cups chilled day-old cooked jasmine rice",
                "100g Char siu roast pork (diced) or Chinese lap cheong sausage",
                "100g Peeled baby prawns",
                "3 Farm eggs (beaten)",
                "Sweet green peas, finely diced carrots, scallions",
                "Light soy sauce, white pepper, pure toasted sesame oil"
            ),
            cookingInstructions = listOf(
                "Break up chilled rice grains with damp fingers to ensure no clumps remain.",
                "Heat wok with oil until lightly smoking; pour in beaten eggs and scramble softly.",
                "Add pork, prawns, peas, and carrots; stir-fry for 2 minutes.",
                "Add cold rice and toss vigorously over highest flame, seasoning with soy sauce and white pepper.",
                "Drizzle sesame oil, fold in green scallions and serve with smoky wok hei flavor."
            ),
            flavorProfile = "Dry, fluffy separate grains, sweet BBQ pork, savory shrimp and smoky wok aroma.",
            stressRelief = 10,
            energyBoost = 34
        ),
        MealItem(
            id = "cn_peking_duck",
            name = "Crispy Peking Duck Bao Buns",
            cuisine = MealCuisine.CHINESE,
            iconEmoji = "🦆",
            tagline = "Crisp lacquered duck skin inside steamed lotus buns with sweet hoisin",
            description = "Five-spice roasted duck with paper-thin crisp amber skin, rolled inside fluffy cloud-soft steamed bao buns with cucumber sticks, scallion threads, and dark hoisin sauce.",
            takeoutPrice = 11_500L,
            cookingCost = 5_000L,
            cookingTimeMinutes = 40,
            ingredients = listOf(
                "Roast duck breast with crisp lacquered skin",
                "6 Fluffy steamed lotus leaf bao buns",
                "Premium sweet hoisin sauce",
                "Fresh cucumber ribbons & scallion brushes",
                "Chinese five-spice powder & maltose glaze"
            ),
            cookingInstructions = listOf(
                "Steam bao buns in a bamboo steamer for 6 minutes until pillowy and soft.",
                "Crisp roast duck under a hot oven broiler for 5 minutes until skin crackles.",
                "Carve duck into thin slices with equal parts crisp skin and tender meat.",
                "Brush the interior of each open bao bun with rich hoisin sauce.",
                "Tuck in warm duck slices, fresh cucumber, and scallions; fold and enjoy immediately."
            ),
            flavorProfile = "Crispy glazed duck skin, sweet savory plum-hoisin, pillowy steamed dough and cool cucumber.",
            stressRelief = 17,
            energyBoost = 38
        ),

        // --- 🇫🇷 FRENCH MEALS ---
        MealItem(
            id = "fr_beef_bourguignon",
            name = "Classic Beef Bourguignon",
            cuisine = MealCuisine.FRENCH,
            iconEmoji = "🥘",
            tagline = "Slow-braised tender beef chuck in red wine with pearl onions and mushrooms",
            description = "Julia Child's French masterpiece. Prime beef chunks seared and braised for 3 hours in Burgundy red wine, rich beef bone broth, caramelized pearl onions, smoked bacon lardons, and fresh thyme.",
            takeoutPrice = 13_500L,
            cookingCost = 6_200L,
            cookingTimeMinutes = 90,
            ingredients = listOf(
                "600g Beef chuck roast (cut into 2-inch cubes)",
                "150g Thick-cut bacon lardons",
                "1 Bottle French Burgundy or dry Cabernet wine",
                "200g Fresh cremini button mushrooms & pearl onions",
                "Carrots, garlic cloves, fresh thyme sprigs, bay leaf",
                "Tomato paste, butter, and rich beef bone broth"
            ),
            cookingInstructions = listOf(
                "Crisp bacon lardons in a heavy Dutch oven, remove and reserve flavorful bacon fat.",
                "Dry beef cubes thoroughly, sear in hot bacon fat in batches until deeply browned on all sides.",
                "Sauté carrots and onions, stir in tomato paste, garlic and flour to create a roux.",
                "Pour in full bottle of red wine and beef broth; nestle beef, bacon, thyme and bay leaves back in.",
                "Cover and braise gently on low heat for 2.5 hours until fork-tender; fold in butter-sautéed mushrooms."
            ),
            flavorProfile = "Velvety wine reduction, melt-in-your-mouth beef, earthy mushrooms and smoky bacon richness.",
            stressRelief = 20,
            energyBoost = 42
        ),
        MealItem(
            id = "fr_coq_au_vin",
            name = "Coq au Vin (Chicken in Wine)",
            cuisine = MealCuisine.FRENCH,
            iconEmoji = "🍗",
            tagline = "Golden pan-seared chicken thighs simmered in red wine and butter sauce",
            description = "Crispy skin chicken thighs slowly braised in red wine reduction with sweet shallots, garlic, and wild mushrooms, garnished with fresh flat-leaf parsley and served with buttered potatoes.",
            takeoutPrice = 11_000L,
            cookingCost = 5_000L,
            cookingTimeMinutes = 50,
            ingredients = listOf(
                "4 Bone-in skin-on chicken thighs",
                "100g Pancetta or smoked bacon (diced)",
                "2 Cups dry French red wine",
                "Fresh thyme, bay leaves, shallots, garlic",
                "Brown mushrooms quartered, butter, chicken stock"
            ),
            cookingInstructions = listOf(
                "Season chicken thighs with salt and pepper; sear skin-side down until deep golden and crisp.",
                "Remove chicken and fry pancetta, shallots, and mushrooms in remaining rendered fat.",
                "Deglaze pan with red wine, scraping all brown caramelized bits off the bottom.",
                "Return chicken to pan with fresh thyme; simmer uncovered for 30 minutes until sauce reduces to a syrup.",
                "Swirl in cold butter for a glossy restaurant sheen, sprinkle with parsley and serve hot."
            ),
            flavorProfile = "Deep fruity wine notes, crispy poultry skin, smoky bacon lardons and herbal thyme.",
            stressRelief = 16,
            energyBoost = 37
        ),
        MealItem(
            id = "fr_ratatouille",
            name = "Ratatouille Niçoise",
            cuisine = MealCuisine.FRENCH,
            iconEmoji = "🍆",
            tagline = "Beautiful spiraled zucchini, eggplant and tomatoes over roasted pepper purée",
            description = "The pride of Provence. Thin medallions of zucchini, yellow squash, Japanese eggplant, and ripe plum tomatoes baked over a fragrant bell pepper and herb purée.",
            takeoutPrice = 8_500L,
            cookingCost = 3_800L,
            cookingTimeMinutes = 45,
            ingredients = listOf(
                "2 Medium zucchinis & 2 yellow summer squash",
                "2 Slim Japanese eggplants",
                "4 Firm Roma tomatoes",
                "Roasted red pepper and crushed tomato piperade sauce",
                "Extra virgin olive oil, minced garlic, fresh thyme, herbes de Provence"
            ),
            cookingInstructions = listOf(
                "Spread a layer of warm seasoned roasted red pepper sauce across the base of a baking dish.",
                "Mandoline slice vegetables into uniform 2mm discs.",
                "Arrange vegetable discs in an alternating colorful spiral across the dish from rim to center.",
                "Drizzle with garlic-infused olive oil, sea salt, pepper, and fresh thyme leaves.",
                "Cover with parchment paper and bake at 190°C for 40 minutes until tender and caramelized."
            ),
            flavorProfile = "Sweet roasted pepper base, tender summer squash, garlic aromatics and fragrant herbs.",
            stressRelief = 14,
            energyBoost = 26
        ),
        MealItem(
            id = "fr_onion_soup",
            name = "French Onion Soup Gratinée",
            cuisine = MealCuisine.FRENCH,
            iconEmoji = "🍲",
            tagline = "Caramelized sweet onions, rich beef broth, toasted baguette and broiled Gruyère",
            description = "Sweet yellow onions slow-caramelized for 50 minutes until mahogany brown, simmered in beef broth, topped with crusty French sourdough and bubbling broiled Gruyère cheese.",
            takeoutPrice = 7_500L,
            cookingCost = 3_200L,
            cookingTimeMinutes = 60,
            ingredients = listOf(
                "5 Large yellow onions (thinly sliced)",
                "Butter & olive oil",
                "Rich beef bone stock & dry white wine",
                "Fresh thyme sprigs & bay leaf",
                "Toasted crusty baguette rounds",
                "Generous grated Gruyère & Parmesan cheese"
            ),
            cookingInstructions = listOf(
                "Cook sliced onions in butter over medium-low heat for 45 minutes, stirring until deep caramel brown.",
                "Deglaze the pan with white wine and scrape up all sweet caramelized sugars.",
                "Add beef broth and fresh thyme; simmer gently for 20 minutes to combine flavours.",
                "Ladle hot soup into oven-safe ceramic bowls; float toasted baguette rounds on top.",
                "Mound grated Gruyère cheese over bread and broil under high heat until cheese bubbles and chars."
            ),
            flavorProfile = "Intensely sweet caramelized onions, savory beef broth, crunchy bread and nutty melted cheese.",
            stressRelief = 18,
            energyBoost = 30
        ),

        // --- 🍕 ARTISAN PIZZAS ---
        MealItem(
            id = "pz_margherita",
            name = "Woodfired Neapolitan Margherita",
            cuisine = MealCuisine.PIZZA,
            iconEmoji = "🍕",
            tagline = "San Marzano tomatoes, creamy fior di latte mozzarella and fresh sweet basil",
            description = "48-hour fermented sourdough crust blistered at 450°C, simple crushed San Marzano tomatoes, melted fresh mozzarella, aromatic basil leaves, and cold-pressed olive oil.",
            takeoutPrice = 7_500L,
            cookingCost = 3_000L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "Fermented Neapolitan pizza dough ball",
                "Crushed Italian San Marzano peeled tomatoes",
                "Fresh Fior di latte or buffalo mozzarella cheese",
                "Fresh sweet basil leaves",
                "Extra virgin olive oil and flaky sea salt"
            ),
            cookingInstructions = listOf(
                "Stretch dough by hand from the center outward, leaving an airy 1-inch raised cornicione crust.",
                "Ladle a thin swirl of crushed seasoned tomato sauce across the base.",
                "Tear fresh mozzarella balls into chunks and scatter evenly over the sauce.",
                "Slide onto a blazing hot pizza stone or steel at maximum oven temperature for 7 minutes.",
                "Top with torn fresh basil leaves and a generous drizzle of olive oil before slicing."
            ),
            flavorProfile = "Airy crispy crust with charred leopard spots, sweet bright tomato and creamy melted cheese.",
            stressRelief = 14,
            energyBoost = 35
        ),
        MealItem(
            id = "pz_pepperoni_feast",
            name = "Double Pepperoni Feast Pizza",
            cuisine = MealCuisine.PIZZA,
            iconEmoji = "🍕",
            tagline = "Loaded with double layer artisan pepperoni cups, mozzarella and chili honey",
            description = "Crispy golden crust loaded from edge to edge with spicy cured pepperoni slices that curl into cups of seasoned oil, smothered in whole milk mozzarella.",
            takeoutPrice = 8_900L,
            cookingCost = 3_800L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "Crispy sourdough pizza dough base",
                "Seasoned garlic herb tomato pizza sauce",
                "Whole milk shredded mozzarella & provolone blend",
                "80g Artisanal dry-cured spicy pepperoni slices",
                "Hot chili honey drizzle & dried oregano"
            ),
            cookingInstructions = listOf(
                "Preheat oven with pizza stone to 250°C.",
                "Spread garlic herb tomato sauce to within 1/2 inch of dough perimeter.",
                "Scatter mozzarella blend, then layer pepperoni cups overlapping generously across the entire surface.",
                "Bake for 9 minutes until cheese bubbles and pepperoni edges char and curl into cups.",
                "Drizzle warm spicy chili honey and slice with a rolling pizza wheel."
            ),
            flavorProfile = "Crispy spicy pepperoni, gooey melted cheese, sweet chili kick and chewy crust.",
            stressRelief = 16,
            energyBoost = 40
        ),
        MealItem(
            id = "pz_bbq_chicken",
            name = "Smoky BBQ Chicken & Bacon Pizza",
            cuisine = MealCuisine.PIZZA,
            iconEmoji = "🍕",
            tagline = "Hickory BBQ sauce base with tender smoked chicken, bacon and red onions",
            description = "Sweet tangy hickory BBQ sauce, tender shredded grilled chicken, crispy smoked bacon bits, sweet red onion rings, and fresh cilantro leaves.",
            takeoutPrice = 8_500L,
            cookingCost = 3_600L,
            cookingTimeMinutes = 22,
            ingredients = listOf(
                "Fresh artisan pizza dough",
                "Smoky hickory BBQ sauce base",
                "Shredded grilled chicken breast",
                "Crispy smoked bacon crumbles",
                "Thinly sliced red onions, mozzarella, fresh cilantro"
            ),
            cookingInstructions = listOf(
                "Roll dough and dock lightly with a fork to prevent excessive bubbling.",
                "Spread BBQ sauce evenly over dough base.",
                "Layer shredded mozzarella, grilled chicken cubes, and bacon crumbles.",
                "Scatter red onion rings on top for caramelization in the oven.",
                "Bake at 240°C for 10 minutes; finish with freshly chopped cilantro."
            ),
            flavorProfile = "Sweet tangy barbecue smoke, salty bacon crunch, savory chicken and melted cheese.",
            stressRelief = 15,
            energyBoost = 38
        ),
        MealItem(
            id = "pz_suya_fusion",
            name = "Lagos Suya Spiced Fusion Pizza",
            cuisine = MealCuisine.PIZZA,
            iconEmoji = "🍕",
            tagline = "Street suya beef strips, roasted peppers, onions, mozzarella and Yaji dust",
            description = "The ultimate fusion masterpiece! Thin crust pizza layered with spicy habanero tomato sauce, shredded beef suya, red bell peppers, purple onions, and a sprinkle of authentic yaji spice.",
            takeoutPrice = 9_200L,
            cookingCost = 4_000L,
            cookingTimeMinutes = 22,
            ingredients = listOf(
                "Sourdough pizza base",
                "Spicy tomato pepper pizza sauce",
                "Grilled seasoned beef suya strips",
                "Sweet bell pepper slices & purple onions",
                "Mozzarella blend & authentic Yaji peanut powder"
            ),
            cookingInstructions = listOf(
                "Spread spicy tomato pepper reduction across pizza base.",
                "Cover with generous mozzarella layer.",
                "Distribute beef suya strips, roasted sweet peppers, and onion rings.",
                "Bake until cheese turns golden brown and edges blister crisply.",
                "Dust hot pizza with fine Yaji spice powder and serve piping hot."
            ),
            flavorProfile = "Smoky street suya heat, nutty Yaji aroma, sweet roasted peppers and savory crust.",
            stressRelief = 18,
            energyBoost = 42
        ),

        // --- 🌯 SHAWARMA & WRAPS ---
        MealItem(
            id = "sw_beef_classic",
            name = "Classic Street Beef Shawarma",
            cuisine = MealCuisine.SHAWARMA,
            iconEmoji = "🌯",
            tagline = "Tender shaved beef, crunchy cabbage, garlic cream and spicy pepper mayo",
            description = "Thinly shaved marinated beef grilled on a vertical spit, rolled in warm Lebanese flatbread with shredded white cabbage, pickled cucumber, creamy garlic toum, and Nigerian spicy mayo sauce.",
            takeoutPrice = 4_500L,
            cookingCost = 1_900L,
            cookingTimeMinutes = 18,
            ingredients = listOf(
                "Large Lebanese pita flatbread",
                "200g Marinated beef flank steak (cumin, coriander, paprika, garlic)",
                "Finely shredded green cabbage & grated carrot",
                "Pickled dill cucumber slices",
                "Creamy garlic mayonnaise & sweet spicy chili ketchup sauce"
            ),
            cookingInstructions = listOf(
                "Sear seasoned beef strips in a screaming hot cast-iron skillet for 4 minutes until charred.",
                "Warm flatbread on a dry pan for 30 seconds until pliable and aromatic.",
                "Spread a thick line of garlic mayo and spicy chili ketchup across the lower third of the bread.",
                "Mound grilled beef, crunchy shredded cabbage, and pickled cucumber on top.",
                "Roll tightly, tucking in sides, and toast on a sandwich press until wrap is sealed and grill-marked."
            ),
            flavorProfile = "Spicy creamy garlic crunch, juicy charred beef strips, tangy pickles and warm toasted wrap.",
            stressRelief = 13,
            energyBoost = 33
        ),
        MealItem(
            id = "sw_chicken_sausage",
            name = "Double Sausage Chicken Shawarma",
            cuisine = MealCuisine.SHAWARMA,
            iconEmoji = "🌯",
            tagline = "Juicy spiced chicken, two grilled cocktail sausages, sweet cream and fiery pepper",
            description = "The quintessential Nigerian nightlife shawarma! Packed with shredded grilled chicken thighs, two plump cocktail hotdogs, shredded sweet cabbage, and extra drizzled cream sauce.",
            takeoutPrice = 5_000L,
            cookingCost = 2_200L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "Large thin Lebanese flatbread",
                "Grilled seasoned chicken thigh strips",
                "2 Plump cocktail sausages (pan-fried)",
                "Crispy shredded cabbage, carrot, and sweet mayonnaise dressing",
                "Scotch bonnet pepper oil drizzle"
            ),
            cookingInstructions = listOf(
                "Grill sausages until plump and lightly blistered; slice lengthwise.",
                "Toss chicken strips with garlic, ginger, curry, and chili flakes in hot pan.",
                "Lay flatbread, spread sweet mayonnaise and fiery pepper oil.",
                "Fill with hot chicken, both sausages, and seasoned crisp cabbage.",
                "Fold tightly into a burrito-style parcel; press on a hot ribbed grill until crispy."
            ),
            flavorProfile = "Explosive savoury juices, sweet creamy dressing, fiery scotch bonnet heat and smoky sausage bite.",
            stressRelief = 15,
            energyBoost = 36
        ),
        MealItem(
            id = "sw_lamb_falafel",
            name = "Crispy Lamb & Falafel Wrap",
            cuisine = MealCuisine.SHAWARMA,
            iconEmoji = "🌯",
            tagline = "Herbed chickpea falafel, shaved lamb, mint tahini and fresh parsley salad",
            description = "Freshly fried golden chickpea falafels with coriander and cumin, paired with shaved roasted lamb shoulder, diced tomato-cucumber salad, and velvety lemon tahini cream.",
            takeoutPrice = 5_800L,
            cookingCost = 2_600L,
            cookingTimeMinutes = 25,
            ingredients = listOf(
                "Warm Middle Eastern flatbread wrap",
                "Spiced minced lamb shoulder (roasted)",
                "3 Crisp homemade chickpea falafel patties",
                "Cucumber, tomato, and fresh mint parsley salad",
                "Lemon tahini sauce & pickled red turnip"
            ),
            cookingInstructions = listOf(
                "Pan-fry falafel patties in hot oil until crunchy and deep golden on both sides.",
                "Warm flatbread and spread velvety tahini across the center.",
                "Crush falafel patties gently with a fork, add hot shaved spiced lamb.",
                "Top with mint parsley salad and crunchy pickled turnips.",
                "Roll snugly and serve with extra lemon tahini for dipping."
            ),
            flavorProfile = "Nutty sesame tahini, crisp herbaceous falafel, savory tender lamb and refreshing mint.",
            stressRelief = 14,
            energyBoost = 34
        ),

        // --- 🍨 GOURMET ICE CREAMS ---
        MealItem(
            id = "ic_belgian_chocolate",
            name = "Belgian Dark Chocolate Gelato",
            cuisine = MealCuisine.ICE_CREAM,
            iconEmoji = "🍨",
            tagline = "Rich 70% Belgian cocoa custard with molten dark chocolate ribbons",
            description = "Dense, velvety slow-churned gelato crafted with authentic 70% Belgian chocolate, Madagascar cocoa butter, and folded through with dark chocolate ganache swirl.",
            takeoutPrice = 4_500L,
            cookingCost = 1_800L,
            cookingTimeMinutes = 15,
            ingredients = listOf(
                "200g 70% Belgian dark chocolate callets",
                "Whole milk & heavy whipping cream",
                "Egg yolks and granulated pure sugar",
                "Unsweetened Dutch-process cocoa powder",
                "Pinch of espresso powder to deepen chocolate notes"
            ),
            cookingInstructions = listOf(
                "Whisk egg yolks and sugar until pale and fluffy.",
                "Heat cream, milk, and cocoa powder until simmering; temper into egg mixture slowly.",
                "Pour custard over chopped Belgian chocolate; whisk until glossy and completely melted.",
                "Chill base thoroughly for 4 hours, then churn in an ice cream maker for 25 minutes.",
                "Swirl warm ganache through churned gelato and freeze until scoopable."
            ),
            flavorProfile = "Intense bittersweet cocoa, ultra-silky creaminess and luxurious dark chocolate fudge.",
            stressRelief = 20,
            energyBoost = 25,
            flavourVariant = "Belgian Dark Chocolate (70%)"
        ),
        MealItem(
            id = "ic_vanilla_bean",
            name = "Madagascar Vanilla Bean Cream",
            cuisine = MealCuisine.ICE_CREAM,
            iconEmoji = "🍨",
            tagline = "Infused with real Bourbon vanilla pods and organic sweet farm cream",
            description = "Pure luxury in its simplest form. Real Madagascar Bourbon vanilla pods scraped and steeped in organic sweet cream, speckled with thousands of aromatic vanilla seeds.",
            takeoutPrice = 4_200L,
            cookingCost = 1_600L,
            cookingTimeMinutes = 15,
            ingredients = listOf(
                "2 Plump Madagascar Bourbon vanilla bean pods",
                "500ml Heavy whipping cream",
                "250ml Whole organic milk",
                "150g Cane sugar & 5 egg yolks",
                "Crispy butter waffle cone"
            ),
            cookingInstructions = listOf(
                "Split vanilla pods lengthwise with a sharp knife and scrape out thousands of tiny black caviar seeds.",
                "Steep pods and seeds in warm milk and cream for 30 minutes to extract floral aroma.",
                "Whisk yolks and sugar until thick; whisk into warm vanilla cream.",
                "Cook over low heat until custard coats the back of a wooden spoon; strain out empty pods.",
                "Churn to velvet perfection and serve in fresh waffle cones."
            ),
            flavorProfile = "Floral aromatic vanilla, clean dairy richness, sweet comforting warmth.",
            stressRelief = 16,
            energyBoost = 22,
            flavourVariant = "Madagascar Vanilla Bean"
        ),
        MealItem(
            id = "ic_salted_caramel",
            name = "Sea Salted Caramel Crunch",
            cuisine = MealCuisine.ICE_CREAM,
            iconEmoji = "🍨",
            tagline = "Handcrafted amber burnt caramel with pink Himalayan salt and praline brittle",
            description = "Slow-caramelized cane sugar taken to the edge of bitterness, softened with rich butter and pink Himalayan salt, churning into a decadent custard with crunchy pecan praline.",
            takeoutPrice = 4_800L,
            cookingCost = 2_000L,
            cookingTimeMinutes = 20,
            ingredients = listOf(
                "Granulated sugar (caramelized to deep amber)",
                "French unsalted butter & heavy cream",
                "Coarse pink Himalayan sea salt",
                "Custard ice cream base",
                "Handmade crushed pecan praline brittle"
            ),
            cookingInstructions = listOf(
                "Melt sugar in a stainless saucepan undisturbed until it liquifies into deep copper amber.",
                "Carefully whisk in butter and warm cream; watch out for bubbling steam.",
                "Stir in Himalayan salt and allow caramel sauce to cool to room temperature.",
                "Blend half of the salted caramel into the sweet cream base and churn until frozen.",
                "Fold in ribbons of pure salted caramel and crushed praline brittle chunks."
            ),
            flavorProfile = "Sweet butterscotch, salty crystalline crunch, deep smoky caramel finish.",
            stressRelief = 22,
            energyBoost = 28,
            flavourVariant = "Sea Salted Caramel"
        ),
        MealItem(
            id = "ic_mango_sorbet",
            name = "Mango Passionfruit Tropical Sorbet",
            cuisine = MealCuisine.ICE_CREAM,
            iconEmoji = "🍧",
            tagline = "Sun-ripened Alphonso mango purée and tart passionfruit pulp",
            description = "Dairy-free, refreshing tropical sorbet made with ripe sweet mango pulp, tangy passionfruit seeds, and a splash of fresh lime zest.",
            takeoutPrice = 4_000L,
            cookingCost = 1_500L,
            cookingTimeMinutes = 10,
            ingredients = listOf(
                "3 Ripe sweet Alphonso mangoes (pureed)",
                "Pulp and seeds from 4 fresh passionfruits",
                "Simple sugar syrup & fresh lime juice",
                "Touch of sea salt to elevate fruit brightness"
            ),
            cookingInstructions = listOf(
                "Blend mango flesh with simple syrup and lime juice until silky smooth.",
                "Fold in fresh passionfruit pulp with aromatic seeds.",
                "Chill mixture in the refrigerator for 2 hours.",
                "Churn in sorbet maker until light and frozen.",
                "Scoop into chilled glass bowls for instant cool refreshment."
            ),
            flavorProfile = "Electric tropical citrus, sweet mango nectar, zesty tart passionfruit punch.",
            stressRelief = 18,
            energyBoost = 24,
            flavourVariant = "Mango Passionfruit Sorbet"
        ),

        // --- 🎂 LUXURY CAKES & CONFECTIONERY ---
        MealItem(
            id = "ck_red_velvet",
            name = "Red Velvet Dream Cake (6-inch / 10-inch)",
            cuisine = MealCuisine.CAKES,
            iconEmoji = "🎂",
            tagline = "Velvety cocoa crimson crumb layered with fluffy cream cheese frosting",
            description = "Rich, tender ruby sponge with subtle hints of cocoa, filled and coated with silky whipped Philadelphia cream cheese frosting and dusted with red velvet sponge crumbs.",
            takeoutPrice = 14_000L,
            cookingCost = 5_800L,
            cookingTimeMinutes = 60,
            ingredients = listOf(
                "2.5 cups All-purpose cake flour & 2 tbsp cocoa powder",
                "Buttermilk, vegetable oil, eggs, and pure vanilla extract",
                "Baking soda, vinegar reaction, and crimson food color",
                "Philadelphia cream cheese, unsalted butter, and powdered confectioner sugar",
                "Choice of Petit 6-inch or Grand 10-inch cake tin size"
            ),
            cookingInstructions = listOf(
                "Whisk dry ingredients; combine oil, sugar, buttermilk, eggs, and crimson color in a bowl.",
                "Mix wet and dry ingredients gently; trigger with vinegar and baking soda fizz.",
                "Bake in prepared round tins at 175°C for 30 minutes until a toothpick comes out clean.",
                "Beat cream cheese and butter until white and light; gradually whip in powdered sugar.",
                "Level cake layers, stack with generous frosting, coat exterior and decorate with red velvet crumbs."
            ),
            flavorProfile = "Subtle chocolate undertone, tang of buttermilk, silky sweet cream cheese decadence.",
            stressRelief = 25,
            energyBoost = 35,
            cakeSizeOption = "6-inch Petit / 10-inch Family"
        ),
        MealItem(
            id = "ck_chocolate_fudge",
            name = "Triple Chocolate Fudge Tiered Cake",
            cuisine = MealCuisine.CAKES,
            iconEmoji = "🎂",
            tagline = "Moist dark chocolate sponge coated in molten chocolate ganache",
            description = "Three tiers of dark cocoa sponge soaked in vanilla syrup, filled with whipped chocolate mousse, and smothered in mirror-shine dark chocolate ganache glaze.",
            takeoutPrice = 16_500L,
            cookingCost = 6_500L,
            cookingTimeMinutes = 75,
            ingredients = listOf(
                "Dark Dutch cocoa powder & hot brewed coffee",
                "Buttermilk, eggs, sugar, flour, baking powder",
                "Semi-sweet chocolate callets & heavy cream for ganache",
                "Shaved dark chocolate curl garnish"
            ),
            cookingInstructions = listOf(
                "Bloom cocoa powder in hot fresh coffee to activate deep chocolate aromatics.",
                "Mix with buttermilk, sugar, and flour to create a luscious chocolate batter.",
                "Bake cake layers in tiers until springy to the touch; cool completely on wire racks.",
                "Heat heavy cream and pour over chopped chocolate; stir into a glossy ganache.",
                "Sandwich layers with ganache, pour warm glaze over top and shower with chocolate curls."
            ),
            flavorProfile = "Intensely chocolatey, moist fudgy crumb, smooth glossy ganache melt.",
            stressRelief = 28,
            energyBoost = 40,
            cakeSizeOption = "3-Tier Celebration"
        ),
        MealItem(
            id = "ck_nigerian_fruit_cake",
            name = "Rich Traditional Fruit Cake",
            cuisine = MealCuisine.CAKES,
            iconEmoji = "🎂",
            tagline = "Dark rum-soaked currants, candied citrus peel, nutmeg and royal icing",
            description = "The classic Nigerian wedding and Christmas celebration fruit cake. Steeped for weeks in dark rum and brandy with plump sultanas, cherries, and warm nutmeg.",
            takeoutPrice = 18_000L,
            cookingCost = 7_500L,
            cookingTimeMinutes = 120,
            ingredients = listOf(
                "Dark rum & brandy soaked dried currants, raisins, cherries",
                "Candied citrus peel & ground almonds",
                "Brown muscovado sugar, butter, and farm eggs",
                "Nutmeg, cinnamon, allspice, and black treacle molasses",
                "Almond marzipan layer & pure white royal icing"
            ),
            cookingInstructions = listOf(
                "Cream butter and dark muscovado sugar until light and fluffy.",
                "Add eggs one at a time, followed by treacle, flour, and aromatic ground spices.",
                "Fold in heavy bounty of rum-macerated fruit until batter is dense with fruit.",
                "Bake low and slow at 140°C for 2.5 hours wrapped in brown paper to protect edges.",
                "Cool, feed with extra brandy, blanket in marzipan and finish with crisp royal icing."
            ),
            flavorProfile = "Deep boozy fruitiness, caramelized treacle, warm holiday spices and sweet marzipan.",
            stressRelief = 24,
            energyBoost = 38,
            cakeSizeOption = "10-inch Royal"
        ),
        MealItem(
            id = "ck_lotus_cheesecake",
            name = "Lotus Biscoff Caramel Cheesecake",
            cuisine = MealCuisine.CAKES,
            iconEmoji = "🍰",
            tagline = "Speculoos biscuit crust, creamy baked cheesecake and melted Biscoff spread",
            description = "Buttery crushed Lotus Biscoff biscuit base topped with baked vanilla cream cheesecake, drenched in melted Biscoff spread and ringed with whole biscuits.",
            takeoutPrice = 15_500L,
            cookingCost = 6_200L,
            cookingTimeMinutes = 55,
            ingredients = listOf(
                "250g Lotus Biscoff speculoos biscuits (crushed)",
                "Melted unsalted butter",
                "600g Full-fat cream cheese & sour cream",
                "Eggs, sugar, and pure vanilla bean paste",
                "Smooth Lotus Biscoff spread jar (melted for topping)"
            ),
            cookingInstructions = listOf(
                "Press crushed Biscoff crumbs and melted butter into a 9-inch springform pan; chill.",
                "Beat cream cheese, sugar, and sour cream until velvety; gently incorporate eggs.",
                "Pour over crust and bake in a water bath at 160°C for 50 minutes until gently set.",
                "Cool in oven with door ajar, then refrigerate for at least 6 hours.",
                "Warm Biscoff spread until pourable; cascade over cheesecake and garnish with biscuits."
            ),
            flavorProfile = "Spiced cinnamon caramel biscuit crunch, tangy dense cheesecake, buttery spread sweetness.",
            stressRelief = 26,
            energyBoost = 35,
            cakeSizeOption = "9-inch Round"
        ),

        // --- 🌍 INTERCONTINENTAL DISHES ---
        MealItem(
            id = "ic_grilled_salmon",
            name = "Pan-Seared Atlantic Salmon & Asparagus",
            cuisine = MealCuisine.INTERCONTINENTAL,
            iconEmoji = "🐟",
            tagline = "Crisp-skin salmon fillet with lemon herb butter sauce and baby potatoes",
            description = "Fresh Norwegian Atlantic salmon pan-seared to medium tender with skin crackling like glass, accompanied by butter-glazed baby asparagus and golden crushed potatoes.",
            takeoutPrice = 15_000L,
            cookingCost = 7_000L,
            cookingTimeMinutes = 25,
            ingredients = listOf(
                "200g Fresh Atlantic salmon fillet with skin on",
                "French unsalted butter & extra virgin olive oil",
                "Fresh garlic cloves, fresh dill, and flat-leaf parsley",
                "Fresh lemon juice and lemon zest",
                "Bundle of tender green baby asparagus & roasted baby potatoes"
            ),
            cookingInstructions = listOf(
                "Pat salmon skin bone-dry with paper towels and score skin lightly; season with sea salt.",
                "Heat olive oil in stainless skillet until shimmering; press salmon skin-side down for 5 minutes.",
                "Flip gently once skin is shatteringly crisp; add butter, crushed garlic, and fresh dill.",
                "Baste bubbling herb butter continuously over salmon flesh for 2 minutes.",
                "Sauté asparagus in remaining pan juices with a squeeze of fresh lemon; plate together elegantly."
            ),
            flavorProfile = "Crispy salty skin, buttery flaky salmon, bright lemon citrus and fresh herbal dill.",
            stressRelief = 20,
            energyBoost = 45
        ),
        MealItem(
            id = "ic_filet_mignon",
            name = "Filet Mignon & Truffle Parmesan Fries",
            cuisine = MealCuisine.INTERCONTINENTAL,
            iconEmoji = "🥩",
            tagline = "Prime beef tenderloin steak, red wine demi-glace and hand-cut truffle fries",
            description = "Prime center-cut beef tenderloin steak seared to medium rare in foaming butter, rosemary and garlic, served with house-cut fries tossed in white truffle oil and aged Parmigiano.",
            takeoutPrice = 22_000L,
            cookingCost = 10_000L,
            cookingTimeMinutes = 30,
            ingredients = listOf(
                "250g Prime center-cut beef tenderloin steak",
                "Fresh rosemary sprigs, thyme, and whole garlic cloves",
                "Hand-cut Russet potato fries",
                "White truffle oil & freshly grated Parmigiano Reggiano cheese",
                "Red wine reduction demi-glace sauce"
            ),
            cookingInstructions = listOf(
                "Bring steak to room temperature; season heavily with coarse sea salt and cracked black pepper.",
                "Sear in smoking hot cast-iron skillet for 3 minutes on each side.",
                "Add copious butter, smashed garlic, and rosemary; tilt pan and baste steak for 2 minutes.",
                "Rest steak on a warm cutting board for 8 minutes to redistribute natural juices.",
                "Double-fry potatoes until crisp, toss with truffle oil and parmesan, and serve alongside steak."
            ),
            flavorProfile = "Tender melt-in-the-mouth beef, earthy aromatic truffle, nutty parmesan and savory demi-glace.",
            stressRelief = 30,
            energyBoost = 50
        ),
        MealItem(
            id = "ic_seafood_paella",
            name = "Seafood Paella Valenciana",
            cuisine = MealCuisine.INTERCONTINENTAL,
            iconEmoji = "🥘",
            tagline = "Saffron bomba rice with colossal prawns, calamari, mussels and crispy socarrat",
            description = "Spain's coastal showstopper. Spanish Bomba rice simmered in rich shellfish broth, infused with precious saffron threads, colossal black tiger prawns, tender calamari rings, and green peas.",
            takeoutPrice = 17_500L,
            cookingCost = 8_000L,
            cookingTimeMinutes = 45,
            ingredients = listOf(
                "Spanish Bomba short-grain rice",
                "Pinch of pure saffron threads steeped in warm water",
                "Jumbo tiger prawns, cleaned squid rings, fresh green mussels",
                "Smoked sweet pimentón paprika, garlic, grated tomato sofrito",
                "Rich seafood lobster stock & sweet garden peas"
            ),
            cookingInstructions = listOf(
                "Heat olive oil in a wide carbon steel paella pan; sear seafood quickly and remove.",
                "Fry sofrito of garlic, grated tomato, and sweet pimentón until thick and caramelized.",
                "Add Bomba rice, coating every grain in the flavorful oil.",
                "Pour in hot saffron-infused seafood stock; do NOT stir once boiling to allow socarrat to form.",
                "Arrange prawns and mussels on top; cook until broth is absorbed and bottom crust crackles."
            ),
            flavorProfile = "Floral earthy saffron, sweet juicy seafood, smoky paprika and crunchy toasted rice bottom.",
            stressRelief = 24,
            energyBoost = 44
        ),
        MealItem(
            id = "ic_birria_tacos",
            name = "Mexican Birria Tacos with Consomé Dip",
            cuisine = MealCuisine.INTERCONTINENTAL,
            iconEmoji = "🌮",
            tagline = "Slow-stewed spiced beef short ribs in chili broth with melted Oaxaca cheese",
            description = "Three golden corn tortillas dipped in rich guajillo-ancho chili broth, grilled with melted Oaxaca cheese, stuffed with shredded braised beef, and served with a bowl of hot consomé for dipping.",
            takeoutPrice = 8_500L,
            cookingCost = 3_800L,
            cookingTimeMinutes = 50,
            ingredients = listOf(
                "Beef short ribs and chuck roast",
                "Dried Guajillo, Ancho and Chipotle chilies",
                "Cumin, cloves, Mexican oregano, cinnamon stick, garlic",
                "White corn tortillas",
                "Oaxaca or mozzarella melting cheese, diced onions, fresh cilantro, lime wedges"
            ),
            cookingInstructions = listOf(
                "Toast dried chilies, blend with roasted tomatoes, garlic, vinegar, and spices into a smooth paste.",
                "Sear beef and braise in chili broth for 2.5 hours until falling apart; shred with two forks.",
                "Dip corn tortillas into the top chili-oil layer of the consomé; lay on a hot flat-top grill.",
                "Melt cheese on tortilla, top with shredded braised beef, fold in half and grill until crispy.",
                "Serve with a hot bowl of cilantro-onion consomé for luxurious dunking."
            ),
            flavorProfile = "Rich deeply savory chili broth, crispy cheesy fried tortilla, tender beef and zesty lime.",
            stressRelief = 18,
            energyBoost = 40
        )
    )

    fun getPartyOptions(): List<PartyEvent> = listOf(
        PartyEvent(
            id = "pty_suya_chill",
            title = "Suya & Afrobeats Backyard Chill",
            theme = "Casual Hangout",
            cost = 25_000L,
            iconEmoji = "🍢",
            invitedGuests = listOf("Chioma (Tech Founder)", "Babatunde (Balogun Trader)", "Emeka (Logistics Boss)"),
            playlist = "Burna Boy, Asake & Wizkid Chill Vibes",
            drinksProvided = "Chilled Chapman & Palm Wine",
            reputationBoost = 8,
            stressReduction = 20,
            hypeLevel = 75
        ),
        PartyEvent(
            id = "pty_house_rave",
            title = "Lekki House Party & DJ Rave",
            theme = "Night Rhythms",
            cost = 60_000L,
            iconEmoji = "🎧",
            invitedGuests = listOf("DJ Neptune", "Amara (Fashion Stylist)", "Tariq (Crypto Trader)", "Halima (Chef)"),
            playlist = "Amapiano & High-Energy Afrobeats",
            drinksProvided = "Champagne Cocktails & Premium Stout",
            reputationBoost = 15,
            stressReduction = 28,
            hypeLevel = 90
        ),
        PartyEvent(
            id = "pty_owambe_luxury",
            title = "Grand Owambe Celebration",
            theme = "Cultural Royalty",
            cost = 150_000L,
            iconEmoji = "👑",
            invitedGuests = listOf("Chief Alabi", "Alhaja Ronke", "Senator Adeleke", "All Community Members"),
            playlist = "Live Juju & Fuji Band Performance",
            drinksProvided = "Full Bar, Assorted Meat & Party Jollof",
            reputationBoost = 28,
            stressReduction = 35,
            hypeLevel = 98
        )
    )

    fun getClubbingVenues(): List<ClubbingVenue> = listOf(
        ClubbingVenue(
            id = "clb_quilox",
            name = "Quilox Ultra Lounge",
            location = "Victoria Island, Lagos",
            iconEmoji = "✨",
            vibeDescription = "Celebrity hotspot with laser light shows, world-class sound, and VIP table bottle service.",
            regularEntryFee = 15_000L,
            vipTableCost = 120_000L,
            stressReduction = 25,
            musicGenre = "Afrobeats & Hip Hop"
        ),
        ClubbingVenue(
            id = "clb_cubana",
            name = "Cubana Chief Lounge",
            location = "Ikeja GRA, Lagos",
            iconEmoji = "🍾",
            vibeDescription = "High-energy nightlife sanctuary known for dazzling sparklers, live hype men, and luxury energy.",
            regularEntryFee = 10_000L,
            vipTableCost = 90_000L,
            stressReduction = 22,
            musicGenre = "Amapiano & Street Pop"
        ),
        ClubbingVenue(
            id = "clb_beach_club",
            name = "Landmark Beach Club After-Dark",
            location = "Oniru Beach, Lagos",
            iconEmoji = "🌊",
            vibeDescription = "Open-air ocean breeze, barefoot dancing on warm sand, fire dancers, and cool seaside cocktails.",
            regularEntryFee = 8_000L,
            vipTableCost = 75_000L,
            stressReduction = 30,
            musicGenre = "Deep House & Afro-Fusion"
        )
    )
}
