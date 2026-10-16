package com.example.model

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    fun formatNaira(amount: Long): String {
        return "₦${numberFormat.format(amount)}"
    }

    fun formatSignedNaira(amount: Long): String {
        val sign = if (amount > 0) "+" else if (amount < 0) "-" else ""
        val absVal = kotlin.math.abs(amount)
        return "$sign₦${numberFormat.format(absVal)}"
    }

    fun formatCompact(amount: Long): String {
        val sign = if (amount < 0) "-" else ""
        val abs = kotlin.math.abs(amount)
        return when {
            abs >= 1_000_000 -> "$sign₦%.2fM".format(abs / 1_000_000.0)
            abs >= 1_000 -> "$sign₦%.1fK".format(abs / 1_000.0)
            else -> "$sign₦$abs"
        }
    }
}

enum class BusinessType {
    PROVISIONS,
    PHONE_ACCESSORIES,
    FOOD_VENDOR,
    BARBERSHOP,
    POS_AGENT
}

enum class LifestyleLevel(val label: String, val multiplier: Float, val stressRelief: Int) {
    FRUGAL("Frugal / Survival", 0.7f, 0),
    BALANCED("Balanced / Decent", 1.0f, -3),
    COMFORTABLE("Comfortable / Soft Life", 1.5f, -8)
}

enum class RiskLevel(val label: String, val badgeColorHex: Long) {
    LOW("Low Risk", 0xFF15803D),
    MEDIUM("Medium Risk", 0xFFD97706),
    HIGH("High Risk", 0xFFDC2626)
}

enum class AdType(val title: String, val iconEmoji: String) {
    TV("Television Broadcast", "📺"),
    BILLBOARD("Highway Billboard", "🪧"),
    MOBILE_INTERNET("Mobile Internet & Social", "📱")
}

enum class GamePhase {
    DASHBOARD,
    BUSINESS,
    PERSONAL,
    MARKETING_ADS,
    COMMUNITY,
    MARKET_3D,
    EVENT,
    SUMMARY,
    GAME_OVER,
    DAILY_HUSTLE,
    LIFE_ROUTINE,
    GOURMET_MEALS
}

data class CharacterProfile(
    val id: String,
    val name: String,
    val title: String,
    val avatarEmoji: String,
    val backstory: String,
    val perkName: String,
    val perkDescription: String,
    val demandBoostPercent: Int = 0,
    val costDiscountPercent: Int = 0,
    val startingReputation: Int = 50,
    val startingCapitalBonus: Long = 0L
)

data class AdCampaign(
    val id: String,
    val type: AdType,
    val name: String,
    val channelName: String,
    val tagline: String,
    val cost: Long,
    val durationWeeks: Int,
    val weeksRemaining: Int,
    val demandBoostPercent: Int,
    val reputationBoost: Int,
    val iconEmoji: String,
    val isActive: Boolean = false
)

data class CommunityMember(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val businessName: String,
    val location: String,
    val netWorth: Long,
    val reputation: Int,
    val status: String,
    val isPartner: Boolean = false
)

data class CommunityProposal(
    val id: String,
    val title: String,
    val description: String,
    val costPerMember: Long,
    val votesFor: Int,
    val votesTotal: Int,
    val hasVoted: Boolean = false,
    val benefitDescription: String
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val authorTitle: String,
    val content: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val tag: String,
    val timeAgo: String
)

data class Business(
    val id: String,
    val name: String,
    val type: BusinessType,
    val iconEmoji: String,
    val tagline: String,
    val description: String,
    val itemUnitName: String,
    val baseUnitCost: Long,
    val baseSellingPrice: Long,
    val defaultStock: Int,
    val startingCapital: Long,
    val spoilageRate: Float = 0.0f,
    val baseWeeklyDemand: Int = 50
)

data class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val cost: Long,
    val weeklyMaintenance: Long,
    val demandBoostPercent: Int = 0,
    val blackoutProtectionPercent: Int = 0,
    val spoilageReductionPercent: Int = 0,
    val reputationBonus: Int = 0,
    val isPurchased: Boolean = false
)

