package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketLocation
import com.example.data.MarketLocationsData
import com.example.model.CurrencyFormatter
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
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
fun MarketGoogleMap(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locations = remember { MarketLocationsData.locations }
    var selectedLocation by remember { mutableStateOf(locations.first()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_map_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Map Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(HustleBlueContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = HustleBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Google Maps Commercial Hubs",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Live Nigerian market trade coordinates",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HustleGreenContainer
                ) {
                    Text(
                        text = "GPS Online",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Google Maps Styled Canvas View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE5F1E8), RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            ) {
                // Vector Map Canvas representing Lagos Lagoon & Highway Grids
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw Lagos Lagoon Water
                    val lagoonPath = Path().apply {
                        moveTo(0f, h * 0.45f)
                        cubicTo(w * 0.3f, h * 0.35f, w * 0.6f, h * 0.65f, w, h * 0.50f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = lagoonPath,
                        color = Color(0xFFA5D8F3) // Soft Google Maps water blue
                    )

                    // Draw Third Mainland Bridge & Major Expressways
                    drawLine(
                        color = Color(0xFFFED7AA), // Highway orange
                        start = Offset(0f, h * 0.25f),
                        end = Offset(w, h * 0.25f),
                        strokeWidth = 6.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFED7AA),
                        start = Offset(w * 0.55f, 0f),
                        end = Offset(w * 0.55f, h),
                        strokeWidth = 5.dp.toPx()
                    )
                    // Secondary streets
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = Offset(0f, h * 0.70f),
                        end = Offset(w, h * 0.70f),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = Offset(w * 0.25f, 0f),
                        end = Offset(w * 0.25f, h),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Interactive Pin Markers Overlay
                val pinPositions = listOf(
                    0.20f to 0.22f, // Comp Village
                    0.15f to 0.60f, // Alaba
                    0.65f to 0.52f, // Balogun
                    0.48f to 0.38f, // Tejuosho
                    0.80f to 0.18f, // Wuse
                    0.35f to 0.15f  // Bodija
                )

                locations.forEachIndexed { index, loc ->
                    val pos = pinPositions.getOrElse(index) { 0.5f to 0.5f }
                    val isSelected = selectedLocation.id == loc.id

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = (pos.first * 260).dp,
                                top = (pos.second * 120).dp
                            )
                            .clickable { selectedLocation = loc }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) HustleGreen else PureWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (isSelected) HustleGold else HustleGreen
                            ),
                            shadowElevation = if (isSelected) 6.dp else 2.dp,
                            modifier = Modifier.size(if (isSelected) 36.dp else 28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = loc.iconEmoji,
                                    fontSize = if (isSelected) 18.sp else 14.sp
                                )
                            }
                        }
                    }
                }

                // Live Street Map Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = PureWhite.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "🗺️ Street GPS",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Market Location Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(locations) { loc ->
                    val isSelected = selectedLocation.id == loc.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HustleGreen else CardSurfaceSubtle,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGreen else BorderSubtle),
                        modifier = Modifier.clickable { selectedLocation = loc }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = loc.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loc.name.split(" ").take(2).joinToString(" "),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PureWhite else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Market Info Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardSurfaceSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedLocation.iconEmoji} ${selectedLocation.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${selectedLocation.city} • Lat: ${selectedLocation.latitude}, Lng: ${selectedLocation.longitude}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = HustleGreenContainer
                        ) {
                            Text(
                                text = "${selectedLocation.footTrafficScore}% Foot Traffic",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedLocation.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avg Daily Spend: ${CurrencyFormatter.formatNaira(selectedLocation.avgDailySpend)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGoldDark
                        )

                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("geo:${selectedLocation.latitude},${selectedLocation.longitude}?q=${Uri.encode(selectedLocation.name + ", " + selectedLocation.city)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HustleBlue),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("open_in_google_maps_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = HustleBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open in Maps", fontSize = 11.sp, color = HustleBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
