package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyLifeAndMealData
import com.example.data.FirebaseLeaderboardEntry
import com.example.data.RivalChallenge
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.ui.HustleHeroViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HustleBlue
import com.example.ui.theme.HustleBlueContainer
import com.example.ui.theme.HustleGold
import com.example.ui.theme.HustleGoldContainer
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenContainer
import com.example.ui.theme.HustleGreenDark
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningOrange

enum class LeaderboardScope(val title: String, val subtitle: String, val iconEmoji: String) {
    GLOBAL("Global Tycoons", "Rank all entrepreneurs across Nigeria", "🌍"),
    FRIENDS_ONLY("Friends Circle", "Rank and duel your inner network", "👥")
}

fun LazyListScope.competitiveLeaderboardContent(
    state: GameState,
    viewModel: HustleHeroViewModel
) {
    item {
        CompetitiveLeaderboardSection(
            state = state,
            viewModel = viewModel
        )
    }
}

@Composable
fun CompetitiveLeaderboardSection(
    state: GameState,
    viewModel: HustleHeroViewModel
) {
    val context = LocalContext.current
    var scope by remember { mutableStateOf(LeaderboardScope.GLOBAL) }
    var selectedCity by remember { mutableStateOf("All Cities") }

    val rawLeaderboard by viewModel.leaderboard.collectAsState()
    val friendsList by viewModel.friendsList.collectAsState()
    val activeChallenge by viewModel.activeRivalChallenge.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedEntryForDossier by remember { mutableStateOf<FirebaseLeaderboardEntry?>(null) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showMyCodeDialog by remember { mutableStateOf(false) }

    // Merge current player into the leaderboard view if not present
    val myEntry = remember(state, currentUser) {
        FirebaseLeaderboardEntry(
            userId = currentUser?.uid ?: "my_player_local",
            userName = state.characterName,
            userAvatar = state.character.avatarEmoji,
            businessName = state.business.name,
            cityName = state.cityName,
            netWorth = state.netWorth,
            week = state.week,
            reputation = state.reputation,
            friendCode = viewModel.getPlayerFriendCode(),
            titleTier = when {
                state.netWorth >= 10_000_000L -> "👑 Forbes Africa Mogul"
                state.netWorth >= 5_000_000L -> "💎 Market Tycoon"
                state.netWorth >= 2_500_000L -> "🥇 Commercial Boss"
                state.netWorth >= 1_000_000L -> "🥈 Rising Merchant"
                else -> "🥉 Street Hustler"
            },
            streakDays = state.dailyStreak.currentStreak
        )
    }

    // Filter and sort entries based on scope and city
    val filteredEntries = remember(rawLeaderboard, myEntry, scope, friendsList, selectedCity) {
        val pool = rawLeaderboard.toMutableList()
        // Ensure current player is included in the ranking calculation
        if (pool.none { it.userId == myEntry.userId || (it.userName == myEntry.userName && myEntry.userId.startsWith("my_player")) }) {
            pool.add(myEntry)
        } else {
            // Update player's real-time net worth
            val idx = pool.indexOfFirst { it.userId == myEntry.userId || (it.userName == myEntry.userName && myEntry.userId.startsWith("my_player")) }
            if (idx != -1) {
                pool[idx] = myEntry
            }
        }

        var list = when (scope) {
            LeaderboardScope.GLOBAL -> pool
            LeaderboardScope.FRIENDS_ONLY -> {
                pool.filter { entry ->
                    entry.userId == myEntry.userId ||
                            friendsList.contains(entry.userId) ||
                            (entry.friendCode.isNotEmpty() && friendsList.contains(entry.friendCode))
                }
            }
        }

        if (selectedCity != "All Cities") {
            list = list.filter { it.cityName.equals(selectedCity, ignoreCase = true) }
        }

        list.sortedByDescending { it.netWorth }
    }

    // Calculate current player's standing and distance to next rival
    val myRankIndex = filteredEntries.indexOfFirst {
        it.userId == myEntry.userId || (it.userName == myEntry.userName && myEntry.userId.startsWith("my_player"))
    }
    val myRank = if (myRankIndex >= 0) myRankIndex + 1 else 1
    val rivalAhead = if (myRankIndex > 0) filteredEntries[myRankIndex - 1] else null
    val rivalBehind = if (myRankIndex >= 0 && myRankIndex < filteredEntries.size - 1) filteredEntries[myRankIndex + 1] else null

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. SCOPE SELECTOR (GLOBAL vs. FRIENDS)
        ScopeToggleBar(
            currentScope = scope,
            onSelectScope = { scope = it }
        )

        // 2. CITY FILTER CHIPS
        CityFilterScrollRow(
            selectedCity = selectedCity,
            onSelectCity = { selectedCity = it }
        )

        // 3. ACTIVE RIVALRY CHALLENGE BANNER (if any)
        if (activeChallenge != null) {
            ActiveRivalryBanner(
                challenge = activeChallenge!!,
                currentNetWorth = state.netWorth,
                onClaim = {
                    val (success, msg) = viewModel.claimRivalChallengeReward()
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                },
                onDismiss = { viewModel.dismissRivalChallenge() }
            )
        }

        // 4. MY STANDING & COMPETITIVE GAP CARD
        MyStandingBanner(
            myRank = myRank,
            totalRanked = filteredEntries.size,
            scope = scope,
            netWorth = state.netWorth,
            tier = myEntry.titleTier,
            rivalAhead = rivalAhead,
            rivalBehind = rivalBehind,
            onSync = {
                viewModel.syncToFirebase()
                Toast.makeText(context, "Net worth (₦${CurrencyFormatter.formatNaira(state.netWorth)}) synced to Firebase!", Toast.LENGTH_SHORT).show()
            },
            onShowCode = { showMyCodeDialog = true }
        )

        // 5. FRIENDS MANAGEMENT ACTIONS (Visible in Friends-Only scope)
        if (scope == LeaderboardScope.FRIENDS_ONLY) {
            FriendsActionRow(
                friendCount = friendsList.size,
                onAddFriendClick = { showAddFriendDialog = true },
                onShareCodeClick = { showMyCodeDialog = true }
            )
        }

        // 6. TOP 3 PODIUM SHOWCASE (If >= 3 entries exist)
        if (filteredEntries.size >= 3) {
            PodiumShowcase(
                top1 = filteredEntries[0],
                top2 = filteredEntries[1],
                top3 = filteredEntries[2],
                myUserId = myEntry.userId,
                onInspect = { selectedEntryForDossier = it }
            )
        }

        // 7. LEADERBOARD LIST
        Text(
            text = when (scope) {
                LeaderboardScope.GLOBAL -> "🌐 All Ranked Entrepreneurs (${filteredEntries.size})"
                LeaderboardScope.FRIENDS_ONLY -> "👥 Your Friends & Circle Rankings (${filteredEntries.size})"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        if (filteredEntries.isEmpty()) {
            EmptyFriendsCircleCard(
                onAddFriends = { showAddFriendDialog = true }
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredEntries.forEachIndexed { index, entry ->
                    val rank = index + 1
                    val isMe = entry.userId == myEntry.userId || (entry.userName == myEntry.userName && myEntry.userId.startsWith("my_player"))
                    val isFriend = friendsList.contains(entry.userId) || (entry.friendCode.isNotEmpty() && friendsList.contains(entry.friendCode))

                    LeaderboardEntrepreneurCard(
                        rank = rank,
                        entry = entry,
                        isMe = isMe,
                        isFriend = isFriend,
                        onClick = { selectedEntryForDossier = entry },
                        onToggleFriend = { viewModel.toggleFriend(entry) }
                    )
                }
            }
        }
    }

    // DIALOGS
    if (showAddFriendDialog) {
        AddFriendDialog(
            viewModel = viewModel,
            onDismiss = { showAddFriendDialog = false }
        )
    }

    if (showMyCodeDialog) {
        MyFriendCodeDialog(
            code = viewModel.getPlayerFriendCode(),
            playerName = state.characterName,
            onDismiss = { showMyCodeDialog = false }
        )
    }

    if (selectedEntryForDossier != null) {
        EntrepreneurDossierDialog(
            entry = selectedEntryForDossier!!,
            isMe = selectedEntryForDossier!!.userId == myEntry.userId || (selectedEntryForDossier!!.userName == myEntry.userName && myEntry.userId.startsWith("my_player")),
            isFriend = friendsList.contains(selectedEntryForDossier!!.userId) || (selectedEntryForDossier!!.friendCode.isNotEmpty() && friendsList.contains(selectedEntryForDossier!!.friendCode)),
            onToggleFriend = {
                viewModel.toggleFriend(selectedEntryForDossier!!)
            },
            onChallengeRival = {
                val msg = viewModel.launchRivalChallenge(selectedEntryForDossier!!)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                selectedEntryForDossier = null
            },
            onDismiss = { selectedEntryForDossier = null }
        )
    }
}

@Composable
fun ScopeToggleBar(
    currentScope: LeaderboardScope,
    onSelectScope: (LeaderboardScope) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            LeaderboardScope.entries.forEach { item ->
                val isSelected = currentScope == item
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) HustleGreen else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectScope(item) }
                        .testTag("scope_tab_${item.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) PureWhite else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CityFilterScrollRow(
    selectedCity: String,
    onSelectCity: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val cities = listOf("All Cities") + DailyLifeAndMealData.cities

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        cities.forEach { city ->
            val isSelected = selectedCity == city
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) HustleGoldContainer else CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) HustleGold else BorderSubtle
                ),
                modifier = Modifier
                    .clickable { onSelectCity(city) }
                    .testTag("city_filter_${city.replace(" ", "_").lowercase()}")
            ) {
                Text(
                    text = city,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) HustleGoldDark else TextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun MyStandingBanner(
    myRank: Int,
    totalRanked: Int,
    scope: LeaderboardScope,
    netWorth: Long,
    tier: String,
    rivalAhead: FirebaseLeaderboardEntry?,
    rivalBehind: FirebaseLeaderboardEntry?,
    onSync: () -> Unit,
    onShowCode: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HustleGoldContainer.copy(alpha = 0.55f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HustleGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_standing_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = HustleGold,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (myRank <= 3) "👑" else "#$myRank",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "YOUR STANDING (${scope.title})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = HustleGoldDark,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rank #$myRank of $totalRanked",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tier,
                                fontSize = 10.sp,
                                color = HustleGoldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Total Net Worth", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = CurrencyFormatter.formatNaira(netWorth),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PositiveGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Competitive gap / rivalry advice
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PureWhite.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Grind Tip",
                        tint = HustleGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            myRank == 1 -> "👑 You lead the leaderboard! Keep restocking and closing deals to protect your throne."
                            rivalAhead != null -> {
                                val gap = rivalAhead.netWorth - netWorth
                                "⚡ Need ${CurrencyFormatter.formatNaira(gap)} more to overtake ${rivalAhead.userName} (#${myRank - 1})!"
                            }
                            else -> "Grind daily hustles and expand shop inventory to climb higher!"
                        },
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons: Sync & Share Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSync,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HustleGreenDark),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sync_net_worth_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync My Score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onShowCode,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_friend_code_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("My Friend Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

@Composable
fun PodiumShowcase(
    top1: FirebaseLeaderboardEntry,
    top2: FirebaseLeaderboardEntry,
    top3: FirebaseLeaderboardEntry,
    myUserId: String,
    onInspect: (FirebaseLeaderboardEntry) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("podium_showcase")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 ENTREPRENEUR PODIUM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = HustleGoldDark,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Top 3 Tycoons",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2ND PLACE (SILVER)
                PodiumColumn(
                    rank = 2,
                    entry = top2,
                    badgeEmoji = "🥈",
                    accentColor = Color(0xFF94A3B8),
                    podiumHeight = 75,
                    isMe = top2.userId == myUserId,
                    onInspect = { onInspect(top2) }
                )

                // 1ST PLACE (GOLD)
                PodiumColumn(
                    rank = 1,
                    entry = top1,
                    badgeEmoji = "🥇",
                    accentColor = HustleGold,
                    podiumHeight = 100,
                    isMe = top1.userId == myUserId,
                    onInspect = { onInspect(top1) }
                )

                // 3RD PLACE (BRONZE)
                PodiumColumn(
                    rank = 3,
                    entry = top3,
                    badgeEmoji = "🥉",
                    accentColor = Color(0xFFB45309),
                    podiumHeight = 60,
                    isMe = top3.userId == myUserId,
                    onInspect = { onInspect(top3) }
                )
            }
        }
    }
}