data class PersonalExpenses(
    val baseRentWeekly: Long = 15_000L,
    val baseFoodWeekly: Long = 18_000L,
    val baseTransportWeekly: Long = 10_000L,
    val familySupportWeekly: Long = 12_000L,
    val lifestyle: LifestyleLevel = LifestyleLevel.BALANCED
) {
    fun calculateTotal(inflationRate: Float): Long {
        val base = (baseRentWeekly + baseFoodWeekly + baseTransportWeekly + familySupportWeekly)
        val adjustedForInflation = (base * (1f + inflationRate)).toLong()
        return (adjustedForInflation * lifestyle.multiplier).toLong()
    }
}

data class AjoGroup(
    val isEnrolled: Boolean = false,
    val weeklyContribution: Long = 15_000L,
    val cycleWeeks: Int = 5,
    val currentCycleWeek: Int = 1,
    val payoutWeek: Int = 4,
    val totalMembers: Int = 5
) {
    val totalPot: Long get() = weeklyContribution * totalMembers
}

enum class AjoGoalDeadlineStatus(val label: String, val badgeEmoji: String) {
    ON_TRACK("On Track", "⏳"),
    URGENT("Deadline Approaching", "⚠️"),
    OVERDUE("Target Deadline Missed", "🚨"),
    COMPLETED("Target Completed", "✅")
}

data class AjoSavingGoal(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconEmoji: String,
    val targetAmount: Long,
    val currentAmount: Long = 0L,
    val recommendedWeekly: Long = 10_000L,
    val isCompleted: Boolean = false,
    val perkReward: String = "",
    val completedWeek: Int? = null,
    val createdWeek: Int = 1,
    val deadlineWeek: Int = 12
) {
    val progressPercent: Float
        get() = if (targetAmount <= 0L) 1.0f else (currentAmount.toFloat() / targetAmount.toFloat()).coerceIn(0f, 1f)

    val remainingAmount: Long
        get() = maxOf(0L, targetAmount - currentAmount)

    fun weeksRemaining(currentWeek: Int): Int = maxOf(0, deadlineWeek - currentWeek)

    fun isOverdue(currentWeek: Int): Boolean = !isCompleted && currentWeek > deadlineWeek

    fun isDueSoon(currentWeek: Int): Boolean = !isCompleted && !isOverdue(currentWeek) && (deadlineWeek - currentWeek) <= 2

    fun requiredWeeklyToMeetDeadline(currentWeek: Int): Long {
        if (isCompleted) return 0L
        val remWeeks = weeksRemaining(currentWeek)
        return if (remWeeks <= 0) remainingAmount else (remainingAmount + remWeeks - 1) / remWeeks
    }

    fun getDeadlineStatus(currentWeek: Int): AjoGoalDeadlineStatus {
        return when {
            isCompleted -> AjoGoalDeadlineStatus.COMPLETED
            currentWeek > deadlineWeek -> AjoGoalDeadlineStatus.OVERDUE
            deadlineWeek - currentWeek <= 2 -> AjoGoalDeadlineStatus.URGENT
            else -> AjoGoalDeadlineStatus.ON_TRACK
        }
    }
}

data class AjoContributionRecord(
    val id: String,
    val goalId: String,
    val goalTitle: String,
    val amount: Long,
    val sourceWallet: WalletSource,
    val week: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val balanceAfter: Long,
    val note: String = ""
)

data class AjoContributionValidation(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val maxAffordable: Long = 0L
)

data class AjoContributionResult(
    val success: Boolean,
    val message: String,
    val goal: AjoSavingGoal?,
    val amountContributed: Long = 0L,
    val remainingBalance: Long = 0L,
    val isGoalCompletedNow: Boolean = false,
    val perkUnlocked: String? = null
)

data class Investment(
    val id: String,
    val name: String,
    val type: RiskLevel,
    val description: String,
    val investedAmount: Long = 0L,
    val totalValue: Long = 0L,
    val lastYieldPercent: Float = 0f
)

