package com.example.logic

import com.example.model.AjoContributionRecord
import com.example.model.AjoContributionResult
import com.example.model.AjoContributionValidation
import com.example.model.AjoSavingGoal
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransfer
import com.example.model.WalletTransferType
import kotlin.math.max
import kotlin.math.min

/**
 * AjoSavingsManager: Core Responsive Backend for Ajo / Esusu Targeted Savings Goals.
 *
 * Handles:
 * - Real-time validation of contribution amounts against wallet balances
 * - Multi-wallet sourcing (Personal Cash, Emergency Savings, Business Capital)
 * - Goal progress updates and automatic completion triggers
 * - Unlocking perks and achievements upon goal completion
 * - Immutable transaction audit history and dual-wallet audit records
 */
object AjoSavingsManager {

    /**
     * Validates whether a contribution can be executed from the specified wallet balance.
     */
    fun validateContribution(
        state: GameState,
        goalId: String,
        amount: Long,
        source: WalletSource
    ): AjoContributionValidation {
        val goal = state.ajoGoals.find { it.id == goalId }
            ?: return AjoContributionValidation(
                isValid = false,
                errorMessage = "Selected Ajo saving goal was not found."
            )

        if (amount <= 0L) {
            return AjoContributionValidation(
                isValid = false,
                errorMessage = "Contribution amount must be greater than ₦0."
            )
        }

        val availableBalance = getWalletBalance(state, source)

        if (amount > availableBalance) {
            val walletName = getWalletName(source)
            return AjoContributionValidation(
                isValid = false,
                errorMessage = "Insufficient $walletName! You have ${CurrencyFormatter.formatNaira(availableBalance)} available, but entered ${CurrencyFormatter.formatNaira(amount)}.",
                maxAffordable = availableBalance
            )
        }

        val warning = if (goal.isCompleted) {
            "This goal was already completed in Week ${goal.completedWeek ?: state.week}. Deposit will add to bonus reserve."
        } else null

        return AjoContributionValidation(
            isValid = true,
            warningMessage = warning,
            maxAffordable = availableBalance
        )
    }

    /**
     * Executes the contribution, updates the goal, deducts the chosen wallet,
     * logs transaction history, and unlocks perks if completed.
     */
    fun executeContribution(
        state: GameState,
        goalId: String,
        amount: Long,
        source: WalletSource,
        note: String = ""
    ): Pair<GameState, AjoContributionResult> {
        val validation = validateContribution(state, goalId, amount, source)
        if (!validation.isValid) {
            val failedResult = AjoContributionResult(
                success = false,
                message = validation.errorMessage ?: "Validation failed.",
                goal = state.ajoGoals.find { it.id == goalId },
                amountContributed = 0L,
                remainingBalance = getWalletBalance(state, source),
                isGoalCompletedNow = false
            )
            return Pair(state, failedResult)
        }

        val goal = state.ajoGoals.find { it.id == goalId }!!
        val newGoalAmount = goal.currentAmount + amount
        val isCompletedNow = !goal.isCompleted && newGoalAmount >= goal.targetAmount

        val updatedGoal = goal.copy(
            currentAmount = newGoalAmount,
            isCompleted = goal.isCompleted || isCompletedNow,
            completedWeek = if (isCompletedNow) state.week else goal.completedWeek
        )

        // Deduct from specified balance
        var newPersonalCash = state.personalCash
        var newPersonalSavings = state.personalSavings
        var newBusinessCash = state.businessCash

        when (source) {
            WalletSource.PERSONAL_CASH -> {
                newPersonalCash = max(0L, state.personalCash - amount)
            }
            WalletSource.PERSONAL_SAVINGS -> {
                newPersonalSavings = max(0L, state.personalSavings - amount)
            }
            WalletSource.BUSINESS_CASH -> {
                newBusinessCash = max(0L, state.businessCash - amount)
            }
        }

        val remainingSourceBalance = when (source) {
            WalletSource.PERSONAL_CASH -> newPersonalCash
            WalletSource.PERSONAL_SAVINGS -> newPersonalSavings
            WalletSource.BUSINESS_CASH -> newBusinessCash
        }

        // Record Contribution History
        val contributionRecord = AjoContributionRecord(
            id = "ajo_tx_${System.currentTimeMillis()}_${(1000..9999).random()}",
            goalId = goal.id,
            goalTitle = goal.title,
            amount = amount,
            sourceWallet = source,
            week = state.week,
            timestamp = System.currentTimeMillis(),
            balanceAfter = remainingSourceBalance,
            note = if (note.isNotBlank()) note else "Deposit into ${goal.title}"
        )

        // If contributed from Business Cash, also create a dual-wallet transfer record
        val additionalTransfers = if (source == WalletSource.BUSINESS_CASH) {
            val transferRecord = WalletTransfer(
                id = "tx_ajo_biz_${System.currentTimeMillis()}",
                week = state.week,
                timestamp = System.currentTimeMillis(),
                type = WalletTransferType.CAPITAL_INJECTION,
                amount = amount,
                fee = 0L,
                netAmount = amount,
                source = WalletSource.BUSINESS_CASH,
                destination = WalletDestination.PERSONAL_SAVINGS,
                reason = "Ajo Goal Deposit: ${goal.title}",
                businessCashAfter = newBusinessCash,
                personalCashAfter = newPersonalCash
            )
            state.walletTransfers + transferRecord
        } else {
            state.walletTransfers
        }

        // Apply completion perks & morale bonuses
        var newReputation = state.reputation
        var newStress = state.stress
        var newCreditScore = state.creditScore
        var perkMessage: String? = null

        if (isCompletedNow) {
            newReputation = min(100, state.reputation + 6)
            newStress = max(0, state.stress - 10)
            newCreditScore = min(850, state.creditScore + 15)
            perkMessage = "🎯 GOAL REACHED! Unlocked: ${goal.perkReward} (+6 Reputation, -10 Stress)"
        }

        val updatedGoalsList = state.ajoGoals.map {
            if (it.id == goalId) updatedGoal else it
        }

        val nextState = state.copy(
            personalCash = newPersonalCash,
            personalSavings = newPersonalSavings,
            businessCash = newBusinessCash,
            ajoGoals = updatedGoalsList,
            selectedAjoGoalId = goalId,
            ajoContributionHistory = state.ajoContributionHistory + contributionRecord,
            walletTransfers = additionalTransfers,
            reputation = newReputation,
            stress = newStress,
            creditScore = newCreditScore
        )

        val successMessage = if (isCompletedNow) {
            "🎉 Target Achieved! Successfully contributed ${CurrencyFormatter.formatNaira(amount)} to complete '${goal.title}'!"
        } else {
            "✅ Deposited ${CurrencyFormatter.formatNaira(amount)} into '${goal.title}'. Progress: ${((updatedGoal.progressPercent) * 100).toInt()}%."
        }

        val result = AjoContributionResult(
            success = true,
            message = successMessage,
            goal = updatedGoal,
            amountContributed = amount,
            remainingBalance = remainingSourceBalance,
            isGoalCompletedNow = isCompletedNow,
            perkUnlocked = perkMessage
        )

        return Pair(nextState, result)
    }

