package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.model.Upgrade
import com.example.model.WalletDestination
import com.example.model.WalletSource
import com.example.model.WalletTransferRequest
import com.example.model.WalletTransferType
import com.example.ui.HustleHeroViewModel
import com.example.ui.components.StreetHaggleDialog
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
import kotlin.math.min

@Composable
fun BusinessPhaseScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    salaryTransfer: Long,
    onNextPhase: () -> Unit,
    modifier: Modifier = Modifier
) {
    var restockBatch by remember { mutableIntStateOf(20) }
    var currentPrice by remember(state.sellingPrice) { mutableStateOf(state.sellingPrice.toFloat()) }

    var showTransferDialog by remember { mutableStateOf(false) }
    var activeTransferType by remember { mutableStateOf(WalletTransferType.OWNER_DIVIDEND) }
    var showClearanceDialog by remember { mutableStateOf(false) }
    var showHaggleDialog by remember { mutableStateOf(false) }

    val restockQuote = remember(restockBatch, state) {
        viewModel.calculateRestockQuote(restockBatch)
    }
    val reorderRecommendation = remember(state) {
        viewModel.getRecommendedRestock()
    }
    val recommendedSalary = remember(state) {
        viewModel.getRecommendedSalary()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("business_phase_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = HustleGreenContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = state.business.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PHASE 1: BUSINESS OPERATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGreenDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = state.business.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = state.business.tagline,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Section 1: Inventory & Wholesale Restocking
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
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
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = HustleGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Wholesale Inventory",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HustleGoldContainer
                        ) {
                            Text(
                                text = "Stock: ${state.inventoryUnits} ${state.business.itemUnitName}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = HustleGoldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Wholesale Pricing Tier Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "1-49: Base" to (restockBatch < 50),
                            "50+: -5% Bulk" to (restockBatch in 50..99),
                            "100+: -10% Crate" to (restockBatch in 100..199),
                            "200+: -15% Direct" to (restockBatch >= 200)
                        ).forEach { (label, isActive) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isActive) HustleGreenContainer else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isActive) HustleGreen else BorderSubtle
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isActive) HustleGreenDark else TextMuted,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Smart Reorder Tip Button
                    if (reorderRecommendation != null && reorderRecommendation.recommendedRestockUnits > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HustleGoldContainer.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    restockBatch = reorderRecommendation.recommendedRestockUnits
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = "Market Elder Tip", tint = HustleGoldDark, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Papa Chukwudi's Advice: Restock +${reorderRecommendation.recommendedRestockUnits} units (${reorderRecommendation.rationale})",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Quick Quantity Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 25, 50, 100).forEach { qty ->
                            val isSelected = restockBatch == qty
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) HustleGreen else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) HustleGreen else BorderSubtle
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { restockBatch = qty }
                            ) {
                                Text(
                                    text = "+$qty",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else TextPrimary,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Restock Quote Details
                    restockQuote?.let { quote ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Wholesale Unit Cost: ${CurrencyFormatter.formatNaira(quote.discountedUnitCost)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Tier: ${quote.tierName} • Cap: ${quote.resultingStock}/${quote.maxStorageCapacity}",
                                        fontSize = 10.sp,
                                        color = if (quote.fitsInStorage) HustleGreenDark else DangerRed
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = CurrencyFormatter.formatNaira(quote.totalCost),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (quote.isAffordable) HustleGreenDark else DangerRed
                                    )
                                    Text(
                                        text = if (quote.discountPercent > 0) "-${quote.discountPercent.toInt()}% off base" else "Std price",
                                        fontSize = 10.sp,
                                        color = PositiveGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stepper + Restock CTA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { restockBatch = max(5, restockBatch - 5) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardSurfaceSubtle, CircleShape)
                                    .border(1.dp, BorderSubtle, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "$restockBatch units",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { restockBatch += 5 },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardSurfaceSubtle, CircleShape)
                                    .border(1.dp, BorderSubtle, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showHaggleDialog = true },
                                enabled = (restockQuote?.isAffordable == true) && restockBatch > 0,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("haggle_button")
                            ) {
                                Text("🤝 Haggle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.restockInventory(restockBatch) },
                                enabled = (restockQuote?.isAffordable == true) && (restockQuote?.fitsInStorage == true) && restockBatch > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("buy_stock_button")
                            ) {
                                Text(
                                    text = "Buy (${CurrencyFormatter.formatCompact(restockQuote?.totalCost ?: 0L)})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Emergency Stock Liquidation Option (if player has inventory)
                    if (state.inventoryUnits > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showClearanceDialog = true }
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Clearance / Liquidation Sale",
                                    fontSize = 11.sp,
                                    color = WarningOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Pricing Strategy
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
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = null,
                                tint = HustleGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Selling Price per Unit",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = CurrencyFormatter.formatNaira(currentPrice.toLong()),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val profitMargin = currentPrice.toLong() - state.costPerUnit
                    Text(
                        text = "Margin: ${CurrencyFormatter.formatSignedNaira(profitMargin)} / unit | Market Baseline: ${CurrencyFormatter.formatNaira(state.business.baseSellingPrice)}",
                        fontSize = 12.sp,
                        color = if (profitMargin > 0) PositiveGreen else DangerRed
                    )

                    Slider(
                        value = currentPrice,
                        onValueChange = { currentPrice = it },
                        onValueChangeFinished = { viewModel.setSellingPrice(currentPrice.toLong()) },
                        valueRange = (state.costPerUnit * 0.7f)..(state.business.baseSellingPrice * 2.2f),
                        steps = 20,
                        colors = SliderDefaults.colors(
                            thumbColor = HustleGold,
                            activeTrackColor = HustleGold,
                            inactiveTrackColor = CardSurfaceSubtle
                        ),
                        modifier = Modifier.testTag("price_slider")
                    )
                }
            }
        }

        // Section 3: Staffing
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
                            text = "Staff & Apprentices",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${state.staffCount} / 5 Hired",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HustleGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Each apprentice increases weekly capacity by +35 customers and expands stock storage.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payroll: ${CurrencyFormatter.formatNaira(state.staffCount * state.staffWageWeekly)}/wk",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Row {
                            OutlinedButton(
                                onClick = { viewModel.fireStaff() },
                                enabled = state.staffCount > 0,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Let Go", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.hireStaff() },
                                enabled = state.staffCount < 5,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                            ) {
                                Text("Hire (+₦10k)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Business Shop Upgrades
        item {
            Text(
                text = "🏬 Shop Upgrades & Tools",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(state.upgrades) { upgrade ->
            UpgradeCardItem(
                upgrade = upgrade,
                canAfford = state.businessCash >= upgrade.cost,
                onBuy = { viewModel.buyUpgrade(upgrade.id) }
            )
        }

        // Section 5: Core Game Logic Dual-Wallet Transfers & Salary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HustleGoldContainer.copy(alpha = 0.35f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💼", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dual-Wallet Management",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGoldDark
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardSurface
                        ) {
                            Text(
                                text = "Rec Salary: ${CurrencyFormatter.formatCompact(recommendedSalary)}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = HustleGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Separate business working capital from household funds. Never let living expenses eat your inventory capital!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Current Dual Wallet Balances
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "BUSINESS CASH", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(
                                    text = CurrencyFormatter.formatNaira(state.businessCash),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (state.businessCash >= 0) HustleGreenDark else DangerRed
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "PERSONAL CASH", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(
                                    text = CurrencyFormatter.formatNaira(state.personalCash),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HustleGoldDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Salary Withdrawal Selection
                    Text(
                        text = "Weekly Salary Withdrawal (drawn upon turn completion):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(5_000L, 15_000L, 30_000L, 50_000L, recommendedSalary).distinct().forEach { salary ->
                            val isSelected = salaryTransfer == salary
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) HustleGold else CardSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGold else BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setSalaryWithdrawal(salary) }
                            ) {
                                Text(
                                    text = CurrencyFormatter.formatCompact(salary),
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else TextPrimary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Instant Transfers Actions
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
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Draw Dividend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                activeTransferType = WalletTransferType.CAPITAL_INJECTION
                                showTransferDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inject Capital", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Audit Trail Preview
                    if (state.walletTransfers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Recent Transfer Audit Log:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        state.walletTransfers.take(2).forEach { tx ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CardSurfaceSubtle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${tx.type.iconEmoji} ${tx.type.title} (Wk ${tx.week})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatNaira(tx.netAmount),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = HustleGreenDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Proceed CTA
        item {
            Button(
                onClick = onNextPhase,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("proceed_personal_phase_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Text(
                    text = "Finish Business Decisions -> Personal Finance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
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

    // Modal Dialog: Stock Liquidation / Fire Sale
    if (showClearanceDialog) {
        StockLiquidationModal(
            currentStock = state.inventoryUnits,
            unitPrice = state.sellingPrice,
            onDismiss = { showClearanceDialog = false },
            onConfirm = { unitsToClear ->
                viewModel.liquidateInventory(unitsToClear)
                showClearanceDialog = false
            }
        )
    }

    // Modal Dialog: Street Bargaining / Haggling with Wholesaler
    if (showHaggleDialog) {
        StreetHaggleDialog(
            itemName = "$restockBatch units ${state.business.name} Stock",
            sellerName = "Alhaji Danladi (Alaba Wholesaler)",
            originalPrice = restockQuote?.totalCost ?: 0L,
            onAcceptDeal = { discountPercent ->
                viewModel.restockInventory(restockBatch)
                showHaggleDialog = false
            },
            onDismiss = { showHaggleDialog = false }
        )
    }
}

@Composable
fun WalletTransferModal(
    type: WalletTransferType,
    state: GameState,
    onDismiss: () -> Unit,
    onExecute: (amount: Long, fromSavings: Boolean) -> Unit
) {
    var selectedAmount by remember { mutableLongStateOf(20_000L) }
    var useSavings by remember { mutableStateOf(false) }

    val available = when (type) {
        WalletTransferType.OWNER_DIVIDEND,
        WalletTransferType.SALARY_DRAW -> max(0L, state.businessCash)
        WalletTransferType.CAPITAL_INJECTION -> if (useSavings) max(0L, state.personalSavings) else max(0L, state.personalCash)
        WalletTransferType.EMERGENCY_BAILOUT -> max(0L, state.personalSavings)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${type.iconEmoji} ${type.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                Text(
                    text = type.description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Available to Transfer: ${CurrencyFormatter.formatNaira(available)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (available > 0) HustleGreenDark else DangerRed
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10_000L, 25_000L, 50_000L, min(available, 100_000L)).distinct().forEach { amt ->
                        if (amt > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedAmount == amt) HustleGreen else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedAmount = amt }
                            ) {
                                Text(
                                    text = CurrencyFormatter.formatCompact(amt),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedAmount == amt) PureWhite else TextPrimary,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onExecute(selectedAmount, useSavings) },
                enabled = selectedAmount > 0 && selectedAmount <= available,
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Text("Confirm Transfer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StockLiquidationModal(
    currentStock: Int,
    unitPrice: Long,
    onDismiss: () -> Unit,
    onConfirm: (units: Int) -> Unit
) {
    var unitsToClear by remember { mutableIntStateOf(min(15, currentStock)) }
    val clearancePrice = (unitPrice * 0.70f).toLong()
    val totalCash = unitsToClear.toLong() * clearancePrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🏷️ Clearance Fire Sale", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column {
                Text(
                    text = "Liquidate slow-moving stock at a 30% discount for immediate cash injection.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Clear $unitsToClear units -> Recover ${CurrencyFormatter.formatNaira(totalCash)} in Business Cash.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PositiveGreen
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(unitsToClear) },
                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
            ) {
                Text("Liquidate Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UpgradeCardItem(
    upgrade: Upgrade,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(CardSurfaceSubtle, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = upgrade.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = upgrade.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = upgrade.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Upkeep: ${CurrencyFormatter.formatNaira(upgrade.weeklyMaintenance)}/wk",
                    fontSize = 10.sp,
                    color = HustleGoldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (upgrade.isPurchased) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HustleGreenContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Active", fontSize = 11.sp, color = HustleGreenDark, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("buy_upgrade_${upgrade.id}")
                ) {
                    Text(text = CurrencyFormatter.formatCompact(upgrade.cost), fontSize = 11.sp)
                }
            }
        }
    }
}