data class Loan(
    val id: String,
    val sourceName: String,
    val principal: Long,
    val balance: Long,
    val weeklyInterestRate: Float,
    val minWeeklyPayment: Long
)

data class EventChoice(
    val id: String,
    val text: String,
    val businessCashImpact: Long = 0L,
    val personalCashImpact: Long = 0L,
    val savingsImpact: Long = 0L,
    val inventoryImpact: Int = 0,
    val stressImpact: Int = 0,
    val reputationImpact: Int = 0,
    val debtImpact: Long = 0L,
    val outcomeMessage: String,
    val moneyLesson: String
)

data class GameEvent(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconEmoji: String,
    val choices: List<EventChoice>
)

data class WeeklyTurnReport(
    val weekNumber: Int,
    val unitsSold: Int,
    val salesRevenue: Long,
    val cogs: Long,
    val operatingExpenses: Long,
    val businessNetProfit: Long,
    val salaryWithdrawn: Long,
    val personalLivingCost: Long,
    val ajoContributionPaid: Long,
    val ajoPayoutReceived: Long,
    val investmentGainLoss: Long,
    val loanInterestPaid: Long,
    val loansRepaid: Long,
    val endingNetWorth: Long,
    val netWorthChange: Long,
    val endingBusinessCash: Long,
    val endingPersonalCash: Long,
    val endingSavings: Long,
    val endingDebt: Long,
    val endingStress: Int,
    val endingReputation: Int,
    val eventTitle: String?,
    val eventOutcome: String?,
    val eventLesson: String?
)

data class GameState(
    val characterName: String = "Tunde",
    val character: CharacterProfile,
    val cityName: String = "Lagos",
    val week: Int = 1,
    val maxWeeks: Int = 52,
    val netWorthGoal: Long = 5_000_000L,
    
    // Core Business
    val business: Business,
    val businessCash: Long,
    val inventoryUnits: Int,
    val sellingPrice: Long,
    val costPerUnit: Long,
    val staffCount: Int = 0,
    val staffWageWeekly: Long = 10_000L,
    val marketingBudgetWeekly: Long = 0L,
    val upgrades: List<Upgrade>,
    
    // Marketing Paid Ads Spot (TV, Billboard, Mobile Internet)
    val activeAds: List<AdCampaign> = emptyList(),
    
    // Personal Finance
    val personalCash: Long,
    val personalSavings: Long = 0L,
    val personalExpenses: PersonalExpenses = PersonalExpenses(),
    val ajoGroup: AjoGroup = AjoGroup(),
    val ajoGoals: List<AjoSavingGoal> = emptyList(),
    val selectedAjoGoalId: String? = null,
    val ajoContributionHistory: List<AjoContributionRecord> = emptyList(),
    val investments: List<Investment> = emptyList(),
    val loans: List<Loan> = emptyList(),
    
    // Community
    val communityMembers: List<CommunityMember> = emptyList(),
    val communityProposals: List<CommunityProposal> = emptyList(),
    val communityPosts: List<CommunityPost> = emptyList(),
    
    // Core Vitals
    val stress: Int = 20,
    val reputation: Int = 50,
    val creditScore: Int = 620,
    val inflationRate: Float = 0.08f,
    
    // Phases & Flow
    val currentPhase: GamePhase = GamePhase.DASHBOARD,
    val currentEvent: GameEvent? = null,
    val selectedChoice: EventChoice? = null,
    val turnHistory: List<WeeklyTurnReport> = emptyList(),
    val latestReport: WeeklyTurnReport? = null,
    
    // End states
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val gameOverTitle: String? = null,
    val gameOverMessage: String? = null,
    
    // Dual-Wallet Transfers Audit Trail
    val walletTransfers: List<WalletTransfer> = emptyList(),
    
    // Theme & UI Toggle
    val isDarkMode: Boolean = false,
    val solarModeEnabled: Boolean = false,
    val tutorialDismissedTurn: Int = 0,

    // Everyday Life & Daily Hustle System
    val dailyHustles: List<DailyHustleTask> = emptyList(),
    val dailyRoutine: DailyRoutineState = DailyRoutineState(),
    val careerTrack: CareerTrack = CareerTrack.BUSINESS_OWNER,
    val currentJob: JobRole? = null,
    val familyState: FamilyState = FamilyState(),
    val mealHistory: List<MealConsumptionRecord> = emptyList(),
    val activeParty: PartyEvent? = null,
    val totalHustleCashEarned: Long = 0L,
    val currentStreakDays: Int = 1,
    val shopLedger: List<ShopLedgerEntry> = defaultShopLedgerEntries,
    val customerDebtors: List<CustomerDebtor> = defaultCustomerDebtors,
    val radioStation: RadioStationState = RadioStationState(),
    val generatorState: GeneratorGridState = GeneratorGridState(),
    val dailyStreak: DailyStreakInfo = DailyStreakInfo(currentStreak = currentStreakDays)
) {
    val totalDebt: Long
        get() = loans.sumOf { it.balance }

    val totalInvestmentsValue: Long
        get() = investments.sumOf { it.totalValue }

    val inventoryValue: Long
        get() = inventoryUnits.toLong() * costPerUnit

    val netWorth: Long
        get() = (businessCash + personalCash + personalSavings + totalInvestmentsValue + inventoryValue) - totalDebt

    val selectedAjoGoal: AjoSavingGoal?
        get() = ajoGoals.find { it.id == selectedAjoGoalId } ?: ajoGoals.firstOrNull()

    val totalAjoGoalsSaved: Long
        get() = ajoGoals.sumOf { it.currentAmount }

    val completedAjoGoalsCount: Int
        get() = ajoGoals.count { it.isCompleted }

    val pendingAjoGoals: List<AjoSavingGoal>
        get() = ajoGoals.filter { !it.isCompleted }

    val urgentAjoGoals: List<AjoSavingGoal>
        get() = ajoGoals.filter { it.isDueSoon(week) }

    val overdueAjoGoals: List<AjoSavingGoal>
        get() = ajoGoals.filter { it.isOverdue(week) }

    val urgentAjoGoalsCount: Int
        get() = urgentAjoGoals.size

    val overdueAjoGoalsCount: Int
        get() = overdueAjoGoals.size

    val player: Player
        get() = toPlayerModel()
}

