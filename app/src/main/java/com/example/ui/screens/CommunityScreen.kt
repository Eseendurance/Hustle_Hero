package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirebaseLeaderboardEntry
import com.example.data.FirebaseUserAchievement
import com.example.model.CommunityMember
import com.example.model.CommunityProposal
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.MarketGoogleMap
import com.example.ui.components.competitiveLeaderboardContent
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

enum class CommunityTab {
    LEADERBOARD,
    ACHIEVEMENTS,
    GOOGLE_MAPS,
    UNION_PROPOSALS
}

@Composable
fun CommunityScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var activeTab by remember { mutableStateOf(CommunityTab.LEADERBOARD) }
    val leaderboard by viewModel.leaderboard.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val authError by viewModel.authError.collectAsState()

    var isSigningIn by remember { mutableStateOf(false) }
    var syncSuccessMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("community_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Community Dashboard Header
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = HustleGreenContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌍", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMMUNITY DASHBOARD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGreenDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Street Moguls & Trader Network",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Track global leaderboard rankings, shared weekly achievements, and map hubs.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Firebase Sync Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PureWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen),
                        modifier = Modifier
                            .clickable {
                                viewModel.syncToFirebase()
                                syncSuccessMsg = "Synced!"
                            }
                            .testTag("sync_score_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = HustleGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = syncSuccessMsg ?: "Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                        }
                    }
                }
            }
        }

        // GOOGLE ACCOUNT & FIREBASE AUTHENTICATION CARD
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser != null) PureWhite else HustleGoldContainer.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (currentUser != null) HustleGreen.copy(alpha = 0.5f) else HustleGold
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_status_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (currentUser != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(HustleGreenContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = HustleGreenDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentUser?.displayName ?: currentUser?.email ?: "Google Entrepreneur",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(PositiveGreen, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Firebase Firestore Connected",
                                            fontSize = 10.sp,
                                            color = PositiveGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.broadcastAchievement(
                                            title = "🚀 ${state.business.name} Milestone!",
                                            description = "${state.characterName} achieved ${CurrencyFormatter.formatNaira(state.netWorth)} net worth in ${state.cityName} in Week ${state.week}!"
                                        )
                                        syncSuccessMsg = "Broadcasted!"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("broadcast_milestone_button")
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = HustleGreen, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Broadcast", fontSize = 10.sp, color = HustleGreenDark, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.signOut() },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("sign_out_button")
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Exit", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    } else {
                        // Unauthenticated prompt with visible Sign In With Google button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connect with Google",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Join the live Firebase cloud leaderboard and post weekly achievements.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = {
                                    if (activity != null) {
                                        isSigningIn = true
                                        viewModel.signInWithGoogle(activity) { success ->
                                            isSigningIn = false
                                        }
                                    }
                                },
                                enabled = !isSigningIn,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                modifier = Modifier.testTag("google_sign_in_button")
                            ) {
                                if (isSigningIn) {
                                    CircularProgressIndicator(
                                        color = PureWhite,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "G", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PureWhite)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                                    }
                                }
                            }
                        }

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = authError ?: "",
                                fontSize = 10.sp,
                                color = DangerRed
                            )
                        }
                    }
                }
            }
        }

        // 3D View Shortcut in Community
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = HustleGreenContainer.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateToPhase(com.example.model.GamePhase.MARKET_3D) }
                    .testTag("community_enter_3d_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore 3D Market Plaza & Character Avatars",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark
                        )
                    }
                    Text(text = "Enter 3D ->", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                }
            }
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CommunitySubTab(
                    title = "🏆 Leaderboard",
                    isSelected = activeTab == CommunityTab.LEADERBOARD,
                    modifier = Modifier.weight(1f)
                ) { activeTab = CommunityTab.LEADERBOARD }

                CommunitySubTab(
                    title = "🌟 Achievements",
                    isSelected = activeTab == CommunityTab.ACHIEVEMENTS,
                    modifier = Modifier.weight(1f)
                ) { activeTab = CommunityTab.ACHIEVEMENTS }

                CommunitySubTab(
                    title = "🗺️ Google Map",
                    isSelected = activeTab == CommunityTab.GOOGLE_MAPS,
                    modifier = Modifier.weight(1f)
                ) { activeTab = CommunityTab.GOOGLE_MAPS }

                CommunitySubTab(
                    title = "🏛️ Union",
                    isSelected = activeTab == CommunityTab.UNION_PROPOSALS,
                    modifier = Modifier.weight(1f)
                ) { activeTab = CommunityTab.UNION_PROPOSALS }
            }
        }

        // TAB 1: COMPETITIVE LEADERBOARD (GLOBAL & FRIENDS-ONLY)
        if (activeTab == CommunityTab.LEADERBOARD) {
            competitiveLeaderboardContent(
                state = state,
                viewModel = viewModel
            )
        }

        // TAB 2: ACHIEVEMENTS FEED
        if (activeTab == CommunityTab.ACHIEVEMENTS) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌟 Weekly Community Achievements",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${achievements.size} Milestones",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            items(achievements) { item ->
                AchievementCardItem(achievement = item)
            }
        }

        // TAB 3: GOOGLE MAPS
        if (activeTab == CommunityTab.GOOGLE_MAPS) {
            item {
                MarketGoogleMap()
            }
        }

        // TAB 4: UNION PROPOSALS & NETWORK
        if (activeTab == CommunityTab.UNION_PROPOSALS) {
            item {
                Text(
                    text = "🗳️ Plaza Union Projects & Proposals",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(state.communityProposals) { proposal ->
                CommunityProposalCard(
                    proposal = proposal,
                    canAfford = state.businessCash >= proposal.costPerMember || state.personalCash >= proposal.costPerMember,
                    onVote = { viewModel.voteCommunityProposal(proposal.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "👥 Market Traders Directory",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(state.communityMembers) { member ->
                TraderNetworkCard(
                    member = member,
                    onPartnerToggle = { viewModel.partnerWithTrader(member.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CommunitySubTab(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("community_subtab_${title.filter { it.isLetter() }}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) HustleGreen else CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGreen else BorderSubtle),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) PureWhite else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LeaderboardRowItem(
    rank: Int,
    entry: FirebaseLeaderboardEntry
) {
    val rankBadge = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_$rank")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rankBadge,
                fontSize = if (rank <= 3) 22.sp else 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HustleGoldDark,
                modifier = Modifier.width(36.dp)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(HustleGreenContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = entry.userAvatar, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.userName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${entry.businessName} • ${entry.cityName} • Wk ${entry.week}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

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
                    color = HustleGoldDark
                )
            }
        }
    }
}

@Composable
fun AchievementCardItem(
    achievement: FirebaseUserAchievement
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = achievement.userAvatar, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = achievement.userName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${achievement.businessName} • Wk ${achievement.weekNumber}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = HustleGoldContainer
                ) {
                    Text(
                        text = achievement.createdAt,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.achievementTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HustleGreenDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = achievement.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Worth: ${CurrencyFormatter.formatCompact(achievement.netWorth)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PositiveGreen
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CardSurfaceSubtle
                ) {
                    Text(
                        text = "👏 Kudoz",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityProposalCard(
    proposal: CommunityProposal,
    canAfford: Boolean,
    onVote: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("proposal_card_${proposal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = proposal.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (proposal.hasVoted) HustleGreenContainer else HustleGoldContainer
                ) {
                    Text(
                        text = if (proposal.hasVoted) "Voted ✓" else "${proposal.votesFor}/${proposal.votesTotal} Votes",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (proposal.hasVoted) HustleGreenDark else HustleGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = proposal.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardSurfaceSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎁", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = proposal.benefitDescription,
                        fontSize = 11.sp,
                        color = HustleGreenDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val progress = (proposal.votesFor.toFloat() / proposal.votesTotal.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = HustleGreen,
                trackColor = BorderSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Member Contribution",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = CurrencyFormatter.formatNaira(proposal.costPerMember),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = onVote,
                    enabled = !proposal.hasVoted && canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HustleGreen,
                        disabledContainerColor = CardSurfaceSubtle
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("vote_button_${proposal.id}")
                ) {
                    Text(
                        text = if (proposal.hasVoted) "Voted ✓" else if (!canAfford) "Insufficient Funds" else "Vote & Back (₦${proposal.costPerMember})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TraderNetworkCard(
    member: CommunityMember,
    onPartnerToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trader_card_${member.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(HustleGreenContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = member.avatarEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${member.businessName} • ${member.location}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = HustleGoldContainer
                ) {
                    Text(
                        text = "Rep ${member.reputation}%",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardSurfaceSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${member.status}\"",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Worth",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = CurrencyFormatter.formatCompact(member.netWorth),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PositiveGreen
                    )
                }

                Button(
                    onClick = onPartnerToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (member.isPartner) HustleGold else HustleGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("partner_button_${member.id}")
                ) {
                    Text(
                        text = if (member.isPartner) "Partnered 🤝" else "Partner (+5% Trade)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
