package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CurrencyFormatter
import com.example.model.DailyStreakDayReward
import com.example.model.DailyStreakInfo
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
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
fun DailyStreakDialog(
    streakInfo: DailyStreakInfo,
    onClaimStandard: () -> Unit,
    onClaimDoubleWithAd: () -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_pulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("daily_streak_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.5.dp, if (streakInfo.canClaimToday) HustleGold else BorderSubtle),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .scale(if (streakInfo.canClaimToday) scalePulse else 1f)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(WarningOrange, Color(0xFFD84315))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔥", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY HUSTLE STREAK",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Consistency creates Lagos Moguls",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("dismiss_daily_streak_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close streak dialog",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Streak Highlight Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (streakInfo.canClaimToday) HustleGoldContainer else HustleGreenContainer,
                    border = BorderStroke(
                        1.dp,
                        if (streakInfo.canClaimToday) HustleGoldDark.copy(alpha = 0.4f) else HustleGreenDark.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT MOMENTUM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (streakInfo.canClaimToday) HustleGoldDark else HustleGreenDark
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${streakInfo.currentStreak}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (streakInfo.currentStreak == 1) "DAY ACTIVE" else "DAYS UNBROKEN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL STREAK CASH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Text(
                                text = CurrencyFormatter.formatNaira(streakInfo.totalCashEarnedFromStreaks),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PositiveGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7-Day Rewards Cycle Visual Grid
                Text(
                    text = "7-DAY PROGRESSIVE WEALTH CYCLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(2.dp)
                ) {
                    items(streakInfo.weeklyCycle) { reward ->
                        StreakDayCard(
                            reward = reward,
                            currentStreak = streakInfo.currentStreak,
                            canClaimToday = streakInfo.canClaimToday,
                            modifier = Modifier.testTag("streak_day_card_${reward.dayNumber}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reward perk preview description
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceSubtle,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = streakInfo.todayReward.iconEmoji,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Day ${streakInfo.currentDayInCycle}: ${streakInfo.todayReward.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = streakInfo.todayReward.perkDescription,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                        if (streakInfo.todayReward.reputationBonus > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HustleGreenContainer
                            ) {
                                Text(
                                    text = "+${streakInfo.todayReward.reputationBonus} REP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HustleGreenDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Claim Action Buttons
                if (streakInfo.canClaimToday) {
                    // 2x Sponsored Ad Multiplier (Real-Life Ad Monetization Hook!)
                    Button(
                        onClick = onClaimDoubleWithAd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("claim_2x_ad_streak_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HustleGold,
                            contentColor = PureWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Sponsor Ad for 2X (+${CurrencyFormatter.formatNaira(streakInfo.todayReward.bonusAmount * 2)})",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Standard 1x Claim
                    OutlinedButton(
                        onClick = onClaimStandard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("claim_daily_streak_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, HustleGreen),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HustleGreen
                        )
                    ) {
                        Text(
                            text = "Claim Standard (+${CurrencyFormatter.formatNaira(streakInfo.todayReward.bonusAmount)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // Already Claimed Today banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CardSurfaceSubtle,
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = HustleGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bonus claimed today! Return tomorrow for Day ${streakInfo.nextDayInCycle} (+${CurrencyFormatter.formatNaira(streakInfo.nextDayReward.bonusAmount)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("dismiss_daily_streak_continue_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HustleGreen,
                            contentColor = PureWhite
                        )
                    ) {
                        Text(
                            text = "Back to Market",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakDayCard(
    reward: DailyStreakDayReward,
    currentStreak: Int,
    canClaimToday: Boolean,
    modifier: Modifier = Modifier
) {
    val dayInCycle = ((currentStreak - 1) % 7) + 1
    val isPastClaimed = if (canClaimToday) reward.dayNumber < dayInCycle else reward.dayNumber <= dayInCycle
    val isTodayReady = canClaimToday && reward.dayNumber == dayInCycle
    val isLocked = !isPastClaimed && !isTodayReady
    val isJackpot = reward.dayNumber == 7

    val cardBg = when {
        isTodayReady -> HustleGoldContainer
        isPastClaimed -> HustleGreenContainer.copy(alpha = 0.6f)
        isJackpot -> HustleGoldContainer.copy(alpha = 0.3f)
        else -> CardSurfaceSubtle
    }

    val borderColor = when {
        isTodayReady -> HustleGold
        isPastClaimed -> HustleGreen.copy(alpha = 0.5f)
        isJackpot -> HustleGoldDark.copy(alpha = 0.5f)
        else -> BorderSubtle
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(if (isTodayReady) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "D${reward.dayNumber}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isTodayReady) HustleGoldDark else TextMuted
                )

                if (isPastClaimed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Claimed",
                        tint = HustleGreen,
                        modifier = Modifier.size(12.dp)
                    )
                } else if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(WarningOrange, CircleShape)
                    )
                }
            }

            Text(
                text = reward.iconEmoji,
                fontSize = if (isJackpot) 20.sp else 16.sp
            )

            Text(
                text = "+₦${formatCompactCash(reward.bonusAmount)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTodayReady) HustleGoldDark else TextPrimary,
                maxLines = 1
            )
        }
    }
}

private fun formatCompactCash(amount: Long): String {
    return when {
        amount >= 1_000_000L -> "${amount / 1_000_000L}M"
        amount >= 1_000L -> "${amount / 1_000L}K"
        else -> "$amount"
    }
}
