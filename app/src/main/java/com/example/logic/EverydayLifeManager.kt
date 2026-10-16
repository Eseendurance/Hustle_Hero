package com.example.logic

import com.example.model.ClubbingVenue
import com.example.model.DailyRoutineState
import com.example.model.FamilyState
import com.example.model.GameState
import com.example.model.KidInfo
import com.example.model.MealConsumptionRecord
import com.example.model.MealItem
import com.example.model.MissionType
import com.example.model.PartnerProfile
import com.example.model.PartyEvent
import com.example.model.RelationshipStatus
import com.example.model.TimeOfDay
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

object EverydayLifeManager {

    fun wakeUp(state: GameState): GameState {
        val routine = state.dailyRoutine.copy(
            hasWokenUp = true,
            hasSleptNight = false,
            energyLevel = (state.dailyRoutine.energyLevel + 20).coerceAtMost(100),
            lastActionMessage = "Good morning! Woke up refreshed ready to conquer the day."
        )
        return state.copy(dailyRoutine = routine)
    }

    fun takeBath(state: GameState): GameState {
        val routine = state.dailyRoutine.copy(
            hasTakenBath = true,
            hygieneLevel = 100,
            energyLevel = (state.dailyRoutine.energyLevel + 10).coerceAtMost(100),
            lastActionMessage = "Took a warm, invigorating shower. Feeling clean and sharp!"
        )
        val newStress = (state.stress - 5).coerceAtLeast(0)
        val stateUpdated = state.copy(dailyRoutine = routine, stress = newStress)
        return DailyHustleManager.updateProgress(stateUpdated, MissionType.MORNING_HYGIENE, 1)
    }

    fun brushTeeth(state: GameState): GameState {
        val routine = state.dailyRoutine.copy(
            hasBrushedTeeth = true,
            hygieneLevel = (state.dailyRoutine.hygieneLevel + 15).coerceAtMost(100),
            lastActionMessage = "Brushed teeth thoroughly. Fresh minty breath for the hustle!"
        )
        val stateUpdated = state.copy(dailyRoutine = routine)
        return DailyHustleManager.updateProgress(stateUpdated, MissionType.MORNING_HYGIENE, 1)
    }

    fun prepareBreakfast(state: GameState): GameState {
        val routine = state.dailyRoutine.copy(
            hasHadBreakfast = true,
            hungerLevel = 0,
            energyLevel = (state.dailyRoutine.energyLevel + 25).coerceAtMost(100),
            lastActionMessage = "Made a wholesome breakfast. Energized and ready for work!"
        )
        val newStress = (state.stress - 4).coerceAtLeast(0)
        return state.copy(dailyRoutine = routine, stress = newStress)
    }

    fun cookMeal(state: GameState, meal: MealItem): Pair<GameState, String> {
        val cost = meal.cookingCost
        if (state.personalCash < cost) {
            return state to "Insufficient personal cash (₦${state.personalCash}) to buy ingredients for ${meal.name} (costs ₦$cost)."
        }

        val newCash = state.personalCash - cost
        val newEnergy = (state.dailyRoutine.energyLevel + meal.energyBoost).coerceAtMost(100)
        val newStress = (state.stress - meal.stressRelief).coerceAtLeast(0)
        val record = MealConsumptionRecord(
            id = UUID.randomUUID().toString(),
            mealName = meal.name,
            cuisine = meal.cuisine,
            wasCooked = true,
            costPaid = cost,
            dayNumber = state.dailyRoutine.currentDay,
            timeLabel = state.dailyRoutine.timeOfDay.displayName
        )

        val updatedRoutine = state.dailyRoutine.copy(
            hasHadBreakfast = if (state.dailyRoutine.timeOfDay == TimeOfDay.MORNING) true else state.dailyRoutine.hasHadBreakfast,
            hungerLevel = 0,
            energyLevel = newEnergy,
            lastActionMessage = "Cooked delicious homemade ${meal.name}! Saved money and nourished the body."
        )

        val updated = state.copy(
            personalCash = newCash,
            stress = newStress,
            dailyRoutine = updatedRoutine,
            mealHistory = listOf(record) + state.mealHistory
        )

        val withMissionProgress = DailyHustleManager.updateProgress(updated, MissionType.COOK_MEAL, 1)
        return withMissionProgress to "Successfully cooked ${meal.name}! -₦$cost spent on ingredients. Hunger satiated!"
    }

