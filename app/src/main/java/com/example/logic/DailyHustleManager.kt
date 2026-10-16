package com.example.logic

import com.example.data.DailyLifeAndMealData
import com.example.model.Business
import com.example.model.DailyHustleTask
import com.example.model.GameState
import com.example.model.MissionType
import kotlin.math.max
import kotlin.math.min

object DailyHustleManager {

    fun generateDailyTasks(day: Int, business: Business? = null): List<DailyHustleTask> {
        val bizUnit = business?.itemUnitName ?: "Yam Tubers"
        val bizName = business?.name ?: "Yam & Produce Depo"
        val bizEmoji = business?.iconEmoji ?: "🥔"

        // 1. Business Sales Mission (like "Sell 5 units of Yam Tubers")
        val salesTask = DailyHustleTask(
            id = "mission_sell_day_${day}",
            title = "Sell 5 units of $bizUnit",
            description = "Attend to customers at $bizName and sell at least 5 units of $bizUnit today.",
            category = "Shop Target",
            iconEmoji = bizEmoji,
            rewardCash = 35_000L,
            reputationReward = 5,
            stressCost = 1,
            energyCost = 10,
            difficulty = "Easy",
            clientName = "Market Customers",
            location = "Main Shop Counter",
            currentProgress = 0,
            targetQuantity = 5,
            unitLabel = bizUnit,
            missionType = MissionType.SELL_ITEM,
            isCompleted = false
        )

        // 2. Supply Chain Restock Mission
        val restockTask = DailyHustleTask(
            id = "mission_restock_day_${day}",
            title = "Wholesale Restock Run",
            description = "Replenish shop inventory by restocking at least 10 units with wholesale volume discount.",
            category = "Supply Chain",
            iconEmoji = "📦",
            rewardCash = 28_000L,
            reputationReward = 4,
            stressCost = 2,
            energyCost = 12,
            difficulty = "Medium",
            clientName = "Wholesale Depot",
            location = "Alaba / Balogun Market",
            currentProgress = 0,
            targetQuantity = 10,
            unitLabel = "units",
            missionType = MissionType.RESTOCK_WHOLESALE,
            isCompleted = false
        )

        // 3. Living / Ajo / Cooking Mission
        val lifeTask = if (day % 2 == 1) {
            DailyHustleTask(
                id = "mission_ajo_day_${day}",
                title = "Ajo Discipline Contribution",
                description = "Deposit funds into your active Ajo saving goal to maintain collective wealth discipline.",
                category = "Ajo Savings",
                iconEmoji = "🎯",
                rewardCash = 30_000L,
                reputationReward = 5,
                stressCost = 0,
                energyCost = 5,
                difficulty = "Easy",
                clientName = "Ajo Saving Circle",
                location = "Treasury Vault",
                currentProgress = 0,
                targetQuantity = 1,
                unitLabel = "deposit",
                missionType = MissionType.AJO_CONTRIBUTION,
                isCompleted = false
            )
        } else {
            DailyHustleTask(
                id = "mission_cook_day_${day}",
                title = "Naija Home Cooking Feast",
                description = "Cook any homemade meal (Jollof, Egusi, or stew) to nourish your character and save food expenses.",
                category = "Home Life",
                iconEmoji = "🍲",
                rewardCash = 25_000L,
                reputationReward = 3,
                stressCost = 0,
                energyCost = 5,
                difficulty = "Easy",
                clientName = "Household Kitchen",
                location = "Home",
                currentProgress = 0,
                targetQuantity = 1,
                unitLabel = "meal",
                missionType = MissionType.COOK_MEAL,
                isCompleted = false
            )
        }

        // 4. Street Hustle Gig from pool
        val pool = DailyLifeAndMealData.getDefaultDailyHustles()
        val streetGigTemplate = pool[(day - 1 + pool.size) % pool.size]
        val streetGig = streetGigTemplate.copy(
            id = "${streetGigTemplate.id}_day_${day}",
            currentProgress = 0,
            targetQuantity = 1,
            unitLabel = "gig",
            missionType = MissionType.CLIENT_GIG,
            isCompleted = false
        )

        return listOf(salesTask, restockTask, lifeTask, streetGig)
    }

    /**
     * Automatically increments progress for missions matching the given MissionType.
     */
    fun updateProgress(
        state: GameState,
        type: MissionType,
        amount: Int = 1
    ): GameState {
        var missionJustMet: DailyHustleTask? = null

        val updatedTasks = state.dailyHustles.map { task ->
            if (task.missionType == type && !task.isCompleted) {
                val newProgress = min(task.targetQuantity, task.currentProgress + amount)
                val isNowMet = newProgress >= task.targetQuantity && task.currentProgress < task.targetQuantity
                val updated = task.copy(currentProgress = newProgress)
                if (isNowMet) missionJustMet = updated
                updated
            } else {
                task
            }
        }

        val updatedRoutine = if (missionJustMet != null) {
            state.dailyRoutine.copy(
                lastActionMessage = "🎯 Mission Completed: '${missionJustMet?.title}'! Ready to claim ₦${missionJustMet?.rewardCash} reward in Daily Hustle tab!"
            )
        } else {
            state.dailyRoutine
        }

        return state.copy(
            dailyHustles = updatedTasks,
            dailyRoutine = updatedRoutine
        )
    }

