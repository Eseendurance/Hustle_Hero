package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.GamePhase
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.LightDarkSegmentedSwitch
import com.example.ui.components.RealisticBankSmsPopup
import com.example.ui.components.ShopCounterLedgerDialog
import com.example.ui.components.TutorialCard
import com.example.ui.components.WazobiaRadioBanner
import com.example.ui.components.WalletHeader
import com.example.ui.theme.WarningOrange
import com.example.ui.screens.BusinessPhaseScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DailyHustleScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventModalScreen
import com.example.ui.screens.EverydayLifeScreen
import com.example.ui.screens.GameOverScreen
import com.example.ui.screens.GourmetMealsScreen
import com.example.ui.screens.Market3DScreen
import com.example.ui.screens.MarketingAdScreen
import com.example.ui.screens.NewGameDialog
import com.example.ui.screens.PersonalPhaseScreen
import com.example.ui.screens.WeeklySummaryScreen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.HustleGold
import com.example.ui.theme.HustleGoldContainer
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenContainer
import com.example.ui.theme.HustleGreenDark
import com.example.ui.theme.HustleHeroTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private val viewModel: HustleHeroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val gameState by viewModel.gameState.collectAsState()
            val isDarkMode = gameState?.isDarkMode ?: false

            HustleHeroTheme(isDarkMode = isDarkMode) {
                HustleHeroApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HustleHeroApp(
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val salaryTransfer by viewModel.selectedSalaryTransfer.collectAsState()
    val activeEvent by viewModel.activeEvent.collectAsState()
    val latestBankAlert by viewModel.latestBankAlert.collectAsState()
    val showDailyStreakDialog by viewModel.showDailyStreakDialog.collectAsState()
    val dailyStreakInfo by viewModel.dailyStreakInfo.collectAsState()

    var showNewGameDialog by remember { mutableStateOf(false) }
    var showHowToPlayDialog by remember { mutableStateOf(false) }
    var showShopLedgerDialog by remember { mutableStateOf(false) }
    var dismissedTutorialTurn by remember { mutableStateOf(0) }

    val state = gameState ?: return

    BackHandler(enabled = state.currentPhase != GamePhase.DASHBOARD && !state.isGameOver) {
        viewModel.navigateToPhase(GamePhase.DASHBOARD)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HustleGreen
                        ) {
                            Text(
                                text = "HUSTLE HERO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showNewGameDialog = true }
                        ) {
                            Text(text = state.character.avatarEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.business.iconEmoji} ${state.business.name}",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Tactile Light Mode vs Dark Mode Segmented Switch
                    LightDarkSegmentedSwitch(
                        isDarkMode = state.isDarkMode,
                        onToggle = { viewModel.toggleDarkMode() },
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Daily Streak Flame Tracker Button
                    IconButton(
                        onClick = { viewModel.openDailyStreakDialog() },
                        modifier = Modifier.testTag("streak_top_button")
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Text(text = "🔥", fontSize = 18.sp)
                            if (dailyStreakInfo.canClaimToday) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(WarningOrange, CircleShape)
                                )
                            }
                        }
                    }

                    // Shop Counter Ledger & Debt Book Button
                    IconButton(
                        onClick = { showShopLedgerDialog = true },
                        modifier = Modifier.testTag("ledger_top_button")
                    ) {
                        Text(text = "📒", fontSize = 18.sp)
                    }

                    IconButton(
                        onClick = { showHowToPlayDialog = true },
                        modifier = Modifier.testTag("help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "How to Play Guide",
                            tint = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = { showNewGameDialog = true },
                        modifier = Modifier.testTag("new_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Switch Character / New Hustle",
                            tint = HustleGoldDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardSurface,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Realistic Instant Bank SMS Notification (GTBank / Zenith / Apex Trust)
            RealisticBankSmsPopup(
                alert = latestBankAlert,
                onDismiss = { viewModel.dismissBankAlert() }
            )

            // Persistent Dual-Wallet & Vital Stats Card
            WalletHeader(
                state = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                onStreakClick = { viewModel.openDailyStreakDialog() }
            )

            // Tutorial Hint Card for first 3 turns
            if (state.week <= 3 && dismissedTutorialTurn != state.week && !state.isGameOver) {
                TutorialCard(
                    week = state.week,
                    onDismiss = { dismissedTutorialTurn = state.week },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Wazobia Hustle Radio & Live Street News Ticker Banner
            WazobiaRadioBanner(
                radioState = state.radioStation,
                onTogglePlayback = { viewModel.toggleRadioPlayback() },
                onSelectStation = { viewModel.switchRadioStation(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
            )

            // Top Hub Navigation Tabs Bar
            ScrollableHubNavBar(
                currentPhase = state.currentPhase,
                onSelectPhase = { viewModel.navigateToPhase(it) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main View Switching
            Box(modifier = Modifier.weight(1f)) {
                when (state.currentPhase) {
                    GamePhase.DASHBOARD -> {
                        DashboardScreen(
                            state = state,
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateToPhase(it) }
                        )
                    }
                    GamePhase.DAILY_HUSTLE -> {
                        DailyHustleScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.LIFE_ROUTINE -> {
                        EverydayLifeScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.GOURMET_MEALS -> {
                        GourmetMealsScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.BUSINESS -> {
                        BusinessPhaseScreen(
                            state = state,
                            viewModel = viewModel,
                            salaryTransfer = salaryTransfer,
                            onNextPhase = { viewModel.proceedToPersonalPhase() }
                        )
                    }
                    GamePhase.PERSONAL -> {
                        PersonalPhaseScreen(
                            state = state,
                            viewModel = viewModel,
                            onTriggerEvent = { viewModel.triggerEventPhase() }
                        )
                    }
                    GamePhase.MARKETING_ADS -> {
                        MarketingAdScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.COMMUNITY -> {
                        CommunityScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.MARKET_3D -> {
                        Market3DScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    GamePhase.EVENT -> {
                        val eventToDisplay = activeEvent ?: state.currentEvent
                        if (eventToDisplay != null) {
                            EventModalScreen(
                                event = eventToDisplay,
                                viewModel = viewModel
                            )
                        } else {
                            viewModel.triggerEventPhase()
                        }
                    }
                    GamePhase.SUMMARY -> {
                        val report = state.latestReport
                        if (report != null) {
                            WeeklySummaryScreen(
                                state = state,
                                report = report,
                                onNextWeek = { viewModel.nextWeek() }
                            )
                        }
                    }
                    GamePhase.GAME_OVER -> {
                        GameOverScreen(
                            state = state,
                            onRestart = { viewModel.restartGame() }
                        )
                    }
                }
            }
        }
    }

    if (showNewGameDialog) {
        NewGameDialog(
            onDismiss = { showNewGameDialog = false },
            onStartGame = { name, business, city, character, track, job ->
                viewModel.startNewGame(name, business, city, character, track, job)
            }
        )
    }

    if (showHowToPlayDialog) {
        HowToPlayDialog(
            onDismiss = { showHowToPlayDialog = false }
        )
    }

    if (showShopLedgerDialog) {
        ShopCounterLedgerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showShopLedgerDialog = false }
        )
    }

    if (showDailyStreakDialog) {
        DailyStreakDialog(
            streakInfo = dailyStreakInfo,
            onClaimStandard = { viewModel.claimDailyStreakReward(doubleWithAd = false) },
            onClaimDoubleWithAd = { viewModel.claimDailyStreakReward(doubleWithAd = true) },
            onDismiss = { viewModel.dismissDailyStreakDialog() }
        )
    }
}

@Composable
fun ScrollableHubNavBar(
    currentPhase: GamePhase,
    onSelectPhase: (GamePhase) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HubTab("🏠 Dashboard", isActive = currentPhase == GamePhase.DASHBOARD) { onSelectPhase(GamePhase.DASHBOARD) }
        HubTab("⚡ Daily Hustle", isActive = currentPhase == GamePhase.DAILY_HUSTLE) { onSelectPhase(GamePhase.DAILY_HUSTLE) }
        HubTab("☀️ Everyday Life", isActive = currentPhase == GamePhase.LIFE_ROUTINE) { onSelectPhase(GamePhase.LIFE_ROUTINE) }
        HubTab("🍲 Gourmet Meals", isActive = currentPhase == GamePhase.GOURMET_MEALS) { onSelectPhase(GamePhase.GOURMET_MEALS) }
        HubTab("🛒 Business/Job", isActive = currentPhase == GamePhase.BUSINESS) { onSelectPhase(GamePhase.BUSINESS) }
        HubTab("👛 Personal", isActive = currentPhase == GamePhase.PERSONAL) { onSelectPhase(GamePhase.PERSONAL) }
        HubTab("📢 Paid Ads Spot", isActive = currentPhase == GamePhase.MARKETING_ADS) { onSelectPhase(GamePhase.MARKETING_ADS) }
        HubTab("🤝 Community", isActive = currentPhase == GamePhase.COMMUNITY) { onSelectPhase(GamePhase.COMMUNITY) }
        HubTab("🌐 3D View", isActive = currentPhase == GamePhase.MARKET_3D) { onSelectPhase(GamePhase.MARKET_3D) }
    }
}

@Composable
fun HubTab(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("tab_${label.filter { it.isLetter() }}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) HustleGreen else CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) HustleGreen else BorderSubtle),
        shadowElevation = if (isActive) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isActive) PureWhite else TextSecondary
            )
        }
    }
}
