package com.example.data

import com.example.model.AdCampaign
import com.example.model.AdType
import com.example.model.AjoSavingGoal
import com.example.model.Business
import com.example.model.BusinessType
import com.example.model.CharacterProfile
import com.example.model.CommunityMember
import com.example.model.CommunityPost
import com.example.model.CommunityProposal
import com.example.model.Investment
import com.example.model.RiskLevel
import com.example.model.Upgrade

object BusinessData {

    val characters = listOf(
        CharacterProfile(
            id = "tunde_tech",
            name = "Tunde",
            title = "Computer Village Tech Whiz",
            avatarEmoji = "👨🏾‍💻",
            backstory = "Learned mobile repairs and accessories at Otigba Street. Sharp eye for fast-moving gadget trends and youth appeal.",
            perkName = "Tech Arbitrage",
            perkDescription = "+20% customer demand on electronics & modern products",
            demandBoostPercent = 20,
            costDiscountPercent = 5,
            startingReputation = 55,
            startingCapitalBonus = 20_000L
        ),
        CharacterProfile(
            id = "mama_blessing",
            name = "Mama Blessing",
            title = "Wholesale Market Queen",
            avatarEmoji = "👩🏾‍🦱",
            backstory = "Has traded provisions in Balogun and Tejuosho markets for 15 years. Every major importer knows and trusts her word.",
            perkName = "Wholesale Supplier Trust",
            perkDescription = "12% cheaper wholesale restock costs from day one",
            demandBoostPercent = 10,
            costDiscountPercent = 12,
            startingReputation = 65,
            startingCapitalBonus = 35_000L
        ),
        CharacterProfile(
            id = "chinedu_trader",
            name = "Chinedu",
            title = "Alaba Commercial Apprentice",
            avatarEmoji = "🧔🏾‍♂️",
            backstory = "Graduated with honors from the traditional 7-year trade settlement system. Equipped with capital and keen business discipline.",
            perkName = "Settlement Capital",
            perkDescription = "Starts with extra ₦50,000 cash and +10 credit score",
            demandBoostPercent = 15,
            costDiscountPercent = 8,
            startingReputation = 60,
            startingCapitalBonus = 50_000L
        ),
        CharacterProfile(
            id = "iya_basira",
            name = "Iya Basira",
            title = "Culinary Mama Put Legend",
            avatarEmoji = "👩🏾‍🍳",
            backstory = "Famous across the mainland for aromatic firewood party Jollof, spicy goat pepper soup, and crispy dodo.",
            perkName = "Secret Flavour Aura",
            perkDescription = "+30% natural customer walk-in foot traffic",
            demandBoostPercent = 30,
            costDiscountPercent = 0,
            startingReputation = 58,
            startingCapitalBonus = 25_000L
        ),
        CharacterProfile(
            id = "amaka_pos",
            name = "Amaka",
            title = "Fintech & Mobile Money Boss",
            avatarEmoji = "👩🏾‍💼",
            backstory = "Former banking officer who recognized that the street runs on instant cash liquidity and trust.",
            perkName = "Rapid Cash Flow",
            perkDescription = "Reduced operational stress (-5%) and high cash liquidity float",
            demandBoostPercent = 18,
            costDiscountPercent = 6,
            startingReputation = 62,
            startingCapitalBonus = 40_000L
        ),
        CharacterProfile(
            id = "bayo_barber",
            name = "Bayo",
            title = "Celebrity Barber & Stylist",
            avatarEmoji = "💇🏾‍♂️",
            backstory = "Pioneered viral fade designs and bridal styling. Influencers and music artists travel across town for his chair.",
            perkName = "Viral Aesthetics",
            perkDescription = "High customer tipping and +15 starting brand reputation",
            demandBoostPercent = 22,
            costDiscountPercent = 0,
            startingReputation = 70,
            startingCapitalBonus = 15_000L
        )
    )

