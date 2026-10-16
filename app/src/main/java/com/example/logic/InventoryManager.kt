package com.example.logic

import com.example.model.BusinessType
import com.example.model.ClearanceResult
import com.example.model.CurrencyFormatter
import com.example.model.DemandEstimate
import com.example.model.GameState
import com.example.model.RestockQuote
import com.example.model.RestockRecommendation
import com.example.model.RestockResult
import com.example.model.SpoilageQuote
import com.example.model.WholesaleTier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Core Game Logic Module: Inventory Updates & Wholesale Purchasing
 *
 * Handles:
 * - Tiered wholesale volume discounts
 * - Storage capacity limits & expansion
 * - Cost of Goods Sold (COGS) weighted average calculations
 * - Demand forecasting and optimal reorder recommendations
 * - Perishable spoilage risk analysis
 * - Fire sale stock liquidation
 */
object InventoryManager {

    val WHOLESALE_TIERS = listOf(
        WholesaleTier(minUnits = 200, discountPercent = 0.15f, tierName = "Direct Importer Container (-15%)"),
        WholesaleTier(minUnits = 100, discountPercent = 0.10f, tierName = "Wholesale Master Crate (-10%)"),
        WholesaleTier(minUnits = 50, discountPercent = 0.05f, tierName = "Distributor Bulk Carton (-5%)"),
        WholesaleTier(minUnits = 1, discountPercent = 0.00f, tierName = "Standard Street Batch")
    )

    /**
     * Calculates total storage capacity based on business type, staff, and physical upgrades.
     */
    fun calculateMaxStorageCapacity(state: GameState): Int {
        val baseCapacity = when (state.business.type) {
            BusinessType.FOOD_VENDOR -> 140
            BusinessType.PROVISIONS -> 300
            BusinessType.PHONE_ACCESSORIES -> 450
            BusinessType.BARBERSHOP -> 120
            BusinessType.POS_AGENT -> 100
        }

        // Staff adds handling and storage organization capacity
        val staffBonus = state.staffCount * 40

        // Delivery bike or storage upgrades expand effective capacity
        val hasBike = state.upgrades.any { it.id == "delivery_bike" && it.isPurchased }
        val hasFreezer = state.upgrades.any { it.id == "deep_freezer" && it.isPurchased }

        val upgradeBonus = (if (hasBike) 60 else 0) + (if (hasFreezer) 50 else 0)

        return baseCapacity + staffBonus + upgradeBonus
    }

    /**
     * Determines wholesale volume discount based on order quantity.
     */
    fun getWholesaleTier(units: Int): WholesaleTier {
        return WHOLESALE_TIERS.firstOrNull { units >= it.minUnits } ?: WHOLESALE_TIERS.last()
    }

    /**
     * Generates a detailed restock quote including discounts, affordability, and storage limits.
     */
    fun calculateRestockQuote(state: GameState, units: Int): RestockQuote {
        val safeUnits = max(0, units)
        val tier = getWholesaleTier(safeUnits)
        
        // Base discounted unit cost with character perk
        val characterPerkDiscount = state.character.costDiscountPercent / 100f
        val wholesaleDiscount = tier.discountPercent
        val totalDiscountPercent = (characterPerkDiscount + wholesaleDiscount).coerceAtMost(0.40f)

        val baseUnitCost = state.business.baseUnitCost
        val discountedUnitCost = max(50L, (baseUnitCost * (1f + state.inflationRate) * (1f - totalDiscountPercent)).toLong())
        val totalCost = safeUnits.toLong() * discountedUnitCost

        val maxCapacity = calculateMaxStorageCapacity(state)
        val resultingStock = state.inventoryUnits + safeUnits
        val fitsInStorage = resultingStock <= maxCapacity

        val isAffordable = state.businessCash >= totalCost
        val maxAffordableUnits = if (discountedUnitCost > 0) {
            (max(0L, state.businessCash) / discountedUnitCost).toInt()
        } else 0

        return RestockQuote(
            units = safeUnits,
            baseUnitCost = baseUnitCost,
            discountedUnitCost = discountedUnitCost,
            discountPercent = totalDiscountPercent * 100f,
            tierName = tier.tierName,
            totalCost = totalCost,
            currentStock = state.inventoryUnits,
            resultingStock = resultingStock,
            maxStorageCapacity = maxCapacity,
            fitsInStorage = fitsInStorage,
            isAffordable = isAffordable,
            maxAffordableUnits = maxAffordableUnits
        )
    }

