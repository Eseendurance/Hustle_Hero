package com.example.logic

import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.TransferValidation
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransfer
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferResult
import com.example.model.WalletTransferType
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

/**
 * Core Game Logic Module: Dual-Wallet Transfer System
 *
 * Enforces strict financial separation between:
 * 1. Business Wallet (working capital, stock replenishment, staff wages, equipment, loans)
 * 2. Personal Wallet (household rent, food, transport, family support, emergency savings, Ajo)
 */
object WalletTransferManager {

    /**
     * Minimum working capital buffer recommended for a business (1 week of baseline expenses).
     */
    fun calculateRecommendedWorkingCapitalBuffer(state: GameState): Long {
        val staffPayroll = state.staffCount * state.staffWageWeekly
        val upgradeMaintenance = state.upgrades.filter { it.isPurchased }.sumOf { it.weeklyMaintenance }
        val baselineStockCost = (state.business.baseWeeklyDemand / 2L) * state.costPerUnit
        return max(50_000L, staffPayroll + upgradeMaintenance + baselineStockCost)
    }

    /**
     * Validates if a proposed wallet transfer is allowed and safe.
     */
    fun validateTransfer(
        state: GameState,
        type: WalletTransferType,
        amount: Long,
        source: WalletSource = WalletSource.BUSINESS_CASH
    ): TransferValidation {
        if (amount <= 0) {
            return TransferValidation(
                isValid = false,
                errorMessage = "Transfer amount must be greater than ₦0",
                maxTransferable = 0L
            )
        }

        val availableFunds = when (source) {
            WalletSource.BUSINESS_CASH -> max(0L, state.businessCash)
            WalletSource.PERSONAL_CASH -> max(0L, state.personalCash)
            WalletSource.PERSONAL_SAVINGS -> max(0L, state.personalSavings)
        }

        if (amount > availableFunds) {
            return TransferValidation(
                isValid = false,
                errorMessage = "Insufficient funds in ${source.name.lowercase().replace('_', ' ')}. Available: ${CurrencyFormatter.formatNaira(availableFunds)}",
                maxTransferable = availableFunds
            )
        }

        // Business to Personal checks
        if (source == WalletSource.BUSINESS_CASH) {
            val buffer = calculateRecommendedWorkingCapitalBuffer(state)
            val cashAfter = state.businessCash - amount
            val warning = if (cashAfter < buffer) {
                "Caution: This withdrawal leaves ₦${cashAfter} in business cash, which is below the safe working capital buffer (${CurrencyFormatter.formatNaira(buffer)}). You may struggle to restock!"
            } else null

            return TransferValidation(
                isValid = true,
                warningMessage = warning,
                maxTransferable = availableFunds
            )
        }

        return TransferValidation(
            isValid = true,
            warningMessage = null,
            maxTransferable = availableFunds
        )
    }

    /**
     * Executes a wallet transfer between business and personal accounts.
     */
    fun executeTransfer(
        state: GameState,
        request: WalletTransferRequest
    ): WalletTransferResult {
        val validation = validateTransfer(
            state = state,
            type = request.type,
            amount = request.amount,
            source = request.source
        )

        if (!validation.isValid) {
            return WalletTransferResult(
                success = false,
                updatedState = state,
                transferRecord = null,
                message = validation.errorMessage ?: "Transfer rejected"
            )
        }

        var newBizCash = state.businessCash
        var newPersCash = state.personalCash
        var newSavings = state.personalSavings
        var newStress = state.stress
        var fee = 0L

        when (request.type) {
            WalletTransferType.SALARY_DRAW -> {
                // Business Cash -> Personal Cash
                newBizCash -= request.amount
                newPersCash += request.amount
                // Drawing regular salary relieves entrepreneurial anxiety
                newStress = max(0, newStress - 2)
            }
            WalletTransferType.OWNER_DIVIDEND -> {
                // Business Cash -> Personal Cash with 2% cooperative union handling fee
                fee = (request.amount * 0.02f).toLong()
                val net = request.amount - fee
                newBizCash -= request.amount
                newPersCash += net
                newStress = max(0, newStress - 3)
            }
            WalletTransferType.CAPITAL_INJECTION -> {
                // Personal Cash or Savings -> Business Cash
                if (request.source == WalletSource.PERSONAL_SAVINGS) {
                    newSavings -= request.amount
                } else {
                    newPersCash -= request.amount
                }
                newBizCash += request.amount
            }
            WalletTransferType.EMERGENCY_BAILOUT -> {
                // Personal Savings -> Business Cash
                newSavings -= request.amount
                newBizCash += request.amount
                // Emergency bailout adds mild stress
                newStress = min(100, newStress + 4)
            }
        }

        val record = WalletTransfer(
            id = "tx_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
            week = state.week,
            timestamp = System.currentTimeMillis(),
            type = request.type,
            amount = request.amount,
            fee = fee,
            netAmount = request.amount - fee,
            source = request.source,
            destination = request.destination,
            reason = request.reason.ifBlank { request.type.description },
            businessCashAfter = newBizCash,
            personalCashAfter = newPersCash
        )

        val updatedState = state.copy(
            businessCash = newBizCash,
            personalCash = newPersCash,
            personalSavings = newSavings,
            stress = newStress,
            walletTransfers = listOf(record) + state.walletTransfers
        )

        val successMessage = "${request.type.title}: Transferred ${CurrencyFormatter.formatNaira(request.amount)} from ${request.source.name.lowercase().replace('_', ' ')} to ${request.destination.name.lowercase().replace('_', ' ')}."

        return WalletTransferResult(
            success = true,
            updatedState = updatedState,
            transferRecord = record,
            message = successMessage
        )
    }

    /**
     * Calculates recommended salary withdrawal for the current turn based on personal living
     * costs and expected business profitability.
     */
    fun getRecommendedSalary(state: GameState): Long {
        val weeklyLivingCost = state.personalExpenses.calculateTotal(state.inflationRate)
        val availableBiz = max(0L, state.businessCash)
        val workingCapitalBuffer = calculateRecommendedWorkingCapitalBuffer(state)
        val surplus = max(0L, availableBiz - workingCapitalBuffer)

        return when {
            surplus >= weeklyLivingCost -> weeklyLivingCost
            surplus > 0 -> surplus
            availableBiz > weeklyLivingCost -> weeklyLivingCost / 2
            else -> min(availableBiz, 5_000L)
        }
    }

    /**
     * Calculates how many weeks the business can survive with zero sales at current OpEx.
     */
    fun calculateBusinessSolvencyWeeks(state: GameState): Float {
        val staffPayroll = state.staffCount * state.staffWageWeekly
        val upgradeMaintenance = state.upgrades.filter { it.isPurchased }.sumOf { it.weeklyMaintenance }
        val weeklyOpEx = staffPayroll + upgradeMaintenance + state.marketingBudgetWeekly

        if (weeklyOpEx <= 0L) return 99f
        if (state.businessCash <= 0L) return 0f
        return state.businessCash.toFloat() / weeklyOpEx
    }

    /**
     * Calculates how many weeks personal household can survive at current burn rate.
     */
    fun calculatePersonalRunwayWeeks(state: GameState): Float {
        val livingCost = state.personalExpenses.calculateTotal(state.inflationRate)
        if (livingCost <= 0L) return 99f
        val totalPersonalLiquidity = max(0L, state.personalCash) + max(0L, state.personalSavings)
        return totalPersonalLiquidity.toFloat() / livingCost
    }
}
