package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.DangerRed
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

@Composable
fun WalletHeader(
    state: GameState,
    modifier: Modifier = Modifier,
    onStreakClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wallet_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar: Week + City + Streak + Net Worth
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HustleGreenContainer
                    ) {
                        Text(
                            text = "WEEK ${state.week} / 52",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = HustleGreenDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📍 ${state.cityName}",
                        color = HustleGoldDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (state.dailyStreak.canClaimToday) WarningOrange.copy(alpha = 0.15f) else CardSurfaceSubtle,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (state.dailyStreak.canClaimToday) WarningOrange else BorderSubtle
                        ),
                        modifier = Modifier
                            .then(
                                if (onStreakClick != null) Modifier.clickable { onStreakClick() }
                                else Modifier
                            )
                            .testTag("wallet_header_streak_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 ${state.dailyStreak.currentStreak}d",
                                color = if (state.dailyStreak.canClaimToday) WarningOrange else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            if (state.dailyStreak.canClaimToday) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(WarningOrange, CircleShape)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "NET WORTH",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = CurrencyFormatter.formatNaira(state.netWorth),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (state.netWorth >= 0) PositiveGreen else DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Dual Wallets (Core Game Concept)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Business Wallet Card
                WalletMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "BUSINESS CASH",
                    subtext = state.business.name,
                    amount = state.businessCash,
                    icon = Icons.Default.BusinessCenter,
                    accentColor = HustleGreenDark,
                    containerColor = HustleGreenContainer
                )

                // Personal Wallet Card
                WalletMiniCard(
                    modifier = Modifier.weight(1f),
                    title = "PERSONAL CASH",
                    subtext = state.characterName,
                    amount = state.personalCash,
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = HustleGoldDark,
                    containerColor = HustleGoldContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Wallets: Savings (Emergency Fund) + Debt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatChip(
                    modifier = Modifier.weight(1f),
                    label = "Emergency Savings",
                    value = CurrencyFormatter.formatCompact(state.personalSavings),
                    icon = Icons.Default.Savings,
                    valueColor = PositiveGreen
                )

                MiniStatChip(
                    modifier = Modifier.weight(1f),
                    label = "Debt / Loans",
                    value = if (state.totalDebt > 0) CurrencyFormatter.formatCompact(state.totalDebt) else "₦0",
                    icon = Icons.Default.CreditCard,
                    valueColor = if (state.totalDebt > 0) DangerRed else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vitals Gauges: Stress + Reputation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stress Meter
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stress ${state.stress}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                state.stress >= 75 -> DangerRed
                                state.stress >= 50 -> WarningOrange
                                else -> TextPrimary
                            }
                        )
                        if (state.stress >= 75) {
                            Text(text = "⚠️ BURNOUT RISK", fontSize = 9.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { state.stress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when {
                            state.stress >= 75 -> DangerRed
                            state.stress >= 50 -> WarningOrange
                            else -> PositiveGreen
                        },
                        trackColor = CardSurfaceSubtle
                    )
                }

                // Reputation Meter
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Reputation ${state.reputation}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "⭐ Trust", fontSize = 10.sp, color = HustleGoldDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { state.reputation / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HustleGold,
                        trackColor = CardSurfaceSubtle
                    )
                }
            }
        }
    }
}

@Composable
fun WalletMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    subtext: String,
    amount: Long,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyFormatter.formatNaira(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = subtext,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MiniStatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 10.sp, color = TextMuted)
            }
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