    fun orderTakeoutMeal(state: GameState, meal: MealItem): Pair<GameState, String> {
        val cost = meal.takeoutPrice
        if (state.personalCash < cost) {
            return state to "Insufficient personal cash (₦${state.personalCash}) to order takeout for ${meal.name} (costs ₦$cost)."
        }

        val newCash = state.personalCash - cost
        val newEnergy = (state.dailyRoutine.energyLevel + meal.energyBoost + 5).coerceAtMost(100)
        val newStress = (state.stress - (meal.stressRelief + 5)).coerceAtLeast(0)
        val record = MealConsumptionRecord(
            id = UUID.randomUUID().toString(),
            mealName = meal.name,
            cuisine = meal.cuisine,
            wasCooked = false,
            costPaid = cost,
            dayNumber = state.dailyRoutine.currentDay,
            timeLabel = state.dailyRoutine.timeOfDay.displayName
        )

        val updatedRoutine = state.dailyRoutine.copy(
            hasHadBreakfast = if (state.dailyRoutine.timeOfDay == TimeOfDay.MORNING) true else state.dailyRoutine.hasHadBreakfast,
            hungerLevel = 0,
            energyLevel = newEnergy,
            lastActionMessage = "Order arrived! Feasted on gourmet ${meal.name} takeout in premium packaging."
        )

        val updated = state.copy(
            personalCash = newCash,
            stress = newStress,
            dailyRoutine = updatedRoutine,
            mealHistory = listOf(record) + state.mealHistory
        )

        return updated to "Takeout delivered! Enjoyed delicious ${meal.name}. -₦$cost paid to courier."
    }

    fun takeKidsToSchool(state: GameState): Pair<GameState, String> {
        if (state.familyState.kids.isEmpty()) {
            return state to "You don't have kids yet! Go on a date, get married, and start a family first."
        }

        val updatedKids = state.familyState.kids.map { it.copy(happiness = 100) }
        val updatedFamily = state.familyState.copy(
            kids = updatedKids,
            lastSchoolDropOffDay = state.dailyRoutine.currentDay
        )
        val newReputation = (state.reputation + 4).coerceAtMost(100)
        val updatedRoutine = state.dailyRoutine.copy(
            hasTakenKidsToSchool = true,
            lastActionMessage = "Dropped the kids off safely at school! Proud parent moment."
        )

        val updatedState = state.copy(
            familyState = updatedFamily,
            reputation = newReputation,
            dailyRoutine = updatedRoutine
        )

        val withMission = DailyHustleManager.updateProgress(updatedState, MissionType.TAKE_KIDS_SCHOOL, 1)
        return withMission to "Kids dropped off safely at school! Family happiness at 100%."
    }

    fun workOrRunBusiness(state: GameState): Pair<GameState, String> {
        val energyCost = 25
        if (state.dailyRoutine.energyLevel < energyCost) {
            return state to "You are too tired to work effectively! Eat a meal or take a rest first."
        }

        val newEnergy = (state.dailyRoutine.energyLevel - energyCost).coerceAtLeast(0)

        return if (state.careerTrack == com.example.model.CareerTrack.EMPLOYED_JOB && state.currentJob != null) {
            val dailyPay = state.currentJob.weeklySalary / 5
            val newCash = state.personalCash + dailyPay
            val newReputation = (state.reputation + 2).coerceAtMost(100)
            val updatedRoutine = state.dailyRoutine.copy(
                hasWorkedOrRunBusiness = true,
                energyLevel = newEnergy,
                timeOfDay = TimeOfDay.AFTERNOON,
                lastActionMessage = "Put in a solid shift as ${state.currentJob.title} at ${state.currentJob.company}! Earned daily salary of ₦$dailyPay."
            )
            val resState = state.copy(
                personalCash = newCash,
                reputation = newReputation,
                dailyRoutine = updatedRoutine
            )
            val withShiftMission = DailyHustleManager.updateProgress(resState, MissionType.WORK_SHIFT, 1)
            withShiftMission to "Completed work shift! Earned ₦$dailyPay."
        } else {
            // Business Owner: sells items from inventory
            val unitsToSell = min(5, max(1, state.inventoryUnits))
            val estimatedSales = (state.sellingPrice * unitsToSell)
            val costOfGoods = (state.costPerUnit * unitsToSell)
            val profit = estimatedSales - costOfGoods
            val newBizCash = state.businessCash + estimatedSales
            val newInventory = max(0, state.inventoryUnits - unitsToSell)
            val newReputation = (state.reputation + 3).coerceAtMost(100)
            val updatedRoutine = state.dailyRoutine.copy(
                hasWorkedOrRunBusiness = true,
                energyLevel = newEnergy,
                timeOfDay = TimeOfDay.AFTERNOON,
                lastActionMessage = "Ran shop operations for ${state.business.name}! Sold $unitsToSell ${state.business.itemUnitName} and booked ₦$estimatedSales revenue."
            )
            val resState = state.copy(
                inventoryUnits = newInventory,
                businessCash = newBizCash,
                reputation = newReputation,
                dailyRoutine = updatedRoutine
            )
            val withSalesMission = DailyHustleManager.updateProgress(resState, MissionType.SELL_ITEM, unitsToSell)
            val withShiftMission = DailyHustleManager.updateProgress(withSalesMission, MissionType.WORK_SHIFT, 1)
            withShiftMission to "Ran shop hustle! Sold $unitsToSell ${state.business.itemUnitName} for ₦$estimatedSales revenue."
        }
    }

