package com.example

import com.example.data.BusinessData
import com.example.logic.GameEngine
import com.example.model.CurrencyFormatter
import com.example.model.GamePhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testInitialGameState() {
        val biz = BusinessData.startingBusinesses.first()
        val defaultChar = BusinessData.characters.first()
        val state = GameEngine.createInitialState("Chidi", biz, "Lagos", defaultChar)

        assertEquals("Chidi", state.characterName)
        assertEquals("Lagos", state.cityName)
        assertEquals(1, state.week)
        assertEquals(biz.startingCapital + defaultChar.startingCapitalBonus, state.businessCash)
        assertEquals(35_000L, state.personalCash)
        assertEquals(20_000L, state.personalSavings)
        assertEquals(GamePhase.DASHBOARD, state.currentPhase)
        assertTrue(state.netWorth > 0)
        assertTrue(state.communityMembers.isNotEmpty())
        assertTrue(state.communityProposals.isNotEmpty())
    }

    @Test
    fun testCurrencyFormatting() {
        assertEquals("₦50,000", CurrencyFormatter.formatNaira(50_000L))
        assertEquals("+₦25,000", CurrencyFormatter.formatSignedNaira(25_000L))
        assertEquals("-₦10,000", CurrencyFormatter.formatSignedNaira(-10_000L))
        assertEquals("₦1.50M", CurrencyFormatter.formatCompact(1_500_000L))
        assertEquals("₦45.0K", CurrencyFormatter.formatCompact(45_000L))
    }

    @Test
    fun testWeeklyTurnSimulationWithAds() {
        val biz = BusinessData.startingBusinesses.first()
        val state = GameEngine.createInitialState("Bimbo", biz, "Lagos")

        val ad = BusinessData.getAvailableAds().first().copy(isActive = true, weeksRemaining = 3)
        val stateWithAd = state.copy(activeAds = listOf(ad))

        val nextState = GameEngine.simulateWeeklyTurn(
            state = stateWithAd,
            chosenEvent = null,
            choice = null,
            salaryWithdrawal = 15_000L
        )

        assertEquals(2, nextState.week)
        assertEquals(GamePhase.SUMMARY, nextState.currentPhase)
        assertNotNull(nextState.latestReport)
        assertEquals(1, nextState.turnHistory.size)
        // Ad weeks remaining decremented
        assertEquals(2, nextState.activeAds.first().weeksRemaining)
    }
}
