package com.example

import com.example.data.DailyLifeAndMealData
import com.example.logic.DailyHustleManager
import com.example.logic.EverydayLifeManager
import com.example.logic.TurnCycleManager
import com.example.model.CareerTrack
import com.example.model.MissionType
import com.example.model.RelationshipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyHustleLogicTest {

    @Test
    fun testGenerateDailyTasksIncludesBusinessUnit() {
        val initial = TurnCycleManager.createInitialState()
        val tasks = DailyHustleManager.generateDailyTasks(day = 1, business = initial.business)
        
        assertEquals(4, tasks.size)
        val salesTask = tasks.find { it.missionType == MissionType.SELL_ITEM }
        assertNotNull(salesTask)
        assertTrue(salesTask!!.title.contains(initial.business.itemUnitName))
        assertEquals(5, salesTask.targetQuantity)
        assertFalse(salesTask.isCompleted)
    }

    @Test
    fun testTackleStreetGigRewardsCash() {
        val initial = TurnCycleManager.createInitialState()
        val streetGig = initial.dailyHustles.find { it.missionType == MissionType.CLIENT_GIG }
        assertNotNull(streetGig)

        val startingCash = initial.personalCash
        val (nextState, msg) = DailyHustleManager.tackleTaskDirect(initial, streetGig!!.id)

        assertEquals(startingCash + streetGig.rewardCash, nextState.personalCash)
        assertTrue(nextState.dailyHustles.find { it.id == streetGig.id }!!.isCompleted)
        assertTrue(msg.contains("claimed"))
    }

    @Test
    fun testEverydayLifeSleepAndMorningCycle() {
        val initial = TurnCycleManager.createInitialState()
        val (morningState, _) = EverydayLifeManager.sleepNight(initial)

        assertEquals(2, morningState.dailyRoutine.currentDay)
        assertEquals(100, morningState.dailyRoutine.energyLevel)
        assertTrue(morningState.dailyRoutine.hasWokenUp)
        assertFalse(morningState.dailyRoutine.hasTakenBath)

        // Bathing
        val bathedState = EverydayLifeManager.takeBath(morningState)
        assertTrue(bathedState.dailyRoutine.hasTakenBath)
        assertEquals(100, bathedState.dailyRoutine.hygieneLevel)

        // Brushing teeth
        val brushedState = EverydayLifeManager.brushTeeth(bathedState)
        assertTrue(brushedState.dailyRoutine.hasBrushedTeeth)
    }

    @Test
    fun testFamilyDatingAndChildren() {
        val initial = TurnCycleManager.createInitialState()
        val richState = initial.copy(personalCash = 500_000L)

        // Date
        val (datedState, _) = EverydayLifeManager.goOnDate(richState, "Breeze Beach Club", 20_000L)
        assertEquals(RelationshipStatus.DATING, datedState.familyState.relationshipStatus)

        // Propose & Marry
        val (marriedState, _) = EverydayLifeManager.proposeAndMarry(datedState)
        assertEquals(RelationshipStatus.MARRIED, marriedState.familyState.relationshipStatus)

        // Have Kid
        val (familyWithKid, _) = EverydayLifeManager.haveBaby(marriedState, "Kamsi")
        assertEquals(1, familyWithKid.familyState.kids.size)
        assertEquals("Kamsi", familyWithKid.familyState.kids.first().name)
    }
}
