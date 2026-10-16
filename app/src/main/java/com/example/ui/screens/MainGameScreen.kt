package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import com.example.model.AjoGoalDeadlineStatus
import com.example.model.AjoSavingGoal
import kotlin.math.min
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.GameEngine
import com.example.model.AjoSavingsAccountStatus
import com.example.model.CurrencyFormatter
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.Player
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferType
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.ShopCounterLedgerDialog
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

/**
 * MainGameScreen: Primary scalable game interface displaying the player's core attributes
 * (Currency, Business Level, Reputation Score, and Ajo Savings Account Status)
 * connected directly to the ViewModel and GameEngine simulation layer.
 */
@Composable
fun MainGameScreen(
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier,
    onNavigate: ((GamePhase) -> Unit)? = null
) {
    val state by viewModel.gameState.collectAsState()
    val activeState = state ?: return

    MainGameScreen(
        state = activeState,
        viewModel = viewModel,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun MainGameScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier,
    onNavigate: ((GamePhase) -> Unit)? = null
) {
    val player: Player = state.player
    var showLedgerModal by remember { mutableStateOf(false) }
    var quickNoticeMessage by remember { mutableStateOf<String?>(null) }

    val bizRunway = GameEngine.calculateBusinessSolvencyWeeks(state)
    val personalRunway = GameEngine.calculatePersonalRunwayWeeks(state)
    val demandEstimate = GameEngine.estimateWeeklyDemand(state)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_game_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. HERO PLAYER IDENTITY & LEVEL BANNER ---
        item {
            PlayerHeroBanner(
                player = player,
                week = state.week,
                cityName = state.cityName,
                businessName = state.business.name,
                isDarkMode = state.isDarkMode,
                onToggleTheme = { viewModel.setDarkMode(it) }
            )
        }

        // Quick Notice Banner if present
        if (quickNoticeMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HustleGreenContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen),
                    modifier = Modifier.fillMaxWidth().testTag("main_notice_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "✅ $quickNoticeMessage",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark
                        )
                        Text(
                            text = "DISMISS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGreenDark,
                            modifier = Modifier.clickable { quickNoticeMessage = null }
                        )
                    }
                }
            }
        }

        // --- 1B. DAILY HUSTLE STREAK CARD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openDailyStreakDialog() }
                    .testTag("main_game_streak_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (player.dailyStreak.canClaimToday) HustleGoldContainer else CardSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (player.dailyStreak.canClaimToday) HustleGold else BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "🔥", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Day ${player.dailyStreak.currentStreak} Daily Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                if (player.dailyStreak.canClaimToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WarningOrange
                                    ) {
                                        Text(
                                            text = "READY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PureWhite,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (player.dailyStreak.canClaimToday) "Tap to claim today's +${player.dailyStreak.formattedTodayBonus} bonus!"
                                       else "Streak active! Next: Day ${player.dailyStreak.nextDayInCycle} (+${CurrencyFormatter.formatNaira(player.dailyStreak.nextDayReward.bonusAmount)})",
                                fontSize = 11.sp,
                                color = if (player.dailyStreak.canClaimToday) HustleGoldDark else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.openDailyStreakDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (player.dailyStreak.canClaimToday) HustleGold else HustleGreen,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("main_game_streak_action_btn")
                    ) {
                        Text(
                            text = if (player.dailyStreak.canClaimToday) "Claim 🎁" else "View 🔥",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 2. PLAYER CURRENCY CARD (CORE REQUIREMENT) ---
        item {
            PlayerCurrencyCard(
                player = player,
                personalCash = state.personalCash,
                businessCash = state.businessCash,
                personalSavings = state.personalSavings,
                onTransferSalary = {
                    val salary = GameEngine.getRecommendedSalary(state)
                    if (state.businessCash >= salary) {
                        viewModel.executeWalletTransfer(
                            WalletTransferRequest(
                                type = WalletTransferType.SALARY_DRAW,
                                amount = salary,
                                reason = "Quick Weekly Salary Draw"
                            )
                        )
                        quickNoticeMessage = "Drew ₦${CurrencyFormatter.formatNaira(salary)} Salary!"
                    }
                },
                onInjectCapital = {
                    val inject = 20_000L.coerceAtMost(state.personalCash)
                    if (inject > 0) {
                        viewModel.executeWalletTransfer(
                            WalletTransferRequest(
                                type = WalletTransferType.CAPITAL_INJECTION,
                                amount = inject,
                                reason = "Quick Capital Injection"
                            )
                        )
                        quickNoticeMessage = "Injected ₦${CurrencyFormatter.formatNaira(inject)} into Business!"
                    }
                }
            )
        }

        // --- 3. BUSINESS LEVEL & ENTERPRISE SCALE (CORE REQUIREMENT) ---
        item {
            BusinessLevelCard(
                player = player,
                inventoryCount = state.inventoryUnits,
                weeklyDemand = demandEstimate.expectedDemand,
                solvencyWeeks = bizRunway,
                canLevelUp = state.businessCash >= 50_000L && player.businessLevel < 5,
                onLevelUp = {
                    viewModel.upgradeBusinessLevel()
                    quickNoticeMessage = "Business Upgraded! Reputation & Stall capacity expanded."
                },
                onQuickRestock = {
                    val quote = GameEngine.calculateRestockQuote(state, 20)
                    if (state.businessCash >= quote.totalCost) {
                        viewModel.restockInventory(20)
                        quickNoticeMessage = "Restocked 20 units wholesale for ₦${CurrencyFormatter.formatNaira(quote.totalCost)}!"
                    }
                }
            )
        }

        // --- 4. REPUTATION SCORE & MARKET CREDIBILITY (CORE REQUIREMENT) ---
        item {
            ReputationScoreCard(
                player = player,
                stress = state.stress,
                onImproveReputation = {
                    viewModel.updatePlayerReputation(5)
                    quickNoticeMessage = "Organized customer appreciation day! +5 Street Goodwill."
                }
            )
        }

        // --- 5. AJO SAVINGS ACCOUNT STATUS (ROTATING CIRCLE) ---
        item {
            AjoSavingsAccountStatusCard(
                status = player.ajoSavingsAccountStatus,
                currentWeek = state.week,
                canAffordContribution = state.personalCash >= player.ajoSavingsAccountStatus.weeklyContribution,
                onContribute = {
                    viewModel.contributeToAjoGoal(
                        goalId = state.selectedAjoGoalId ?: "",
                        amount = player.ajoSavingsAccountStatus.weeklyContribution
                    )
                    quickNoticeMessage = "Deposited ₦${CurrencyFormatter.formatNaira(player.ajoSavingsAccountStatus.weeklyContribution)} into Ajo Circle!"
                }
            )
        }

        // --- 5B. MAJOR SAVINGS TARGETS WITH DEADLINES (EXTENDED REQUIREMENT) ---
        item {
            MajorAjoGoalsWithDeadlinesCard(
                goals = state.ajoGoals,
                currentWeek = state.week,
                personalCash = state.personalCash,
                onContributeToGoal = { goalId, amount ->
                    viewModel.contributeToAjoGoal(goalId, amount)
                    quickNoticeMessage = "Saved ₦${CurrencyFormatter.formatNaira(amount)} towards target milestone!"
                },
                onCreateCustomGoal = { title, desc, cat, emoji, target, weekly, perk, deadline ->
                    viewModel.createAjoGoal(title, desc, cat, emoji, target, weekly, perk, deadline)
                    quickNoticeMessage = "New target created with Week $deadline deadline!"
                }
            )
        }

        // --- 6. RAW ENGINE TELEMETRY & RUNWAY HEALTH ---
        item {
            RawEngineTelemetryCard(
                bizRunway = bizRunway,
                personalRunway = personalRunway,
                isGeneratorRunning = state.generatorState.isGeneratorRunning,
                fuelLitres = state.generatorState.fuelLitres,
                radioPlaying = state.radioStation.isPlaying,
                stationName = state.radioStation.currentStation.stationName,
                onToggleGenerator = { viewModel.toggleGeneratorPower() },
                onToggleRadio = { viewModel.toggleRadioPlayback() },
                onOpenLedger = { showLedgerModal = true }
            )
        }

        // --- 7. SCALABILITY CONTROLS & NAVIGATION ACTIONS ---
        item {
            ScalabilityActionDock(
                onNavigate = onNavigate,
                onSimulateTurn = {
                    viewModel.navigateToPhase(GamePhase.BUSINESS)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showLedgerModal) {
        ShopCounterLedgerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showLedgerModal = false }
        )
    }
}

// ---------------------------------------------------------------------------
// SUB-COMPONENTS FOR CLEAN MODULARITY
// ---------------------------------------------------------------------------

@Composable
fun PlayerHeroBanner(
    player: Player,
    week: Int,
    cityName: String,
    businessName: String,
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("player_hero_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(HustleGreenContainer, CircleShape)
                        .border(2.dp, HustleGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = player.avatarEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HustleGreen
                        ) {
                            Text(
                                text = "Week $week / 52",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    Text(
                        text = "$businessName • 📍 $cityName",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = player.businessLevelTitle,
                        fontSize = 11.sp,
                        color = HustleGoldDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BorderSubtle)

            // Night Market / Day Light Theme Mode Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isDarkMode) "🌙" else "☀️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDarkMode) "Night Market Mode" else "Day Light Mode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggleTheme,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = HustleGreen,
                        checkedTrackColor = HustleGreenContainer,
                        uncheckedThumbColor = HustleGold,
                        uncheckedTrackColor = HustleGoldContainer
                    ),
                    modifier = Modifier.testTag("main_screen_theme_switch")
                )
            }
        }
    }
}