    /**
     * Claims reward once mission target objective has been reached.
     */
    fun claimReward(
        currentState: GameState,
        taskId: String
    ): Pair<GameState, String> {
        val task = currentState.dailyHustles.find { it.id == taskId }
            ?: return currentState to "Task not found."

        if (task.isCompleted) {
            return currentState to "This reward was already claimed!"
        }

        if (!task.isObjectiveMet) {
            return currentState to "Mission objective not yet reached! Progress: ${task.currentProgress}/${task.targetQuantity} ${task.unitLabel}."
        }

        val updatedTasks = currentState.dailyHustles.map {
            if (it.id == taskId) it.copy(isCompleted = true, completedAtMillis = System.currentTimeMillis())
            else it
        }

        val newPersonalCash = currentState.personalCash + task.rewardCash
        val newReputation = (currentState.reputation + task.reputationReward).coerceIn(0, 100)
        val newTotalEarned = currentState.totalHustleCashEarned + task.rewardCash

        val updatedRoutine = currentState.dailyRoutine.copy(
            lastActionMessage = "Claimed reward for '${task.title}'! +₦${task.rewardCash} extra cash."
        )

        val updatedState = currentState.copy(
            personalCash = newPersonalCash,
            reputation = newReputation,
            dailyHustles = updatedTasks,
            dailyRoutine = updatedRoutine,
            totalHustleCashEarned = newTotalEarned
        )

        return updatedState to "🎉 Mission Completed! Reward claimed: +₦${task.rewardCash} in-game currency, +${task.reputationReward} reputation!"
    }

    /**
     * Direct grind action:
     * - For SELL_ITEM: quickly makes a street sale of 1 unit (adding revenue to business and +1 to mission).
     * - For CLIENT_GIG: completes street challenge using energy and awards cash.
     * - For completed objectives: claims reward.
     */
    fun tackleTaskDirect(
        currentState: GameState,
        taskId: String
    ): Pair<GameState, String> {
        val task = currentState.dailyHustles.find { it.id == taskId }
            ?: return currentState to "Task not found."

        if (task.isCompleted) {
            return currentState to "This mission is already completed!"
        }

        if (task.isObjectiveMet) {
            return claimReward(currentState, taskId)
        }

        // Handle specific mission types
        return when (task.missionType) {
            MissionType.SELL_ITEM -> {
                if (currentState.inventoryUnits <= 0) {
                    currentState to "Shop inventory is out of stock! Restock wholesale first."
                } else {
                    // Sell 1 unit
                    val newInventory = currentState.inventoryUnits - 1
                    val saleRevenue = currentState.sellingPrice
                    val newBizCash = currentState.businessCash + saleRevenue
                    val newProgress = min(task.targetQuantity, task.currentProgress + 1)
                    val isNowMet = newProgress >= task.targetQuantity

                    val updatedTasks = currentState.dailyHustles.map {
                        if (it.id == taskId) it.copy(currentProgress = newProgress)
                        else it
                    }

                    val updatedState = currentState.copy(
                        inventoryUnits = newInventory,
                        businessCash = newBizCash,
                        dailyHustles = updatedTasks,
                        dailyRoutine = currentState.dailyRoutine.copy(
                            lastActionMessage = "Rush-sold 1 ${task.unitLabel} for ₦$saleRevenue! Mission progress: $newProgress/${task.targetQuantity}."
                        )
                    )

                    if (isNowMet) {
                        claimReward(updatedState, taskId)
                    } else {
                        updatedState to "Sold 1 unit of ${task.unitLabel}! Progress: $newProgress/${task.targetQuantity}. (Earned ₦$saleRevenue shop revenue)"
                    }
                }
            }

            MissionType.RESTOCK_WHOLESALE -> {
                // If user wants to do a quick restock of 5 units
                val restockCost = currentState.costPerUnit * 5
                if (currentState.businessCash < restockCost) {
                    currentState to "Insufficient business cash (₦${currentState.businessCash}) for wholesale restock."
                } else {
                    val newInventory = currentState.inventoryUnits + 5
                    val newBizCash = currentState.businessCash - restockCost
                    val newProgress = min(task.targetQuantity, task.currentProgress + 5)
                    val isNowMet = newProgress >= task.targetQuantity

                    val updatedTasks = currentState.dailyHustles.map {
                        if (it.id == taskId) it.copy(currentProgress = newProgress)
                        else it
                    }

                    val updatedState = currentState.copy(
                        inventoryUnits = newInventory,
                        businessCash = newBizCash,
                        dailyHustles = updatedTasks
                    )

                    if (isNowMet) {
                        claimReward(updatedState, taskId)
                    } else {
                        updatedState to "Restocked 5 units wholesale! Mission progress: $newProgress/${task.targetQuantity}."
                    }
                }
            }

            MissionType.CLIENT_GIG -> {
                // Standard energy street gig
                if (currentState.dailyRoutine.energyLevel < task.energyCost) {
                    currentState to "Too tired to tackle this gig (${currentState.dailyRoutine.energyLevel}/${task.energyCost}% energy). Take a bath or eat a meal!"
                } else {
                    val newEnergy = max(0, currentState.dailyRoutine.energyLevel - task.energyCost)
                    val updatedState = currentState.copy(
                        dailyRoutine = currentState.dailyRoutine.copy(energyLevel = newEnergy),
                        stress = min(100, currentState.stress + task.stressCost)
                    )
                    // Mark progress completed and claim
                    val taskWithMetProgress = updatedState.dailyHustles.map {
                        if (it.id == taskId) it.copy(currentProgress = it.targetQuantity)
                        else it
                    }
                    claimReward(updatedState.copy(dailyHustles = taskWithMetProgress), taskId)
                }
            }

            else -> {
                // For other lifestyle actions (Cooking, Ajo, School Run), perform them from their respective tabs or quick action
                currentState to "Perform '${task.title}' via Everyday Life, Meals, or Ajo tab to advance this mission!"
            }
        }
    }

    /**
     * Backward-compatible completeTask method.
     */
    fun completeTask(
        currentState: GameState,
        taskId: String
    ): Pair<GameState, String> {
        return tackleTaskDirect(currentState, taskId)
    }
}