@Composable
fun PodiumColumn(
    rank: Int,
    entry: FirebaseLeaderboardEntry,
    badgeEmoji: String,
    accentColor: Color,
    podiumHeight: Int,
    isMe: Boolean,
    onInspect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(96.dp)
            .clickable { onInspect() }
            .testTag("podium_rank_$rank")
    ) {
        Text(text = badgeEmoji, fontSize = if (rank == 1) 22.sp else 16.sp)

        Box(
            modifier = Modifier
                .size(if (rank == 1) 48.dp else 42.dp)
                .background(accentColor.copy(alpha = 0.2f), CircleShape)
                .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.userAvatar, fontSize = if (rank == 1) 24.sp else 20.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.userName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMe) PositiveGreen else TextPrimary,
            maxLines = 1
        )

        Text(
            text = CurrencyFormatter.formatNaira(entry.netWorth),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PositiveGreen,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Tier Block
        Surface(
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = accentColor.copy(alpha = 0.25f),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
            }
        }
    }
}

@Composable
fun FriendsActionRow(
    friendCount: Int,
    onAddFriendClick: () -> Unit,
    onShareCodeClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HustleBlueContainer.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, HustleBlue.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👥 Friends Circle ($friendCount)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HustleBlue
                )
                Text(
                    text = "Compete with friends & trade rivals",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onShareCodeClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HustleBlue),
                    modifier = Modifier.testTag("circle_share_code")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Code", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAddFriendClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleBlue),
                    modifier = Modifier.testTag("circle_add_friend")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Friend", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

