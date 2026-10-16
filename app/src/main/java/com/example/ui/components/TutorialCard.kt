package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.HustleGoldContainer
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenContainer
import com.example.ui.theme.HustleGreenDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TutorialCard(
    week: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tutorialInfo = when (week) {
        1 -> Triple(
            "TUTORIAL: SEPARATE YOUR WALLETS!",
            "Rule #1 of street business: Your Business Cash and Personal Cash are strictly separate. Use business money only for stock, staff, and generator fuel. Pay yourself a fixed salary to cover food and rent. Never 'eat your seed capital'!",
            "💡"
        )
        2 -> Triple(
            "TUTORIAL: EMERGENCY FUND SHOCK ABSORBER",
            "Shocks happen fast: malaria, police levy, or power cuts. Deposit ₦10,000 into Personal Savings. When crises strike, your emergency fund shields your shop from forced borrowing or emergency shutdowns.",
            "🛡️"
        )
        3 -> Triple(
            "TUTORIAL: AJO (ESUSU) & AD CAMPAIGNS",
            "Rotating savings clubs (Ajo) allow traders to pool funds together. Plus, use the Paid Ads Spot (TV, Billboard, Mobile Internet) to explode your weekly walk-in customer volume across town!",
            "🤝"
        )
        else -> return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tutorial_card_week_$week"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HustleGreenContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CardSurface, CircleShape)
                    .border(1.dp, HustleGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = tutorialInfo.third, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tutorialInfo.first,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HustleGreenDark,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tutorialInfo.second,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}