    val startingBusinesses = listOf(
        Business(
            id = "provisions",
            name = "Provisions Store",
            type = BusinessType.PROVISIONS,
            iconEmoji = "🛒",
            tagline = "Staples, drinks & everyday household goods",
            description = "High turnover, loyal neighborhood customers, and predictable sales. Low spoilage risk, moderate profit margins.",
            itemUnitName = "Cartons",
            baseUnitCost = 4_000L,
            baseSellingPrice = 5_500L,
            defaultStock = 40,
            startingCapital = 180_000L,
            spoilageRate = 0.02f,
            baseWeeklyDemand = 60
        ),
        Business(
            id = "phone_accessories",
            name = "Phone Accessories Hub",
            type = BusinessType.PHONE_ACCESSORIES,
            iconEmoji = "📱",
            tagline = "Chargers, powerbanks, earphones & cases",
            description = "High profit margins and zero spoilage, but customers bargain hard and trends shift fast. Requires good marketing.",
            itemUnitName = "Packs",
            baseUnitCost = 3_500L,
            baseSellingPrice = 6_500L,
            defaultStock = 30,
            startingCapital = 150_000L,
            spoilageRate = 0.0f,
            baseWeeklyDemand = 45
        ),
        Business(
            id = "food_vendor",
            name = "Mama Put Food Stall",
            type = BusinessType.FOOD_VENDOR,
            iconEmoji = "🍲",
            tagline = "Hot Jollof, fried fish, plantain & swallow",
            description = "Daily cash in hand and strong customer loyalty, but ingredients spoil without cold storage and fuel costs bite.",
            itemUnitName = "Food Cooler Batches",
            baseUnitCost = 5_000L,
            baseSellingPrice = 8_500L,
            defaultStock = 25,
            startingCapital = 140_000L,
            spoilageRate = 0.15f,
            baseWeeklyDemand = 55
        ),
        Business(
            id = "barbershop",
            name = "Barbershop & Beauty Salon",
            type = BusinessType.BARBERSHOP,
            iconEmoji = "💈",
            tagline = "Fades, braids, styling & grooming",
            description = "Service-driven business with incredible margins on labor, but 100% dependent on steady electricity and skilled staff.",
            itemUnitName = "Service Kits",
            baseUnitCost = 1_500L,
            baseSellingPrice = 4_500L,
            defaultStock = 35,
            startingCapital = 120_000L,
            spoilageRate = 0.0f,
            baseWeeklyDemand = 50
        ),
        Business(
            id = "pos_agent",
            name = "POS & Cash Point",
            type = BusinessType.POS_AGENT,
            iconEmoji = "💳",
            tagline = "Cash withdrawal, transfers & utility bills",
            description = "Fastest velocity of money in town! Earn commissions on every transaction. Requires large cash float and street security.",
            itemUnitName = "Transaction Batches",
            baseUnitCost = 2_000L,
            baseSellingPrice = 4_200L,
            defaultStock = 50,
            startingCapital = 200_000L,
            spoilageRate = 0.0f,
            baseWeeklyDemand = 75
        ),
        Business(
            id = "yam_depot",
            name = "Yam & Produce Depo",
            type = BusinessType.PROVISIONS,
            iconEmoji = "🥔",
            tagline = "Wholesale Benue yams, plantain & foodstuffs",
            description = "Direct farmgate supply of premium tubers of yam, plantain bunches, and bags of gari. High volume demand across city markets.",
            itemUnitName = "Tubers of Yam",
            baseUnitCost = 2_800L,
            baseSellingPrice = 4_800L,
            defaultStock = 45,
            startingCapital = 160_000L,
            spoilageRate = 0.04f,
            baseWeeklyDemand = 65
        )
    )

