package com.example.logic

import com.example.data.BusinessData
import com.example.model.Business
import com.example.model.CharacterProfile
import com.example.model.ClearanceResult
import com.example.model.DemandEstimate
import com.example.model.EventChoice
import com.example.model.GameEvent
import com.example.model.GameState
import com.example.model.RestockQuote
import com.example.model.RestockRecommendation
import com.example.model.RestockResult
import com.example.model.SpoilageQuote
import com.example.model.TurnSimulationResult
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferResult

/**
 * GameEngine: Unified Core Game Logic Module
 *
 * Coordinates:
 * - [TurnCycleManager]: Weekly turn cycle execution, state machine, and simulation
 * - [InventoryManager]: Inventory updates, wholesale pricing tiers, spoilage, and stock replenishment
 * - [WalletTransferManager]: Dual-wallet transfer system (salary draw, dividend, capital injection, bailout)
 */
object GameEngine {

    val turnCycle: TurnCycleManager = TurnCycleManager
    val inventory: InventoryManager = InventoryManager
    val walletTransfers: WalletTransferManager = WalletTransferManager
    val ajoSavings: AjoSavingsManager = AjoSavingsManager

    // --- TURN CYCLE METHODS ---

    fun simulateWeeklyTurn(
        state: GameState,
        chosenEvent: GameEvent?,
        choice: EventChoice?,
        salaryWithdrawal: Long
    ): GameState {
        return TurnCycleManager.simulateWeeklyTurn(
            state = state,
            chosenEvent = chosenEvent,
            choice = choice,
            salaryWithdrawal = salaryWithdrawal
        ).state
    }

    fun simulateWeeklyTurnWithTransfers(
        state: GameState,
        chosenEvent: GameEvent?,
        choice: EventChoice?,
        salaryWithdrawal: Long,
        additionalTransfers: List<WalletTransferRequest> = emptyList()
    ): TurnSimulationResult {
        return TurnCycleManager.simulateWeeklyTurn(
            state = state,
            chosenEvent = chosenEvent,
            choice = choice,
            salaryWithdrawal = salaryWithdrawal,
            additionalTransfers = additionalTransfers
        )
    }

    fun createInitialState(
        characterName: String,
        business: Business,
        city: String = "Lagos",
        character: CharacterProfile = BusinessData.characters.first()
    ): GameState {
        return TurnCycleManager.createInitialState(
            characterName = characterName,
            business = business,
            city = city,
            character = character
        )
    }

    // --- INVENTORY MANAGEMENT METHODS ---

    fun restockInventory(state: GameState, units: Int): RestockResult {
        return InventoryManager.restock(state, units)
    }

    fun calculateRestockQuote(state: GameState, units: Int): RestockQuote {
        return InventoryManager.calculateRestockQuote(state, units)
    }

    fun getRecommendedRestock(state: GameState): RestockRecommendation {
        return InventoryManager.calculateRecommendedRestock(state)
    }

    fun estimateWeeklyDemand(state: GameState): DemandEstimate {
        return InventoryManager.estimateWeeklyDemand(state)
    }

    fun calculateSpoilage(state: GameState): SpoilageQuote {
        return InventoryManager.calculateSpoilage(state)
    }

    fun liquidateInventory(state: GameState, units: Int, discountPercent: Float = 0.30f): ClearanceResult {
        return InventoryManager.liquidateInventory(state, units, discountPercent)
    }

    // --- DUAL-WALLET TRANSFER METHODS ---

    fun executeWalletTransfer(state: GameState, request: WalletTransferRequest): WalletTransferResult {
        return WalletTransferManager.executeTransfer(state, request)
    }

    fun getRecommendedSalary(state: GameState): Long {
        return WalletTransferManager.getRecommendedSalary(state)
    }

    fun calculateBusinessSolvencyWeeks(state: GameState): Float {
        return WalletTransferManager.calculateBusinessSolvencyWeeks(state)
    }

    fun calculatePersonalRunwayWeeks(state: GameState): Float {
        return WalletTransferManager.calculatePersonalRunwayWeeks(state)
    }
}