// --- DUAL-WALLET TRANSFER MODELS ---

enum class WalletTransferType(val title: String, val description: String, val iconEmoji: String) {
    SALARY_DRAW("Salary Withdrawal", "Regular owner wage drawn from business into personal wallet", "💼"),
    OWNER_DIVIDEND("Owner's Dividend", "Surplus business profit payout to personal wallet", "💰"),
    CAPITAL_INJECTION("Capital Injection", "Personal funds transferred to business working capital", "🚀"),
    EMERGENCY_BAILOUT("Emergency Bailout", "Emergency savings transferred to rescue business", "🛡️")
}

enum class WalletSource {
    BUSINESS_CASH,
    PERSONAL_CASH,
    PERSONAL_SAVINGS
}

enum class WalletDestination {
    BUSINESS_CASH,
    PERSONAL_CASH,
    PERSONAL_SAVINGS
}

data class WalletTransfer(
    val id: String,
    val week: Int,
    val timestamp: Long,
    val type: WalletTransferType,
    val amount: Long,
    val fee: Long = 0L,
    val netAmount: Long = amount,
    val source: WalletSource,
    val destination: WalletDestination,
    val reason: String,
    val businessCashAfter: Long,
    val personalCashAfter: Long
)

data class WalletTransferRequest(
    val type: WalletTransferType,
    val amount: Long,
    val source: WalletSource = WalletSource.BUSINESS_CASH,
    val destination: WalletDestination = WalletDestination.PERSONAL_CASH,
    val reason: String = ""
)

data class TransferValidation(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val maxTransferable: Long = 0L
)

data class WalletTransferResult(
    val success: Boolean,
    val updatedState: GameState,
    val transferRecord: WalletTransfer?,
    val message: String
)

// --- INVENTORY MANAGEMENT & WHOLESALE MODELS ---

data class WholesaleTier(
    val minUnits: Int,
    val discountPercent: Float,
    val tierName: String
)

