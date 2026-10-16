package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FirebaseLeaderboardEntry(
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "👨🏾‍💻",
    val businessName: String = "",
    val cityName: String = "Lagos",
    val netWorth: Long = 0L,
    val week: Int = 1,
    val reputation: Int = 50,
    val friendCode: String = "",
    val titleTier: String = "Rising Merchant",
    val updatedAt: String = "",
    val streakDays: Int = 1
)

data class RivalChallenge(
    val rivalId: String,
    val rivalName: String,
    val rivalAvatar: String,
    val rivalNetWorth: Long,
    val targetNetWorth: Long,
    val rewardReputation: Int = 12,
    val rewardCashBonus: Long = 100_000L,
    val description: String
)

data class FirebaseUserAchievement(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "👨🏾‍💻",
    val achievementTitle: String = "",
    val description: String = "",
    val businessName: String = "",
    val netWorth: Long = 0L,
    val weekNumber: Int = 1,
    val createdAt: String = ""
)

class FirebaseCommunityRepository(private val context: Context) {

    val authManager = FirebaseAuthManager(context)

    private val firestore: FirebaseFirestore by lazy {
        val databaseId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(databaseId)
    }

    val currentUser: FirebaseUser?
        get() = authManager.currentUser

    private val prefs = context.getSharedPreferences("hustle_hero_friends", Context.MODE_PRIVATE)

    fun getSavedFriendIds(): Set<String> {
        val saved = prefs.getStringSet("friend_ids", null)
        return saved ?: setOf("top_2", "top_3", "top_4") // Mama Blessing, Chinedu, Amaka as default circle
    }

    fun saveFriendId(friendId: String) {
        val current = getSavedFriendIds().toMutableSet()
        current.add(friendId)
        prefs.edit().putStringSet("friend_ids", current).apply()
    }

    fun removeFriendId(friendId: String) {
        val current = getSavedFriendIds().toMutableSet()
        current.remove(friendId)
        prefs.edit().putStringSet("friend_ids", current).apply()
    }

    fun computeWealthTier(netWorth: Long): String {
        return when {
            netWorth >= 10_000_000L -> "👑 Forbes Africa Mogul"
            netWorth >= 5_000_000L -> "💎 Market Tycoon"
            netWorth >= 2_500_000L -> "🥇 Commercial Boss"
            netWorth >= 1_000_000L -> "🥈 Rising Merchant"
            else -> "🥉 Street Hustler"
        }
    }

    fun generateFriendCode(userId: String, userName: String): String {
        val cleanName = userName.filter { it.isLetter() }.take(4).uppercase().ifEmpty { "HERO" }
        val hash = kotlin.math.abs((userId + userName).hashCode() % 9000 + 1000)
        return "HH-$cleanName-$hash"
    }

    fun getInitialFallbackLeaderboard(): List<FirebaseLeaderboardEntry> {
        return listOf(
            FirebaseLeaderboardEntry(
                userId = "top_1",
                userName = "Alhaji Danladi",
                userAvatar = "👑",
                businessName = "Danladi Agro Wholesale",
                cityName = "Kano",
                netWorth = 4_850_000L,
                week = 38,
                reputation = 94,
                friendCode = "HH-DANL-9812",
                titleTier = "💎 Market Tycoon"
            ),
            FirebaseLeaderboardEntry(
                userId = "top_2",
                userName = "Mama Blessing",
                userAvatar = "👩🏾‍🦱",
                businessName = "Blessing Mega Mart",
                cityName = "Lagos",
                netWorth = 3_920_000L,
                week = 32,
                reputation = 88,
                friendCode = "HH-BLES-4410",
                titleTier = "🥇 Commercial Boss"
            ),
            FirebaseLeaderboardEntry(
                userId = "top_3",
                userName = "Chinedu Okeke",
                userAvatar = "🧔🏾‍♂️",
                businessName = "Alaba Tech Hub",
                cityName = "Lagos",
                netWorth = 2_750_000L,
                week = 24,
                reputation = 82,
                friendCode = "HH-CHIN-7721",
                titleTier = "🥇 Commercial Boss"
            ),
            FirebaseLeaderboardEntry(
                userId = "top_4",
                userName = "Amaka Fintech",
                userAvatar = "👩🏾‍💼",
                businessName = "QuickCash Point",
                cityName = "Abuja",
                netWorth = 1_980_000L,
                week = 19,
                reputation = 79,
                friendCode = "HH-AMAK-3305",
                titleTier = "🥈 Rising Merchant"
            ),
            FirebaseLeaderboardEntry(
                userId = "top_5",
                userName = "Chef Basira",
                userAvatar = "👩🏾‍🍳",
                businessName = "Basira Kitchen",
                cityName = "Ibadan",
                netWorth = 1_420_000L,
                week = 15,
                reputation = 75,
                friendCode = "HH-BASI-5529",
                titleTier = "🥈 Rising Merchant"
            ),
            FirebaseLeaderboardEntry(
                userId = "top_6",
                userName = "Bayo Cuts",
                userAvatar = "💇🏾‍♂️",
                businessName = "Bayo VIP Grooming",
                cityName = "Lagos",
                netWorth = 980_000L,
                week = 11,
                reputation = 71,
                friendCode = "HH-BAYO-8802",
                titleTier = "🥉 Street Hustler"
            )
        )
    }

