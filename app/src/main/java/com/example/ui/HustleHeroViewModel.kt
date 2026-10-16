package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BusinessData
import com.example.data.EventsRepository
import com.example.data.FirebaseCommunityRepository
import com.example.data.FirebaseLeaderboardEntry
import com.example.data.FirebaseUserAchievement
import com.example.data.RivalChallenge
import com.example.logic.GameEngine
import com.example.ui.components.BankSmsAlert
import com.example.logic.AjoSavingsManager
import com.example.logic.DailyHustleManager
import com.example.logic.EverydayLifeManager
import com.example.model.AdCampaign
import com.example.model.AjoSavingGoal
import com.example.model.AjoContributionRecord
import com.example.model.AjoContributionResult
import com.example.model.AjoContributionValidation
import com.example.model.Business
import com.example.model.CareerTrack
import com.example.model.CharacterProfile
import com.example.model.ClearanceResult
import com.example.model.ClubbingVenue
import com.example.model.CurrencyFormatter
import com.example.model.CustomerDebtor
import com.example.model.EventChoice
import com.example.model.GameEvent
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.GeneratorGridState
import com.example.model.JobRole
import com.example.model.LifestyleLevel
import com.example.model.Loan
import com.example.model.MealItem
import com.example.model.PartyEvent
import com.example.model.RadioStation
import com.example.model.RadioStationState
import com.example.model.RestockQuote
import com.example.model.RestockRecommendation
import com.example.model.RestockResult
import com.example.model.RiskLevel
import com.example.model.ShopLedgerEntry
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransferRequest
import android.content.Context
import com.example.model.DailyStreakConfig
import com.example.model.DailyStreakInfo
import com.example.model.Player
import com.example.model.WalletTransferResult
import com.example.model.WalletTransferType
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class HustleHeroViewModel(application: Application) : AndroidViewModel(application) {

    private val allEvents: List<GameEvent> by lazy {
        EventsRepository.loadEvents(application.applicationContext)
    }

    private val firebaseRepo by lazy {
        FirebaseCommunityRepository(application.applicationContext)
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    val player: StateFlow<Player?> = _gameState.map { it?.player }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        _gameState.value?.player
    )

    private val _selectedSalaryTransfer = MutableStateFlow(15_000L)
    val selectedSalaryTransfer: StateFlow<Long> = _selectedSalaryTransfer.asStateFlow()

    private val _activeEvent = MutableStateFlow<GameEvent?>(null)
    val activeEvent: StateFlow<GameEvent?> = _activeEvent.asStateFlow()

    private val _chosenChoice = MutableStateFlow<EventChoice?>(null)
    val chosenChoice: StateFlow<EventChoice?> = _chosenChoice.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<FirebaseLeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<FirebaseLeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _achievements = MutableStateFlow<List<FirebaseUserAchievement>>(emptyList())
    val achievements: StateFlow<List<FirebaseUserAchievement>> = _achievements.asStateFlow()

    private val _friendsList = MutableStateFlow<Set<String>>(firebaseRepo.getSavedFriendIds())
    val friendsList: StateFlow<Set<String>> = _friendsList.asStateFlow()

    private val _activeRivalChallenge = MutableStateFlow<RivalChallenge?>(null)
    val activeRivalChallenge: StateFlow<RivalChallenge?> = _activeRivalChallenge.asStateFlow()

    private val _ajoContributionResult = MutableStateFlow<AjoContributionResult?>(null)
    val ajoContributionResult: StateFlow<AjoContributionResult?> = _ajoContributionResult.asStateFlow()

    private val _routineActionToast = MutableStateFlow<String?>(null)
    val routineActionToast: StateFlow<String?> = _routineActionToast.asStateFlow()

    private val streakPrefs by lazy {
        getApplication<Application>().getSharedPreferences("hustle_hero_daily_streak", Context.MODE_PRIVATE)
    }

    private val _showDailyStreakDialog = MutableStateFlow(false)
    val showDailyStreakDialog: StateFlow<Boolean> = _showDailyStreakDialog.asStateFlow()

    private val _dailyStreakInfo = MutableStateFlow(DailyStreakInfo())
    val dailyStreakInfo: StateFlow<DailyStreakInfo> = _dailyStreakInfo.asStateFlow()

    private val _latestBankAlert = MutableStateFlow<BankSmsAlert?>(null)
    val latestBankAlert: StateFlow<BankSmsAlert?> = _latestBankAlert.asStateFlow()

    fun triggerBankAlert(isCredit: Boolean, amount: Long, senderOrMerchant: String) {
        val current = _gameState.value ?: return
        val balance = current.personalCash + (if (isCredit) amount else -amount)
        _latestBankAlert.value = BankSmsAlert(
            isCredit = isCredit,
            amount = amount,
            senderOrMerchant = senderOrMerchant,
            balanceAfter = maxOf(0L, balance)
        )
    }

    fun dismissBankAlert() {
        _latestBankAlert.value = null
    }

    fun clearToast() {
        _routineActionToast.value = null
    }

    fun openDailyStreakDialog() {
        _showDailyStreakDialog.value = true
    }

    fun dismissDailyStreakDialog() {
        _showDailyStreakDialog.value = false
    }

    private fun checkAndInitializeDailyStreak() {
        val todayEpochDay = System.currentTimeMillis() / (1000L * 60L * 60L * 24L)
        val savedLastClaim = streakPrefs.getLong("last_claim_epoch_day", 0L)
        val savedStreak = streakPrefs.getInt("current_streak", 1).coerceAtLeast(1)
        val totalClaimed = streakPrefs.getInt("total_claimed", 0)
        val totalCashEarned = streakPrefs.getLong("total_cash_earned", 0L)

        val canClaimToday: Boolean
        val isBroken: Boolean
        val effectiveStreak: Int

        when {
            savedLastClaim == 0L -> {
                // First-time player opening app
                canClaimToday = true
                isBroken = false
                effectiveStreak = 1
            }
            todayEpochDay == savedLastClaim -> {
                // Already claimed today
                canClaimToday = false
                isBroken = false
                effectiveStreak = savedStreak
            }
            todayEpochDay == savedLastClaim + 1L -> {
                // Consecutive active day
                canClaimToday = true
                isBroken = false
                effectiveStreak = savedStreak
            }
            else -> {
                // Broken streak
                canClaimToday = true
                isBroken = true
                effectiveStreak = 1
            }
        }

        val todayReward = DailyStreakConfig.getRewardForDay(effectiveStreak)
        val updatedInfo = DailyStreakInfo(
            currentStreak = effectiveStreak,
            lastClaimEpochDay = savedLastClaim,
            canClaimToday = canClaimToday,
            isStreakBroken = isBroken,
            todayReward = todayReward,
            weeklyCycle = DailyStreakConfig.REWARDS,
            totalClaimedCount = totalClaimed,
            totalCashEarnedFromStreaks = totalCashEarned
        )

        _dailyStreakInfo.value = updatedInfo
        _gameState.value = _gameState.value?.copy(
            currentStreakDays = effectiveStreak,
            dailyStreak = updatedInfo
        )

        if (canClaimToday) {
            _showDailyStreakDialog.value = true
        }
    }

    fun claimDailyStreakReward(doubleWithAd: Boolean = false) {
        val currentInfo = _dailyStreakInfo.value
        if (!currentInfo.canClaimToday) return

        val todayEpochDay = System.currentTimeMillis() / (1000L * 60L * 60L * 24L)
        val multiplier = if (doubleWithAd) 2L else 1L
        val cashBonus = currentInfo.todayReward.bonusAmount * multiplier
        val repBonus = currentInfo.todayReward.reputationBonus * (if (doubleWithAd) 2 else 1)

        val nextStreakCount = currentInfo.currentStreak + 1
        val newTotalClaimed = currentInfo.totalClaimedCount + 1
        val newTotalCashEarned = currentInfo.totalCashEarnedFromStreaks + cashBonus

        streakPrefs.edit()
            .putLong("last_claim_epoch_day", todayEpochDay)
            .putInt("current_streak", nextStreakCount)
            .putInt("total_claimed", newTotalClaimed)
            .putLong("total_cash_earned", newTotalCashEarned)
            .apply()

        val updatedInfo = currentInfo.copy(
            currentStreak = nextStreakCount,
            lastClaimEpochDay = todayEpochDay,
            canClaimToday = false,
            isStreakBroken = false,
            todayReward = DailyStreakConfig.getRewardForDay(nextStreakCount),
            totalClaimedCount = newTotalClaimed,
            totalCashEarnedFromStreaks = newTotalCashEarned
        )

        _dailyStreakInfo.value = updatedInfo

        val current = _gameState.value
        if (current != null) {
            val updatedState = current.copy(
                personalCash = current.personalCash + cashBonus,
                reputation = (current.reputation + repBonus).coerceIn(0, 100),
                currentStreakDays = nextStreakCount,
                dailyStreak = updatedInfo
            )
            _gameState.value = updatedState

            val sourceTag = if (doubleWithAd) {
                "Daily Streak 2X Ad Bonus (Day ${currentInfo.currentDayInCycle})"
            } else {
                "Daily Streak Bonus (Day ${currentInfo.currentDayInCycle})"
            }

            triggerBankAlert(
                isCredit = true,
                amount = cashBonus,
                senderOrMerchant = sourceTag
            )

            if (currentInfo.isJackpotDay) {
                broadcastAchievement(
                    title = "🔥 7-Day Lagos Mogul Streak!",
                    description = "Maintained unbroken daily entrepreneurial momentum for 7 days in a row! Claimed grand ₦${CurrencyFormatter.formatNaira(cashBonus)} jackpot."
                )
            }

            syncToFirebase()
        }
    }

    init {
        val defaultBusiness = BusinessData.startingBusinesses.first()
        val defaultChar = BusinessData.characters.first()
        _gameState.value = GameEngine.createInitialState(
            characterName = defaultChar.name,
            business = defaultBusiness,
            city = "Lagos",
            character = defaultChar
        )

        checkAndInitializeDailyStreak()

        viewModelScope.launch {
            firebaseRepo.observeLeaderboard().collect {
                _leaderboard.value = it
            }
        }

        viewModelScope.launch {
            firebaseRepo.observeAchievements().collect {
                _achievements.value = it
            }
        }

        viewModelScope.launch {
            firebaseRepo.authManager.authStateFlow.collect { user ->
                _currentUser.value = user
                if (user != null) {
                    syncToFirebase()
                }
            }
        }

        viewModelScope.launch {
            firebaseRepo.authManager.attemptAutoSignIn()
        }
    }

    fun signInWithGoogle(activity: Activity, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _authError.value = null
            val result = firebaseRepo.authManager.signInWithGoogle(activity)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                syncToFirebase()
                onComplete?.invoke(true)
            } else {
                val err = result.exceptionOrNull()
                if (err !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                    _authError.value = err?.localizedMessage ?: "Google sign-in failed"
                }
                onComplete?.invoke(false)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseRepo.authManager.signOut()
            _currentUser.value = null
        }
    }

    fun startNewGame(
        name: String,
        business: Business,
        city: String = "Lagos",
        character: CharacterProfile = BusinessData.characters.first(),
        careerTrack: CareerTrack = CareerTrack.BUSINESS_OWNER,
        jobRole: JobRole? = null
    ) {
        val baseState = GameEngine.createInitialState(
            characterName = name.ifBlank { character.name },
            business = business,
            city = city,
            character = character
        )
        _gameState.value = baseState.copy(
            careerTrack = careerTrack,
            currentJob = jobRole
        )
        _activeEvent.value = null
        _chosenChoice.value = null
        _selectedSalaryTransfer.value = 15_000L
    }

    fun navigateToPhase(phase: GamePhase) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(currentPhase = phase)
    }

    fun toggleDarkMode() {
        val current = _gameState.value ?: return
        val newDark = !current.isDarkMode
        setDarkMode(newDark)
    }

    fun setDarkMode(newDark: Boolean) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(isDarkMode = newDark)
        com.example.ui.theme.ThemeState.isDarkMode.value = newDark
        _routineActionToast.value = if (newDark) "🌙 Night Market Dark Mode Activated" else "☀️ Daytime Light Mode Activated"
    }

    fun toggleSolarMode() {
        toggleDarkMode()
    }

    /**
     * Adjusts player's street reputation (clamped between 0 and 100).
     */
    fun updatePlayerReputation(delta: Int) {
        val current = _gameState.value ?: return
        val newRep = (current.reputation + delta).coerceIn(0, 100)
        _gameState.value = current.copy(reputation = newRep)
    }

    /**
     * Upgrades or expands the player's business scale tier.
     */
    fun upgradeBusinessLevel() {
        val current = _gameState.value ?: return
        val cost = 50_000L
        if (current.businessCash >= cost) {
            val updatedCash = current.businessCash - cost
            val updatedRep = (current.reputation + 5).coerceIn(0, 100)
            _gameState.value = current.copy(
                businessCash = updatedCash,
                reputation = updatedRep
            )
            triggerBankAlert(
                isCredit = false,
                amount = cost,
                senderOrMerchant = "CAC & Market Stall Expansion Permit"
            )
        }
    }

    // --- FIREBASE SYNC & ACHIEVEMENTS ---

    fun syncToFirebase() {
        val current = _gameState.value ?: return
        firebaseRepo.syncPlayerScore(
            userName = current.characterName,
            userAvatar = current.character.avatarEmoji,
            businessName = current.business.name,
            cityName = current.cityName,
            netWorth = current.netWorth,
            week = current.week,
            reputation = current.reputation,
            streakDays = _dailyStreakInfo.value.currentStreak
        )
    }

    fun broadcastAchievement(title: String, description: String) {
        val current = _gameState.value ?: return
        firebaseRepo.broadcastMilestone(
            userName = current.characterName,
            userAvatar = current.character.avatarEmoji,
            achievementTitle = title,
            description = description,
            businessName = current.business.name,
            netWorth = current.netWorth,
            weekNumber = current.week
        )
    }

    // --- LEADERBOARD & FRIENDS NETWORK ACTIONS ---

    fun isFriend(idOrCode: String): Boolean {
        return _friendsList.value.contains(idOrCode)
    }

    fun toggleFriend(entry: FirebaseLeaderboardEntry) {
        val current = _friendsList.value.toMutableSet()
        val isAlreadyFriend = current.contains(entry.userId) || (entry.friendCode.isNotEmpty() && current.contains(entry.friendCode))
        if (isAlreadyFriend) {
            current.remove(entry.userId)
            if (entry.friendCode.isNotEmpty()) current.remove(entry.friendCode)
            firebaseRepo.removeFriendId(entry.userId)
            _routineActionToast.value = "Removed ${entry.userName} from friends circle."
        } else {
            current.add(entry.userId)
            if (entry.friendCode.isNotEmpty()) current.add(entry.friendCode)
            firebaseRepo.saveFriendId(entry.userId)
            _routineActionToast.value = "Added ${entry.userName} to friends circle! 🤝"
        }
        _friendsList.value = current
    }

    fun addFriendByCode(rawCode: String): Pair<Boolean, String> {
        val code = rawCode.trim().uppercase()
        if (code.isBlank()) return false to "Friend code cannot be empty."
        val myCode = getPlayerFriendCode().uppercase()
        if (code == myCode) return false to "You cannot add yourself as a friend!"

        val matchedEntry = _leaderboard.value.find {
            it.friendCode.equals(code, ignoreCase = true) || it.userId.equals(code, ignoreCase = true)
        }

        val current = _friendsList.value.toMutableSet()
        if (matchedEntry != null) {
            current.add(matchedEntry.userId)
            if (matchedEntry.friendCode.isNotEmpty()) current.add(matchedEntry.friendCode)
            firebaseRepo.saveFriendId(matchedEntry.userId)
            _friendsList.value = current
            return true to "Added ${matchedEntry.userName} to your friends leaderboard!"
        } else {
            current.add(code)
            firebaseRepo.saveFriendId(code)
            _friendsList.value = current
            return true to "Friend code '$code' added to your circle!"
        }
    }

    fun getPlayerFriendCode(): String {
        val current = _gameState.value ?: return "HH-HERO-1000"
        return firebaseRepo.generateFriendCode(firebaseRepo.currentUser?.uid ?: "local_player", current.characterName)
    }

    fun launchRivalChallenge(entry: FirebaseLeaderboardEntry): String {
        val current = _gameState.value ?: return "Game not initialized"
        val target = max(entry.netWorth + 100_000L, (current.netWorth * 1.15f).toLong())
        val challenge = RivalChallenge(
            rivalId = entry.userId,
            rivalName = entry.userName,
            rivalAvatar = entry.userAvatar,
            rivalNetWorth = entry.netWorth,
            targetNetWorth = target,
            rewardReputation = 12,
            rewardCashBonus = 120_000L,
            description = "Overtake ${entry.userName}'s net worth (${CurrencyFormatter.formatNaira(entry.netWorth)}) by reaching ${CurrencyFormatter.formatNaira(target)}!"
        )
        _activeRivalChallenge.value = challenge
        _routineActionToast.value = "⚔️ Challenge accepted vs ${entry.userName}! Target: ${CurrencyFormatter.formatNaira(target)} net worth."
        return "Challenge active! Beat ${entry.userName} to earn ₦120,000 cash bonus + 12 reputation!"
    }

    fun claimRivalChallengeReward(): Pair<Boolean, String> {
        val challenge = _activeRivalChallenge.value ?: return false to "No active rival challenge."
        val current = _gameState.value ?: return false to "Game not initialized."

        if (current.netWorth < challenge.targetNetWorth) {
            val gap = challenge.targetNetWorth - current.netWorth
            return false to "Not yet! Need ${CurrencyFormatter.formatNaira(gap)} more net worth to beat ${challenge.rivalName}."
        }

        val updated = current.copy(
            personalCash = current.personalCash + challenge.rewardCashBonus,
            reputation = min(100, current.reputation + challenge.rewardReputation)
        )
        _gameState.value = updated
        _activeRivalChallenge.value = null
        _routineActionToast.value = "🏆 Rivalry Defeated! Claimed +${CurrencyFormatter.formatNaira(challenge.rewardCashBonus)} & +${challenge.rewardReputation} Rep!"
        syncToFirebase()
        broadcastAchievement(
            title = "Defeated Rival ${challenge.rivalName}!",
            description = "Surpassed ${challenge.rivalName} with a glorious net worth of ${CurrencyFormatter.formatNaira(current.netWorth)}!"
        )
        return true to "Congratulations! Beat ${challenge.rivalName} and earned ${CurrencyFormatter.formatNaira(challenge.rewardCashBonus)}!"
    }

    fun dismissRivalChallenge() {
        _activeRivalChallenge.value = null
    }

    // --- PAID ADS SPOT (TV, BILLBOARD, MOBILE INTERNET) ---

    fun launchAdCampaign(campaign: AdCampaign) {
        val current = _gameState.value ?: return
        if (current.businessCash < campaign.cost) return
        if (current.activeAds.any { it.id == campaign.id && it.isActive }) return

        val activeInstance = campaign.copy(
            weeksRemaining = campaign.durationWeeks,
            isActive = true
        )

        _gameState.value = current.copy(
            businessCash = current.businessCash - campaign.cost,
            reputation = min(100, current.reputation + campaign.reputationBoost),
            activeAds = current.activeAds + activeInstance
        )

        broadcastAchievement(
            title = "Aired ${campaign.name}!",
            description = "Launched ${campaign.type.title} on ${campaign.channelName} for +${campaign.demandBoostPercent}% demand boost."
        )
    }

    // --- COMMUNITY ACTIONS ---

    fun voteCommunityProposal(proposalId: String) {
        val current = _gameState.value ?: return
        val proposal = current.communityProposals.find { it.id == proposalId } ?: return
        if (proposal.hasVoted) return
        if (current.personalCash < proposal.costPerMember && current.businessCash < proposal.costPerMember) return

        val payFromBiz = current.businessCash >= proposal.costPerMember
        val newBiz = if (payFromBiz) current.businessCash - proposal.costPerMember else current.businessCash
        val newPers = if (!payFromBiz) current.personalCash - proposal.costPerMember else current.personalCash

        val updatedProposals = current.communityProposals.map {
            if (it.id == proposalId) {
                it.copy(
                    votesFor = it.votesFor + 1,
                    hasVoted = true
                )
            } else it
        }

        _gameState.value = current.copy(
            businessCash = newBiz,
            personalCash = newPers,
            reputation = min(100, current.reputation + 6),
            communityProposals = updatedProposals
        )

        broadcastAchievement(
            title = "Backed ${proposal.title}!",
            description = "Contributed to market union infrastructure in ${current.cityName}."
        )
    }

    fun togglePostLike(postId: String) {
        val current = _gameState.value ?: return
        val updated = current.communityPosts.map {
            if (it.id == postId) {
                val newLiked = !it.isLiked
                it.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                )
            } else it
        }
        _gameState.value = current.copy(communityPosts = updated)
    }

    fun partnerWithTrader(memberId: String) {
        val current = _gameState.value ?: return
        val updatedMembers = current.communityMembers.map {
            if (it.id == memberId) it.copy(isPartner = !it.isPartner) else it
        }
        _gameState.value = current.copy(communityMembers = updatedMembers)
    }

    // --- BUSINESS & INVENTORY ACTIONS ---

    fun restockInventory(unitsToAdd: Int): RestockResult? {
        val current = _gameState.value ?: return null
        val result = GameEngine.restockInventory(current, unitsToAdd)
        if (result.success) {
            _gameState.value = result.updatedState
            if (result.unitsAdded >= 50) {
                broadcastAchievement(
                    title = "Wholesale Stock Influx!",
                    description = "Stocked ${result.unitsAdded} units of ${current.business.itemUnitName} in ${current.cityName} with wholesale volume discount."
                )
            }
        }
        return result
    }

    fun calculateRestockQuote(units: Int): RestockQuote? {
        val current = _gameState.value ?: return null
        return GameEngine.calculateRestockQuote(current, units)
    }

    fun getRecommendedRestock(): RestockRecommendation? {
        val current = _gameState.value ?: return null
        return GameEngine.getRecommendedRestock(current)
    }

    fun liquidateInventory(units: Int, discountPercent: Float = 0.30f): ClearanceResult? {
        val current = _gameState.value ?: return null
        val result = GameEngine.liquidateInventory(current, units, discountPercent)
        if (result.success) {
            _gameState.value = result.updatedState
        }
        return result
    }

    fun setSellingPrice(price: Long) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(sellingPrice = max(500L, price))
    }

    fun setMarketingBudget(budget: Long) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(marketingBudgetWeekly = max(0L, budget))
    }

    fun hireStaff() {
        val current = _gameState.value ?: return
        if (current.staffCount < 5) {
            _gameState.value = current.copy(staffCount = current.staffCount + 1)
        }
    }

    fun fireStaff() {
        val current = _gameState.value ?: return
        if (current.staffCount > 0) {
            _gameState.value = current.copy(staffCount = current.staffCount - 1)
        }
    }

    fun buyUpgrade(upgradeId: String) {
        val current = _gameState.value ?: return
        val upgrade = current.upgrades.find { it.id == upgradeId } ?: return
        if (upgrade.isPurchased || current.businessCash < upgrade.cost) return

        val updatedList = current.upgrades.map {
            if (it.id == upgradeId) it.copy(isPurchased = true) else it
        }
        _gameState.value = current.copy(
            businessCash = current.businessCash - upgrade.cost,
            reputation = min(100, current.reputation + upgrade.reputationBonus),
            upgrades = updatedList
        )

        broadcastAchievement(
            title = "Equipped ${upgrade.name}!",
            description = "Upgraded ${current.business.name} with ${upgrade.description}"
        )
    }

    // --- DUAL-WALLET TRANSFER ACTIONS (BUSINESS <-> PERSONAL) ---

    fun setSalaryWithdrawal(amount: Long) {
        _selectedSalaryTransfer.value = max(0L, amount)
    }

    fun getRecommendedSalary(): Long {
        val current = _gameState.value ?: return 15_000L
        return GameEngine.getRecommendedSalary(current)
    }

    fun executeWalletTransfer(request: WalletTransferRequest): WalletTransferResult? {
        val current = _gameState.value ?: return null
        val result = GameEngine.executeWalletTransfer(current, request)
        if (result.success) {
            _gameState.value = result.updatedState
            broadcastAchievement(
                title = "Wallet Transfer: ${request.type.title}",
                description = "Moved ${CurrencyFormatter.formatNaira(request.amount)} between accounts in ${current.cityName}."
            )
        }
        return result
    }

    fun transferSalaryImmediate(amount: Long): WalletTransferResult? {
        return executeWalletTransfer(
            WalletTransferRequest(
                type = WalletTransferType.SALARY_DRAW,
                amount = amount,
                source = WalletSource.BUSINESS_CASH,
                destination = WalletDestination.PERSONAL_CASH,
                reason = "Instant owner salary drawing"
            )
        )
    }

    fun transferOwnerDividend(amount: Long): WalletTransferResult? {
        return executeWalletTransfer(
            WalletTransferRequest(
                type = WalletTransferType.OWNER_DIVIDEND,
                amount = amount,
                source = WalletSource.BUSINESS_CASH,
                destination = WalletDestination.PERSONAL_CASH,
                reason = "Owner dividend from retained profits"
            )
        )
    }

    fun injectPersonalCapital(amount: Long, fromSavings: Boolean = false): WalletTransferResult? {
        return executeWalletTransfer(
            WalletTransferRequest(
                type = WalletTransferType.CAPITAL_INJECTION,
                amount = amount,
                source = if (fromSavings) WalletSource.PERSONAL_SAVINGS else WalletSource.PERSONAL_CASH,
                destination = WalletDestination.BUSINESS_CASH,
                reason = "Personal equity injected into business capital"
            )
        )
    }

    fun emergencyBailout(amount: Long): WalletTransferResult? {
        return executeWalletTransfer(
            WalletTransferRequest(
                type = WalletTransferType.EMERGENCY_BAILOUT,
                amount = amount,
                source = WalletSource.PERSONAL_SAVINGS,
                destination = WalletDestination.BUSINESS_CASH,
                reason = "Emergency bailout to avert business bankruptcy"
            )
        )
    }

    fun proceedToPersonalPhase() {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(currentPhase = GamePhase.PERSONAL)
    }

    // --- PERSONAL PHASE ACTIONS ---

    fun setLifestyle(level: LifestyleLevel) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(
            personalExpenses = current.personalExpenses.copy(lifestyle = level)
        )
    }

    fun depositToSavings(amount: Long) {
        val current = _gameState.value ?: return
        if (amount <= 0 || current.personalCash < amount) return
        _gameState.value = current.copy(
            personalCash = current.personalCash - amount,
            personalSavings = current.personalSavings + amount
        )
    }

    fun withdrawFromSavings(amount: Long) {
        val current = _gameState.value ?: return
        if (amount <= 0 || current.personalSavings < amount) return
        _gameState.value = current.copy(
            personalCash = current.personalCash + amount,
            personalSavings = current.personalSavings - amount
        )
    }

    fun toggleAjoMembership(enrolled: Boolean, contribution: Long = 10_000L) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(
            ajoGroup = current.ajoGroup.copy(
                isEnrolled = enrolled,
                weeklyContribution = contribution
            )
        )
    }

    fun joinAjoGroup(weeklyContribution: Long, cycleWeeks: Int = 5, payoutWeek: Int = 3) {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(
            ajoGroup = current.ajoGroup.copy(
                isEnrolled = true,
                weeklyContribution = weeklyContribution,
                cycleWeeks = cycleWeeks,
                payoutWeek = payoutWeek,
                currentCycleWeek = 1
            )
        )
    }

    fun leaveAjoGroup() {
        val current = _gameState.value ?: return
        _gameState.value = current.copy(
            ajoGroup = current.ajoGroup.copy(
                isEnrolled = false
            )
        )
    }

    // --- AJO SAVING GOALS & CONTRIBUTION BACKEND ---

    fun contributeToAjoGoal(
        goalId: String,
        amount: Long,
        source: WalletSource = WalletSource.PERSONAL_CASH,
        note: String = ""
    ): AjoContributionResult {
        val current = _gameState.value ?: return AjoContributionResult(
            success = false,
            message = "Game state not initialized.",
            goal = null
        )

        val (nextState, result) = AjoSavingsManager.executeContribution(
            state = current,
            goalId = goalId,
            amount = amount,
            source = source,
            note = note
        )

        _gameState.value = nextState
        _ajoContributionResult.value = result

        if (result.success) {
            syncToFirebase()
            if (result.isGoalCompletedNow) {
                val goal = result.goal
                firebaseRepo.broadcastMilestone(
                    userName = nextState.characterName,
                    userAvatar = nextState.character.avatarEmoji,
                    achievementTitle = "🎯 Reached Ajo Goal: ${goal?.title ?: "Milestone"}!",
                    description = "Saved ${CurrencyFormatter.formatNaira(goal?.targetAmount ?: 0L)} through collective Ajo discipline. Active perk: ${goal?.perkReward ?: "Growth Unlocked"}.",
                    businessName = nextState.business.name,
                    netWorth = nextState.netWorth,
                    weekNumber = nextState.week
                )
            }
        }

        return result
    }

    fun createAjoGoal(
        title: String,
        description: String,
        category: String,
        iconEmoji: String,
        targetAmount: Long,
        weeklyTarget: Long = 10_000L,
        perkReward: String = "Custom SME Growth Perk",
        deadlineWeek: Int? = null
    ): AjoSavingGoal? {
        val current = _gameState.value ?: return null
        val targetDeadline = deadlineWeek ?: (current.week + 8)
        val (nextState, newGoal) = AjoSavingsManager.createGoal(
            state = current,
            title = title,
            description = description,
            category = category,
            iconEmoji = iconEmoji,
            targetAmount = targetAmount,
            weeklyTarget = weeklyTarget,
            perkReward = perkReward,
            deadlineWeek = targetDeadline
        )
        _gameState.value = nextState
        return newGoal
    }

    fun updateAjoGoalDeadline(goalId: String, newDeadlineWeek: Int) {
        val current = _gameState.value ?: return
        _gameState.value = AjoSavingsManager.updateGoalDeadline(current, goalId, newDeadlineWeek)
    }

    fun selectAjoGoal(goalId: String) {
        val current = _gameState.value ?: return
        _gameState.value = AjoSavingsManager.selectGoal(current, goalId)
    }

    fun clearAjoContributionResult() {
        _ajoContributionResult.value = null
    }

    fun investFunds(investmentId: String, amount: Long) {
        val current = _gameState.value ?: return
        if (amount <= 0 || current.personalCash < amount) return

        val updated = current.investments.map { inv ->
            if (inv.id == investmentId) {
                inv.copy(
                    investedAmount = inv.investedAmount + amount,
                    totalValue = inv.totalValue + amount
                )
            } else inv
        }
        _gameState.value = current.copy(
            personalCash = current.personalCash - amount,
            investments = updated
        )
    }

    fun withdrawInvestment(investmentId: String, amount: Long) {
        val current = _gameState.value ?: return
        val inv = current.investments.find { it.id == investmentId } ?: return
        val withdrawable = min(amount, inv.totalValue)
        if (withdrawable <= 0) return

        val updated = current.investments.map {
            if (it.id == investmentId) {
                it.copy(totalValue = it.totalValue - withdrawable)
            } else it
        }
        _gameState.value = current.copy(
            personalCash = current.personalCash + withdrawable,
            investments = updated
        )
    }

    fun takeLoan(sourceName: String, principal: Long, rate: Float, weeklyPayment: Long) {
        val current = _gameState.value ?: return
        val newLoan = Loan(
            id = "loan_${System.currentTimeMillis()}",
            sourceName = sourceName,
            principal = principal,
            balance = principal,
            weeklyInterestRate = rate,
            minWeeklyPayment = weeklyPayment
        )
        _gameState.value = current.copy(
            businessCash = current.businessCash + principal,
            loans = current.loans + newLoan,
            creditScore = max(300, current.creditScore - 10)
        )
    }

    fun triggerEventPhase() {
        val current = _gameState.value ?: return
        val event = allEvents.randomOrNull() ?: allEvents.first()
        _activeEvent.value = event
        _chosenChoice.value = null
        _gameState.value = current.copy(
            currentPhase = GamePhase.EVENT,
            currentEvent = event
        )
    }

    // --- EVENT PHASE ACTIONS ---

    fun resolveEventChoice(choice: EventChoice) {
        val current = _gameState.value ?: return
        val event = _activeEvent.value ?: current.currentEvent
        _chosenChoice.value = choice

        val salary = _selectedSalaryTransfer.value
        val newState = GameEngine.simulateWeeklyTurn(
            state = current,
            chosenEvent = event,
            choice = choice,
            salaryWithdrawal = salary
        )
        _gameState.value = newState
        syncToFirebase()
    }

    // --- DAILY HUSTLE & EVERYDAY LIFE ACTIONS ---

    fun completeDailyHustle(taskId: String) {
        val current = _gameState.value ?: return
        val (nextState, msg) = DailyHustleManager.completeTask(current, taskId)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun refreshDailyHustles() {
        val current = _gameState.value ?: return
        val fresh = DailyHustleManager.generateDailyTasks(current.dailyRoutine.currentDay, current.business)
        _gameState.value = current.copy(dailyHustles = fresh)
        _routineActionToast.value = "Daily hustle challenges refreshed for ${current.business.name}!"
    }

    fun wakeUp() {
        val current = _gameState.value ?: return
        _gameState.value = EverydayLifeManager.wakeUp(current)
        _routineActionToast.value = "Awake and alert for the grind!"
    }

    fun takeBath() {
        val current = _gameState.value ?: return
        _gameState.value = EverydayLifeManager.takeBath(current)
        _routineActionToast.value = "Took a warm bath. Hygiene 100%, feeling fresh!"
    }

    fun brushTeeth() {
        val current = _gameState.value ?: return
        _gameState.value = EverydayLifeManager.brushTeeth(current)
        _routineActionToast.value = "Teeth brushed with minty shine!"
    }

    fun prepareBreakfast() {
        val current = _gameState.value ?: return
        _gameState.value = EverydayLifeManager.prepareBreakfast(current)
        _routineActionToast.value = "Wholesome breakfast made! Energy recharged."
    }

    fun cookMeal(meal: MealItem) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.cookMeal(current, meal)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun orderTakeoutMeal(meal: MealItem) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.orderTakeoutMeal(current, meal)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun workOrRunBusiness() {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.workOrRunBusiness(current)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun takeKidsToSchool() {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.takeKidsToSchool(current)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun advanceTimeOfDay() {
        val current = _gameState.value ?: return
        _gameState.value = EverydayLifeManager.advanceTime(current)
    }

    fun sleepNight() {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.sleepNight(current)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun goOnDate(spotName: String, cost: Long) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.goOnDate(current, spotName, cost)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun proposeAndMarry() {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.proposeAndMarry(current)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun haveBaby(babyName: String) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.haveBaby(current, babyName)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun hostParty(party: PartyEvent) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.hostParty(current, party)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun goClubbing(venue: ClubbingVenue, isVip: Boolean) {
        val current = _gameState.value ?: return
        val (nextState, msg) = EverydayLifeManager.goClubbing(current, venue, isVip)
        _gameState.value = nextState
        _routineActionToast.value = msg
        syncToFirebase()
    }

    fun switchCareer(track: CareerTrack, job: JobRole?, business: Business?) {
        val current = _gameState.value ?: return
        val updated = current.copy(
            careerTrack = track,
            currentJob = job,
            business = business ?: current.business
        )
        _gameState.value = updated
        _routineActionToast.value = if (track == CareerTrack.EMPLOYED_JOB) {
            "Started career as ${job?.title} at ${job?.company}!"
        } else {
            "Now running ${business?.name ?: current.business.name} full-time!"
        }
        syncToFirebase()
    }

    // --- SUMMARY PHASE ACTIONS ---

    fun nextWeek() {
        val current = _gameState.value ?: return
        if (current.isGameOver) return
        _activeEvent.value = null
        _chosenChoice.value = null
        _gameState.value = current.copy(
            currentPhase = GamePhase.DASHBOARD,
            currentEvent = null,
            selectedChoice = null
        )
    }

    // --- REALISTIC STREET AMBIANCE: WAZOBIA RADIO & MARKET BROADCAST ---

    fun toggleRadioPlayback() {
        val current = _gameState.value ?: return
        val currentRadio = current.radioStation
        val newPlaying = !currentRadio.isPlaying
        val updatedRadio = currentRadio.copy(isPlaying = newPlaying)
        _gameState.value = current.copy(radioStation = updatedRadio)
        _routineActionToast.value = if (newPlaying) "📻 Tuned in to ${currentRadio.currentStation.stationName}!" else "🔇 Radio paused"
    }

    fun switchRadioStation(station: RadioStation) {
        val current = _gameState.value ?: return
        val updatedRadio = current.radioStation.copy(
            currentStation = station,
            isPlaying = true,
            currentNewsTicker = "Now playing: ${station.callSign} - ${station.genre}. ${station.motto}"
        )
        _gameState.value = current.copy(radioStation = updatedRadio)
        _routineActionToast.value = "📻 Station switched to ${station.stationName} (${station.callSign})"
    }

    // --- REALISTIC SHOP COUNTER LEDGER & REVENUE RECORDS ---

    fun recordLedgerSale(
        customer: String,
        item: String,
        amount: Long,
        channel: String = "Cash",
        note: String = "Settled on counter"
    ) {
        val current = _gameState.value ?: return
        val newEntry = ShopLedgerEntry(
            dayLabel = "Day ${current.dailyRoutine.currentDay} - ${current.dailyRoutine.timeOfDay.displayName}",
            customerOrSupplier = customer,
            itemDescription = item,
            amount = amount,
            paymentChannel = channel,
            note = note
        )
        val updatedLedger = listOf(newEntry) + current.shopLedger
        val updatedCash = current.businessCash + amount
        _gameState.value = current.copy(
            shopLedger = updatedLedger.take(50),
            businessCash = updatedCash
        )
        triggerBankAlert(
            isCredit = true,
            amount = amount,
            senderOrMerchant = customer
        )
        _routineActionToast.value = "📒 Sale recorded in Counter Ledger: +${CurrencyFormatter.formatNaira(amount)} from $customer"
    }

    // --- RECOVER OVERDUE DEBTORS (BOOK OF DEBTS) ---

    fun collectDebtorPayment(debtorId: String, discountPercent: Int = 0): Pair<Boolean, String> {
        val current = _gameState.value ?: return false to "Game state not found"
        val debtor = current.customerDebtors.find { it.id == debtorId }
            ?: return false to "Debtor not found in ledger"

        val payableAmount = if (discountPercent > 0) {
            debtor.amountOwed - (debtor.amountOwed * discountPercent / 100)
        } else {
            debtor.amountOwed
        }

        val updatedDebtors = current.customerDebtors.map {
            if (it.id == debtorId) it.copy(isSettled = true) else it
        }

        val ledgerEntry = ShopLedgerEntry(
            dayLabel = "Day ${current.dailyRoutine.currentDay} - Debt Recovered",
            customerOrSupplier = debtor.name,
            itemDescription = "Overdue debt settlement (${debtor.originalPurchase})",
            amount = payableAmount,
            paymentChannel = "POS Transfer / Cash",
            note = if (discountPercent > 0) "Settled with $discountPercent% prompt discount" else "Settled in full"
        )

        val updatedCash = current.businessCash + payableAmount
        _gameState.value = current.copy(
            customerDebtors = updatedDebtors,
            shopLedger = listOf(ledgerEntry) + current.shopLedger,
            businessCash = updatedCash,
            reputation = min(100, current.reputation + 2)
        )

        triggerBankAlert(
            isCredit = true,
            amount = payableAmount,
            senderOrMerchant = debtor.name
        )

        val msg = "🎉 Recovered ${CurrencyFormatter.formatNaira(payableAmount)} from ${debtor.name}! Street credit restored."
        _routineActionToast.value = msg
        return true to msg
    }

    fun sendDebtSmsReminder(debtorId: String): String {
        val current = _gameState.value ?: return "Error"
        val debtor = current.customerDebtors.find { it.id == debtorId } ?: return "Debtor not found"
        val msg = "📱 SMS Sent to ${debtor.phoneContact}: 'Peace oga ${debtor.name}, humble reminder regarding your balance of ${CurrencyFormatter.formatNaira(debtor.amountOwed)}.'"
        _routineActionToast.value = msg
        return msg
    }

    fun forgiveDebtorDebt(debtorId: String): String {
        val current = _gameState.value ?: return "Error"
        val debtor = current.customerDebtors.find { it.id == debtorId } ?: return "Debtor not found"
        val updatedDebtors = current.customerDebtors.filterNot { it.id == debtorId }
        _gameState.value = current.copy(
            customerDebtors = updatedDebtors,
            reputation = min(100, current.reputation + 4)
        )
        val msg = "🕊️ Debt forgiven for ${debtor.name}. Earned +4 Street Goodwill & Community Blessings!"
        _routineActionToast.value = msg
        return msg
    }

    // --- REALISTIC POWER GRID & GENERATOR MAINTENANCE ---

    fun toggleGeneratorPower(): String {
        val current = _gameState.value ?: return "Error"
        val gen = current.generatorState
        if (!gen.isGeneratorRunning && gen.fuelLitres <= 0) {
            val err = "⚠️ No fuel in generator tank! Refuel at the filling station first."
            _routineActionToast.value = err
            return err
        }
        val newState = !gen.isGeneratorRunning
        _gameState.value = current.copy(
            generatorState = gen.copy(isGeneratorRunning = newState)
        )
        val msg = if (newState) "⚡ Generator switched ON! Shop appliances powered." else "🔌 Generator turned OFF to conserve fuel."
        _routineActionToast.value = msg
        return msg
    }

    fun refuelGenerator(litres: Int = 10): Pair<Boolean, String> {
        val current = _gameState.value ?: return false to "Error"
        val fuelPricePerLitre = 750L // ₦750/litre realistic Nigerian PMS fuel
        val totalCost = litres * fuelPricePerLitre
        if (current.businessCash < totalCost) {
            return false to "Insufficient business cash! Need ${CurrencyFormatter.formatNaira(totalCost)} for $litres L fuel."
        }
        val gen = current.generatorState
        val newFuel = gen.fuelLitres + litres
        val newCash = current.businessCash - totalCost
        _gameState.value = current.copy(
            businessCash = newCash,
            generatorState = gen.copy(fuelLitres = newFuel)
        )
        val msg = "⛽ Purchased $litres L fuel at ₦$fuelPricePerLitre/L (-${CurrencyFormatter.formatNaira(totalCost)}). Tank now has ${newFuel}L."
        _routineActionToast.value = msg
        return true to msg
    }

    fun restartGame() {
        val current = _gameState.value
        val defaultBusiness = current?.business ?: BusinessData.startingBusinesses.first()
        val defaultChar = current?.character ?: BusinessData.characters.first()
        _gameState.value = GameEngine.createInitialState(
            characterName = current?.characterName ?: defaultChar.name,
            business = defaultBusiness,
            city = current?.cityName ?: "Lagos",
            character = defaultChar
        )
        _activeEvent.value = null
        _chosenChoice.value = null
    }
}