    fun advanceTime(state: GameState): GameState {
        val nextTime = when (state.dailyRoutine.timeOfDay) {
            TimeOfDay.MORNING -> TimeOfDay.AFTERNOON
            TimeOfDay.AFTERNOON -> TimeOfDay.EVENING
            TimeOfDay.EVENING -> TimeOfDay.NIGHT
            TimeOfDay.NIGHT -> TimeOfDay.MORNING
        }
        val routine = state.dailyRoutine.copy(timeOfDay = nextTime)
        return state.copy(dailyRoutine = routine)
    }

    fun sleepNight(state: GameState): Pair<GameState, String> {
        val nextDay = state.dailyRoutine.currentDay + 1
        val freshTasks = DailyHustleManager.generateDailyTasks(nextDay, state.business)
        val newStreak = state.currentStreakDays + 1
        val newStress = (state.stress - 20).coerceAtLeast(0)

        val updatedRoutine = DailyRoutineState(
            timeOfDay = TimeOfDay.MORNING,
            currentDay = nextDay,
            hasWokenUp = true,
            hasTakenBath = false,
            hasBrushedTeeth = false,
            hasHadBreakfast = false,
            hasWorkedOrRunBusiness = false,
            hasTakenKidsToSchool = false,
            hasSleptNight = true,
            energyLevel = 100,
            hygieneLevel = 80,
            hungerLevel = 40,
            lastActionMessage = "Slept soundly through the night. Woke up on Day $nextDay full of vitality!"
        )

        val updated = state.copy(
            stress = newStress,
            dailyRoutine = updatedRoutine,
            dailyHustles = freshTasks,
            currentStreakDays = newStreak
        )

        return updated to "A good night's sleep restored 100% energy! Welcome to Day $nextDay. Hustle streak: $newStreak days!"
    }

    fun goOnDate(state: GameState, spotName: String, cost: Long): Pair<GameState, String> {
        if (state.personalCash < cost) {
            return state to "Insufficient cash for date (costs ₦$cost)."
        }

        val currentFamily = state.familyState
        val partner = currentFamily.partner ?: PartnerProfile(name = "Amina")
        val newAffection = (partner.affection + 15).coerceAtMost(100)
        val newStatus = when (currentFamily.relationshipStatus) {
            RelationshipStatus.SINGLE -> RelationshipStatus.DATING
            else -> currentFamily.relationshipStatus
        }

        val updatedFamily = currentFamily.copy(
            relationshipStatus = newStatus,
            partner = partner.copy(affection = newAffection)
        )

        val newStress = (state.stress - 15).coerceAtLeast(0)
        val newRep = (state.reputation + 3).coerceAtMost(100)

        val updated = state.copy(
            personalCash = state.personalCash - cost,
            stress = newStress,
            reputation = newRep,
            familyState = updatedFamily,
            dailyRoutine = state.dailyRoutine.copy(
                lastActionMessage = "Romantic date with ${partner.name} at $spotName! Affection soared to $newAffection%."
            )
        )

        return updated to "Had a wonderful date at $spotName with ${partner.name}! Affection: $newAffection%."
    }