@Composable
fun PlayerCurrencyCard(
    player: Player,
    personalCash: Long,
    businessCash: Long,
    personalSavings: Long,
    onTransferSalary: () -> Unit,
    onInjectCapital: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HustleGreen.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth().testTag("player_currency_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = HustleGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TOTAL SPENDABLE CURRENCY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGreenDark,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (player.isSolvent) HustleGreenContainer else DangerRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (player.isSolvent) "Solvent & Active" else "Capital Deficit",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (player.isSolvent) HustleGreenDark else DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = player.formattedCurrency,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = HustleGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dual Wallet Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CurrencyBreakdownChip(
                    title = "Business Cash",
                    amount = businessCash,
                    icon = "🏢",
                    accent = HustleGreenDark,
                    bg = HustleGreenContainer,
                    modifier = Modifier.weight(1f)
                )
                CurrencyBreakdownChip(
                    title = "Personal Cash",
                    amount = personalCash,
                    icon = "👛",
                    accent = HustleGoldDark,
                    bg = HustleGoldContainer,
                    modifier = Modifier.weight(1f)
                )
                CurrencyBreakdownChip(
                    title = "Savings Vault",
                    amount = personalSavings,
                    icon = "🏦",
                    accent = HustleBlue,
                    bg = HustleBlueContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Dual-Wallet Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTransferSalary,
                    modifier = Modifier.weight(1f).testTag("quick_salary_draw_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Salary Draw",
                        modifier = Modifier.size(16.dp),
                        tint = HustleGoldDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Draw Salary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onInjectCapital,
                    modifier = Modifier.weight(1f).testTag("quick_capital_inject_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upgrade,
                        contentDescription = "Inject Capital",
                        modifier = Modifier.size(16.dp),
                        tint = PureWhite
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fund Business", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

@Composable
fun CurrencyBreakdownChip(
    title: String,
    amount: Long,
    icon: String,
    accent: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "$icon $title", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = accent)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = CurrencyFormatter.formatCompact(amount),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
        }
    }
}

@Composable
fun BusinessLevelCard(
    player: Player,
    inventoryCount: Int,
    weeklyDemand: Int,
    solvencyWeeks: Float,
    canLevelUp: Boolean,
    onLevelUp: () -> Unit,
    onQuickRestock: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth().testTag("business_level_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Business Level",
                        tint = HustleGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BUSINESS SCALE & CAPACITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HustleGoldContainer
                ) {
                    Text(
                        text = "TIER ${player.businessLevel} / 5",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = HustleGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = player.businessLevelTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (player.businessLevel / 5f).coerceIn(0.2f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = HustleGold,
                trackColor = CardSurfaceSubtle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Operational metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📦 In Stock", fontSize = 10.sp, color = TextMuted)
                        Text("$inventoryCount units", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📈 Est. Demand", fontSize = 10.sp, color = TextMuted)
                        Text("$weeklyDemand units/wk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("🛡️ Runway", fontSize = 10.sp, color = TextMuted)
                        Text("${"%.1f".format(solvencyWeeks)} wks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (solvencyWeeks < 2f) DangerRed else PositiveGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onQuickRestock,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("quick_restock_btn")
                ) {
                    Text("Restock 20 Units", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onLevelUp,
                    enabled = canLevelUp,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark),
                    modifier = Modifier.weight(1f).testTag("level_up_btn")
                ) {
                    Text(
                        text = if (player.businessLevel >= 5) "Max Scale" else "Upgrade (₦50K)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun ReputationScoreCard(
    player: Player,
    stress: Int,
    onImproveReputation: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth().testTag("reputation_score_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Reputation",
                        tint = HustleGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REPUTATION & STREET GOODWILL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HustleGoldContainer
                ) {
                    Text(
                        text = "${player.reputationScore} / 100",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { (player.reputationScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = when {
                    player.reputationScore >= 75 -> PositiveGreen
                    player.reputationScore >= 45 -> HustleGold
                    else -> DangerRed
                },
                trackColor = CardSurfaceSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            val rankTitle = when {
                player.reputationScore >= 80 -> "🌟 Market Icon: Suppliers offer wholesale credit & priority stock"
                player.reputationScore >= 55 -> "🤝 Trusted Merchant: Loyal customers return weekly"
                player.reputationScore >= 35 -> "🛒 Street Trader: Building credibility in the market"
                else -> "⚠️ Shaky Standing: Suppliers demand strictly cash upfront"
            }

            Text(
                text = rankTitle,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onImproveReputation,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("boost_reputation_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Boost Reputation",
                    modifier = Modifier.size(16.dp),
                    tint = HustleGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Offer Loyal Customer Perk (+5 Reputation)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AjoSavingsAccountStatusCard(
    status: AjoSavingsAccountStatus,
    currentWeek: Int,
    canAffordContribution: Boolean,
    onContribute: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, HustleGreen.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("ajo_savings_status_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Ajo Savings",
                        tint = HustleGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AJO COOPERATIVE SAVINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGreenDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (status.isActive) HustleGreenContainer else CardSurfaceSubtle
                ) {
                    Text(
                        text = if (status.isActive) "Active Circle" else "Inactive",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status.isActive) HustleGreenDark else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = status.groupName,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = status.statusMessage,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("💰 Total Saved", fontSize = 10.sp, color = TextMuted)
                        Text(status.formattedTotalSaved, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📅 Weekly Pledge", fontSize = 10.sp, color = TextMuted)
                        Text(status.formattedWeeklyContribution, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("🎯 Payout Turn", fontSize = 10.sp, color = TextMuted)
                        Text("Round ${status.payoutWeek} of ${status.totalRounds}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onContribute,
                enabled = canAffordContribution,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                modifier = Modifier.fillMaxWidth().testTag("ajo_contribute_weekly_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = "Contribute",
                    modifier = Modifier.size(16.dp),
                    tint = PureWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Deposit Weekly Quota (${status.formattedWeeklyContribution})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}

@Composable
fun RawEngineTelemetryCard(
    bizRunway: Float,
    personalRunway: Float,
    isGeneratorRunning: Boolean,
    fuelLitres: Int,
    radioPlaying: Boolean,
    stationName: String,
    onToggleGenerator: () -> Unit,
    onToggleRadio: () -> Unit,
    onOpenLedger: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth().testTag("raw_engine_telemetry_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Telemetry",
                        tint = HustleBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RAW ENGINE TELEMETRY & AMBIANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleBlue
                    )
                }
                Text("60 FPS Raw Engine", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PositiveGreen)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Generator Status Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isGeneratorRunning) HustleGreenContainer else CardSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isGeneratorRunning) HustleGreen else BorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleGenerator() }
                        .testTag("telemetry_generator_toggle")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (isGeneratorRunning) "⚡" else "🔌", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGeneratorRunning) "Generator On" else "NEPA / Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGeneratorRunning) HustleGreenDark else TextPrimary
                            )
                        }
                        Text(text = "$fuelLitres Litres fuel", fontSize = 9.sp, color = TextSecondary)
                    }
                }

                // Wazobia Radio Status Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (radioPlaying) HustleGoldContainer else CardSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (radioPlaying) HustleGold else BorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleRadio() }
                        .testTag("telemetry_radio_toggle")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (radioPlaying) "📻" else "🔇", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (radioPlaying) "Radio Live" else "Radio Paused",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (radioPlaying) HustleGoldDark else TextPrimary
                            )
                        }
                        Text(text = stationName, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenLedger,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("telemetry_open_ledger_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Ledger",
                    modifier = Modifier.size(16.dp),
                    tint = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Merchant Counter Ledger & Debts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScalabilityActionDock(
    onNavigate: ((GamePhase) -> Unit)?,
    onSimulateTurn: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth().testTag("scalability_action_dock")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "HUSTLE EXPANSION & FAST NAVIGATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onNavigate?.invoke(GamePhase.DAILY_HUSTLE) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreenDark),
                    modifier = Modifier.weight(1f).testTag("dock_daily_hustle_btn")
                ) {
                    Text("⚡ Daily Hustle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }

                Button(
                    onClick = { onNavigate?.invoke(GamePhase.BUSINESS) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleBlue),
                    modifier = Modifier.weight(1f).testTag("dock_business_btn")
                ) {
                    Text("🛒 Wholesale Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSimulateTurn,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                modifier = Modifier.fillMaxWidth().testTag("dock_advance_turn_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Advance Week",
                    tint = PureWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Weekly Turn Cycle", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = PureWhite)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// MAJOR AJO SAVINGS TARGETS WITH DEADLINES COMPOSABLES
// ---------------------------------------------------------------------------

@Composable
fun MajorAjoGoalsWithDeadlinesCard(
    goals: List<AjoSavingGoal>,
    currentWeek: Int,
    personalCash: Long,
    onContributeToGoal: (String, Long) -> Unit,
    onCreateCustomGoal: (String, String, String, String, Long, Long, String, Int) -> Unit
) {
    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var filterMode by remember { mutableStateOf(0) } // 0: All, 1: Urgent/Overdue, 2: Completed

    val urgentOrOverdueCount = goals.count { it.isDueSoon(currentWeek) || it.isOverdue(currentWeek) }
    val completedCount = goals.count { it.isCompleted }

    val filteredGoals = when (filterMode) {
        1 -> goals.filter { it.isDueSoon(currentWeek) || it.isOverdue(currentWeek) }
        2 -> goals.filter { it.isCompleted }
        else -> goals
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, HustleGold.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("ajo_major_goals_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Deadlines",
                        tint = HustleGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SAVINGS TARGETS WITH DEADLINES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HustleGoldContainer,
                    modifier = Modifier
                        .clickable { showCreateGoalDialog = true }
                        .testTag("create_new_target_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal", modifier = Modifier.size(12.dp), tint = HustleGoldDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "New Target", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All (${goals.size})", "Urgent / Overdue ($urgentOrOverdueCount)", "Completed ($completedCount)").forEachIndexed { index, label ->
                    val isSelected = filterMode == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HustleGold else CardSurfaceSubtle,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGold else BorderSubtle),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { filterMode = index }
                            .testTag("filter_ajo_tab_$index")
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PureWhite else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (filterMode == 1) "No savings targets currently urgent or overdue! All on track." else "No targets in this category.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredGoals.forEach { goal ->
                        AjoGoalDeadlineItem(
                            goal = goal,
                            currentWeek = currentWeek,
                            canAfford = personalCash >= min(goal.recommendedWeekly, goal.remainingAmount),
                            onQuickDeposit = {
                                val amount = min(goal.recommendedWeekly, goal.remainingAmount)
                                onContributeToGoal(goal.id, if (amount > 0) amount else 5_000L)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateGoalDialog) {
        CreateAjoGoalWithDeadlineDialog(
            currentWeek = currentWeek,
            onDismiss = { showCreateGoalDialog = false },
            onCreate = { title, desc, cat, emoji, target, weekly, perk, deadline ->
                onCreateCustomGoal(title, desc, cat, emoji, target, weekly, perk, deadline)
                showCreateGoalDialog = false
            }
        )
    }
}

@Composable
fun AjoGoalDeadlineItem(
    goal: AjoSavingGoal,
    currentWeek: Int,
    canAfford: Boolean,
    onQuickDeposit: () -> Unit
) {
    val deadlineStatus = goal.getDeadlineStatus(currentWeek)
    val remWeeks = goal.weeksRemaining(currentWeek)
    val requiredWeekly = goal.requiredWeeklyToMeetDeadline(currentWeek)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (deadlineStatus) {
                AjoGoalDeadlineStatus.OVERDUE -> DangerRed.copy(alpha = 0.8f)
                AjoGoalDeadlineStatus.URGENT -> WarningOrange.copy(alpha = 0.8f)
                AjoGoalDeadlineStatus.COMPLETED -> HustleGreen.copy(alpha = 0.8f)
                AjoGoalDeadlineStatus.ON_TRACK -> BorderSubtle
            }
        ),
        modifier = Modifier.fillMaxWidth().testTag("ajo_goal_item_${goal.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Category + Title + Deadline Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = goal.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = goal.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "${goal.category} • Created Wk ${goal.createdWeek}",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                // Deadline Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (deadlineStatus) {
                        AjoGoalDeadlineStatus.OVERDUE -> DangerRed.copy(alpha = 0.15f)
                        AjoGoalDeadlineStatus.URGENT -> WarningOrange.copy(alpha = 0.15f)
                        AjoGoalDeadlineStatus.COMPLETED -> HustleGreenContainer
                        AjoGoalDeadlineStatus.ON_TRACK -> HustleGoldContainer
                    }
                ) {
                    Text(
                        text = when (deadlineStatus) {
                            AjoGoalDeadlineStatus.OVERDUE -> "🚨 Wk ${goal.deadlineWeek} (+${currentWeek - goal.deadlineWeek}w overdue)"
                            AjoGoalDeadlineStatus.URGENT -> "⚠️ Wk ${goal.deadlineWeek} (${remWeeks}w left)"
                            AjoGoalDeadlineStatus.COMPLETED -> "✅ Wk ${goal.completedWeek ?: currentWeek} Done"
                            AjoGoalDeadlineStatus.ON_TRACK -> "⏳ Wk ${goal.deadlineWeek} (${remWeeks}w left)"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (deadlineStatus) {
                            AjoGoalDeadlineStatus.OVERDUE -> DangerRed
                            AjoGoalDeadlineStatus.URGENT -> WarningOrange
                            AjoGoalDeadlineStatus.COMPLETED -> HustleGreenDark
                            AjoGoalDeadlineStatus.ON_TRACK -> HustleGoldDark
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = when (deadlineStatus) {
                    AjoGoalDeadlineStatus.COMPLETED -> HustleGreen
                    AjoGoalDeadlineStatus.OVERDUE -> DangerRed
                    AjoGoalDeadlineStatus.URGENT -> WarningOrange
                    AjoGoalDeadlineStatus.ON_TRACK -> HustleGold
                },
                trackColor = CardSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Amount stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${CurrencyFormatter.formatNaira(goal.currentAmount)} / ${CurrencyFormatter.formatNaira(goal.targetAmount)} (${(goal.progressPercent * 100).toInt()}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (goal.isCompleted) HustleGreenDark else TextPrimary
                )

                if (!goal.isCompleted) {
                    Text(
                        text = "₦${CurrencyFormatter.formatNaira(requiredWeekly)}/wk needed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deadlineStatus == AjoGoalDeadlineStatus.OVERDUE) DangerRed else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Perk Reward line
            Text(
                text = "🎁 Perk: ${goal.perkReward}",
                fontSize = 10.sp,
                color = HustleGoldDark,
                fontWeight = FontWeight.Medium
            )

            if (!goal.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))

                val depositAmount = min(goal.recommendedWeekly, goal.remainingAmount)
                Button(
                    onClick = onQuickDeposit,
                    enabled = canAfford,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (deadlineStatus == AjoGoalDeadlineStatus.URGENT || deadlineStatus == AjoGoalDeadlineStatus.OVERDUE) HustleGoldDark else HustleGreen
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ajo_goal_quick_deposit_${goal.id}")
                ) {
                    Text(
                        text = "Quick Save ₦${CurrencyFormatter.formatNaira(depositAmount)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun CreateAjoGoalWithDeadlineDialog(
    currentWeek: Int,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, category: String, emoji: String, targetAmount: Long, weeklyTarget: Long, perkReward: String, deadlineWeek: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Expansion") }
    var emoji by remember { mutableStateOf("🎯") }
    var targetText by remember { mutableStateOf("100000") }
    var weeklyText by remember { mutableStateOf("10000") }
    var deadlineText by remember { mutableStateOf((currentWeek + 8).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "🎯 Set Major Ajo Target with Deadline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Target Title (e.g., Shop Lease Renewal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_goal_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Purpose & Milestone Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_goal_desc_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it.filter { char -> char.isDigit() } },
                        label = { Text("Target (₦)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("new_goal_target_input")
                    )

                    OutlinedTextField(
                        value = deadlineText,
                        onValueChange = { deadlineText = it.filter { char -> char.isDigit() } },
                        label = { Text("Deadline (Wk)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("new_goal_deadline_input")
                    )
                }

                OutlinedTextField(
                    value = weeklyText,
                    onValueChange = { weeklyText = it.filter { char -> char.isDigit() } },
                    label = { Text("Weekly Contribution Quota (₦)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_goal_weekly_input")
                )

                Text(
                    text = "Current simulation week is Week $currentWeek. Meeting deadline requires discipline and steady working capital draws.",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toLongOrNull() ?: 50_000L
                    val weekly = weeklyText.toLongOrNull() ?: 10_000L
                    val deadline = deadlineText.toIntOrNull() ?: (currentWeek + 8)
                    onCreate(
                        title.ifBlank { "Major Business Milestone" },
                        description.ifBlank { "Personal capital savings target." },
                        category,
                        emoji,
                        target,
                        weekly,
                        "+10 Business Reputation upon completion",
                        deadline
                    )
                },
                enabled = title.isNotBlank() && (targetText.toLongOrNull() ?: 0L) > 0L,
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                modifier = Modifier.testTag("confirm_create_goal_btn")
            ) {
                Text("Create Target", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