    fun getAvailableAds(): List<AdCampaign> {
        return listOf(
            // TV Ads
            AdCampaign(
                id = "tv_prime_broadcast",
                type = AdType.TV,
                name = "Prime-Time TV Commercial",
                channelName = "Channels TV & NTA Network",
                tagline = "Run a glossy 30-second ad slot during the 8:00 PM evening news.",
                cost = 75_000L,
                durationWeeks = 3,
                weeksRemaining = 3,
                demandBoostPercent = 45,
                reputationBoost = 18,
                iconEmoji = "📺"
            ),
            AdCampaign(
                id = "tv_music_lifestyle",
                type = AdType.TV,
                name = "Music & Entertainment Spot",
                channelName = "HipTV & SoundCity Broadcast",
                tagline = "Target the vibrant youth audience with catchy video jingling.",
                cost = 45_000L,
                durationWeeks = 2,
                weeksRemaining = 2,
                demandBoostPercent = 30,
                reputationBoost = 12,
                iconEmoji = "🎬"
            ),

            // Billboard Ads
            AdCampaign(
                id = "billboard_expressway",
                type = AdType.BILLBOARD,
                name = "Third Mainland Mega Billboard",
                channelName = "Third Mainland Bridge Expressway",
                tagline = "High-definition illuminated board seen by over 200,000 commuters daily.",
                cost = 55_000L,
                durationWeeks = 4,
                weeksRemaining = 4,
                demandBoostPercent = 35,
                reputationBoost = 15,
                iconEmoji = "🪧"
            ),
            AdCampaign(
                id = "billboard_market_gate",
                type = AdType.BILLBOARD,
                name = "Market Junction Lightbox",
                channelName = "Central Plaza Main Roundabout",
                tagline = "Eye-level double-sided banner right at the main entrance gate.",
                cost = 35_000L,
                durationWeeks = 3,
                weeksRemaining = 3,
                demandBoostPercent = 25,
                reputationBoost = 10,
                iconEmoji = "🏮"
            ),

            // Mobile Internet & Social
            AdCampaign(
                id = "mobile_viral_social",
                type = AdType.MOBILE_INTERNET,
                name = "TikTok & Instagram Viral Ads",
                channelName = "Social Media Video Boost",
                tagline = "Sponsored vertical video stories targeted within a 5km radius.",
                cost = 25_000L,
                durationWeeks = 2,
                weeksRemaining = 2,
                demandBoostPercent = 28,
                reputationBoost = 8,
                iconEmoji = "📱"
            ),
            AdCampaign(
                id = "mobile_local_search",
                type = AdType.MOBILE_INTERNET,
                name = "WhatsApp Blast & Google Maps Ads",
                channelName = "Direct Messenger & Search Priority",
                tagline = "Promote your phone number and location on Google and WhatsApp broadcasts.",
                cost = 14_000L,
                durationWeeks = 2,
                weeksRemaining = 2,
                demandBoostPercent = 18,
                reputationBoost = 5,
                iconEmoji = "💬"
            )
        )
    }

    fun getDefaultCommunityMembers(): List<CommunityMember> {
        return listOf(
            CommunityMember(
                id = "trader_alaba",
                name = "Chief Emeka",
                avatarEmoji = "👑",
                businessName = "Emeka Mega Electronics",
                location = "Alaba International",
                netWorth = 4_200_000L,
                reputation = 88,
                status = "Wholesale container cleared yesterday. DM for bulk deals!",
                isPartner = true
            ),
            CommunityMember(
                id = "trader_balogun",
                name = "Alhaja Ronke",
                avatarEmoji = "🧕🏾",
                businessName = "Ronke Fabric & Lace",
                location = "Balogun Market",
                netWorth = 3_100_000L,
                reputation = 82,
                status = "Ajo cycle 4 completed successfully. Trust is business.",
                isPartner = false
            ),
            CommunityMember(
                id = "trader_yaba",
                name = "Kelechi Tech",
                avatarEmoji = "🎧",
                businessName = "Yaba Tech Hub Repairs",
                location = "Yaba Left Tech",
                netWorth = 1_850_000L,
                reputation = 74,
                status = "Testing new TikTok video ad format. Foot traffic is doubling!",
                isPartner = false
            ),
            CommunityMember(
                id = "trader_ikeja",
                name = "Mama Nkechi",
                avatarEmoji = "👩🏾‍🍳",
                businessName = "Nkechi Kitchen & Catering",
                location = "Allen Avenue",
                netWorth = 2_450_000L,
                reputation = 79,
                status = "Booked 3 wedding catering orders for Saturday. Hard work pays.",
                isPartner = false
            )
        )
    }

