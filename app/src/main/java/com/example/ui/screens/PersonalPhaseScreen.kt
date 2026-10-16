package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.Investment
import com.example.model.LifestyleLevel
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.AjoGoalContributionBottomSheet
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
fun PersonalPhaseScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    onTriggerEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAjoGoalSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("personal_phase_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = HustleGoldContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏠", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PHASE 2: PERSONAL FINANCES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGoldDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Household Budget & Wealth Building",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Separating business capital from personal living is your superpower.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Section 1: Weekly Living Expenses
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val totalCost = state.personalExpenses.calculateTotal(state.inflationRate)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Living Costs",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = CurrencyFormatter.formatNaira(totalCost),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (state.personalCash >= totalCost) PositiveGreen else DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Rent, Food, Danfo transport, and Family Black Tax adjusted for ${(state.inflationRate * 100).toInt()}% inflation.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose Lifestyle Level:",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LifestyleLevel.values().forEach { level ->
                            val isSelected = state.personalExpenses.lifestyle == level
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) HustleGold else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGold else BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lifestyle_${level.name}"),
                                onClick = { viewModel.setLifestyle(level) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = level.label.split(" / ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PureWhite else TextPrimary
                                    )
                                    Text(
                                        text = "${(level.multiplier * 100).toInt()}% cost",
                                        fontSize = 10.sp,
                                        color = if (isSelected) PureWhite.copy(alpha = 0.9f) else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Emergency Savings Fund
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Savings Fund",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = CurrencyFormatter.formatNaira(state.personalSavings),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PositiveGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your shock absorber! Automatically covers sudden medical emergencies or shortfalls without touching business trading cash.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.depositToSavings(10_000L) },
                            enabled = state.personalCash >= 10_000L,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1f).testTag("save_10k_button")
                        ) {
                            Text("Deposit ₦10K", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.withdrawFromSavings(10_000L) },
                            enabled = state.personalSavings >= 10_000L,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.weight(1f).testTag("withdraw_10k_button")
                        ) {
                            Text("Withdraw ₦10K", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Section: Transfer Personal Funds to Business (Capital Injection / Working Capital Support)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HustleGreenContainer.copy(alpha = 0.35f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🚀", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inject Personal Capital to Business",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreenDark
                            )
                        }
                        Text(
                            text = "Biz: ${CurrencyFormatter.formatCompact(state.businessCash)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.businessCash >= 0) HustleGreenDark else DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Transfer household savings or personal cash back into business working capital to purchase wholesale inventory or prevent bankruptcy.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.injectPersonalCapital(25_000L, fromSavings = false) },
                            enabled = state.personalCash >= 25_000L,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Inject ₦25K Cash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.injectPersonalCapital(25_000L, fromSavings = true) },
                            enabled = state.personalSavings >= 25_000L,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("From Savings ₦25K", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 3: Ajo / Esusu Savings Group
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HustleGreenContainer.copy(alpha = 0.4f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🤝", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ajo / Esusu Savings Club",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HustleGreenDark
                                )
                                Text(
                                    text = "5-Trader Rotating Savings Group",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = state.ajoGroup.isEnrolled,
                            onCheckedChange = { viewModel.toggleAjoMembership(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = HustleGreen,
                                checkedTrackColor = HustleGreenContainer
                            ),
                            modifier = Modifier.testTag("ajo_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.ajoGroup.isEnrolled) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Weekly Contribution: ${CurrencyFormatter.formatNaira(state.ajoGroup.weeklyContribution)}",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Cycle Status: Week ${state.ajoGroup.currentCycleWeek} of ${state.ajoGroup.cycleWeeks}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HustleGoldDark
                                )
                                Text(
                                    text = "🎯 YOUR PAYOUT: Receives full ₦${state.ajoGroup.totalPot} pot on Week ${state.ajoGroup.payoutWeek}!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PositiveGreen
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Contribute ₦10,000 every week with 4 fellow market traders. When your turn arrives, collect a lump sum of ₦50,000 to buy big shop upgrades without high bank interest!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showAjoGoalSheet = true },
                        modifier = Modifier.fillMaxWidth().testTag("personal_phase_contribute_ajo_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGold)
                    ) {
                        Text("🎯 Contribute to Ajo Saving Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
        }

        // Section 4: Investment Portfolio
        item {
            Text(
                text = "📊 Investment Desk",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(state.investments) { inv ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = inv.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = inv.description, fontSize = 10.sp, color = TextMuted)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(inv.type.badgeColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = inv.type.label,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(inv.type.badgeColorHex)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Current Value", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = CurrencyFormatter.formatNaira(inv.totalValue),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (inv.totalValue > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.withdrawInvestment(inv.id, inv.totalValue) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Cash Out", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            Button(
                                onClick = { viewModel.investFunds(inv.id, 15_000L) },
                                enabled = state.personalCash >= 15_000L,
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("invest_${inv.id}")
                            ) {
                                Text("+ ₦15K", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Credit & Micro-loans
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Credit & Micro-Loans",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Credit Score: ${state.creditScore}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.creditScore >= 650) PositiveGreen else WarningOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Active Debt: ${CurrencyFormatter.formatNaira(state.totalDebt)}",
                        fontSize = 12.sp,
                        color = if (state.totalDebt > 0) DangerRed else TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.takeLoan(
                                sourceName = "Trader Credit Cooperative",
                                principal = 50_000L,
                                rate = 0.03f,
                                weeklyPayment = 15_000L
                            )
                        },
                        enabled = state.totalDebt < 150_000L,
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("take_micro_loan_button")
                    ) {
                        Text("Borrow ₦50,000 Working Capital (3% wkly interest)", fontSize = 12.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Proceed to Random Event CTA
        item {
            Button(
                onClick = onTriggerEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("face_event_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Face This Week's Market Event 🎲",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showAjoGoalSheet) {
        AjoGoalContributionBottomSheet(
            state = state,
            onDismiss = { showAjoGoalSheet = false },
            onContribute = { goalId, amount, source ->
                viewModel.contributeToAjoGoal(goalId, amount, source)
                showAjoGoalSheet = false
            },
            onCreateCustomGoal = { title, desc, cat, emoji, target, weekly, perk ->
                viewModel.createAjoGoal(title, desc, cat, emoji, target, weekly, perk)
            }
        )
    }
}