data class RestockQuote(
    val units: Int,
    val baseUnitCost: Long,
    val discountedUnitCost: Long,
    val discountPercent: Float,
    val tierName: String,
    val totalCost: Long,
    val currentStock: Int,
    val resultingStock: Int,
    val maxStorageCapacity: Int,
    val fitsInStorage: Boolean,
    val isAffordable: Boolean,
    val maxAffordableUnits: Int
)

data class RestockResult(
    val success: Boolean,
    val updatedState: GameState,
    val unitsAdded: Int,
    val totalCost: Long,
    val unitCostPaid: Long,
    val message: String
)

data class DemandEstimate(
    val minDemand: Int,
    val expectedDemand: Int,
    val maxDemand: Int,
    val priceElasticityNote: String,
    val activeAdBoostPercent: Int,
    val reputationEffectPercent: Int
)

data class RestockRecommendation(
    val currentStock: Int,
    val projectedDemand: Int,
    val recommendedRestockUnits: Int,
    val safetyStockBuffer: Int,
    val estimatedCost: Long,
    val rationale: String
)

data class SpoilageQuote(
    val perishableUnitsAtRisk: Int,
    val estimatedSpoilageUnits: Int,
    val potentialFinancialLoss: Long,
    val hasRefrigeration: Boolean,
    val riskReductionTip: String
)

data class ClearanceResult(
    val success: Boolean,
    val updatedState: GameState,
    val unitsLiquidated: Int,
    val cashRecovered: Long,
    val message: String
)

data class TurnSimulationResult(
    val state: GameState,
    val report: WeeklyTurnReport,
    val autoTransfersExecuted: List<WalletTransfer>
)

// --- DAILY HUSTLE & EVERYDAY LIFE MODELS ---

enum class MissionType(val label: String, val iconEmoji: String) {
    SELL_ITEM("Shop Sales", "🛒"),
    RESTOCK_WHOLESALE("Restock Wholesale", "📦"),
    AJO_CONTRIBUTION("Ajo Savings Goal", "🎯"),
    COOK_MEAL("Cook Homemade Meal", "🍲"),
    MORNING_HYGIENE("Hygiene & Grooming", "🚿"),
    TAKE_KIDS_SCHOOL("School Run", "🎒"),
    WORK_SHIFT("Work Shift", "💼"),
    CLIENT_GIG("Street Hustle Gig", "⚡")
}

data class DailyHustleTask(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconEmoji: String,
    val rewardCash: Long,
    val reputationReward: Int = 3,
    val stressCost: Int = 2,
    val energyCost: Int = 15,
    val difficulty: String = "Medium",
    val clientName: String = "Street Client",
    val location: String = "Lagos Island",
    val currentProgress: Int = 0,
    val targetQuantity: Int = 1,
    val unitLabel: String = "tasks",
    val missionType: MissionType = MissionType.CLIENT_GIG,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null
) {
    val isObjectiveMet: Boolean
        get() = currentProgress >= targetQuantity

    val progressFraction: Float
        get() = if (targetQuantity <= 0) 1f else (currentProgress.toFloat() / targetQuantity.toFloat()).coerceIn(0f, 1f)
}

enum class TimeOfDay(val displayName: String, val iconEmoji: String, val hourLabel: String) {
    MORNING("Morning", "🌅", "07:00 AM"),
    AFTERNOON("Afternoon", "☀️", "01:00 PM"),
    EVENING("Evening", "🌆", "07:00 PM"),
    NIGHT("Night", "🌙", "11:00 PM")
}

data class DailyRoutineState(
    val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    val currentDay: Int = 1,
    val hasWokenUp: Boolean = true,
    val hasTakenBath: Boolean = false,
    val hasBrushedTeeth: Boolean = false,
    val hasHadBreakfast: Boolean = false,
    val hasWorkedOrRunBusiness: Boolean = false,
    val hasTakenKidsToSchool: Boolean = false,
    val hasSleptNight: Boolean = false,
    val energyLevel: Int = 85,
    val hygieneLevel: Int = 90,
    val hungerLevel: Int = 30,
    val lastActionMessage: String = "Rise and shine! A new hustle day awaits in the city."
)