    /**
     * Executes inventory restocking, applies wholesale discounts, and deducts business cash.
     */
    fun restock(state: GameState, units: Int): RestockResult {
        if (units <= 0) {
            return RestockResult(
                success = false,
                updatedState = state,
                unitsAdded = 0,
                totalCost = 0L,
                unitCostPaid = state.costPerUnit,
                message = "Please specify a positive unit quantity to restock."
            )
        }

        val quote = calculateRestockQuote(state, units)

        if (!quote.isAffordable) {
            return RestockResult(
                success = false,
                updatedState = state,
                unitsAdded = 0,
                totalCost = 0L,
                unitCostPaid = state.costPerUnit,
                message = "Insufficient business cash! Needed: ${CurrencyFormatter.formatNaira(quote.totalCost)}, Available: ${CurrencyFormatter.formatNaira(state.businessCash)}."
            )
        }

        if (!quote.fitsInStorage) {
            return RestockResult(
                success = false,
                updatedState = state,
                unitsAdded = 0,
                totalCost = 0L,
                unitCostPaid = state.costPerUnit,
                message = "Storage exceeded! Resulting stock (${quote.resultingStock}) exceeds max capacity (${quote.maxStorageCapacity})."
            )
        }

        // Weighted Average Unit Cost
        val previousTotalStockCost = state.inventoryUnits.toLong() * state.costPerUnit
        val newStockTotalCost = quote.totalCost
        val totalCombinedUnits = state.inventoryUnits + units
        val weightedAverageUnitCost = if (totalCombinedUnits > 0) {
            (previousTotalStockCost + newStockTotalCost) / totalCombinedUnits
        } else quote.discountedUnitCost

        val updatedState = state.copy(
            businessCash = state.businessCash - quote.totalCost,
            inventoryUnits = totalCombinedUnits,
            costPerUnit = weightedAverageUnitCost
        )

        val successMsg = "Successfully bought $units units of ${state.business.itemUnitName} (${quote.tierName}) for ${CurrencyFormatter.formatNaira(quote.totalCost)}."

        return RestockResult(
            success = true,
            updatedState = updatedState,
            unitsAdded = units,
            totalCost = quote.totalCost,
            unitCostPaid = quote.discountedUnitCost,
            message = successMsg
        )
    }

    /**
     * Estimates demand based on current pricing, reputation, ad campaigns, and market factors.
     */
    fun estimateWeeklyDemand(state: GameState): DemandEstimate {
        var multiplier = 1.0f

        // Price elasticity
        val basePrice = state.business.baseSellingPrice.toFloat()
        val priceRatio = state.sellingPrice.toFloat() / max(1f, basePrice)
        val priceElasticityNote = when {
            priceRatio < 0.85f -> "Very Competitive (High Sales Volume, Lower Margins)"
            priceRatio <= 1.15f -> "Balanced Market Price (Optimal Sales & Margins)"
            priceRatio <= 1.45f -> "Premium Street Pricing (Lower Volume, High Margins)"
            else -> "Extremely Expensive (Severely Depressed Volume)"
        }

        if (priceRatio < 1.0f) {
            multiplier += (1.0f - priceRatio) * 0.8f
        } else if (priceRatio > 1.0f) {
            multiplier -= (priceRatio - 1.0f) * 0.9f
        }

        // Reputation
        val reputationEffectPercent = ((state.reputation - 50) * 0.5f).roundToInt()
        multiplier += (reputationEffectPercent / 100f)

        // Character Perk
        multiplier += (state.character.demandBoostPercent / 100f)

        // Active Ad Campaigns
        val activeAdBoost = state.activeAds.filter { it.isActive && it.weeksRemaining > 0 }.sumOf { it.demandBoostPercent }
        multiplier += (activeAdBoost / 100f)

        // Marketing budget
        if (state.marketingBudgetWeekly > 0) {
            val marketingRatio = min(1.0f, state.marketingBudgetWeekly / 30_000f)
            multiplier += marketingRatio * 0.35f
        }

        // Upgrades
        if (state.upgrades.any { it.id == "neon_sign" && it.isPurchased }) multiplier += 0.20f
        if (state.upgrades.any { it.id == "delivery_bike" && it.isPurchased }) multiplier += 0.30f
        if (state.upgrades.any { it.id == "pos_terminal" && it.isPurchased }) multiplier += 0.15f

        val baseDemand = state.business.baseWeeklyDemand.toFloat()
        val expected = (baseDemand * max(0.2f, multiplier)).toInt()
        val minDemand = (expected * 0.85f).toInt()
        val maxDemand = (expected * 1.15f).toInt()

        val staffMaxCapacity = 50 + (state.staffCount * 35)
        val cappedExpected = min(expected, staffMaxCapacity)

        return DemandEstimate(
            minDemand = min(minDemand, staffMaxCapacity),
            expectedDemand = cappedExpected,
            maxDemand = min(maxDemand, staffMaxCapacity),
            priceElasticityNote = priceElasticityNote,
            activeAdBoostPercent = activeAdBoost,
            reputationEffectPercent = reputationEffectPercent
        )
    }

