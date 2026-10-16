package com.example.data

data class MarketLocation(
    val id: String,
    val name: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val specialty: String,
    val footTrafficScore: Int,
    val avgDailySpend: Long,
    val description: String,
    val iconEmoji: String
)

object MarketLocationsData {
    val locations = listOf(
        MarketLocation(
            id = "comp_village",
            name = "Computer Village (Otigba)",
            city = "Ikeja, Lagos",
            latitude = 6.5986,
            longitude = 3.3421,
            specialty = "Phones, Electronics & Accessories",
            footTrafficScore = 96,
            avgDailySpend = 280_000L,
            description = "Africa's largest ICT hardware market. Massive high-margin tech trade and youthful foot traffic.",
            iconEmoji = "📱"
        ),
        MarketLocation(
            id = "alaba_intl",
            name = "Alaba International Market",
            city = "Ojo, Lagos",
            latitude = 6.4608,
            longitude = 3.1952,
            specialty = "Wholesale Electronics & Appliances",
            footTrafficScore = 92,
            avgDailySpend = 450_000L,
            description = "The direct hub of shipping containers and importers. Unmatched wholesale clearance discounts.",
            iconEmoji = "📦"
        ),
        MarketLocation(
            id = "balogun_mkt",
            name = "Balogun & Idumota Market",
            city = "Lagos Island",
            latitude = 6.4549,
            longitude = 3.3884,
            specialty = "Provisions, Fabrics & Cosmetics",
            footTrafficScore = 98,
            avgDailySpend = 320_000L,
            description = "Bustling multi-story merchant stalls stretching across the island. Endless cash flow and daily crowds.",
            iconEmoji = "🛒"
        ),
        MarketLocation(
            id = "tejuosho_yaba",
            name = "Tejuosho Ultra-Modern Market",
            city = "Yaba, Lagos",
            latitude = 6.5142,
            longitude = 3.3719,
            specialty = "Fashion, Salon Services & Food",
            footTrafficScore = 88,
            avgDailySpend = 190_000L,
            description = "Organized multi-tier complex next to Yaba bus terminal. Popular with students and professionals.",
            iconEmoji = "💈"
        ),
        MarketLocation(
            id = "wuse_abuja",
            name = "Wuse Main Market",
            city = "Wuse 2, Abuja",
            latitude = 9.0628,
            longitude = 7.4684,
            specialty = "Food Stalls, Provisions & POS",
            footTrafficScore = 86,
            avgDailySpend = 350_000L,
            description = "Prime trading center of the federal capital. Higher consumer purchasing power per customer.",
            iconEmoji = "🍲"
        ),
        MarketLocation(
            id = "bodija_ibadan",
            name = "Bodija International Market",
            city = "Ibadan, Oyo",
            latitude = 7.4326,
            longitude = 3.9056,
            specialty = "Staple Agro Food Distribution",
            footTrafficScore = 90,
            avgDailySpend = 180_000L,
            description = "The food basket gateway where grains, plantains, and provisions arrive in giant trucks daily.",
            iconEmoji = "🌾"
        )
    )
}