    fun proposeAndMarry(state: GameState): Pair<GameState, String> {
        val weddingCost = 150_000L
        if (state.personalCash < weddingCost) {
            return state to "Wedding & dowry costs ₦$weddingCost. Save up from your hustle!"
        }

        val partner = state.familyState.partner ?: PartnerProfile(name = "Amina")
        val updatedFamily = state.familyState.copy(
            relationshipStatus = RelationshipStatus.MARRIED,
            partner = partner.copy(affection = 100),
            weddingHeld = true
        )

        val updated = state.copy(
            personalCash = state.personalCash - weddingCost,
            reputation = (state.reputation + 25).coerceAtMost(100),
            stress = (state.stress - 20).coerceAtLeast(0),
            familyState = updatedFamily,
            dailyRoutine = state.dailyRoutine.copy(
                lastActionMessage = "Traditional & white wedding completed! Happily married to ${partner.name}."
            )
        )

        return updated to "Congratulations! You and ${partner.name} are officially married! 🎉"
    }

    fun haveBaby(state: GameState, babyName: String): Pair<GameState, String> {
        if (state.familyState.relationshipStatus != RelationshipStatus.MARRIED) {
            return state to "Get married first before starting your family!"
        }

        val newKid = KidInfo(
            id = UUID.randomUUID().toString(),
            name = babyName.ifBlank { "Tolu" },
            age = 5,
            schoolGrade = "Primary 1",
            schoolName = "Green Springs Academy",
            avatarEmoji = "🎒",
            happiness = 100
        )

        val updatedKids = state.familyState.kids + newKid
        val updatedFamily = state.familyState.copy(kids = updatedKids)

        val updated = state.copy(
            familyState = updatedFamily,
            reputation = (state.reputation + 10).coerceAtMost(100),
            stress = (state.stress - 5).coerceAtLeast(0),
            dailyRoutine = state.dailyRoutine.copy(
                lastActionMessage = "Blessed with child ${newKid.name}! Family size is now ${updatedKids.size} children."
            )
        )

        return updated to "Welcomed baby ${newKid.name} into the family! Total children: ${updatedKids.size}."
    }

    fun hostParty(state: GameState, party: PartyEvent): Pair<GameState, String> {
        if (state.personalCash < party.cost) {
            return state to "Insufficient personal cash to host ${party.title} (costs ₦${party.cost})."
        }

        val newCash = state.personalCash - party.cost
        val newRep = (state.reputation + party.reputationBoost).coerceAtMost(100)
        val newStress = (state.stress - party.stressReduction).coerceAtLeast(0)

        val updatedRoutine = state.dailyRoutine.copy(
            timeOfDay = TimeOfDay.NIGHT,
            lastActionMessage = "Hosted epic '${party.title}'! All invited guests loved the vibes and food."
        )

        val updated = state.copy(
            personalCash = newCash,
            reputation = newRep,
            stress = newStress,
            activeParty = party,
            dailyRoutine = updatedRoutine
        )

        return updated to "The party '${party.title}' was legendary! +${party.reputationBoost} reputation, -${party.stressReduction} stress!"
    }

    fun goClubbing(state: GameState, venue: ClubbingVenue, isVip: Boolean): Pair<GameState, String> {
        val cost = if (isVip) venue.vipTableCost else venue.regularEntryFee
        if (state.personalCash < cost) {
            val tier = if (isVip) "VIP Table" else "Regular Entry"
            return state to "Insufficient cash for $tier at ${venue.name} (costs ₦$cost)."
        }

        val stressCut = if (isVip) venue.stressReduction + 10 else venue.stressReduction
        val repBoost = if (isVip) 10 else 3

        val updated = state.copy(
            personalCash = state.personalCash - cost,
            stress = (state.stress - stressCut).coerceAtLeast(0),
            reputation = (state.reputation + repBoost).coerceAtMost(100),
            dailyRoutine = state.dailyRoutine.copy(
                timeOfDay = TimeOfDay.NIGHT,
                energyLevel = (state.dailyRoutine.energyLevel - 15).coerceAtLeast(10),
                lastActionMessage = "Hit ${venue.name} for an electric night of dancing and drinks!"
            )
        )

        return updated to "Nightclub vibes at ${venue.name}! Stress evaporated, had maximum fun!"
    }
}
