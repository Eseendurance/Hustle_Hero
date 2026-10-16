package com.example.model

import java.util.UUID

/**
 * Player model representing the player's core identity, financial standing,
 * entrepreneurial status, and community savings participation in the simulation.
 *
 * @property id Unique identifier for the player session.
 * @property name Player's character or trading name.
 * @property avatarEmoji Visual icon/avatar depicting the player.
 * @property currentCurrency Available spendable funds in Naira (combined liquid capital).
 * @property businessLevel Current business tier or enterprise scale (Level 1: Street Trader to Level 5: Mogul).
 * @property reputationScore Street goodwill and merchant credibility score (0 - 100).
 * @property ajoSavingsAccountStatus Status and metrics of the player's rotating Ajo (Esusu) savings circle.
 */
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Tunde",
    val avatarEmoji: String = "👨🏾‍💼",
    val currentCurrency: Long = 50_000L,
    val businessLevel: Int = 1,
    val reputationScore: Int = 50,
    val ajoSavingsAccountStatus: AjoSavingsAccountStatus = AjoSavingsAccountStatus(),
    val ajoGoals: List<AjoSavingGoal> = emptyList(),
    val dailyStreak: DailyStreakInfo = DailyStreakInfo()
) {
    /**
     * Human-readable title based on the player's entrepreneurial level.
     */
    val businessLevelTitle: String
        get() = when (businessLevel) {
            1 -> "🌱 Level 1: Street Hawker & Retailer"
            2 -> "🛒 Level 2: Kiosk & Market Stall Holder"
            3 -> "🏪 Level 3: Registered Store Owner"
            4 -> "🏢 Level 4: Alaba Wholesale Distributor"
            else -> "👑 Level 5+: Lagos Commercial Tycoon"
        }

    /**
     * Checks if the player has positive working capital.
     */
    val isSolvent: Boolean
        get() = currentCurrency > 0L

    /**
     * Formatted currency with Nigerian Naira symbol.
     */
    val formattedCurrency: String
        get() = CurrencyFormatter.formatNaira(currentCurrency)

    /**
     * Returns a copy with updated currency balance.
     */
    fun withCurrencyDelta(delta: Long): Player {
        return copy(currentCurrency = kotlin.math.max(0L, currentCurrency + delta))
    }

    /**
     * Returns a copy with adjusted reputation (clamped between 0 and 100).
     */
    fun withReputationDelta(delta: Int): Player {
        return copy(reputationScore = (reputationScore + delta).coerceIn(0, 100))
    }

    /**
     * Returns a copy with advanced business level.
     */
    fun levelUp(): Player {
        return copy(businessLevel = businessLevel + 1)
    }
}

/**
 * Status and accounting details for the player's Ajo (Esusu) rotating cooperative savings group.
 *
 * @property groupId Unique ID of the active Ajo group circle.
 * @property groupName Traditional name of the cooperative savings circle.
 * @property weeklyContribution Mandatory weekly contribution quota in Naira.
 * @property totalSaved Cumulative savings deposited into the circle so far.
 * @property currentRound Current turn in the 5-week rotation.
 * @property totalRounds Total duration of the rotation (typically 5 weeks for 5 members).
 * @property payoutWeek The designated week when this player collects the lump-sum jackpot pot.
 * @property hasReceivedPayout Whether the player has already claimed their lump-sum payout in this cycle.
 * @property isActive Whether the savings account is currently active and in good standing.
 * @property statusMessage Contextual message regarding the account status.
 */
data class AjoSavingsAccountStatus(
    val groupId: String = UUID.randomUUID().toString(),
    val groupName: String = "Balogun Unity Ajo Cooperative",
    val weeklyContribution: Long = 10_000L,
    val totalSaved: Long = 0L,
    val currentRound: Int = 1,
    val totalRounds: Int = 5,
    val payoutWeek: Int = 3,
    val hasReceivedPayout: Boolean = false,
    val isActive: Boolean = true,
    val statusMessage: String = "Active Saver in Good Standing"
) {
    /**
     * Total lump-sum pool disbursed to the designated member on their payout week.
     */
    val totalLumpSumPayout: Long
        get() = weeklyContribution * totalRounds

    /**
     * Checks if this week is the player's turn to collect the rotational payout pot.
     */
    fun isPayoutDue(weekNumber: Int): Boolean {
        return isActive && !hasReceivedPayout && (weekNumber % totalRounds == payoutWeek % totalRounds || weekNumber == payoutWeek)
    }

    /**
     * Formatted weekly contribution quota string.
     */
    val formattedWeeklyContribution: String
        get() = CurrencyFormatter.formatNaira(weeklyContribution)

    /**
     * Formatted total saved string.
     */
    val formattedTotalSaved: String
        get() = CurrencyFormatter.formatNaira(totalSaved)
}

/**
 * Convenience extension to bridge the full GameState into a compact Player model.
 */
fun GameState.toPlayerModel(): Player {
    val totalAjoSaved = this.ajoContributionHistory.sumOf { it.amount }
    return Player(
        name = this.characterName,
        avatarEmoji = this.character.avatarEmoji,
        currentCurrency = this.personalCash + this.businessCash,
        businessLevel = when {
            this.netWorth >= 3_000_000L -> 5
            this.netWorth >= 1_500_000L -> 4
            this.netWorth >= 600_000L -> 3
            this.netWorth >= 200_000L -> 2
            else -> 1
        },
        reputationScore = this.reputation,
        ajoSavingsAccountStatus = AjoSavingsAccountStatus(
            groupName = "Balogun Unity Ajo Cooperative",
            weeklyContribution = this.ajoGroup.weeklyContribution,
            totalSaved = totalAjoSaved,
            currentRound = this.ajoGroup.currentCycleWeek,
            totalRounds = this.ajoGroup.cycleWeeks,
            payoutWeek = this.ajoGroup.payoutWeek,
            hasReceivedPayout = this.week > this.ajoGroup.payoutWeek,
            isActive = this.ajoGroup.isEnrolled,
            statusMessage = if (this.ajoGroup.isEnrolled) {
                if (this.week >= this.ajoGroup.payoutWeek) "Jackpot collected! Servicing remaining rounds."
                else "Pledging ₦${CurrencyFormatter.formatNaira(this.ajoGroup.weeklyContribution)}/wk towards Turn ${this.ajoGroup.payoutWeek} pot."
            } else {
                "Not currently enrolled in an active Ajo circle."
            }
        ),
        ajoGoals = this.ajoGoals,
        dailyStreak = this.dailyStreak
    )
}
