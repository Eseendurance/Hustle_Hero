package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import com.example.ui.components.ShopCounterLedgerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.GameEngine
import com.example.model.CurrencyFormatter
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.WalletTransferType
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.AjoGoalContributionBottomSheet
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
import kotlin.math.max

/**
 * Main Dashboard UI for Hustle Hero.
 *
 * Core Pillars:
 * 1. Player's Bank Balances (Personal, Savings Vault, Business Account & Solvency Runways)
 * 2. Business Progress & Empire Trajectory (Net Worth Goal to ₦5M, Stock, Margins, Reputation)
 * 3. Status in the Ajo (Esusu) Saving Group (Rotating circle, 5-week timeline, payout week alerts)
 */
enum class DashboardViewMode {
    MAIN_GAME_ENGINE,
    TREASURY_AUDIT
}

@Composable
fun DashboardScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    onNavigate: (GamePhase) -> Unit,
    modifier: Modifier = Modifier
) {
    // Modal states
    var showTransferDialog by remember { mutableStateOf(false) }
    var activeTransferType by remember { mutableStateOf(WalletTransferType.OWNER_DIVIDEND) }
    var showAjoManageDialog by remember { mutableStateOf(false) }
    var showAjoGoalBottomSheet by remember { mutableStateOf(false) }
    var showAuditLedger by remember { mutableStateOf(false) }
    var selectedAjoTier by remember { mutableLongStateOf(10_000L) }
    var showShopLedgerModal by remember { mutableStateOf(false) }

    val totalLiquidWealth = state.personalCash + state.personalSavings + state.businessCash
    val bizRunway = GameEngine.calculateBusinessSolvencyWeeks(state)
    val persRunway = GameEngine.calculatePersonalRunwayWeeks(state)
    val netWorthProgress = (state.netWorth.toFloat() / state.netWorthGoal.toFloat()).coerceIn(0f, 1f)
    var dashboardViewMode by remember { mutableStateOf(DashboardViewMode.MAIN_GAME_ENGINE) }

    Column(modifier = modifier.fillMaxSize()) {
        // Mode Selector Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dashboardViewMode = DashboardViewMode.MAIN_GAME_ENGINE }
                    .testTag("mode_tab_main_game"),
                shape = RoundedCornerShape(10.dp),
                color = if (dashboardViewMode == DashboardViewMode.MAIN_GAME_ENGINE) HustleGreen else CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (dashboardViewMode == DashboardViewMode.MAIN_GAME_ENGINE) HustleGreen else BorderSubtle)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎮 Main Game Hub",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dashboardViewMode == DashboardViewMode.MAIN_GAME_ENGINE) PureWhite else TextSecondary
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dashboardViewMode = DashboardViewMode.TREASURY_AUDIT }
                    .testTag("mode_tab_treasury_audit"),
                shape = RoundedCornerShape(10.dp),
                color = if (dashboardViewMode == DashboardViewMode.TREASURY_AUDIT) HustleGreen else CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (dashboardViewMode == DashboardViewMode.TREASURY_AUDIT) HustleGreen else BorderSubtle)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊 Treasury & Audit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dashboardViewMode == DashboardViewMode.TREASURY_AUDIT) PureWhite else TextSecondary
                    )
                }
            }
        }

        if (dashboardViewMode == DashboardViewMode.MAIN_GAME_ENGINE) {
            MainGameScreen(
                state = state,
                viewModel = viewModel,
                onNavigate = onNavigate,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_screen")
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // --- 1. WELCOME & HUSTLE IDENTITY BANNER ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_welcome_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(HustleGreenContainer, CircleShape)
                            .border(2.dp, HustleGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.character.avatarEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "E kaabo, ${state.characterName}!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HustleGreen
                            ) {
                                Text(
                                    text = "Week ${state.week}/52",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }

                        Text(
                            text = "${state.business.iconEmoji} ${state.business.name} • ${state.cityName}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HustleGoldContainer
                            ) {
                                Text(
                                    text = "💡 ${state.character.perkName}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HustleGoldDark
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.character.perkDescription,
                                fontSize = 10.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // --- 1B. QUICK STREET OPERATIONS & DISPLAY THEME TOGGLE ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_quick_ops_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (state.isDarkMode) "🌙" else "☀️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (state.isDarkMode) "Night Market Dark Mode" else "Daytime Light Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (state.isDarkMode) "Midnight Lagos neon vibe active" else "High-contrast sunny street view",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = state.isDarkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = HustleGreen,
                                checkedTrackColor = HustleGreenContainer,
                                uncheckedThumbColor = HustleGold,
                                uncheckedTrackColor = HustleGoldContainer
                            ),
                            modifier = Modifier.testTag("dashboard_theme_switch")
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showShopLedgerModal = true }
                                .testTag("dashboard_open_ledger_btn"),
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "Shop Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "${state.shopLedger.size} records • Debts", fontSize = 9.sp, color = TextSecondary)
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.toggleGeneratorPower() }
                                .testTag("dashboard_toggle_nepa_btn"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (state.generatorState.isGeneratorRunning) HustleGreenContainer else CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (state.generatorState.isGeneratorRunning) HustleGreen else BorderSubtle
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (state.generatorState.isGeneratorRunning) "⚡" else "🔌", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (state.generatorState.isGeneratorRunning) "Gen: Running" else "Gen: Off",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.generatorState.isGeneratorRunning) HustleGreenDark else TextPrimary
                                    )
                                    Text(
                                        text = "${state.generatorState.fuelLitres}L fuel remaining",
                                        fontSize = 9.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. PILLAR 1: BANK BALANCE & MASTER TREASURY CARD ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, HustleGreen.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_bank_balance_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(HustleGreenContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = HustleGreenDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "TREASURY & BANK BALANCES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGreenDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Total Combined Liquid Capital",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (bizRunway > 4f) HustleGreenContainer else HustleGoldContainer
                        ) {
                            Text(
                                text = if (bizRunway > 4f) "HEALTHY RUNWAY" else "TIGHT CAPITAL",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bizRunway > 4f) HustleGreenDark else HustleGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Master Liquid Wealth Callout
                    Text(
                        text = CurrencyFormatter.formatNaira(totalLiquidWealth),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Account Balances Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Business Operating Account
                        AccountBalancePill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.BusinessCenter,
                            title = "Business Cash",
                            amount = CurrencyFormatter.formatNaira(state.businessCash),
                            subtext = "Runway: ~%.1fw".format(bizRunway),
                            accentColor = HustleGreen,
                            containerColor = HustleGreenContainer.copy(alpha = 0.5f),
                            onClick = {
                                activeTransferType = WalletTransferType.OWNER_DIVIDEND
                                showTransferDialog = true
                            }
                        )

                        // 2. Personal Spendable Account
                        AccountBalancePill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Payments,
                            title = "Personal Cash",
                            amount = CurrencyFormatter.formatNaira(state.personalCash),
                            subtext = "Spend: ~%.1fw".format(persRunway),
                            accentColor = HustleGoldDark,
                            containerColor = HustleGoldContainer.copy(alpha = 0.5f),
                            onClick = {
                                activeTransferType = WalletTransferType.CAPITAL_INJECTION
                                showTransferDialog = true
                            }
                        )

                        // 3. Emergency Savings Vault
                        AccountBalancePill(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Savings,
                            title = "Savings Vault",
                            amount = CurrencyFormatter.formatNaira(state.personalSavings),
                            subtext = "Rainy Day",
                            accentColor = HustleBlue,
                            containerColor = HustleBlueContainer.copy(alpha = 0.5f),
                            onClick = {
                                activeTransferType = WalletTransferType.EMERGENCY_BAILOUT
                                showTransferDialog = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Treasury Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                activeTransferType = WalletTransferType.OWNER_DIVIDEND
                                showTransferDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1.3f).testTag("dashboard_transfer_button")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Transfer Funds", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Stash 10k to Savings
                        OutlinedButton(
                            onClick = { viewModel.depositToSavings(10_000L) },
                            enabled = state.personalCash >= 10_000L,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HustleBlue.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f).testTag("dashboard_stash_savings_button")
                        ) {
                            Text("+₦10k Save", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (state.personalCash >= 10_000L) HustleBlue else TextMuted)
                        }

                        // Withdraw 10k from Savings
                        OutlinedButton(
                            onClick = { viewModel.withdrawFromSavings(10_000L) },
                            enabled = state.personalSavings >= 10_000L,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.weight(1f).testTag("dashboard_withdraw_savings_button")
                        ) {
                            Text("-₦10k Draw", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (state.personalSavings >= 10_000L) TextPrimary else TextMuted)
                        }
                    }

                    // Audit Trail Drawer Toggle
                    if (state.walletTransfers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAuditLedger = !showAuditLedger }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Audit Ledger (${state.walletTransfers.size} Transfers Logged)",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                if (showAuditLedger) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        AnimatedVisibility(visible = showAuditLedger) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .background(CardSurfaceSubtle, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                state.walletTransfers.takeLast(4).reversed().forEach { tx ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${tx.type.iconEmoji} ${tx.type.title}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${CurrencyFormatter.formatNaira(tx.netAmount)} (Wk ${tx.week})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = HustleGreenDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2.5. DAILY LIFE & HUSTLE HUB SNAPSHOT ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_daily_life_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(HustleGoldContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = state.dailyRoutine.timeOfDay.iconEmoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DAY ${state.dailyRoutine.currentDay} • ${state.dailyRoutine.timeOfDay.displayName.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGoldDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Daily Hustle & Everyday Life",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HustleGreenContainer
                        ) {
                            Text(
                                text = "Streak: ${state.currentStreakDays}d 🔥",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.dailyRoutine.lastActionMessage,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick navigation actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(GamePhase.DAILY_HUSTLE) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1f).testTag("dashboard_to_hustles_btn")
                        ) {
                            Text(text = "⚡ Grind Tasks", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate(GamePhase.LIFE_ROUTINE) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark),
                            modifier = Modifier.weight(1f).testTag("dashboard_to_life_btn")
                        ) {
                            Text(text = "☀️ Everyday Life", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate(GamePhase.GOURMET_MEALS) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreenDark),
                            modifier = Modifier.weight(1f).testTag("dashboard_to_meals_btn")
                        ) {
                            Text(text = "🍲 Meals", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 3. PILLAR 2: BUSINESS PROGRESS & EMPIRE TRAJECTORY ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_business_progress_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(HustleGoldContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = HustleGoldDark, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "BUSINESS PROGRESS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGoldDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Milestone Path to ₦5M Mogul Empire",
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
                                text = getHustleRankTitle(state.netWorth),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Net Worth Goal Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(text = "CURRENT NET WORTH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(
                                text = CurrencyFormatter.formatNaira(state.netWorth),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "GOAL: ${CurrencyFormatter.formatCompact(state.netWorthGoal)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreenDark
                            )
                            Text(
                                text = "%.1f%% complete".format(netWorthProgress * 100),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HustleGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { netWorthProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = HustleGold,
                        trackColor = CardSurfaceSubtle
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Card Business Health Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stock on Hand
                        BusinessMetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Stock on Hand",
                            value = "${state.inventoryUnits} ${state.business.itemUnitName}",
                            subtext = CurrencyFormatter.formatCompact(state.inventoryValue),
                            badge = if (state.inventoryUnits < 20) "LOW" else "OK",
                            badgeColor = if (state.inventoryUnits < 20) DangerRed else PositiveGreen
                        )

                        // Profit Margin
                        val unitMargin = state.sellingPrice - state.costPerUnit
                        val marginPercent = if (state.costPerUnit > 0) (unitMargin * 100 / state.costPerUnit) else 0L
                        BusinessMetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Profit Margin",
                            value = "+$marginPercent%",
                            subtext = "${CurrencyFormatter.formatNaira(unitMargin)} / unit",
                            badge = "MARGIN",
                            badgeColor = PositiveGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Customer Reputation & Demand
                        BusinessMetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Reputation",
                            value = "★ ${state.reputation}%",
                            subtext = if (state.activeAds.isNotEmpty()) "+${state.activeAds.sumOf { it.demandBoostPercent }}% ad boost" else "Base footfall",
                            badge = "TRUST",
                            badgeColor = HustleBlue
                        )

                        // Last Week Sales
                        val lastRevenue = state.latestReport?.salesRevenue ?: 0L
                        val lastProfit = state.latestReport?.businessNetProfit ?: 0L
                        BusinessMetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Last Week Net",
                            value = if (state.turnHistory.isNotEmpty()) CurrencyFormatter.formatSignedNaira(lastProfit) else "Pending",
                            subtext = if (state.turnHistory.isNotEmpty()) "Rev: ${CurrencyFormatter.formatCompact(lastRevenue)}" else "Turn 1",
                            badge = if (lastProfit >= 0) "PROFIT" else "LOSS",
                            badgeColor = if (lastProfit >= 0) PositiveGreen else DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Direct Action Button to Business Phase
                    OutlinedButton(
                        onClick = { onNavigate(GamePhase.BUSINESS) },
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_manage_business_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = HustleGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Manage Restock, Prices & Wholesale Tiers ->", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    }
                }
            }
        }

        // --- 4. PILLAR 3: AJO (ESUSU) SAVING GROUP STATUS ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.ajoGroup.isEnrolled) CardSurface else HustleGoldContainer.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (state.ajoGroup.isEnrolled) HustleGold.copy(alpha = 0.7f) else HustleGoldDark.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_ajo_status_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(HustleGoldContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🤝", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AJO (ESUSU) SAVING GROUP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGoldDark,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (state.ajoGroup.isEnrolled) "Rotating Savings & Credit Circle" else "Traditional Collective Savings",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (state.ajoGroup.isEnrolled) HustleGreenContainer else HustleGoldContainer
                        ) {
                            Text(
                                text = if (state.ajoGroup.isEnrolled) "ACTIVE ROTATION" else "NOT ENROLLED",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.ajoGroup.isEnrolled) HustleGreenDark else HustleGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.ajoGroup.isEnrolled) {
                        // ENROLLED: Display Cycle Progress, Payout Slot & Metrics
                        val ajo = state.ajoGroup
                        val isPayoutWeek = ajo.currentCycleWeek == ajo.payoutWeek
                        val hasPassedPayout = ajo.currentCycleWeek > ajo.payoutWeek

                        // Financial Pot Callout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "TOTAL LUMP-SUM POT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text(
                                    text = CurrencyFormatter.formatNaira(ajo.totalPot),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = HustleGoldDark
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "WEEKLY CONTRIBUTION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text(
                                    text = "${CurrencyFormatter.formatNaira(ajo.weeklyContribution)} / wk",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cycle Stepper Timeline (5 Weeks)
                        Text(
                            text = "Cycle Timeline (Week ${ajo.currentCycleWeek} of ${ajo.cycleWeeks}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (stepWeek in 1..ajo.cycleWeeks) {
                                val isCurrent = stepWeek == ajo.currentCycleWeek
                                val isPlayerPayout = stepWeek == ajo.payoutWeek
                                val isPast = stepWeek < ajo.currentCycleWeek

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isCurrent -> HustleGoldContainer
                                        isPlayerPayout -> HustleGreenContainer
                                        isPast -> CardSurfaceSubtle
                                        else -> CardSurfaceSubtle
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isCurrent || isPlayerPayout) 1.5.dp else 1.dp,
                                        when {
                                            isCurrent -> HustleGold
                                            isPlayerPayout -> HustleGreen
                                            else -> BorderSubtle
                                        }
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (isPlayerPayout) "👑 Wk $stepWeek" else "Wk $stepWeek",
                                            fontSize = 9.sp,
                                            fontWeight = if (isCurrent || isPlayerPayout) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (isCurrent) HustleGoldDark else TextPrimary
                                        )
                                        Text(
                                            text = when {
                                                isPlayerPayout -> "YOU 💰"
                                                isPast -> "Paid ✓"
                                                isCurrent -> "Active"
                                                else -> "Wait"
                                            },
                                            fontSize = 8.sp,
                                            color = when {
                                                isPlayerPayout -> HustleGreenDark
                                                isCurrent -> HustleGoldDark
                                                else -> TextMuted
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Payout Notice Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isPayoutWeek -> HustleGreenContainer
                                hasPassedPayout -> CardSurfaceSubtle
                                else -> HustleGoldContainer.copy(alpha = 0.4f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPayoutWeek) HustleGreen else HustleGold.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when {
                                        isPayoutWeek -> "🎉"
                                        hasPassedPayout -> "✅"
                                        else -> "⏳"
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = when {
                                            isPayoutWeek -> "IT'S YOUR PAYOUT TURN THIS WEEK!"
                                            hasPassedPayout -> "PAYOUT COLLECTED THIS CYCLE"
                                            else -> "PAYOUT COMING IN ${ajo.payoutWeek - ajo.currentCycleWeek} WEEK(S)"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isPayoutWeek) HustleGreenDark else TextPrimary
                                    )
                                    Text(
                                        text = when {
                                            isPayoutWeek -> "You will collect the full lump sum of ${CurrencyFormatter.formatNaira(ajo.totalPot)} when finishing this turn."
                                            hasPassedPayout -> "Keep contributing weekly to maintain your standing and prepare for the next cycle."
                                            else -> "Your turn arrives in Week ${ajo.payoutWeek}. Payout amount: ${CurrencyFormatter.formatNaira(ajo.totalPot)}."
                                        },
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Saving Goal Spotlight
                        val activeGoal = state.selectedAjoGoal
                        if (activeGoal != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAjoGoalBottomSheet = true }
                                    .testTag("dashboard_active_ajo_goal_spotlight")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = activeGoal.iconEmoji, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "TARGET SAVING GOAL",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = HustleGoldDark
                                            )
                                        }

                                        Text(
                                            text = "Deposit ➔",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HustleGreenDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeGoal.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { activeGoal.progressPercent },
                                        modifier = Modifier.fillMaxWidth().height(5.dp),
                                        color = if (activeGoal.isCompleted) HustleGreen else HustleGold,
                                        trackColor = BorderSubtle
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${CurrencyFormatter.formatNaira(activeGoal.currentAmount)} / ${CurrencyFormatter.formatNaira(activeGoal.targetAmount)} (${((activeGoal.progressPercent) * 100).toInt()}%)",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = if (activeGoal.isCompleted) "Completed! 🎉" else "${CurrencyFormatter.formatNaira(activeGoal.remainingAmount)} to go",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (activeGoal.isCompleted) HustleGreenDark else HustleGoldDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Ajo Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAjoGoalBottomSheet = true },
                                modifier = Modifier.weight(1.3f).testTag("dashboard_contribute_ajo_goal_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGold)
                            ) {
                                Text("🎯 Deposit to Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                            }

                            OutlinedButton(
                                onClick = { showAjoManageDialog = true },
                                modifier = Modifier.weight(0.85f).testTag("dashboard_manage_ajo_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text("Tiers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            OutlinedButton(
                                onClick = { viewModel.leaveAjoGroup() },
                                modifier = Modifier.weight(0.85f).testTag("dashboard_leave_ajo_button"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
                            ) {
                                Text("Opt Out", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DangerRed)
                            }
                        }

                    } else {
                        // NOT ENROLLED: Welcoming Invitation & Tier Selection
                        Text(
                            text = "Save collectively with 4 trusted Nigerian market traders. Every week, all members deposit a fixed sum. Each week, one trader receives the entire lump sum pot with zero interest!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Select a Rotating Savings Circle:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AjoTierChip(
                                modifier = Modifier.weight(1f),
                                label = "Starter",
                                contribution = 5_000L,
                                pot = 25_000L,
                                isSelected = selectedAjoTier == 5_000L,
                                onClick = { selectedAjoTier = 5_000L }
                            )

                            AjoTierChip(
                                modifier = Modifier.weight(1f),
                                label = "Merchant",
                                contribution = 10_000L,
                                pot = 50_000L,
                                isSelected = selectedAjoTier == 10_000L,
                                onClick = { selectedAjoTier = 10_000L }
                            )

                            AjoTierChip(
                                modifier = Modifier.weight(1f),
                                label = "Mogul",
                                contribution = 25_000L,
                                pot = 125_000L,
                                isSelected = selectedAjoTier == 25_000L,
                                onClick = { selectedAjoTier = 25_000L }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.joinAjoGroup(
                                    weeklyContribution = selectedAjoTier,
                                    cycleWeeks = 5,
                                    payoutWeek = 3
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGold),
                            modifier = Modifier.fillMaxWidth().testTag("dashboard_join_ajo_button")
                        ) {
                            Text(
                                text = "🤝 Join Circle (${CurrencyFormatter.formatNaira(selectedAjoTier)}/wk ➔ ${CurrencyFormatter.formatNaira(selectedAjoTier * 5)} Pot)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showAjoGoalBottomSheet = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().testTag("dashboard_unenrolled_ajo_goal_button")
                        ) {
                            Text(
                                text = "🎯 Or Save for a Specific Ajo Goal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGoldDark
                            )
                        }
                    }
                }
            }
        }

        // --- 5. 3D MARKET CITY PROMINENT FEATURE CARD ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HustleGreenContainer.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, HustleGreen.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(GamePhase.MARKET_3D) }
                    .testTag("dashboard_3d_view_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(HustleGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌐 3D Market City & Characters",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HustleGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HustleGold
                            ) {
                                Text(
                                    text = "3D VIEW",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PureWhite
                                )
                            }
                        }
                        Text(
                            text = "Explore stalls, Danfo buses, billboard towers, and tap characters to inspect their trader perks.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = HustleGreenDark, modifier = Modifier.size(18.dp))
                }
            }
        }

        // --- 6. ACTION STATIONS GRID ---
        item {
            Text(
                text = "⚡ Action Stations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Business Phase",
                    subtext = "Restock, price & staff",
                    icon = Icons.Default.Storefront,
                    containerColor = HustleGreenContainer,
                    iconTint = HustleGreen,
                    onClick = { onNavigate(GamePhase.BUSINESS) }
                )

                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Personal Phase",
                    subtext = "Budget, lifestyle & debts",
                    icon = Icons.Default.Home,
                    containerColor = HustleGoldContainer,
                    iconTint = HustleGoldDark,
                    onClick = { onNavigate(GamePhase.PERSONAL) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Paid Ads Spot",
                    subtext = "TV, Billboard & Mobile",
                    icon = Icons.Default.Campaign,
                    containerColor = HustleBlueContainer,
                    iconTint = HustleBlue,
                    onClick = { onNavigate(GamePhase.MARKETING_ADS) }
                )

                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Community Plaza",
                    subtext = "Union voting & gossip",
                    icon = Icons.Default.Diversity3,
                    containerColor = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF7E22CE),
                    onClick = { onNavigate(GamePhase.COMMUNITY) }
                )
            }
        }

        // --- 7. FACE WEEKLY EVENT CTA ---
        item {
            Button(
                onClick = { viewModel.triggerEventPhase() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("dashboard_event_button"),
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
}

    // Modal Dialog: Wallet Transfer Execution
    if (showTransferDialog) {
        WalletTransferModal(
            type = activeTransferType,
            state = state,
            onDismiss = { showTransferDialog = false },
            onExecute = { amount, fromSavings ->
                when (activeTransferType) {
                    WalletTransferType.OWNER_DIVIDEND -> viewModel.transferOwnerDividend(amount)
                    WalletTransferType.CAPITAL_INJECTION -> viewModel.injectPersonalCapital(amount, fromSavings)
                    WalletTransferType.SALARY_DRAW -> viewModel.transferSalaryImmediate(amount)
                    WalletTransferType.EMERGENCY_BAILOUT -> viewModel.emergencyBailout(amount)
                }
                showTransferDialog = false
            }
        )
    }

    // Modal Dialog: Manage Ajo Tier / Rules
    if (showAjoManageDialog) {
        AlertDialog(
            onDismissRequest = { showAjoManageDialog = false },
            title = {
                Text(text = "🤝 Ajo Circle Contribution Tier", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select your weekly deposit amount for the 5-week rotating savings circle:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    listOf(5_000L, 10_000L, 20_000L, 50_000L).forEach { tier ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (state.ajoGroup.weeklyContribution == tier) HustleGoldContainer else CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (state.ajoGroup.weeklyContribution == tier) HustleGold else BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.toggleAjoMembership(true, tier)
                                    showAjoManageDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${CurrencyFormatter.formatNaira(tier)} / week",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Pot: ${CurrencyFormatter.formatNaira(tier * 5)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGoldDark
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAjoManageDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Modal Bottom Sheet: Ajo Goal Selection & Balance Contribution
    if (showAjoGoalBottomSheet) {
        AjoGoalContributionBottomSheet(
            state = state,
            onDismiss = { showAjoGoalBottomSheet = false },
            onContribute = { goalId, amount, source ->
                viewModel.contributeToAjoGoal(goalId, amount, source)
                showAjoGoalBottomSheet = false
            },
            onCreateCustomGoal = { title, desc, cat, emoji, target, weekly, perk ->
                viewModel.createAjoGoal(title, desc, cat, emoji, target, weekly, perk)
            }
        )
    }

    if (showShopLedgerModal) {
        ShopCounterLedgerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showShopLedgerModal = false }
        )
    }
}
}

// --- HELPER COMPOSABLES ---

@Composable
private fun AccountBalancePill(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    amount: String,
    subtext: String,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary, maxLines = 1)
            Text(text = subtext, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun BusinessMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtext: String,
    badge: String,
    badgeColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontSize = 10.sp, color = TextMuted)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtext, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun AjoTierChip(
    modifier: Modifier = Modifier,
    label: String,
    contribution: Long,
    pot: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) HustleGoldContainer else CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) HustleGold else BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) HustleGoldDark else TextSecondary)
            Text(text = CurrencyFormatter.formatCompact(contribution), fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(text = "Pot: ${CurrencyFormatter.formatCompact(pot)}", fontSize = 8.sp, color = HustleGoldDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtext: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("dashboard_card_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, iconTint.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(PureWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtext, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

private fun getHustleRankTitle(netWorth: Long): String {
    return when {
        netWorth >= 5_000_000L -> "👑 LAGOS MOGUL"
        netWorth >= 3_000_000L -> "🌟 COMMERCIAL TYCOON"
        netWorth >= 1_500_000L -> "🏢 ALABA WHOLESALER"
        netWorth >= 500_000L -> "🛒 RISING TRADER"
        else -> "🌱 STARTING HUSTLER"
    }
}
