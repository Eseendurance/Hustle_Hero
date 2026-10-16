package com.example.logic

import com.example.data.BusinessData
import com.example.data.DailyLifeAndMealData
import com.example.model.Business
import com.example.model.BusinessType
import com.example.model.CareerTrack
import com.example.model.CharacterProfile
import com.example.model.DailyRoutineState
import com.example.model.EventChoice
import com.example.model.FamilyState
import com.example.model.GameEvent
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.RiskLevel
import com.example.model.TurnSimulationResult
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransfer
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferType
import com.example.model.WeeklyTurnReport
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Core Game Logic Module: Weekly Turn Cycle Engine
 *
 * Manages:
 * - Weekly turn execution & state transitions
 * - Dual-wallet salary transfer & personal living expenses
 * - Market demand calculation & sales execution
 * - OpEx, Ajo savings rotation, loan amortization, investment returns
 * - Burnout & insolvency fail-states, net worth victory conditions
 */
object TurnCycleManager {

    /**
     * Checks if moving from currentPhase to targetPhase is allowed.
     */
    fun canAdvanceToPhase(currentPhase: GamePhase, targetPhase: GamePhase): Boolean {
        if (currentPhase == GamePhase.GAME_OVER) return false
        return when (currentPhase) {
            GamePhase.DASHBOARD -> targetPhase in listOf(
                GamePhase.BUSINESS,
                GamePhase.PERSONAL,
                GamePhase.MARKETING_ADS,
                GamePhase.COMMUNITY,
                GamePhase.MARKET_3D,
                GamePhase.EVENT,
                GamePhase.DAILY_HUSTLE,
                GamePhase.LIFE_ROUTINE,
                GamePhase.GOURMET_MEALS
            )
            GamePhase.BUSINESS -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.PERSONAL)
            GamePhase.PERSONAL -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.BUSINESS, GamePhase.EVENT)
            GamePhase.MARKETING_ADS -> targetPhase == GamePhase.DASHBOARD
            GamePhase.COMMUNITY -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.MARKET_3D)
            GamePhase.MARKET_3D -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.COMMUNITY, GamePhase.BUSINESS)
            GamePhase.DAILY_HUSTLE -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.LIFE_ROUTINE, GamePhase.GOURMET_MEALS)
            GamePhase.LIFE_ROUTINE -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.DAILY_HUSTLE, GamePhase.GOURMET_MEALS, GamePhase.BUSINESS)
            GamePhase.GOURMET_MEALS -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.LIFE_ROUTINE)
            GamePhase.EVENT -> targetPhase in listOf(GamePhase.SUMMARY, GamePhase.GAME_OVER)
            GamePhase.SUMMARY -> targetPhase in listOf(GamePhase.DASHBOARD, GamePhase.GAME_OVER)
            GamePhase.GAME_OVER -> false
        }
    }

    /**
     * Core Turn Simulation: Executes an entire weekly cycle.
     */
    fun simulateWeeklyTurn(
        state: GameState,
        chosenEvent: GameEvent?,
        choice: EventChoice?,
        salaryWithdrawal: Long,
        additionalTransfers: List<WalletTransferRequest> = emptyList()
    ): TurnSimulationResult {
        var currentState = state
        val executedTransfers = mutableListOf<WalletTransfer>()

        // 1. Process Additional Requested Wallet Transfers
        for (request in additionalTransfers) {
            val result = WalletTransferManager.executeTransfer(currentState, request)
            if (result.success) {
                currentState = result.updatedState
                result.transferRecord?.let { executedTransfers.add(it) }
            }
        }

        var bizCash = currentState.businessCash
        var persCash = currentState.personalCash
        var savings = currentState.personalSavings
        var stress = currentState.stress
        var reputation = currentState.reputation
        var creditScore = currentState.creditScore
        var inventory = currentState.inventoryUnits

        // 2. Process Weekly Salary Withdrawal (Business -> Personal)
        val actualSalary = min(max(0L, salaryWithdrawal), max(0L, bizCash))
        if (actualSalary > 0L) {
            val salaryRequest = WalletTransferRequest(
                type = WalletTransferType.SALARY_DRAW,
                amount = actualSalary,
                source = WalletSource.BUSINESS_CASH,
                destination = WalletDestination.PERSONAL_CASH,
                reason = "Weekly owner salary draw (Week ${currentState.week})"
            )
            val salaryResult = WalletTransferManager.executeTransfer(currentState, salaryRequest)
            if (salaryResult.success) {
                bizCash = salaryResult.updatedState.businessCash
                persCash = salaryResult.updatedState.personalCash
                stress = salaryResult.updatedState.stress
                salaryResult.transferRecord?.let { executedTransfers.add(it) }
            } else {
                bizCash -= actualSalary
                persCash += actualSalary
            }
        }

        // 3. Paid Ad Campaigns Processing (TV, Billboard, Mobile Internet)
        var totalAdDemandBoost = 0
        var totalAdReputationBoost = 0
        val updatedAds = currentState.activeAds.map { ad ->
            if (ad.isActive && ad.weeksRemaining > 0) {
                totalAdDemandBoost += ad.demandBoostPercent
                totalAdReputationBoost += ad.reputationBoost
                val remaining = ad.weeksRemaining - 1
                ad.copy(weeksRemaining = remaining, isActive = remaining > 0)
            } else {
                ad
            }
        }.filter { it.weeksRemaining > 0 }

        reputation = min(100, reputation + (totalAdReputationBoost / 4))

        // 4. Upgrades & Facilities
        val upgrades = currentState.upgrades
        val hasGen = upgrades.any { it.id == "gen_tiger" && it.isPurchased }
        val hasSolar = upgrades.any { it.id == "solar_inverter" && it.isPurchased }
        val hasSign = upgrades.any { it.id == "neon_sign" && it.isPurchased }
        val hasBike = upgrades.any { it.id == "delivery_bike" && it.isPurchased }
        val hasPos = upgrades.any { it.id == "pos_terminal" && it.isPurchased }
        val hasFreezer = upgrades.any { it.id == "deep_freezer" && it.isPurchased }

        // 5. Demand Multiplier Calculation
        var demandMultiplier = 1.0f

        // Price elasticity
        val basePrice = currentState.business.baseSellingPrice.toFloat()
        val priceRatio = currentState.sellingPrice.toFloat() / max(1f, basePrice)
        if (priceRatio < 1.0f) {
            demandMultiplier += (1.0f - priceRatio) * 0.8f
        } else if (priceRatio > 1.0f) {
            demandMultiplier -= (priceRatio - 1.0f) * 0.9f
        }

        // Reputation bonus
        demandMultiplier += ((reputation - 50) / 100f) * 0.5f

        // Character unique perk bonus
        demandMultiplier += (currentState.character.demandBoostPercent / 100f)

        // Ad campaigns boost
        demandMultiplier += (totalAdDemandBoost / 100f)

        // Weekly marketing budget boost
        if (currentState.marketingBudgetWeekly > 0) {
            val marketingRatio = min(1.0f, currentState.marketingBudgetWeekly / 30_000f)
            demandMultiplier += marketingRatio * 0.35f
        }

        // Upgrade boosts
        if (hasSign) demandMultiplier += 0.20f
        if (hasBike) demandMultiplier += 0.30f
        if (hasPos) demandMultiplier += 0.15f

        // Market noise (+/- 15%)
        val marketNoise = 0.85f + Random.nextFloat() * 0.30f
        demandMultiplier *= marketNoise

        // Lagos power blackout hazard
        val blackoutOccurred = Random.nextFloat() < 0.35f
        if (blackoutOccurred && !hasSolar && !hasGen) {
            demandMultiplier *= 0.65f
            stress += 5
        }

        // 6. Sales & Inventory Depletion
        val rawDemand = (currentState.business.baseWeeklyDemand * max(0.2f, demandMultiplier)).toInt()
        val maxCapacity = 50 + (currentState.staffCount * 35)
        val unitsDemanded = min(rawDemand, maxCapacity)

        val unitsSold = min(inventory, unitsDemanded)
        val revenue = unitsSold * currentState.sellingPrice
        val cogs = unitsSold * currentState.costPerUnit
        inventory -= unitsSold

        // Perishable spoilage (Food Vendor)
        if (currentState.business.type == BusinessType.FOOD_VENDOR && !hasFreezer && inventory > 0) {
            val spoiled = (inventory * currentState.business.spoilageRate).toInt()
            inventory = max(0, inventory - spoiled)
        }

        // 7. Business Operating Expenses
        val staffPayroll = currentState.staffCount * currentState.staffWageWeekly
        val upgradesMaintenance = upgrades.filter { it.isPurchased }.sumOf { it.weeklyMaintenance }
        val marketingCost = currentState.marketingBudgetWeekly
        val businessOpEx = staffPayroll + upgradesMaintenance + marketingCost

        val businessNetProfit = (revenue - cogs) - businessOpEx
        bizCash += (revenue - businessOpEx)

        // 8. Personal Living Expenses
        val livingCost = currentState.personalExpenses.calculateTotal(currentState.inflationRate)
        var personalExpensesPaid = 0L
        if (persCash >= livingCost) {
            persCash -= livingCost
            personalExpensesPaid = livingCost
            stress = max(0, stress + currentState.personalExpenses.lifestyle.stressRelief)
        } else {
            personalExpensesPaid = persCash
            val deficit = livingCost - persCash
            persCash = 0L
            if (savings >= deficit) {
                savings -= deficit
                personalExpensesPaid += deficit
                stress += 3
            } else {
                personalExpensesPaid += savings
                savings = 0L
                stress += 20
                creditScore = max(300, creditScore - 15)
            }
        }

        // 9. Ajo / Esusu Cycle
        var ajoContributed = 0L
        var ajoPayoutReceived = 0L
        var updatedAjo = currentState.ajoGroup
        if (currentState.ajoGroup.isEnrolled) {
            val contribution = currentState.ajoGroup.weeklyContribution
            if (persCash >= contribution) {
                persCash -= contribution
                ajoContributed = contribution
            } else if (savings >= contribution) {
                savings -= contribution
                ajoContributed = contribution
            } else {
                reputation = max(0, reputation - 15)
                stress += 15
            }

            if (currentState.ajoGroup.currentCycleWeek == currentState.ajoGroup.payoutWeek) {
                ajoPayoutReceived = currentState.ajoGroup.totalPot
                persCash += ajoPayoutReceived
                stress = max(0, stress - 10)
            }

            val nextCycleWeek = if (currentState.ajoGroup.currentCycleWeek >= currentState.ajoGroup.cycleWeeks) 1 else currentState.ajoGroup.currentCycleWeek + 1
            updatedAjo = currentState.ajoGroup.copy(currentCycleWeek = nextCycleWeek)
        }

        // 10. Investment Portfolio Returns
        var totalInvestmentGainLoss = 0L
        val updatedInvestments = currentState.investments.map { inv ->
            if (inv.totalValue > 0) {
                val yieldPercent = when (inv.type) {
                    RiskLevel.LOW -> 0.0035f + (Random.nextFloat() * 0.0015f)
                    RiskLevel.MEDIUM -> -0.012f + (Random.nextFloat() * 0.035f)
                    RiskLevel.HIGH -> -0.15f + (Random.nextFloat() * 0.32f)
                }
                val valueChange = (inv.totalValue * yieldPercent).toLong()
                totalInvestmentGainLoss += valueChange
                val newVal = max(0L, inv.totalValue + valueChange)
                inv.copy(totalValue = newVal, lastYieldPercent = yieldPercent * 100f)
            } else {
                inv
            }
        }

        // 11. Loans & Debt Amortization
        var totalLoanInterestPaid = 0L
        var totalLoansRepaid = 0L
        val updatedLoans = currentState.loans.map { loan ->
            if (loan.balance > 0) {
                val interest = (loan.balance * loan.weeklyInterestRate).toLong()
                totalLoanInterestPaid += interest
                val payment = min(loan.balance + interest, loan.minWeeklyPayment)

                if (persCash >= payment) {
                    persCash -= payment
                    totalLoansRepaid += payment
                    creditScore = min(850, creditScore + 4)
                    val newBal = max(0L, (loan.balance + interest) - payment)
                    loan.copy(balance = newBal)
                } else {
                    creditScore = max(300, creditScore - 20)
                    stress += 12
                    loan.copy(balance = loan.balance + interest)
                }
            } else {
                loan
            }
        }.filter { it.balance > 0 }

        // 12. Apply Event Choice
        if (choice != null) {
            bizCash += choice.businessCashImpact
            persCash += choice.personalCashImpact
            savings = max(0L, savings + choice.savingsImpact)
            inventory = max(0, inventory + choice.inventoryImpact)
            stress = (stress + choice.stressImpact).coerceIn(0, 100)
            reputation = (reputation + choice.reputationImpact).coerceIn(0, 100)
        }

        stress = stress.coerceIn(0, 100)
        reputation = reputation.coerceIn(0, 100)

        // 13. Inflation & Supplier Costs
        val newInflation = currentState.inflationRate + 0.0025f
        val discountedUnitCost = currentState.business.baseUnitCost * (1f - (currentState.character.costDiscountPercent / 100f))
        val newUnitCost = (discountedUnitCost * (1f + newInflation)).toLong()

        // 14. Net Worth & Financial Balances
        val totalInvVal = updatedInvestments.sumOf { it.totalValue }
        val invStockVal = inventory.toLong() * newUnitCost
        val totalDebtVal = updatedLoans.sumOf { it.balance }
        val endingNetWorth = (bizCash + persCash + savings + totalInvVal + invStockVal) - totalDebtVal
        val previousNetWorth = currentState.netWorth
        val netWorthChange = endingNetWorth - previousNetWorth

        // 15. Generate Weekly Turn Report
        val report = WeeklyTurnReport(
            weekNumber = currentState.week,
            unitsSold = unitsSold,
            salesRevenue = revenue,
            cogs = cogs,
            operatingExpenses = businessOpEx,
            businessNetProfit = businessNetProfit,
            salaryWithdrawn = actualSalary,
            personalLivingCost = personalExpensesPaid,
            ajoContributionPaid = ajoContributed,
            ajoPayoutReceived = ajoPayoutReceived,
            investmentGainLoss = totalInvestmentGainLoss,
            loanInterestPaid = totalLoanInterestPaid,
            loansRepaid = totalLoansRepaid,
            endingNetWorth = endingNetWorth,
            netWorthChange = netWorthChange,
            endingBusinessCash = bizCash,
            endingPersonalCash = persCash,
            endingSavings = savings,
            endingDebt = totalDebtVal,
            endingStress = stress,
            endingReputation = reputation,
            eventTitle = chosenEvent?.title,
            eventOutcome = choice?.outcomeMessage,
            eventLesson = choice?.moneyLesson
        )

        // 16. Win / Loss Game Condition Evaluations
        var isGameOver = false
        var isVictory = false
        var gameOverTitle: String? = null
        var gameOverMessage: String? = null

        if (stress >= 100) {
            isGameOver = true
            isVictory = false
            gameOverTitle = "Burnout & Physical Breakdown"
            gameOverMessage = "You pushed too hard without resting or balancing your hustle. Stress hit 100% and you were admitted to hospital."
        } else if (bizCash < -150_000L && inventory <= 0) {
            isGameOver = true
            isVictory = false
            gameOverTitle = "Business Insolvency / Bankruptcy"
            gameOverMessage = "Your business cash went deeply negative with zero stock left to trade. Wholesalers stopped credit and the shop was sealed."
        } else if (endingNetWorth >= currentState.netWorthGoal) {
            isGameOver = true
            isVictory = true
            gameOverTitle = "Hustle Hero: Market Legend!"
            gameOverMessage = "Congratulations! You reached your net worth goal of ₦5,000,000! You mastered cashflow, saved through Ajo, and built an empire from the streets."
        } else if (currentState.week >= currentState.maxWeeks) {
            isGameOver = true
            isVictory = true
            gameOverTitle = "One Year Lagos Veteran!"
            gameOverMessage = "You survived 52 full weeks in the world's most dynamic market city! Your final net worth is ₦${endingNetWorth}."
        }

        val allTransfers = executedTransfers + currentState.walletTransfers

        val nextState = currentState.copy(
            week = currentState.week + 1,
            businessCash = bizCash,
            personalCash = persCash,
            personalSavings = savings,
            inventoryUnits = inventory,
            costPerUnit = newUnitCost,
            stress = stress,
            reputation = reputation,
            creditScore = creditScore,
            inflationRate = newInflation,
            activeAds = updatedAds,
            ajoGroup = updatedAjo,
            investments = updatedInvestments,
            loans = updatedLoans,
            currentPhase = if (isGameOver) GamePhase.GAME_OVER else GamePhase.SUMMARY,
            currentEvent = null,
            selectedChoice = null,
            latestReport = report,
            turnHistory = currentState.turnHistory + report,
            walletTransfers = allTransfers,
            isGameOver = isGameOver,
            isVictory = isVictory,
            gameOverTitle = gameOverTitle,
            gameOverMessage = gameOverMessage
        )

        return TurnSimulationResult(
            state = nextState,
            report = report,
            autoTransfersExecuted = executedTransfers
        )
    }

    /**
     * Initializes fresh game state for a new player.
     */
    fun createInitialState(
        characterName: String = "Emeka Okafor",
        business: Business = BusinessData.startingBusinesses.first(),
        city: String = "Lagos",
        character: CharacterProfile = BusinessData.characters.first()
    ): GameState {
        val upgrades = BusinessData.getDefaultUpgrades()
        val investments = BusinessData.getDefaultInvestments()
        val discountedUnitCost = (business.baseUnitCost * (1f - (character.costDiscountPercent / 100f))).toLong()

        return GameState(
            characterName = characterName,
            character = character,
            cityName = city,
            week = 1,
            maxWeeks = 52,
            netWorthGoal = 5_000_000L,
            business = business,
            businessCash = business.startingCapital + character.startingCapitalBonus,
            inventoryUnits = business.defaultStock,
            sellingPrice = business.baseSellingPrice,
            costPerUnit = discountedUnitCost,
            staffCount = 0,
            staffWageWeekly = 10_000L,
            marketingBudgetWeekly = 0L,
            upgrades = upgrades,
            activeAds = emptyList(),
            personalCash = 35_000L,
            personalSavings = 20_000L,
            personalExpenses = com.example.model.PersonalExpenses(),
            ajoGroup = com.example.model.AjoGroup(
                isEnrolled = false,
                weeklyContribution = 10_000L,
                cycleWeeks = 5,
                payoutWeek = 3
            ),
            ajoGoals = BusinessData.getDefaultAjoSavingGoals(),
            selectedAjoGoalId = "goal_shop_expansion",
            ajoContributionHistory = emptyList(),
            investments = investments,
            loans = emptyList(),
            communityMembers = BusinessData.getDefaultCommunityMembers(),
            communityProposals = BusinessData.getDefaultCommunityProposals(),
            communityPosts = BusinessData.getDefaultCommunityPosts(),
            stress = 18,
            reputation = character.startingReputation,
            creditScore = 620,
            inflationRate = 0.08f,
            currentPhase = GamePhase.DASHBOARD,
            dailyHustles = DailyLifeAndMealData.getDefaultDailyHustles(),
            dailyRoutine = DailyRoutineState(),
            careerTrack = CareerTrack.BUSINESS_OWNER,
            familyState = FamilyState()
        )
    }
}