    /**
     * Recommends optimal reorder quantity to avoid stockouts while minimizing dead inventory.
     */
    fun calculateRecommendedRestock(state: GameState): RestockRecommendation {
        val demandEstimate = estimateWeeklyDemand(state)
        val projectedDemand = demandEstimate.expectedDemand
        val safetyBuffer = (projectedDemand * 0.20f).roundToInt()
        val targetStock = projectedDemand + safetyBuffer

        val neededUnits = max(0, targetStock - state.inventoryUnits)
        val quote = calculateRestockQuote(state, neededUnits)

        val rationale = if (neededUnits == 0) {
            "Current stock (${state.inventoryUnits}) is sufficient to cover projected demand of ~$projectedDemand units."
        } else {
            "Restocking $neededUnits units will cover ~$projectedDemand expected sales plus a $safetyBuffer unit buffer against stockouts."
        }

        return RestockRecommendation(
            currentStock = state.inventoryUnits,
            projectedDemand = projectedDemand,
            recommendedRestockUnits = neededUnits,
            safetyStockBuffer = safetyBuffer,
            estimatedCost = quote.totalCost,
            rationale = rationale
        )
    }

    /**
     * Calculates perishable spoilage risk (particularly for Food Vendors).
     */
    fun calculateSpoilage(state: GameState): SpoilageQuote {
        val isPerishable = state.business.type == BusinessType.FOOD_VENDOR
        val hasFreezer = state.upgrades.any { it.id == "deep_freezer" && it.isPurchased }

        if (!isPerishable || hasFreezer || state.inventoryUnits == 0) {
            return SpoilageQuote(
                perishableUnitsAtRisk = 0,
                estimatedSpoilageUnits = 0,
                potentialFinancialLoss = 0L,
                hasRefrigeration = hasFreezer,
                riskReductionTip = if (hasFreezer) "Deep freezer equipped! Spoilage is 0%." else "Non-perishable inventory."
            )
        }

        val spoilageRate = state.business.spoilageRate
        val estSpoiled = (state.inventoryUnits * spoilageRate).toInt()
        val loss = estSpoiled.toLong() * state.costPerUnit

        return SpoilageQuote(
            perishableUnitsAtRisk = state.inventoryUnits,
            estimatedSpoilageUnits = estSpoiled,
            potentialFinancialLoss = loss,
            hasRefrigeration = false,
            riskReductionTip = "Unsold food spoils quickly in Lagos heat! Purchase the Deep Freezer upgrade to eliminate spoilage."
        )
    }

    /**
     * Liquidates slow-moving inventory at a discount for emergency cash.
     */
    fun liquidateInventory(
        state: GameState,
        units: Int,
        discountPercent: Float = 0.30f
    ): ClearanceResult {
        if (units <= 0 || state.inventoryUnits <= 0) {
            return ClearanceResult(
                success = false,
                updatedState = state,
                unitsLiquidated = 0,
                cashRecovered = 0L,
                message = "No stock available to liquidate."
            )
        }

        val unitsToClear = min(units, state.inventoryUnits)
        val clearancePrice = (state.sellingPrice * (1f - discountPercent)).toLong()
        val recoveredCash = unitsToClear.toLong() * clearancePrice

        val updatedState = state.copy(
            inventoryUnits = state.inventoryUnits - unitsToClear,
            businessCash = state.businessCash + recoveredCash
        )

        return ClearanceResult(
            success = true,
            updatedState = updatedState,
            unitsLiquidated = unitsToClear,
            cashRecovered = recoveredCash,
            message = "Cleared $unitsToClear units at -${(discountPercent * 100).toInt()}% discount, recovering ${CurrencyFormatter.formatNaira(recoveredCash)}."
        )
    }
}