enum class CareerTrack(val label: String, val iconEmoji: String) {
    BUSINESS_OWNER("Own Business / Entrepreneur", "🏪"),
    EMPLOYED_JOB("Corporate / Salary Job", "💼")
}

data class JobRole(
    val id: String,
    val title: String,
    val company: String,
    val industry: String,
    val weeklySalary: Long,
    val iconEmoji: String,
    val workHours: String,
    val description: String,
    val prestige: Int = 60
)

enum class RelationshipStatus(val label: String, val emoji: String) {
    SINGLE("Single & Searching", "👤"),
    DATING("Dating / In Love", "❤️"),
    ENGAGED("Engaged / Dowry Paid", "💍"),
    MARRIED("Married / Family Life", "👰🏽🤵🏽")
}

data class PartnerProfile(
    val name: String = "Amina",
    val occupation: String = "Fashion Stylist",
    val avatarEmoji: String = "👩🏽",
    val affection: Int = 80,
    val favoriteDateSpot: String = "Victoria Island Rooftop"
)

data class KidInfo(
    val id: String,
    val name: String,
    val age: Int,
    val schoolGrade: String,
    val schoolName: String,
    val avatarEmoji: String = "🎒",
    val happiness: Int = 90
)

data class FamilyState(
    val relationshipStatus: RelationshipStatus = RelationshipStatus.SINGLE,
    val partner: PartnerProfile? = null,
    val kids: List<KidInfo> = emptyList(),
    val targetKidsCount: Int = 2,
    val weddingHeld: Boolean = false,
    val lastSchoolDropOffDay: Int = 0,
    val schoolFeesPaid: Boolean = true
)

enum class MealCuisine(val displayName: String, val iconEmoji: String, val region: String) {
    NIGERIAN("Nigerian Meals", "🇳🇬", "West Africa"),
    THAI("Thai Meals", "🇹🇭", "Southeast Asia"),
    CHINESE("Chinese Meals", "🇨🇳", "East Asia"),
    FRENCH("French Meals", "🇫🇷", "Europe"),
    PIZZA("Artisan Pizza", "🍕", "Continental"),
    SHAWARMA("Shawarma & Wraps", "🌯", "Middle East & Street"),
    ICE_CREAM("Gourmet Ice Creams", "🍨", "Dessert Parlour"),
    CAKES("Celebration Cakes", "🎂", "Bakery & Patisserie"),
    INTERCONTINENTAL("Intercontinental", "🌍", "Global Dining")
}

data class MealItem(
    val id: String,
    val name: String,
    val cuisine: MealCuisine,
    val iconEmoji: String,
    val tagline: String,
    val description: String,
    val takeoutPrice: Long,
    val cookingCost: Long,
    val cookingTimeMinutes: Int,
    val ingredients: List<String>,
    val cookingInstructions: List<String>,
    val flavorProfile: String,
    val stressRelief: Int = 8,
    val energyBoost: Int = 25,
    val cakeSizeOption: String? = null,
    val flavourVariant: String? = null
)

data class MealConsumptionRecord(
    val id: String,
    val mealName: String,
    val cuisine: MealCuisine,
    val wasCooked: Boolean,
    val costPaid: Long,
    val dayNumber: Int,
    val timeLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PartyEvent(
    val id: String,
    val title: String,
    val theme: String,
    val cost: Long,
    val iconEmoji: String,
    val invitedGuests: List<String>,
    val playlist: String,
    val drinksProvided: String,
    val reputationBoost: Int,
    val stressReduction: Int,
    val hypeLevel: Int
)

data class ClubbingVenue(
    val id: String,
    val name: String,
    val location: String,
    val iconEmoji: String,
    val vibeDescription: String,
    val regularEntryFee: Long,
    val vipTableCost: Long,
    val stressReduction: Int,
    val musicGenre: String
)

data class ShopLedgerEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val dayLabel: String,
    val customerOrSupplier: String,
    val itemDescription: String,
    val amount: Long,
    val isCreditSale: Boolean = false,
    val isExpense: Boolean = false,
    val paymentChannel: String = "Cash", // Cash, POS Transfer, Cheque
    val note: String = "Paid in full",
    val timestamp: Long = System.currentTimeMillis()
)

