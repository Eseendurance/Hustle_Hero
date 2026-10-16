package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
import com.example.model.DailyHustleTask
import com.example.model.GameState
import com.example.ui.HustleHeroViewModel
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
fun DailyHustleScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val toastMessage by viewModel.routineActionToast.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_hustle_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Banner
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = HustleGreenDark,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = HustleGold,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Daily Hustle",
                                        tint = PureWhite,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "DAILY HUSTLE GRIND",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = HustleGold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Street Challenges & Quick Cash",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }

                        // Streak Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = HustleGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${state.currentStreakDays}d Streak",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Vital & Energy Overview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Hustle Cash Earned",
                                    fontSize = 10.sp,
                                    color = PureWhite.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = CurrencyFormatter.formatNaira(state.totalHustleCashEarned),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HustleGold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PureWhite.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Energy Reserve",
                                    fontSize = 10.sp,
                                    color = PureWhite.copy(alpha = 0.8f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${state.dailyRoutine.energyLevel}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.dailyRoutine.energyLevel > 30) PositiveGreen else WarningOrange
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    LinearProgressIndicator(
                                        progress = { state.dailyRoutine.energyLevel / 100f },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = HustleGold,
                                        trackColor = PureWhite.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Toast Feedback
        if (toastMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HustleGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = toastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HustleGoldDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark,
                            modifier = Modifier.clickable { viewModel.clearToast() }
                        )
                    }
                }
            }
        }

        // Section Title & Refresh
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Grind Tasks (${state.dailyHustles.count { it.isCompleted }}/${state.dailyHustles.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Complete random tasks each day to build your cash stash",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.refreshDailyHustles() },
                    modifier = Modifier.testTag("refresh_hustles_button"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Gigs", fontSize = 11.sp)
                }
            }
        }

        // Tasks List
        if (state.dailyHustles.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active challenges!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(onClick = { viewModel.refreshDailyHustles() }) {
                            Text("Generate Today's Challenges")
                        }
                    }
                }
            }
        } else {
            items(state.dailyHustles, key = { it.id }) { task ->
                DailyHustleCard(
                    task = task,
                    userEnergy = state.dailyRoutine.energyLevel,
                    onComplete = { viewModel.completeDailyHustle(task.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DailyHustleCard(
    task: DailyHustleTask,
    userEnergy: Int,
    onComplete: () -> Unit
) {
    val canAffordEnergy = userEnergy >= task.energyCost

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) CardSurfaceSubtle else CardSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted) PositiveGreen.copy(alpha = 0.4f) else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = task.iconEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = task.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) TextSecondary else TextPrimary
                            )
                            if (task.isCompleted) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = PositiveGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${task.category} • Client: ${task.clientName}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // Difficulty pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (task.difficulty) {
                        "Easy" -> HustleGreenContainer
                        "Hard" -> HustleGoldContainer
                        else -> CardSurfaceSubtle
                    }
                ) {
                    Text(
                        text = task.difficulty,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (task.difficulty) {
                            "Easy" -> HustleGreenDark
                            "Hard" -> HustleGoldDark
                            else -> TextSecondary
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location & requirements strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = HustleGoldDark,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = task.location,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "⚡ -${task.energyCost}% Energy",
                        fontSize = 10.sp,
                        color = if (canAffordEnergy) TextMuted else DangerRed,
                        fontWeight = if (!canAffordEnergy) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Text(
                    text = "+${task.reputationReward} Rep",
                    fontSize = 10.sp,
                    color = PositiveGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reward & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Virtual Cash Reward", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = CurrencyFormatter.formatNaira(task.rewardCash),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGreen
                    )
                }

                if (task.isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PositiveGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PositiveGreen)
                    ) {
                        Text(
                            text = "✓ REWARD CLAIMED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PositiveGreen,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        enabled = canAffordEnergy,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                        modifier = Modifier.testTag("tackle_hustle_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Grind",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (canAffordEnergy) "Hustle & Claim" else "Too Tired (Eat/Rest)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
