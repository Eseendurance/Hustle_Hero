package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.WeeklyTurnReport
import com.example.ui.components.NetWorthChart
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DangerRed
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
fun WeeklySummaryScreen(
    state: GameState,
    report: WeeklyTurnReport,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("weekly_summary_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WEEK ${report.weekNumber} STATEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val isProfit = report.netWorthChange >= 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isProfit) PositiveGreen else DangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = CurrencyFormatter.formatSignedNaira(report.netWorthChange),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isProfit) PositiveGreen else DangerRed
                        )
                    }

                    Text(
                        text = "Net Worth Change this week",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Section 1: Business Operations Breakdown
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏪 Business Profit & Loss",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SummaryRow("Sales Revenue (${report.unitsSold} units)", CurrencyFormatter.formatSignedNaira(report.salesRevenue), PositiveGreen)
                    SummaryRow("Cost of Goods Sold (COGS)", CurrencyFormatter.formatSignedNaira(-report.cogs), TextMuted)
                    SummaryRow("Operating Expenses (Staff, Upkeep)", CurrencyFormatter.formatSignedNaira(-report.operatingExpenses), TextMuted)

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    SummaryRow(
                        "Net Business Profit",
                        CurrencyFormatter.formatSignedNaira(report.businessNetProfit),
                        if (report.businessNetProfit >= 0) PositiveGreen else DangerRed,
                        isBold = true
                    )
                }
            }
        }

        // Section 2: Personal & Wealth Flows
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💼 Personal & Portfolio Cashflow",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGoldDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SummaryRow("Salary Paid to Yourself", CurrencyFormatter.formatNaira(report.salaryWithdrawn), PositiveGreen)
                    SummaryRow("Living Expenses (Rent, Food, Family)", CurrencyFormatter.formatSignedNaira(-report.personalLivingCost), DangerRed)

                    if (report.ajoContributionPaid > 0) {
                        SummaryRow("Ajo Savings Contribution", CurrencyFormatter.formatSignedNaira(-report.ajoContributionPaid), TextMuted)
                    }
                    if (report.ajoPayoutReceived > 0) {
                        SummaryRow("🎉 Ajo Lump-Sum Payout Received!", CurrencyFormatter.formatSignedNaira(report.ajoPayoutReceived), PositiveGreen, isBold = true)
                    }
                    if (report.investmentGainLoss != 0L) {
                        SummaryRow(
                            "Investment Yield",
                            CurrencyFormatter.formatSignedNaira(report.investmentGainLoss),
                            if (report.investmentGainLoss > 0) PositiveGreen else DangerRed
                        )
                    }
                    if (report.loanInterestPaid > 0) {
                        SummaryRow("Loan Interest Paid", CurrencyFormatter.formatSignedNaira(-report.loanInterestPaid), DangerRed)
                    }
                }
            }
        }

        // Section 3: The Chart
        item {
            NetWorthChart(
                history = state.turnHistory,
                currentNetWorth = report.endingNetWorth
            )
        }

        // Section 4: Money Lesson Highlight
        if (report.eventLesson != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = HustleGreenContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = HustleGoldDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WEEKLY FINANCIAL WISDOM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGoldDark,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = report.eventLesson,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Proceed to next week CTA
        item {
            Button(
                onClick = onNextWeek,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_next_week_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
            ) {
                Text(
                    text = "Advance to Week ${state.week} ->",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isBold) TextPrimary else TextSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}