@Composable
fun LeaderboardEntrepreneurCard(
    rank: Int,
    entry: FirebaseLeaderboardEntry,
    isMe: Boolean,
    isFriend: Boolean,
    onClick: () -> Unit,
    onToggleFriend: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) HustleGoldContainer.copy(alpha = 0.35f) else CardSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMe) HustleGold else BorderSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isMe) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("leaderboard_card_$rank")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number or Medal
            Text(
                text = when (rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#$rank"
                },
                fontSize = if (rank <= 3) 20.sp else 13.sp,
                fontWeight = FontWeight.Black,
                color = if (rank <= 3) HustleGoldDark else TextSecondary,
                modifier = Modifier.width(34.dp),
                textAlign = TextAlign.Center
            )

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(HustleGreenContainer, CircleShape)
                    .border(1.dp, HustleGreen.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = entry.userAvatar, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isMe) "${entry.userName} (You)" else entry.userName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMe) PositiveGreen else TextPrimary
                    )
                    if (isFriend && !isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Friend",
                            tint = HustleGoldDark,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Text(
                    text = "${entry.businessName} • ${entry.cityName} • Wk ${entry.week}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (entry.titleTier.isNotEmpty()) {
                        Text(
                            text = entry.titleTier,
                            fontSize = 9.sp,
                            color = HustleGoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (entry.streakDays > 0) {
                        Text(
                            text = "🔥 ${entry.streakDays}d",
                            fontSize = 9.sp,
                            color = WarningOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Financial stats & quick friend toggle
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatNaira(entry.netWorth),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PositiveGreen
                )
                Text(
                    text = "Rep: ${entry.reputation}%",
                    fontSize = 10.sp,
                    color = TextMuted
                )

                if (!isMe) {
                    Text(
                        text = if (isFriend) "Friend ⭐" else "+ Add",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFriend) HustleGoldDark else HustleGreen,
                        modifier = Modifier
                            .clickable { onToggleFriend() }
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveRivalryBanner(
    challenge: RivalChallenge,
    currentNetWorth: Long,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val isCompleted = currentNetWorth >= challenge.targetNetWorth
    val progress = (currentNetWorth.toFloat() / challenge.targetNetWorth.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) PositiveGreen.copy(alpha = 0.15f) else Color(0xFFFEF3C7)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isCompleted) PositiveGreen else HustleGoldDark
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_rivalry_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚔️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "ACTIVE NET WORTH DUEL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) PositiveGreen else HustleGoldDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Versus ${challenge.rivalName} ${challenge.rivalAvatar}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp), tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = challenge.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Current: ${CurrencyFormatter.formatNaira(currentNetWorth)}", fontSize = 10.sp, color = TextMuted)
                Text(text = "Goal: ${CurrencyFormatter.formatNaira(challenge.targetNetWorth)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isCompleted) PositiveGreen else HustleGold,
                trackColor = BorderSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isCompleted) {
                Button(
                    onClick = onClaim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PositiveGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("claim_rivalry_reward_button")
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🎉 Defeated! Claim ₦120,000 + 12 Rep", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            } else {
                Text(
                    text = "Bonus: ₦${CurrencyFormatter.formatNaira(challenge.rewardCashBonus)} + ${challenge.rewardReputation} Rep when goal is met!",
                    fontSize = 10.sp,
                    color = HustleGoldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EmptyFriendsCircleCard(
    onAddFriends: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "👥", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Friends Circle is Empty",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add friends using their unique Entrepreneur Friend Code, or add rival traders from Lagos and Kano to kickstart private net worth competitions!",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAddFriends,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleBlue)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Friends or Rivals", fontWeight = FontWeight.Bold, color = PureWhite)
            }
        }
    }
}

@Composable
fun AddFriendDialog(
    viewModel: HustleHeroViewModel,
    onDismiss: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val suggestedRivals = listOf(
        "Mama Blessing" to "HH-BLES-4410",
        "Alhaji Danladi" to "HH-DANL-9812",
        "Chinedu Okeke" to "HH-CHIN-7721",
        "Amaka Fintech" to "HH-AMAK-3305",
        "Chef Basira" to "HH-BASI-5529"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🤝 Add to Friends Circle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter a friend's 6-character Entrepreneur Code or select a rival trader to compare net worth:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        feedbackMessage = null
                    },
                    label = { Text("Friend Code (e.g. HH-BLES-4410)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_code_input")
                )

                if (feedbackMessage != null) {
                    Text(
                        text = feedbackMessage!!,
                        fontSize = 11.sp,
                        color = if (isError) DangerRed else PositiveGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Quick Add Top Rivals:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestedRivals.forEach { (name, code) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.clickable {
                                codeInput = code
                            }
                        ) {
                            Text(
                                text = "$name ($code)",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (success, msg) = viewModel.addFriendByCode(codeInput)
                    feedbackMessage = msg
                    isError = !success
                    if (success) {
                        codeInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                modifier = Modifier.testTag("confirm_add_friend_btn")
            ) {
                Text("Add Friend")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun MyFriendCodeDialog(
    code: String,
    playerName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📋 My Entrepreneur Friend Code", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Share this code with your friends so they can add $playerName to their private Net Worth Leaderboard:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HustleGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, HustleGold),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = code,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Hustle Hero Friend Code", code)
                        clipboard.setPrimaryClip(clip)
                        copied = true
                        Toast.makeText(context, "Friend code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (copied) PositiveGreen else HustleGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(if (copied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (copied) "Copied to Clipboard!" else "Copy Friend Code", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun EntrepreneurDossierDialog(
    entry: FirebaseLeaderboardEntry,
    isMe: Boolean,
    isFriend: Boolean,
    onToggleFriend: () -> Unit,
    onChallengeRival: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(HustleGreenContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = entry.userAvatar, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = entry.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${entry.businessName} • ${entry.cityName}", fontSize = 11.sp, color = TextMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tier & Net Worth Hero
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Net Worth", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = CurrencyFormatter.formatNaira(entry.netWorth),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PositiveGreen
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HustleGoldContainer
                        ) {
                            Text(
                                text = entry.titleTier.ifEmpty { "Merchant" },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGoldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Stats breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(title = "Reputation", value = "${entry.reputation}%", modifier = Modifier.weight(1f))
                    StatPill(title = "Turn Week", value = "Wk ${entry.week}", modifier = Modifier.weight(1f))
                    if (entry.friendCode.isNotEmpty()) {
                        StatPill(title = "Code", value = entry.friendCode, modifier = Modifier.weight(1f))
                    }
                }

                if (!isMe) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // Rivalry duel button
                    Button(
                        onClick = onChallengeRival,
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("challenge_rival_btn")
                    ) {
                        Text("⚔️ Challenge to Net Worth Duel", fontWeight = FontWeight.Bold, color = PureWhite)
                    }

                    // Friend toggle button
                    OutlinedButton(
                        onClick = onToggleFriend,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isFriend) DangerRed else HustleGreenDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isFriend) "Remove from Friends Circle" else "⭐ Add to Friends Circle", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun StatPill(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
        }
    }
}
