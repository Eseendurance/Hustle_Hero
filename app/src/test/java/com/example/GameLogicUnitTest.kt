package com.example

import com.example.data.BusinessData
import com.example.logic.AjoSavingsManager
import com.example.logic.GameEngine
import com.example.logic.InventoryManager
import com.example.logic.TurnCycleManager
import com.example.logic.WalletTransferManager
import com.example.model.BusinessType
import com.example.model.GamePhase
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicUnitTest {

    @Test
    fun testWeeklyTurnCycleProgression() {
        val biz = BusinessData.startingBusinesses.first()
        val defaultChar = BusinessData.characters.first()
        val state = GameEngine.createInitialState("Amaka", biz, "Lagos", defaultChar)

        // Validate Phase Flow Permissions
        assertTrue(TurnCycleManager.canAdvanceToPhase(GamePhase.DASHBOARD, GamePhase.BUSINESS))
        assertTrue(TurnCycleManager.canAdvanceToPhase(GamePhase.BUSINESS, GamePhase.PERSONAL))
        assertTrue(TurnCycleManager.canAdvanceToPhase(GamePhase.PERSONAL, GamePhase.EVENT))
        assertFalse(TurnCycleManager.canAdvanceToPhase(GamePhase.EVENT, GamePhase.BUSINESS))

        val initialWeek = state.week
        val initialBizCash = state.businessCash

        // Simulate turn with ₦20,000 salary withdrawal
        val turnResult = GameEngine.turnCycle.simulateWeeklyTurn(
            state = state,
            chosenEvent = null,
            choice = null,
            salaryWithdrawal = 20_000L
        )

        val nextState = turnResult.state
        val report = turnResult.report

        assertEquals(initialWeek + 1, nextState.week)
        assertEquals(GamePhase.SUMMARY, nextState.currentPhase)
        assertNotNull(report)
        assertEquals(1, nextState.turnHistory.size)
        assertTrue(report.salesRevenue >= 0)
        assertTrue(report.salaryWithdrawn <= 20_000L)
        assertTrue(nextState.walletTransfers.any { it.type == WalletTransferType.SALARY_DRAW })
    }

    @Test
    fun testInventoryWholesaleTiersAndRestock() {
        val biz = BusinessData.startingBusinesses.first()
        val state = GameEngine.createInitialState("Tunde", biz, "Lagos")

        // Wholesale tiers
        assertEquals(0.00f, InventoryManager.getWholesaleTier(20).discountPercent)
        assertEquals(0.05f, InventoryManager.getWholesaleTier(50).discountPercent)
        assertEquals(0.10f, InventoryManager.getWholesaleTier(100).discountPercent)
        assertEquals(0.15f, InventoryManager.getWholesaleTier(200).discountPercent)

        // Restock quote calculation
        val quote50 = InventoryManager.calculateRestockQuote(state, 50)
        assertEquals(50, quote50.units)
        assertTrue(quote50.discountedUnitCost <= quote50.baseUnitCost)
        assertTrue(quote50.totalCost > 0)
        assertTrue(quote50.fitsInStorage)

        // Executing restock
        val initialUnits = state.inventoryUnits
        val restockResult = GameEngine.restockInventory(state, 50)

        assertTrue(restockResult.success)
        assertEquals(initialUnits + 50, restockResult.updatedState.inventoryUnits)
        assertEquals(state.businessCash - restockResult.totalCost, restockResult.updatedState.businessCash)

        // Reorder recommendation check
        val rec = InventoryManager.calculateRecommendedRestock(state)
        assertTrue(rec.projectedDemand > 0)
        assertTrue(rec.safetyStockBuffer >= 0)
    }

    @Test
    fun testInventoryClearanceLiquidation() {
        val biz = BusinessData.startingBusinesses.first()
        val state = GameEngine.createInitialState("Bimbo", biz, "Lagos").copy(inventoryUnits = 40)

        val result = InventoryManager.liquidateInventory(state, 15, discountPercent = 0.30f)
        assertTrue(result.success)
        assertEquals(25, result.updatedState.inventoryUnits)
        assertTrue(result.cashRecovered > 0)
        assertEquals(state.businessCash + result.cashRecovered, result.updatedState.businessCash)
    }

    @Test
    fun testDualWalletTransfersAndAuditing() {
        val biz = BusinessData.startingBusinesses.first()
        val state = GameEngine.createInitialState("Folake", biz, "Lagos").copy(
            businessCash = 150_000L,
            personalCash = 30_000L,
            personalSavings = 25_000L
        )

        // 1. Owner Dividend Transfer (Business -> Personal with 2% cooperative union fee)
        val divReq = WalletTransferRequest(
            type = WalletTransferType.OWNER_DIVIDEND,
            amount = 50_000L,
            source = WalletSource.BUSINESS_CASH,
            destination = WalletDestination.PERSONAL_CASH
        )
        val divResult = WalletTransferManager.executeTransfer(state, divReq)
        assertTrue(divResult.success)
        assertEquals(100_000L, divResult.updatedState.businessCash)
        // 50k - 2% fee (1k) = 49k net added to personal cash (30k + 49k = 79k)
        assertEquals(79_000L, divResult.updatedState.personalCash)
        assertEquals(1, divResult.updatedState.walletTransfers.size)
        assertEquals(1_000L, divResult.updatedState.walletTransfers.first().fee)

        // 2. Personal Capital Injection (Personal Cash -> Business Cash)
        val injectReq = WalletTransferRequest(
            type = WalletTransferType.CAPITAL_INJECTION,
            amount = 20_000L,
            source = WalletSource.PERSONAL_CASH,
            destination = WalletDestination.BUSINESS_CASH
        )
        val injectResult = WalletTransferManager.executeTransfer(divResult.updatedState, injectReq)
        assertTrue(injectResult.success)
        assertEquals(120_000L, injectResult.updatedState.businessCash)
        assertEquals(59_000L, injectResult.updatedState.personalCash)
        assertEquals(2, injectResult.updatedState.walletTransfers.size)

        // 3. Emergency Bailout (Personal Savings -> Business Cash)
        val bailoutReq = WalletTransferRequest(
            type = WalletTransferType.EMERGENCY_BAILOUT,
            amount = 15_000L,
            source = WalletSource.PERSONAL_SAVINGS,
            destination = WalletDestination.BUSINESS_CASH
        )
        val bailoutResult = WalletTransferManager.executeTransfer(injectResult.updatedState, bailoutReq)
        assertTrue(bailoutResult.success)
        assertEquals(135_000L, bailoutResult.updatedState.businessCash)
        assertEquals(10_000L, bailoutResult.updatedState.personalSavings)
        assertEquals(3, bailoutResult.updatedState.walletTransfers.size)

        // 4. Validation: Insufficient Funds Guard
        val overdrawnReq = WalletTransferRequest(
            type = WalletTransferType.SALARY_DRAW,
            amount = 999_999L,
            source = WalletSource.BUSINESS_CASH,
            destination = WalletDestination.PERSONAL_CASH
        )
        val validation = WalletTransferManager.validateTransfer(bailoutResult.updatedState, overdrawnReq.type, overdrawnReq.amount)
        assertFalse(validation.isValid)
    }

    @Test
    fun testPerishableFoodSpoilageCalculation() {
        val foodBiz = BusinessData.startingBusinesses.find { it.type == BusinessType.FOOD_VENDOR }
            ?: BusinessData.startingBusinesses.first().copy(type = BusinessType.FOOD_VENDOR, spoilageRate = 0.20f)
        val state = GameEngine.createInitialState("Mama Nkechi", foodBiz, "Lagos").copy(inventoryUnits = 50)

        // Without freezer -> spoilage detected
        val spoilageNoFreezer = InventoryManager.calculateSpoilage(state)
        assertTrue(spoilageNoFreezer.perishableUnitsAtRisk > 0)
        assertFalse(spoilageNoFreezer.hasRefrigeration)

        // With freezer upgrade -> spoilage mitigated
        val stateWithFreezer = state.copy(
            upgrades = state.upgrades.map {
                if (it.id == "deep_freezer") it.copy(isPurchased = true) else it
            }
        )
        val spoilageWithFreezer = InventoryManager.calculateSpoilage(stateWithFreezer)
        assertEquals(0, spoilageWithFreezer.estimatedSpoilageUnits)
        assertTrue(spoilageWithFreezer.hasRefrigeration)
    }

    @Test
    fun testAjoGroupRotationAndPayoutMechanics() {
        val biz = BusinessData.startingBusinesses.first()
        val baseState = GameEngine.createInitialState("Bayo", biz, "Lagos").copy(
            personalCash = 250_000L,
            personalSavings = 100_000L,
            ajoGroup = com.example.model.AjoGroup(
                isEnrolled = true,
                weeklyContribution = 10_000L,
                cycleWeeks = 5,
                currentCycleWeek = 2,
                payoutWeek = 3,
                totalMembers = 5
            )
        )

        assertEquals(50_000L, baseState.ajoGroup.totalPot)

        // Turn simulation on week 2: player contributes ₦10,000, advances to cycle week 3
        val turn1 = GameEngine.turnCycle.simulateWeeklyTurn(
            state = baseState,
            chosenEvent = null,
            choice = null,
            salaryWithdrawal = 0L
        )

        val stateAfterTurn1 = turn1.state
        assertEquals(3, stateAfterTurn1.ajoGroup.currentCycleWeek)
        assertEquals(10_000L, turn1.report.ajoContributionPaid)
        assertEquals(0L, turn1.report.ajoPayoutReceived)

        // Turn simulation on week 3 (Player's Payout Week): player collects ₦50,000 lump sum
        val turn2 = GameEngine.turnCycle.simulateWeeklyTurn(
            state = stateAfterTurn1,
            chosenEvent = null,
            choice = null,
            salaryWithdrawal = 0L
        )

        val stateAfterTurn2 = turn2.state
        assertEquals(4, stateAfterTurn2.ajoGroup.currentCycleWeek)
        assertEquals(10_000L, turn2.report.ajoContributionPaid)
        assertEquals(50_000L, turn2.report.ajoPayoutReceived)
        assertTrue(stateAfterTurn2.personalCash >= 50_000L)
    }

    @Test
    fun testAjoSavingsGoalBackendValidation() {
        val biz = BusinessData.startingBusinesses.first()
        val baseState = GameEngine.createInitialState("Bayo", biz, "Lagos").copy(
            personalCash = 30_000L,
            personalSavings = 15_000L,
            businessCash = 100_000L
        )

        val targetGoal = baseState.ajoGoals.first()

        // 1. Valid contribution within budget
        val validCheck = AjoSavingsManager.validateContribution(
            state = baseState,
            goalId = targetGoal.id,
            amount = 20_000L,
            source = WalletSource.PERSONAL_CASH
        )
        assertTrue(validCheck.isValid)
        assertEquals(30_000L, validCheck.maxAffordable)

        // 2. Invalid zero/negative amount
        val zeroCheck = AjoSavingsManager.validateContribution(
            state = baseState,
            goalId = targetGoal.id,
            amount = 0L,
            source = WalletSource.PERSONAL_CASH
        )
        assertFalse(zeroCheck.isValid)
        assertNotNull(zeroCheck.errorMessage)

        // 3. Invalid: exceeds wallet balance
        val exceedCheck = AjoSavingsManager.validateContribution(
            state = baseState,
            goalId = targetGoal.id,
            amount = 50_000L,
            source = WalletSource.PERSONAL_CASH
        )
        assertFalse(exceedCheck.isValid)
        assertTrue(exceedCheck.errorMessage!!.contains("Insufficient"))

        // 4. Invalid: non-existent goal ID
        val missingGoalCheck = AjoSavingsManager.validateContribution(
            state = baseState,
            goalId = "non_existent_goal_xyz",
            amount = 10_000L,
            source = WalletSource.PERSONAL_CASH
        )
        assertFalse(missingGoalCheck.isValid)
    }

    @Test
    fun testAjoSavingsGoalContributionAndCompletionPerks() {
        val biz = BusinessData.startingBusinesses.first()
        val baseState = GameEngine.createInitialState("Bayo", biz, "Lagos").copy(
            personalCash = 200_000L,
            personalSavings = 50_000L,
            reputation = 50,
            stress = 30
        )

        val goal = baseState.ajoGoals.first() // Shop expansion (target 150k, starts with 25k)
        val initialCash = baseState.personalCash
        val initialSaved = goal.currentAmount

        // Make partial contribution of ₦20,000 from Personal Cash
        val (stateAfterPart, resultPart) = AjoSavingsManager.executeContribution(
            state = baseState,
            goalId = goal.id,
            amount = 20_000L,
            source = WalletSource.PERSONAL_CASH,
            note = "First weekly store expansion deposit"
        )

        assertTrue(resultPart.success)
        assertFalse(resultPart.isGoalCompletedNow)
        assertEquals(initialCash - 20_000L, stateAfterPart.personalCash)
        val updatedGoal = stateAfterPart.ajoGoals.find { it.id == goal.id }!!
        assertEquals(initialSaved + 20_000L, updatedGoal.currentAmount)
        assertEquals(1, stateAfterPart.ajoContributionHistory.size)
        assertEquals(20_000L, stateAfterPart.ajoContributionHistory.first().amount)

        // Deposit exact remaining amount to reach completion
        val remaining = updatedGoal.remainingAmount
        val (stateAfterFull, resultFull) = AjoSavingsManager.executeContribution(
            state = stateAfterPart,
            goalId = goal.id,
            amount = remaining,
            source = WalletSource.PERSONAL_CASH
        )

        assertTrue(resultFull.success)
        assertTrue(resultFull.isGoalCompletedNow)
        val completedGoal = stateAfterFull.ajoGoals.find { it.id == goal.id }!!
        assertTrue(completedGoal.isCompleted)
        assertEquals(completedGoal.targetAmount, completedGoal.currentAmount)
        // Verify completion perks (reputation bonus, stress relief)
        assertTrue(stateAfterFull.reputation > baseState.reputation)
        assertTrue(stateAfterFull.stress < baseState.stress)
        assertEquals(2, stateAfterFull.ajoContributionHistory.size)
    }

    @Test
    fun testCustomAjoGoalCreationAndMultiWalletFunding() {
        val biz = BusinessData.startingBusinesses.first()
        val baseState = GameEngine.createInitialState("Bayo", biz, "Lagos").copy(
            personalSavings = 60_000L,
            businessCash = 150_000L
        )

        // Create Custom Goal
        val (stateWithGoal, newGoal) = AjoSavingsManager.createGoal(
            state = baseState,
            title = "Import Solar Batteries",
            description = "Direct factory bulk purchase",
            category = "Energy",
            iconEmoji = "🔋",
            targetAmount = 50_000L,
            weeklyTarget = 10_000L,
            perkReward = "Zero power outage generator cost"
        )

        assertNotNull(newGoal)
        assertEquals("Import Solar Batteries", newGoal.title)
        assertEquals(newGoal.id, stateWithGoal.selectedAjoGoalId)

        // Fund from Emergency Savings
        val (stateFundedSavings, resSavings) = AjoSavingsManager.executeContribution(
            state = stateWithGoal,
            goalId = newGoal.id,
            amount = 30_000L,
            source = WalletSource.PERSONAL_SAVINGS
        )
        assertTrue(resSavings.success)
        assertEquals(30_000L, stateFundedSavings.personalSavings) // 60k - 30k

        // Fund remaining from Business Capital
        val (stateFundedBiz, resBiz) = AjoSavingsManager.executeContribution(
            state = stateFundedSavings,
            goalId = newGoal.id,
            amount = 20_000L,
            source = WalletSource.BUSINESS_CASH
        )
        assertTrue(resBiz.success)
        assertTrue(resBiz.isGoalCompletedNow)
        assertEquals(130_000L, stateFundedBiz.businessCash) // 150k - 20k
        assertTrue(stateFundedBiz.walletTransfers.any { it.source == WalletSource.BUSINESS_CASH })
    }

    @Test
    fun testDailyHustleTasksCompletionAndReward() {
        val biz = BusinessData.startingBusinesses.first()
        val char = BusinessData.characters.first()
        val state = GameEngine.createInitialState("Bayo", biz, "Lagos", char)

        val task = state.dailyHustles.first()
        val initialCash = state.personalCash
        val initialEnergy = state.dailyRoutine.energyLevel

        val (nextState, msg) = com.example.logic.DailyHustleManager.completeTask(state, task.id)
        assertEquals(initialCash + task.rewardCash, nextState.personalCash)
        assertEquals(initialEnergy - task.energyCost, nextState.dailyRoutine.energyLevel)
        assertTrue(nextState.dailyHustles.first { it.id == task.id }.isCompleted)
        assertTrue(msg.contains("Completed") || msg.contains("claimed"))
    }

    @Test
    fun testEverydayLifeMorningRoutineAndSleep() {
        val biz = BusinessData.startingBusinesses.first()
        val char = BusinessData.characters.first()
        var state = GameEngine.createInitialState("Bayo", biz, "Lagos", char)

        // Routine: Wake up, bath, brush teeth, make breakfast
        state = com.example.logic.EverydayLifeManager.wakeUp(state)
        assertTrue(state.dailyRoutine.hasWokenUp)

        state = com.example.logic.EverydayLifeManager.takeBath(state)
        assertTrue(state.dailyRoutine.hasTakenBath)
        assertEquals(100, state.dailyRoutine.hygieneLevel)

        state = com.example.logic.EverydayLifeManager.brushTeeth(state)
        assertTrue(state.dailyRoutine.hasBrushedTeeth)

        state = com.example.logic.EverydayLifeManager.prepareBreakfast(state)
        assertTrue(state.dailyRoutine.hasHadBreakfast)
        assertEquals(0, state.dailyRoutine.hungerLevel)

        // Sleep at night
        val (stateAfterSleep, sleepMsg) = com.example.logic.EverydayLifeManager.sleepNight(state)
        assertEquals(2, stateAfterSleep.dailyRoutine.currentDay)
        assertEquals(100, stateAfterSleep.dailyRoutine.energyLevel)
        assertFalse(stateAfterSleep.dailyRoutine.hasTakenBath)
        assertTrue(sleepMsg.contains("Day 2"))
    }

    @Test
    fun testGourmetMealCookingAndTakeout() {
        val biz = BusinessData.startingBusinesses.first()
        val char = BusinessData.characters.first()
        val state = GameEngine.createInitialState("Bayo", biz, "Lagos", char).copy(personalCash = 50_000L)

        val jollofMeal = com.example.data.DailyLifeAndMealData.getAllMeals().first { it.id == "ng_jollof_dodo" }

        // Test Cooking
        val (cookedState, cookMsg) = com.example.logic.EverydayLifeManager.cookMeal(state, jollofMeal)
        assertEquals(50_000L - jollofMeal.cookingCost, cookedState.personalCash)
        assertEquals(1, cookedState.mealHistory.size)
        assertTrue(cookedState.mealHistory.first().wasCooked)
        assertTrue(cookMsg.contains("cooked"))

        // Test Takeout
        val pizzaMeal = com.example.data.DailyLifeAndMealData.getAllMeals().first { it.id == "pz_pepperoni_feast" }
        val (takeoutState, takeoutMsg) = com.example.logic.EverydayLifeManager.orderTakeoutMeal(cookedState, pizzaMeal)
        assertEquals(cookedState.personalCash - pizzaMeal.takeoutPrice, takeoutState.personalCash)
        assertEquals(2, takeoutState.mealHistory.size)
        assertFalse(takeoutState.mealHistory.first().wasCooked)
        assertTrue(takeoutMsg.contains("delivered"))
    }

    @Test
    fun testRomanceMarriageKidsAndSchoolRun() {
        val biz = BusinessData.startingBusinesses.first()
        val char = BusinessData.characters.first()
        var state = GameEngine.createInitialState("Bayo", biz, "Lagos", char).copy(personalCash = 200_000L)

        // Go on date
        val (dateState, _) = com.example.logic.EverydayLifeManager.goOnDate(state, "Victoria Island Rooftop", 12_000L)
        assertEquals(com.example.model.RelationshipStatus.DATING, dateState.familyState.relationshipStatus)

        // Propose & Marry
        val (marriedState, marryMsg) = com.example.logic.EverydayLifeManager.proposeAndMarry(dateState)
        assertEquals(com.example.model.RelationshipStatus.MARRIED, marriedState.familyState.relationshipStatus)
        assertTrue(marryMsg.contains("married"))

        // Have 2 kids
        val (kid1State, _) = com.example.logic.EverydayLifeManager.haveBaby(marriedState, "Tolu")
        val (kid2State, _) = com.example.logic.EverydayLifeManager.haveBaby(kid1State, "Chioma")
        assertEquals(2, kid2State.familyState.kids.size)

        // School Run
        val (schoolState, schoolMsg) = com.example.logic.EverydayLifeManager.takeKidsToSchool(kid2State)
        assertTrue(schoolState.dailyRoutine.hasTakenKidsToSchool)
        assertTrue(schoolMsg.contains("safely at school"))
    }

    @Test
    fun testPartyHostingAndClubbing() {
        val biz = BusinessData.startingBusinesses.first()
        val char = BusinessData.characters.first()
        val state = GameEngine.createInitialState("Bayo", biz, "Lagos", char).copy(personalCash = 100_000L)

        val party = com.example.data.DailyLifeAndMealData.getPartyOptions().first()
        val (partyState, partyMsg) = com.example.logic.EverydayLifeManager.hostParty(state, party)
        assertEquals(100_000L - party.cost, partyState.personalCash)
        assertTrue(partyMsg.contains("legendary"))

        val club = com.example.data.DailyLifeAndMealData.getClubbingVenues().first()
        val (clubState, clubMsg) = com.example.logic.EverydayLifeManager.goClubbing(partyState, club, isVip = false)
        assertEquals(partyState.personalCash - club.regularEntryFee, clubState.personalCash)
        assertTrue(clubMsg.contains("Nightclub"))
    }
}
