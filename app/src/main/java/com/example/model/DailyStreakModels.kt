package com.example.model

/**
 * Daily Streak reward specification for a specific day in the 7-day reward cycle.
 */
data class DailyStreakDayReward(
    val dayNumber: Int,
    val bonusAmount: Long,
    val reputationBonus: Int = 0,
    val title: String,
    val perkDescription: String,
    val iconEmoji: String
)

/**
 * Active player daily streak state for tracking real-world daily engagement,
 * currency bonuses, and ad-boosted rewards.
 */
data class DailyStreakInfo(
    val currentStreak: Int = 1,
    val lastClaimEpochDay: Long = 0L,
    val canClaimToday: Boolean = true,
    val isStreakBroken: Boolean = false,
    val todayReward: DailyStreakDayReward = DailyStreakConfig.getRewardForDay(1),
    val weeklyCycle: List<DailyStreakDayReward> = DailyStreakConfig.REWARDS,
    val totalClaimedCount: Int = 0,
    val totalCashEarnedFromStreaks: Long = 0L
) {
    /**
     * Day in 7-day cyclical rewards progression (1 to 7).
     */
    val currentDayInCycle: Int
        get() = ((currentStreak - 1) % 7) + 1

    /**
     * Next day in the reward cycle (1 to 7).
     */
    val nextDayInCycle: Int
        get() = (currentStreak % 7) + 1

    /**
     * Next scheduled reward.
     */
    val nextDayReward: DailyStreakDayReward
        get() = DailyStreakConfig.getRewardForDay(currentStreak + 1)

    /**
     * Whether today is the 7th day Mogul Jackpot day.
     */
    val isJackpotDay: Boolean
        get() = currentDayInCycle == 7

    /**
     * Formatted string of today's bonus.
     */
    val formattedTodayBonus: String
        get() = CurrencyFormatter.formatNaira(todayReward.bonusAmount)
}

object DailyStreakConfig {
    val REWARDS = listOf(
        DailyStreakDayReward(
            dayNumber = 1,
            bonusAmount = 10_000L,
            reputationBonus = 1,
            title = "Starter Hustler Spark",
            perkDescription = "Morning working capital for inventory",
            iconEmoji = "⚡"
        ),
        DailyStreakDayReward(
            dayNumber = 2,
            bonusAmount = 25_000L,
            reputationBonus = 2,
            title = "Consistent Trader",
            perkDescription = "Stall foot-traffic boost from repeat visits",
            iconEmoji = "🛒"
        ),
        DailyStreakDayReward(
            dayNumber = 3,
            bonusAmount = 50_000L,
            reputationBonus = 3,
            title = "Balogun Market Regular",
            perkDescription = "Wholesale distributor credit line opens",
            iconEmoji = "🏪"
        ),
        DailyStreakDayReward(
            dayNumber = 4,
            bonusAmount = 80_000L,
            reputationBonus = 5,
            title = "Wholesale Cargo Mover",
            perkDescription = "Container shipment priority allocation",
            iconEmoji = "📦"
        ),
        DailyStreakDayReward(
            dayNumber = 5,
            bonusAmount = 120_000L,
            reputationBonus = 6,
            title = "Commercial Dynamo",
            perkDescription = "Supply-chain bulk rebate & prestige",
            iconEmoji = "🚀"
        ),
        DailyStreakDayReward(
            dayNumber = 6,
            bonusAmount = 180_000L,
            reputationBonus = 8,
            title = "Lagos Empire Builder",
            perkDescription = "VIP supplier terms & low interest rate",
            iconEmoji = "🏢"
        ),
        DailyStreakDayReward(
            dayNumber = 7,
            bonusAmount = 300_000L,
            reputationBonus = 15,
            title = "Lagos Mogul Jackpot",
            perkDescription = "Grand weekly jackpot, gold badge & mogul status",
            iconEmoji = "👑"
        )
    )

    fun getRewardForDay(streakDay: Int): DailyStreakDayReward {
        val normalized = if (streakDay <= 0) 1 else streakDay
        val index = (normalized - 1) % REWARDS.size
        return REWARDS[index]
    }
}
