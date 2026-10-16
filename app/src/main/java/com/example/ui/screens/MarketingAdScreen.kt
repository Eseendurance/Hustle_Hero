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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.data.BusinessData
import com.example.model.AdCampaign
import com.example.model.AdType
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.ui.HustleHeroViewModel
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

@Composable
fun MarketingAdScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val allAds = remember { BusinessData.getAvailableAds() }
    var selectedFilter by remember { mutableStateOf<AdType?>(null) }

    val filteredAds = remember(selectedFilter) {
        if (selectedFilter == null) allAds else allAds.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("marketing_ad_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Agency Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = HustleBlueContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, HustleBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📢", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PAID ADS & BROADCAST SPOT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HustleBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "TV, Billboard & Mobile Marketing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Amplify demand across Lagos with paid broadcast media slots.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Active Ad Campaigns Pulse
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📡 Active Live Campaigns",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (state.activeAds.isNotEmpty()) HustleGreenContainer else CardSurfaceSubtle
                        ) {
                            Text(
                                text = "${state.activeAds.size} Airing Now",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.activeAds.isNotEmpty()) HustleGreenDark else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.activeAds.isEmpty()) {
                        Text(
                            text = "No media campaigns are currently running. Launch a TV slot, Expressway Billboard, or Social Media campaign below to massively increase your customer walk-ins!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    } else {
                        state.activeAds.forEach { activeAd ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = activeAd.iconEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = activeAd.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = "+${activeAd.demandBoostPercent}% Demand", fontSize = 10.sp, color = PositiveGreen, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HustleBlueContainer
                                ) {
                                    Text(
                                        text = "${activeAd.weeksRemaining} wks left",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HustleBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabChip("All Channels", isSelected = selectedFilter == null) { selectedFilter = null }
                FilterTabChip("📺 TV", isSelected = selectedFilter == AdType.TV) { selectedFilter = AdType.TV }
                FilterTabChip("🪧 Billboard", isSelected = selectedFilter == AdType.BILLBOARD) { selectedFilter = AdType.BILLBOARD }
                FilterTabChip("📱 Mobile", isSelected = selectedFilter == AdType.MOBILE_INTERNET) { selectedFilter = AdType.MOBILE_INTERNET }
            }
        }

        // Ad Campaigns Catalog List
        items(filteredAds) { campaign ->
            val activeMatch = state.activeAds.find { it.id == campaign.id && it.isActive }
            AdCampaignCard(
                campaign = campaign,
                activeMatch = activeMatch,
                businessCash = state.businessCash,
                onLaunch = { viewModel.launchAdCampaign(campaign) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterTabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) HustleBlue else CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleBlue else BorderSubtle),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PureWhite else TextSecondary
        )
    }
}

@Composable
fun AdCampaignCard(
    campaign: AdCampaign,
    activeMatch: AdCampaign?,
    businessCash: Long,
    onLaunch: () -> Unit
) {
    val isRunning = activeMatch != null && activeMatch.isActive
    val canAfford = businessCash >= campaign.cost

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isRunning) HustleBlue else BorderSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_card_${campaign.id}")
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
                            .size(44.dp)
                            .background(
                                when (campaign.type) {
                                    AdType.TV -> HustleBlueContainer
                                    AdType.BILLBOARD -> HustleGoldContainer
                                    AdType.MOBILE_INTERNET -> HustleGreenContainer
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = campaign.iconEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = campaign.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = campaign.channelName,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (campaign.type) {
                        AdType.TV -> HustleBlueContainer
                        AdType.BILLBOARD -> HustleGoldContainer
                        AdType.MOBILE_INTERNET -> HustleGreenContainer
                    }
                ) {
                    Text(
                        text = campaign.type.title.split(" ").first(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (campaign.type) {
                            AdType.TV -> HustleBlue
                            AdType.BILLBOARD -> HustleGoldDark
                            AdType.MOBILE_INTERNET -> HustleGreenDark
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = campaign.tagline,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Impact & Duration Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdStatPill("🚀 Demand", "+${campaign.demandBoostPercent}%", PositiveGreen)
                AdStatPill("⭐ Brand Rep", "+${campaign.reputationBoost}", HustleGold)
                AdStatPill("⏱️ Air Duration", "${campaign.durationWeeks} Wks", TextPrimary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Campaign Cost", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = CurrencyFormatter.formatNaira(campaign.cost),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                if (isRunning) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = HustleGreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Airing (${activeMatch?.weeksRemaining} wks left)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreenDark
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onLaunch,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = HustleBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("launch_ad_${campaign.id}")
                    ) {
                        Text(
                            text = if (canAfford) "Launch Campaign" else "Insufficient Cash",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdStatPill(label: String, value: String, valueColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label ", fontSize = 10.sp, color = TextMuted)
            Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