    fun getInitialFallbackAchievements(): List<FirebaseUserAchievement> {
        return listOf(
            FirebaseUserAchievement(
                id = "ach_1",
                userId = "user_1",
                userName = "Alhaji Danladi",
                userAvatar = "👑",
                achievementTitle = "🌞 Solar Power Independence!",
                description = "Installed full Solar Inverter setup. Zero fuel generator stress during grid collapse!",
                businessName = "Danladi Agro Wholesale",
                netWorth = 4_850_000L,
                weekNumber = 36,
                createdAt = "Just now"
            ),
            FirebaseUserAchievement(
                id = "ach_2",
                userId = "user_2",
                userName = "Mama Blessing",
                userAvatar = "👩🏾‍🦱",
                achievementTitle = "🎉 Collected ₦250K Ajo Pot!",
                description = "Turn arrived in the 5-week commercial Esusu group. Instant capital boost!",
                businessName = "Blessing Mega Mart",
                netWorth = 3_920_000L,
                weekNumber = 30,
                createdAt = "2h ago"
            ),
            FirebaseUserAchievement(
                id = "ach_3",
                userId = "user_3",
                userName = "Chinedu Okeke",
                userAvatar = "🧔🏾‍♂️",
                achievementTitle = "📺 Prime-Time TV Ad Broadcast",
                description = "Aired 30-second commercial on Channels TV! Customer foot traffic leaped +45%.",
                businessName = "Alaba Tech Hub",
                netWorth = 2_750_000L,
                weekNumber = 22,
                createdAt = "5h ago"
            ),
            FirebaseUserAchievement(
                id = "ach_4",
                userId = "user_4",
                userName = "Amaka Fintech",
                userAvatar = "👩🏾‍💼",
                achievementTitle = "💳 750 Credit Score Unlocked",
                description = "Repaid all micro-loans on time. Low bank interest tier achieved!",
                businessName = "QuickCash Point",
                netWorth = 1_980_000L,
                weekNumber = 18,
                createdAt = "1d ago"
            )
        )
    }

    fun observeLeaderboard(): Flow<List<FirebaseLeaderboardEntry>> = callbackFlow {
        val fallback = getInitialFallbackLeaderboard()
        trySend(fallback)

        var listener: ListenerRegistration? = null

        val authListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            listener?.remove()

            if (user != null) {
                listener = firestore.collection("leaderboard")
                    .orderBy("netWorth", Query.Direction.DESCENDING)
                    .limit(20)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w("FirebaseCommunityRepo", "Leaderboard stream notice: ${error.message}")
                            trySend(fallback)
                            return@addSnapshotListener
                        }
                        if (snapshot == null || snapshot.isEmpty) {
                            trySend(fallback)
                            return@addSnapshotListener
                        }

                        val list = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(FirebaseLeaderboardEntry::class.java)
                        }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(fallback)
                        }
                    }
            } else {
                trySend(fallback)
            }
        }

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        auth.addAuthStateListener(authListener)

        awaitClose {
            listener?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    fun observeAchievements(): Flow<List<FirebaseUserAchievement>> = callbackFlow {
        val fallback = getInitialFallbackAchievements()
        trySend(fallback)

        var listener: ListenerRegistration? = null

        val authListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            listener?.remove()

            if (user != null) {
                listener = firestore.collection("achievements")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(25)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w("FirebaseCommunityRepo", "Achievements stream notice: ${error.message}")
                            trySend(fallback)
                            return@addSnapshotListener
                        }
                        if (snapshot == null || snapshot.isEmpty) {
                            trySend(fallback)
                            return@addSnapshotListener
                        }

                        val list = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(FirebaseUserAchievement::class.java)
                        }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(fallback)
                        }
                    }
            } else {
                trySend(fallback)
            }
        }

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        auth.addAuthStateListener(authListener)

        awaitClose {
            listener?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    fun syncPlayerScore(
        userName: String,
        userAvatar: String,
        businessName: String,
        cityName: String,
        netWorth: Long,
        week: Int,
        reputation: Int,
        streakDays: Int = 1
    ) {
        val user = currentUser ?: return
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())

        val friendCode = generateFriendCode(user.uid, userName)
        val titleTier = computeWealthTier(netWorth)

        val entry = FirebaseLeaderboardEntry(
            userId = user.uid,
            userName = userName,
            userAvatar = userAvatar,
            businessName = businessName,
            cityName = cityName,
            netWorth = netWorth,
            week = week,
            reputation = reputation,
            friendCode = friendCode,
            titleTier = titleTier,
            updatedAt = now,
            streakDays = streakDays
        )

        firestore.collection("leaderboard")
            .document(user.uid)
            .set(entry)
            .addOnFailureListener { e ->
                Log.w("FirebaseCommunityRepo", "Failed to sync player score: ${e.message}")
            }
    }

    fun broadcastMilestone(
        userName: String,
        userAvatar: String,
        achievementTitle: String,
        description: String,
        businessName: String,
        netWorth: Long,
        weekNumber: Int
    ) {
        val user = currentUser ?: return
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())

        val item = FirebaseUserAchievement(
            id = "ach_${System.currentTimeMillis()}",
            userId = user.uid,
            userName = userName,
            userAvatar = userAvatar,
            achievementTitle = achievementTitle,
            description = description,
            businessName = businessName,
            netWorth = netWorth,
            weekNumber = weekNumber,
            createdAt = now
        )

        firestore.collection("achievements")
            .document(item.id)
            .set(item)
            .addOnFailureListener { e ->
                Log.w("FirebaseCommunityRepo", "Failed to broadcast milestone: ${e.message}")
            }
    }
}