    fun getDefaultCommunityProposals(): List<CommunityProposal> {
        return listOf(
            CommunityProposal(
                id = "solar_streetlights",
                title = "Plaza Solar Streetlights Project",
                description = "Pool trader union dues to install 4 bright solar floodlights across our trading lane to eliminate evening shop dark spots.",
                costPerMember = 15_000L,
                votesFor = 18,
                votesTotal = 24,
                hasVoted = false,
                benefitDescription = "+10% evening foot traffic & +5 reputation for all market shops."
            ),
            CommunityProposal(
                id = "security_patrol",
                title = "Joint Armed Night Security Patrol",
                description = "Contract 2 licensed security guards to patrol the perimeter between 10 PM and 6 AM daily.",
                costPerMember = 8_000L,
                votesFor = 21,
                votesTotal = 24,
                hasVoted = false,
                benefitDescription = "Eliminates night burglar attempts and cuts anxiety stress by 15%."
            )
        )
    }

    fun getDefaultCommunityPosts(): List<CommunityPost> {
        return listOf(
            CommunityPost(
                id = "p1",
                authorName = "Chief Emeka",
                authorAvatar = "👑",
                authorTitle = "Trader Union Chairman",
                content = "Warning to all plaza members: Ensure your business register receipt is displayed on your front counter. Local council task force is checking this week!",
                likesCount = 34,
                isLiked = false,
                tag = "NOTICE",
                timeAgo = "2h ago"
            ),
            CommunityPost(
                id = "p2",
                authorName = "Kelechi Tech",
                authorAvatar = "🎧",
                authorTitle = "Gadget Hustler",
                content = "Pro-tip: Don't ignore mobile internet ads. Spent ₦15k on targeted local reels and got 40+ new walk-ins looking for chargers and earpods.",
                likesCount = 28,
                isLiked = true,
                tag = "GROWTH HACK",
                timeAgo = "5h ago"
            ),
            CommunityPost(
                id = "p3",
                authorName = "Alhaja Ronke",
                authorAvatar = "🧕🏾",
                authorTitle = "Esusu Coordinator",
                content = "Next Ajo payout cycle starts next Monday. Remember: Your reputation is your true collateral in Lagos!",
                likesCount = 42,
                isLiked = false,
                tag = "AJO NEWS",
                timeAgo = "1d ago"
            )
        )
    }

    fun getDefaultUpgrades(): List<Upgrade> {
        return listOf(
            Upgrade(
                id = "gen_tiger",
                name = "'I Pass My Neighbor' Generator",
                description = "Keeps the lights on during NEPA power outages. Burns petrol weekly.",
                iconEmoji = "⚡",
                cost = 65_000L,
                weeklyMaintenance = 6_500L,
                demandBoostPercent = 15,
                blackoutProtectionPercent = 75,
                reputationBonus = 8
            ),
            Upgrade(
                id = "solar_inverter",
                name = "Solar Inverter Setup",
                description = "Clean, silent, 24/7 power without buying costly fuel every morning.",
                iconEmoji = "☀️",
                cost = 240_000L,
                weeklyMaintenance = 1_200L,
                demandBoostPercent = 25,
                blackoutProtectionPercent = 100,
                reputationBonus = 18
            ),
            Upgrade(
                id = "neon_sign",
                name = "Bright Street Signboard",
                description = "Eye-catching sign attracts foot traffic from across the main road.",
                iconEmoji = "🪧",
                cost = 35_000L,
                weeklyMaintenance = 500L,
                demandBoostPercent = 20,
                reputationBonus = 12
            ),
            Upgrade(
                id = "delivery_bike",
                name = "Delivery Dispatch Bike",
                description = "Enables home and office deliveries across the neighborhood.",
                iconEmoji = "🛵",
                cost = 180_000L,
                weeklyMaintenance = 5_000L,
                demandBoostPercent = 35,
                reputationBonus = 15
            ),
            Upgrade(
                id = "pos_terminal",
                name = "Wireless Smart POS",
                description = "Accept card and bank transfers easily so you never lose a customer with no cash.",
                iconEmoji = "🏧",
                cost = 45_000L,
                weeklyMaintenance = 1_500L,
                demandBoostPercent = 20,
                reputationBonus = 10
            ),
            Upgrade(
                id = "deep_freezer",
                name = "Solar Chest Freezer",
                description = "Stops perishable goods and food supplies from spoiling.",
                iconEmoji = "🧊",
                cost = 120_000L,
                weeklyMaintenance = 3_000L,
                spoilageReductionPercent = 90,
                reputationBonus = 10
            )
        )
    }