data class CustomerDebtor(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val relationship: String,
    val amountOwed: Long,
    val originalPurchase: String,
    val daysOverdue: Int = 3,
    val phoneContact: String = "0803-xxx-xxxx",
    val isSettled: Boolean = false
)

enum class RadioStation(
    val callSign: String,
    val stationName: String,
    val genre: String,
    val vibeEmoji: String,
    val motto: String
) {
    WAZOBIA_993("99.3 FM", "Wazobia Hustle Radio", "Street Gist & Traffic", "📻", "Ogbonge station for real hustlers!"),
    AFROBEAT_1045("104.5 FM", "Afrobeat Highlife Jamz", "Fela, Burna & Asake", "🎶", "No dulling, ginger your spirit!"),
    MARKET_GIST_911("91.1 FM", "Alaba Traders Network", "Wholesale & FX Spot", "📢", "Live market spot prices & container gist"),
    MORNING_GRACE_889("88.9 FM", "Early Morning Grace", "Praise & Thanksgiving", "⛪", "Start your shop opening with divine blessings")
}

data class RadioStationState(
    val currentStation: RadioStation = RadioStation.WAZOBIA_993,
    val isPlaying: Boolean = true,
    val currentNewsTicker: String = "Live: Third Mainland Bridge moving fast; Balogun market opens early; Fuel pump price steady.",
    val volumeLevel: Float = 0.8f
)

data class GeneratorGridState(
    val hasNepaLight: Boolean = true,
    val isGeneratorRunning: Boolean = false,
    val fuelLitres: Int = 15,
    val generatorCapacityKva: Int = 5,
    val generatorName: String = "I-Better-Pass-My-Neighbor (3.5kVA)",
    val dailyFuelCostNaira: Long = 3500L
)

val defaultShopLedgerEntries: List<ShopLedgerEntry> = listOf(
    ShopLedgerEntry(
        dayLabel = "Day 1 - Morning",
        customerOrSupplier = "Mama Funke (Balogun Regular)",
        itemDescription = "5x Retail sales items",
        amount = 18500L,
        paymentChannel = "Cash",
        note = "Regular customer, gave ₦500 bonus discount"
    ),
    ShopLedgerEntry(
        dayLabel = "Day 1 - Afternoon",
        customerOrSupplier = "Alhaji Bello (Bulk Buyer)",
        itemDescription = "12x Wholesale units express pick-up",
        amount = 54000L,
        paymentChannel = "POS Bank Transfer",
        note = "Confirmed via Instant Bank Alert"
    ),
    ShopLedgerEntry(
        dayLabel = "Day 1 - Evening",
        customerOrSupplier = "Sister Ngozi",
        itemDescription = "2x Walk-in cash purchase",
        amount = 7200L,
        paymentChannel = "Cash",
        note = "Counter payment settled"
    )
)

val defaultCustomerDebtors: List<CustomerDebtor> = listOf(
    CustomerDebtor(
        name = "Chief Mike (Owese)",
        relationship = "Elder Customer / Community Patron",
        amountOwed = 24500L,
        originalPurchase = "Holiday supplies on short-term trust",
        daysOverdue = 4,
        phoneContact = "0802-555-1920"
    ),
    CustomerDebtor(
        name = "Bro Segun (Mechanic)",
        relationship = "Workshop Owner down the street",
        amountOwed = 12000L,
        originalPurchase = "Emergency battery & cables",
        daysOverdue = 2,
        phoneContact = "0803-777-4481"
    ),
    CustomerDebtor(
        name = "Mama Blessing (Hairdresser)",
        relationship = "Salon Neighbor",
        amountOwed = 7500L,
        originalPurchase = "Cosmetics retail supply",
        daysOverdue = 5,
        phoneContact = "0818-999-3112"
    )
)



