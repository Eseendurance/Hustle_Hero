package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyFormatter
import com.example.model.WeeklyTurnReport
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenLight
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlin.math.max

@Composable
fun NetWorthChart(
    history: List<WeeklyTurnReport>,
    currentNetWorth: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📈 Net Worth Trajectory",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${history.size + 1} wks recorded",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val points = history.map { it.endingNetWorth }.toMutableList()
            if (points.isEmpty()) {
                points.add(currentNetWorth)
            }

            val minVal = points.minOrNull() ?: 0L
            val maxVal = max(points.maxOrNull() ?: 1L, minVal + 1L)
            val range = max(1L, maxVal - minVal).toFloat()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    if (points.size < 2) {
                        drawLine(
                            color = HustleGreen,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawCircle(
                            color = PositiveGreen,
                            radius = 6.dp.toPx(),
                            center = Offset(size.width / 2, size.height / 2)
                        )
                        return@Canvas
                    }

                    val stepX = size.width / (points.size - 1)
                    val chartPath = Path()
                    val fillPath = Path()

                    points.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalizedY = (value - minVal) / range
                        val y = size.height - (normalizedY * (size.height - 30.dp.toPx()) + 15.dp.toPx())

                        if (index == 0) {
                            chartPath.moveTo(x, y)
                            fillPath.moveTo(x, size.height)
                            fillPath.lineTo(x, y)
                        } else {
                            chartPath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }

                        if (index == points.size - 1) {
                            fillPath.lineTo(x, size.height)
                            fillPath.close()
                        }
                    }

                    // Area gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                HustleGreen.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )

                    // Stroke line
                    drawPath(
                        path = chartPath,
                        color = HustleGreen,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    // Nodes
                    points.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalizedY = (value - minVal) / range
                        val y = size.height - (normalizedY * (size.height - 30.dp.toPx()) + 15.dp.toPx())

                        drawCircle(
                            color = PureWhite,
                            radius = 5.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = if (index == points.size - 1) PositiveGreen else HustleGreen,
                            radius = 3.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Low: ${CurrencyFormatter.formatCompact(minVal)}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Peak: ${CurrencyFormatter.formatCompact(maxVal)}",
                    fontSize = 11.sp,
                    color = PositiveGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
