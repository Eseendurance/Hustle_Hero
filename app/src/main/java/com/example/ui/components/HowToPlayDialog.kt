package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HowToPlayDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("how_to_play_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = CardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            shadowElevation = 8.dp
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 Hustle Hero Master Guide",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleGoldDark
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }
                }

                item {
                    RuleSection(
                        emoji = "👛",
                        title = "1. Dual Wallet System",
                        description = "Your Business Cash and Personal Cash are separate. Pay yourself a weekly salary from your business profit. Never drain business capital to fund personal lifestyle!"
                    )
                }

                item {
                    RuleSection(
                        emoji = "📢",
                        title = "2. Paid Ads & Broadcast Media",
                        description = "Grow beyond a quiet stall! Run paid Television Broadcast ads, Highway Mega Billboards, or Mobile Internet & Social ads to multiply customer demand by up to +80%."
                    )
                }

                item {
                    RuleSection(
                        emoji = "🤝",
                        title = "3. Community Union & Plaza",
                        description = "Vote on market union projects like solar plaza lighting or security guards, network with fellow street moguls, and save in 5-week rotating Ajo / Esusu clubs."
                    )
                }

                item {
                    RuleSection(
                        emoji = "🎲",
                        title = "4. Street Events & Dilemmas",
                        description = "Every week brings authentic Nigerian dilemmas: fuel price hikes, power cuts, customers asking for credit, and sudden medical emergencies. Each decision teaches a vital financial literacy lesson."
                    )
                }

                item {
                    RuleSection(
                        emoji = "🏆",
                        title = "5. Winning & Surviving",
                        description = "Win by reaching ₦5,000,000 Net Worth or surviving 52 turbulent weeks! Avoid bankruptcy (business cash deep negative) and burnout (stress reaching 100%)."
                    )
                }

                item {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                    ) {
                        Text("Let's Hustle!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RuleSection(emoji: String, title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceSubtle),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
            }
        }
    }
}
