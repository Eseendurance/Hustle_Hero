package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.model.CustomerDebtor
import com.example.model.GameState
import com.example.model.RadioStation
import com.example.model.RadioStationState
import com.example.model.ShopLedgerEntry
import com.example.ui.HustleHeroViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
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
import com.example.ui.theme.HustleGreenLight
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BankSmsAlert(
    val id: String = UUID.randomUUID().toString(),
    val isCredit: Boolean,
    val amount: Long,
    val senderOrMerchant: String,
    val balanceAfter: Long,
    val timestamp: String = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US).format(Date()),
    val bankName: String = "Apex Trust Bank"
)

@Composable
fun RealisticBankSmsPopup(
    alert: BankSmsAlert?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = alert != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (alert != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (alert.isCredit) PositiveGreen else DangerRed
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { onDismiss() }
                    .testTag("bank_sms_popup")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (alert.isCredit) HustleGreenContainer else Color(0xFFFFE4E6),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (alert.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (alert.isCredit) PositiveGreen else DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬 SMS ALERT • ${alert.bankName.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (alert.isCredit) PositiveGreen else DangerRed,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = alert.timestamp,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }

                        Text(
                            text = if (alert.isCredit) {
                                "CREDIT: ₦${CurrencyFormatter.formatNaira(alert.amount)} | From: ${alert.senderOrMerchant}"
                            } else {
                                "DEBIT: ₦${CurrencyFormatter.formatNaira(alert.amount)} | To: ${alert.senderOrMerchant}"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Avail Bal: ₦${CurrencyFormatter.formatNaira(alert.balanceAfter)} | Ref: TRX-${alert.id.take(8).uppercase()}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThermalReceiptDialog(
    merchantName: String,
    location: String,
    itemsBought: List<Pair<String, Long>>,
    totalAmount: Long,
    onDismiss: () -> Unit
) {
    val dateStr = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(Date()) }
    val refCode = remember { "TXN-" + UUID.randomUUID().toString().take(8).uppercase() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🧾 OFFICIAL TRANSACTION RECEIPT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardSurfaceSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = merchantName.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Text(text = location, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
                    Text(text = "TIN: 10482910-001 | RC: 829104", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                    Text(text = "DATE: $dateStr", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    itemsBought.forEach { (item, cost) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                            Text(text = CurrencyFormatter.formatNaira(cost), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "TOTAL PAID:", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = CurrencyFormatter.formatNaira(totalAmount), fontSize = 14.sp, fontWeight = FontWeight.Black, color = PositiveGreen)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "REF: $refCode", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                    Text(text = "*** THANK YOU FOR YOUR PATRONAGE! ***", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(text = "🇳🇬 Street commerce powered by real hustle", fontSize = 9.sp, color = HustleGreen)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
fun StreetHaggleDialog(
    itemName: String,
    sellerName: String,
    originalPrice: Long,
    onAcceptDeal: (discountPercent: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var offerMade by remember { mutableStateOf<Int?>(null) }
    var sellerReply by remember { mutableStateOf<String?>(null) }
    var dealClosed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🤝 'How Much Last?' (Street Bargaining)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Bargain with $sellerName for $itemName. Original Price: ₦${CurrencyFormatter.formatNaira(originalPrice)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                if (sellerReply != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = HustleGoldContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sellerReply!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HustleGoldDark,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (!dealClosed) {
                    Text(text = "Choose your bargaining pitch:", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                offerMade = 10
                                sellerReply = "$sellerName: 'Ah my brother! Because na you, I go cut 10% for you! Pay quick make I give you your goods!'"
                                dealClosed = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("-10% Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                offerMade = 15
                                sellerReply = "$sellerName: 'Hmm! You sabi price o! Oya take 15% discount, but next time you must bring your friends!'"
                                dealClosed = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("-15% Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                offerMade = 0
                                sellerReply = "$sellerName: 'Thank you oga! Good customer like you, heaven go bless your business!'"
                                dealClosed = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pay Full", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (dealClosed && offerMade != null) {
                Button(
                    onClick = {
                        onAcceptDeal(offerMade!!)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PositiveGreen)
                ) {
                    Text("Seal the Deal & Pay")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * High-visibility, tactile Light Mode vs Dark Mode Segmented Switch.
 * Allows instant, natural switching between vibrant daylight market view and midnight night-market mode.
 */
@Composable
fun LightDarkSegmentedSwitch(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onToggle)
            .testTag("theme_mode_switch"),
        shape = RoundedCornerShape(20.dp),
        color = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFE2EAE5),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) HustleGold.copy(alpha = 0.5f) else HustleGreen.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light Option
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (!isDarkMode) HustleGoldContainer else Color.Transparent,
                modifier = Modifier.height(28.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = "Light Mode",
                        tint = if (!isDarkMode) HustleGoldDark else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Light",
                        fontSize = 11.sp,
                        fontWeight = if (!isDarkMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isDarkMode) HustleGoldDark else TextMuted
                    )
                }
            }

            // Dark Option
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkMode) HustleGreenContainer else Color.Transparent,
                modifier = Modifier.height(28.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dark Mode",
                        tint = if (isDarkMode) HustleGreenLight else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dark",
                        fontSize = 11.sp,
                        fontWeight = if (isDarkMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDarkMode) HustleGreenLight else TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Wazobia Hustle Radio & Live Street News Ticker Banner.
 * Simulates genuine Nigerian street radio (Lagos traffic, FX rates, Afrobeat vibes, market morning prayers).
 */
@Composable
fun WazobiaRadioBanner(
    radioState: RadioStationState,
    onTogglePlayback: () -> Unit,
    onSelectStation: (RadioStation) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .testTag("wazobia_radio_banner")
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = radioState.currentStation.vibeEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${radioState.currentStation.callSign} • ${radioState.currentStation.stationName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (radioState.isPlaying) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DangerRed
                                ) {
                                    Text(
                                        text = "LIVE ON AIR",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                        Text(
                            text = radioState.currentStation.genre,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Play / Pause Icon Button
                IconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier.size(32.dp).testTag("radio_play_toggle")
                ) {
                    Icon(
                        imageVector = if (radioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (radioState.isPlaying) "Pause Radio" else "Play Radio",
                        tint = if (radioState.isPlaying) HustleGreen else HustleGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Station Selector Quick Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RadioStation.values().forEach { stn ->
                    val isSelected = radioState.currentStation == stn
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectStation(stn) }
                            .testTag("station_${stn.name}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HustleGreenContainer else CardSurfaceSubtle,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) HustleGreen else BorderSubtle
                        )
                    ) {
                        Text(
                            text = "${stn.vibeEmoji} ${stn.callSign}",
                            modifier = Modifier.padding(vertical = 4.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) HustleGreenDark else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // News Ticker Line
            Text(
                text = "📢 ${radioState.currentNewsTicker}",
                fontSize = 10.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}

/**
 * Authentic Shop Counter Ledger & Debt Recovery ("Book of Debtors / Awuf").
 * Physical-style merchant ledger simulator for true-to-life SME operations.
 */
@Composable
fun ShopCounterLedgerDialog(
    state: GameState,
    viewModel: HustleHeroViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddSaleDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📒", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SHOP COUNTER LEDGER",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${state.business.name} • Certified Records",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardSurfaceSubtle
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Daily Sales (${state.shopLedger.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            val activeDebts = state.customerDebtors.filter { !it.isSettled }.size
                            Text("Debtors ($activeDebts)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Power Grid", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> {
                        // Daily Sales Ledger
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Cash & POS Records",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Button(
                                    onClick = { showAddSaleDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("+ Record Sale", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.height(280.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(state.shopLedger) { entry ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CardSurfaceSubtle,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = entry.customerOrSupplier,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = "+${CurrencyFormatter.formatNaira(entry.amount)}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = PositiveGreen
                                                )
                                            }
                                            Text(
                                                text = "${entry.itemDescription} • ${entry.paymentChannel}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = entry.dayLabel, fontSize = 9.sp, color = TextMuted)
                                                Text(text = "✓ ${entry.note}", fontSize = 9.sp, color = HustleGreenDark)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Book of Debtors / Awuf
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Overdue Community Balances (Credit Sales)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            LazyColumn(
                                modifier = Modifier.height(280.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.customerDebtors) { debtor ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (debtor.isSettled) HustleGreenContainer.copy(alpha = 0.4f) else CardSurfaceSubtle,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (debtor.isSettled) HustleGreen else DangerRed.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(
                                                        text = debtor.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        text = debtor.relationship,
                                                        fontSize = 10.sp,
                                                        color = TextSecondary
                                                    )
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = CurrencyFormatter.formatNaira(debtor.amountOwed),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = if (debtor.isSettled) PositiveGreen else DangerRed
                                                    )
                                                    Text(
                                                        text = if (debtor.isSettled) "PAID & SETTLED" else "Overdue ${debtor.daysOverdue} days",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (debtor.isSettled) PositiveGreen else DangerRed
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "Item: ${debtor.originalPurchase}",
                                                fontSize = 10.sp,
                                                color = TextMuted,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )

                                            if (!debtor.isSettled) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { viewModel.sendDebtSmsReminder(debtor.id) },
                                                        modifier = Modifier.weight(1f).height(30.dp)
                                                    ) {
                                                        Text("📱 SMS Alert", fontSize = 9.sp)
                                                    }

                                                    Button(
                                                        onClick = { viewModel.collectDebtorPayment(debtor.id, discountPercent = 10) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = HustleGold),
                                                        modifier = Modifier.weight(1f).height(30.dp)
                                                    ) {
                                                        Text("-10% Deal", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = { viewModel.collectDebtorPayment(debtor.id, discountPercent = 0) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = PositiveGreen),
                                                        modifier = Modifier.weight(1.1f).height(30.dp)
                                                    ) {
                                                        Text("💰 Collect Full", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Power Grid & Generator status
                        val gen = state.generatorState
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (gen.hasNepaLight) HustleGreenContainer else Color(0xFFFFE4E6),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = if (gen.hasNepaLight) HustleGreenDark else DangerRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (gen.hasNepaLight) "⚡ UP NEPA! Grid Power Active" else "🔌 NEPA Take Light! Grid Outage",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (gen.hasNepaLight) HustleGreenDark else DangerRed
                                        )
                                        Text(
                                            text = if (gen.hasNepaLight) "Commercial electricity steady. Free shop chilling." else "Relying on generator power to avoid stock spoil.",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            // Generator Specs Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "⚙️ ${gen.generatorName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Fuel in Tank:", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "${gen.fuelLitres} Litres PMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Engine Status:", fontSize = 11.sp, color = TextSecondary)
                                        Text(
                                            text = if (gen.isGeneratorRunning) "RUNNING 🟢" else "OFF ⚪",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (gen.isGeneratorRunning) PositiveGreen else TextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.toggleGeneratorPower() },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(if (gen.isGeneratorRunning) "Turn Off" else "Start Gen", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.refuelGenerator(litres = 10) },
                                            colors = ButtonDefaults.buttonColors(containerColor = HustleGold),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("+10L Fuel (₦7,500)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Text("Close Ledger")
            }
        }
    )

    if (showAddSaleDialog) {
        var custName by remember { mutableStateOf("") }
        var saleDesc by remember { mutableStateOf("") }
        var saleAmt by remember { mutableStateOf("15000") }

        AlertDialog(
            onDismissRequest = { showAddSaleDialog = false },
            title = { Text("Record Manual Counter Sale", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = custName,
                        onValueChange = { custName = it },
                        label = { Text("Customer Name (e.g. Papa Jude)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = saleDesc,
                        onValueChange = { saleDesc = it },
                        label = { Text("Items Sold") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = saleAmt,
                        onValueChange = { saleAmt = it },
                        label = { Text("Total Cash Collected (₦)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = saleAmt.toLongOrNull() ?: 5000L
                        val name = if (custName.isNotBlank()) custName else "Walk-in Regular"
                        val desc = if (saleDesc.isNotBlank()) saleDesc else "Counter Retail Goods"
                        viewModel.recordLedgerSale(
                            customer = name,
                            item = desc,
                            amount = amt,
                            channel = "Cash"
                        )
                        showAddSaleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                ) {
                    Text("Add to Ledger & Cash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