    fun getDefaultInvestments(): List<Investment> {
        return listOf(
            Investment(
                id = "tbills",
                name = "Treasury Bills & Safe Lock",
                type = RiskLevel.LOW,
                description = "Backed by government & commercial banks. Modest steady returns, zero principal risk.",
                investedAmount = 0L,
                totalValue = 0L,
                lastYieldPercent = 0.35f
            ),
            Investment(
                id = "agri_etf",
                name = "Agri-Tech & NGX Index",
                type = RiskLevel.MEDIUM,
                description = "Invest in crop farming syndicates and top 30 Nigerian public companies. Balanced growth.",
                investedAmount = 0L,
                totalValue = 0L,
                lastYieldPercent = 1.2f
            ),
            Investment(
                id = "crypto_tech",
                name = "Crypto & Forex Speculation",
                type = RiskLevel.HIGH,
                description = "High volatility digital assets. Can double your money quickly or crash 30% in a week!",
                investedAmount = 0L,
                totalValue = 0L,
                lastYieldPercent = -3.5f
            )
        )
    }

    fun getDefaultAjoSavingGoals(): List<AjoSavingGoal> {
        return listOf(
            AjoSavingGoal(
                id = "goal_shop_expansion",
                title = "Market Stall Lease & Expansion",
                description = "Secure a premium, high-footfall double stall in Tejuosho or Balogun Market.",
                category = "Expansion",
                iconEmoji = "🏪",
                targetAmount = 150_000L,
                currentAmount = 25_000L,
                recommendedWeekly = 15_000L,
                perkReward = "+25% Weekly Customer Demand & Prestige",
                createdWeek = 1,
                deadlineWeek = 10
            ),
            AjoSavingGoal(
                id = "goal_solar_inverter",
                title = "3.5kVA Solar Inverter & Battery",
                description = "Break free from expensive petrol generator bills and national grid blackouts.",
                category = "Energy",
                iconEmoji = "⚡",
                targetAmount = 220_000L,
                currentAmount = 30_000L,
                recommendedWeekly = 20_000L,
                perkReward = "Zero generator fuel expense & 100% uptime during blackouts",
                createdWeek = 1,
                deadlineWeek = 16
            ),
            AjoSavingGoal(
                id = "goal_commercial_freezer",
                title = "Solar Cold-Chain Deep Freezer",
                description = "Preserve chilled drinks and perishable food inventory with zero spoilage risk.",
                category = "Equipment",
                iconEmoji = "❄️",
                targetAmount = 90_000L,
                currentAmount = 15_000L,
                recommendedWeekly = 10_000L,
                perkReward = "Cuts stock spoilage risk to 0% and saves stock loss",
                createdWeek = 1,
                deadlineWeek = 8
            ),
            AjoSavingGoal(
                id = "goal_bulk_import_restock",
                title = "Alaba Direct Import Container Restock",
                description = "Order directly from shipping containers to secure maximum wholesale discounts.",
                category = "Inventory",
                iconEmoji = "📦",
                targetAmount = 120_000L,
                currentAmount = 0L,
                recommendedWeekly = 15_000L,
                perkReward = "Unlocks tier-3 wholesale volume discount (-25% unit cost)",
                createdWeek = 1,
                deadlineWeek = 14
            ),
            AjoSavingGoal(
                id = "goal_delivery_dispatch_bike",
                title = "Express Delivery Dispatch Motorbike",
                description = "Beat Lagos traffic jams and deliver goods across the city within 2 hours.",
                category = "Logistics",
                iconEmoji = "🏍️",
                targetAmount = 80_000L,
                currentAmount = 0L,
                recommendedWeekly = 10_000L,
                perkReward = "+15% Rapid delivery surcharge revenue & +10 Reputation",
                createdWeek = 1,
                deadlineWeek = 12
            ),
            AjoSavingGoal(
                id = "goal_emergency_rainy_day",
                title = "SME Rainy-Day Shield Fund",
                description = "Protect your household and business from sudden medical or council enforcement shocks.",
                category = "Safety",
                iconEmoji = "🛡️",
                targetAmount = 50_000L,
                currentAmount = 10_000L,
                recommendedWeekly = 5_000L,
                perkReward = "-15 Stress buffer and immunity against unforeseen emergency drains",
                createdWeek = 1,
                deadlineWeek = 6
            )
        )
    }
}