    /**
     * Creates a new user-defined custom Ajo saving goal.
     */
    fun createGoal(
        state: GameState,
        title: String,
        description: String,
        category: String,
        iconEmoji: String,
        targetAmount: Long,
        weeklyTarget: Long = 10_000L,
        perkReward: String = "Custom SME Growth Milestone",
        deadlineWeek: Int = state.week + 8
    ): Pair<GameState, AjoSavingGoal> {
        val newGoal = AjoSavingGoal(
            id = "goal_custom_${System.currentTimeMillis()}",
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifBlank { "Personal" },
            iconEmoji = iconEmoji.ifBlank { "🎯" },
            targetAmount = max(1_000L, targetAmount),
            currentAmount = 0L,
            recommendedWeekly = max(1_000L, weeklyTarget),
            isCompleted = false,
            perkReward = perkReward.ifBlank { "Unlocks +5 Business Reputation" },
            createdWeek = state.week,
            deadlineWeek = max(state.week + 1, deadlineWeek)
        )

        val nextState = state.copy(
            ajoGoals = state.ajoGoals + newGoal,
            selectedAjoGoalId = newGoal.id
        )

        return Pair(nextState, newGoal)
    }

    /**
     * Updates target deadline week for an active savings goal.
     */
    fun updateGoalDeadline(state: GameState, goalId: String, newDeadlineWeek: Int): GameState {
        val updatedGoals = state.ajoGoals.map { goal ->
            if (goal.id == goalId) {
                goal.copy(deadlineWeek = max(state.week + 1, newDeadlineWeek))
            } else goal
        }
        return state.copy(ajoGoals = updatedGoals)
    }

    /**
     * Updates the currently selected goal in the active GameState.
     */
    fun selectGoal(state: GameState, goalId: String): GameState {
        val exists = state.ajoGoals.any { it.id == goalId }
        return if (exists) {
            state.copy(selectedAjoGoalId = goalId)
        } else {
            state
        }
    }

    /**
     * Retrieves current balance from the selected wallet.
     */
    fun getWalletBalance(state: GameState, source: WalletSource): Long {
        return when (source) {
            WalletSource.PERSONAL_CASH -> state.personalCash
            WalletSource.PERSONAL_SAVINGS -> state.personalSavings
            WalletSource.BUSINESS_CASH -> state.businessCash
        }
    }

    /**
     * Human-readable label for wallet sources.
     */
    fun getWalletName(source: WalletSource): String {
        return when (source) {
            WalletSource.PERSONAL_CASH -> "Personal Cash"
            WalletSource.PERSONAL_SAVINGS -> "Emergency Savings"
            WalletSource.BUSINESS_CASH -> "Business Capital"
        }
    }

    /**
     * Aggregated summary of all player Ajo goals.
     */
    data class AjoGoalsSummary(
        val totalGoals: Int,
        val completedGoals: Int,
        val totalTarget: Long,
        val totalSaved: Long,
        val averageProgressPercent: Float
    )

    fun getGoalsSummary(state: GameState): AjoGoalsSummary {
        val goals = state.ajoGoals
        if (goals.isEmpty()) {
            return AjoGoalsSummary(0, 0, 0L, 0L, 0f)
        }
        val totalTarget = goals.sumOf { it.targetAmount }
        val totalSaved = goals.sumOf { it.currentAmount }
        val completed = goals.count { it.isCompleted }
        val avgProgress = if (totalTarget > 0) (totalSaved.toFloat() / totalTarget.toFloat()).coerceIn(0f, 1f) else 0f

        return AjoGoalsSummary(
            totalGoals = goals.size,
            completedGoals = completed,
            totalTarget = totalTarget,
            totalSaved = totalSaved,
            averageProgressPercent = avgProgress
        )
    }
}
