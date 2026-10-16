package com.example

import com.example.data.FirebaseCommunityRepository
import com.example.data.FirebaseLeaderboardEntry
import com.example.data.RivalChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CompetitiveLeaderboardTest {

    @Test
    fun testWealthTierThresholds() {
        // Test tier boundaries
        assertEquals("👑 Forbes Africa Mogul", getTestWealthTier(15_000_000L))
        assertEquals("💎 Market Tycoon", getTestWealthTier(6_500_000L))
        assertEquals("🥇 Commercial Boss", getTestWealthTier(3_200_000L))
        assertEquals("🥈 Rising Merchant", getTestWealthTier(1_450_000L))
        assertEquals("🥉 Street Hustler", getTestWealthTier(450_000L))
    }

    private fun getTestWealthTier(netWorth: Long): String {
        return when {
            netWorth >= 10_000_000L -> "👑 Forbes Africa Mogul"
            netWorth >= 5_000_000L -> "💎 Market Tycoon"
            netWorth >= 2_500_000L -> "🥇 Commercial Boss"
            netWorth >= 1_000_000L -> "🥈 Rising Merchant"
            else -> "🥉 Street Hustler"
        }
    }

    @Test
    fun testLeaderboardNetWorthDescendingSort() {
        val entries = listOf(
            FirebaseLeaderboardEntry(userId = "p1", userName = "Bayo", netWorth = 800_000L),
            FirebaseLeaderboardEntry(userId = "p2", userName = "Danladi", netWorth = 5_000_000L),
            FirebaseLeaderboardEntry(userId = "p3", userName = "Amaka", netWorth = 2_100_000L),
            FirebaseLeaderboardEntry(userId = "p4", userName = "Blessing", netWorth = 3_800_000L)
        )

        val sorted = entries.sortedByDescending { it.netWorth }

        assertEquals("p2", sorted[0].userId)
        assertEquals("Danladi", sorted[0].userName)
        assertEquals("p4", sorted[1].userId)
        assertEquals("Blessing", sorted[1].userName)
        assertEquals("p3", sorted[2].userId)
        assertEquals("Amaka", sorted[2].userName)
        assertEquals("p1", sorted[3].userId)
        assertEquals("Bayo", sorted[3].userName)
    }

    @Test
    fun testFriendsOnlyFilterLogic() {
        val entries = listOf(
            FirebaseLeaderboardEntry(userId = "me", userName = "Emeka", netWorth = 1_500_000L),
            FirebaseLeaderboardEntry(userId = "friend_1", userName = "Mama Blessing", friendCode = "HH-BLES-4410", netWorth = 3_900_000L),
            FirebaseLeaderboardEntry(userId = "stranger_1", userName = "Stranger Dave", friendCode = "HH-DAVE-9999", netWorth = 9_000_000L),
            FirebaseLeaderboardEntry(userId = "friend_2", userName = "Chinedu", friendCode = "HH-CHIN-7721", netWorth = 2_700_000L)
        )

        val myFriends = setOf("friend_1", "friend_2")
        val myUserId = "me"

        val friendsOnly = entries.filter { entry ->
            entry.userId == myUserId || myFriends.contains(entry.userId) || myFriends.contains(entry.friendCode)
        }.sortedByDescending { it.netWorth }

        assertEquals(3, friendsOnly.size)
        assertFalse(friendsOnly.any { it.userId == "stranger_1" })
        assertEquals("friend_1", friendsOnly[0].userId)
        assertEquals("friend_2", friendsOnly[1].userId)
        assertEquals("me", friendsOnly[2].userId)
    }

    @Test
    fun testRivalChallengeCreationAndProgress() {
        val rival = FirebaseLeaderboardEntry(
            userId = "rival_danladi",
            userName = "Alhaji Danladi",
            userAvatar = "👑",
            netWorth = 4_850_000L
        )

        val myCurrentNetWorth = 2_000_000L
        val target = maxOf(rival.netWorth + 100_000L, (myCurrentNetWorth * 1.15f).toLong())

        val challenge = RivalChallenge(
            rivalId = rival.userId,
            rivalName = rival.userName,
            rivalAvatar = rival.userAvatar,
            rivalNetWorth = rival.netWorth,
            targetNetWorth = target,
            rewardReputation = 12,
            rewardCashBonus = 120_000L,
            description = "Overtake Danladi!"
        )

        assertEquals(4_950_000L, challenge.targetNetWorth)
        assertFalse(myCurrentNetWorth >= challenge.targetNetWorth)

        val myNewNetWorth = 5_100_000L
        assertTrue(myNewNetWorth >= challenge.targetNetWorth)
    }
}
