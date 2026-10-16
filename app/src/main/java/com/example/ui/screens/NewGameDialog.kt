package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BusinessData
import com.example.data.DailyLifeAndMealData
import com.example.model.Business
import com.example.model.CareerTrack
import com.example.model.CharacterProfile
import com.example.model.CurrencyFormatter
import com.example.model.JobRole
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
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
fun NewGameDialog(
    onDismiss: () -> Unit,
    onStartGame: (name: String, business: Business, city: String, character: CharacterProfile, careerTrack: CareerTrack, jobRole: JobRole?) -> Unit
) {
    val characterList = remember { BusinessData.characters }
    var selectedCharacter by remember { mutableStateOf(characterList.first()) }
    var customName by remember(selectedCharacter) { mutableStateOf(selectedCharacter.name) }
    var selectedCity by remember { mutableStateOf("Lagos") }
    val businesses = remember { BusinessData.startingBusinesses }
    var selectedBusiness by remember { mutableStateOf(businesses.first()) }

    var selectedCareerTrack by remember { mutableStateOf(CareerTrack.BUSINESS_OWNER) }
    val availableJobs = remember { DailyLifeAndMealData.getAvailableJobs() }
    var selectedJob by remember { mutableStateOf(availableJobs.first()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("new_game_dialog"),
            shape = RoundedCornerShape(24.dp),
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
                        Column {
                            Text(
                                text = "🚀 Launch Your Hustle",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Choose your character avatar, market, and business",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }
                }

                // 1. Character & Avatar Selector
                item {
                    Text(
                        text = "1. Choose Your Character & Avatar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(characterList) { char ->
                            val isSelected = selectedCharacter.id == char.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) HustleGreenContainer else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) HustleGreen else BorderSubtle
                                ),
                                modifier = Modifier
                                    .clickable { selectedCharacter = char }
                                    .testTag("avatar_${char.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = char.avatarEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = char.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = char.title.split(" ").take(2).joinToString(" "),
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Character Detail Card
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HustleGoldContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedCharacter.avatarEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${selectedCharacter.name} — ${selectedCharacter.title}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "⚡ Perk: ${selectedCharacter.perkName} (${selectedCharacter.perkDescription})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = HustleGoldDark
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedCharacter.backstory,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // 2. Character Name input
                item {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("character_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HustleGreen,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // 3. City Selection
                item {
                    Text(text = "2. Location / Market City", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(DailyLifeAndMealData.cities) { city ->
                            val isSelected = selectedCity == city
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) HustleGreen else CardSurfaceSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGreen else BorderSubtle),
                                modifier = Modifier
                                    .clickable { selectedCity = city }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = city,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PureWhite else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Career Track: Run Business OR Employed Job
                item {
                    Text(text = "3. Career Path: Business or Job", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCareerTrack == CareerTrack.BUSINESS_OWNER) HustleGreen else CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCareerTrack == CareerTrack.BUSINESS_OWNER) HustleGreen else BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCareerTrack = CareerTrack.BUSINESS_OWNER }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🏪", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Run a Business",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCareerTrack == CareerTrack.BUSINESS_OWNER) PureWhite else TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCareerTrack == CareerTrack.EMPLOYED_JOB) HustleGoldDark else CardSurfaceSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCareerTrack == CareerTrack.EMPLOYED_JOB) HustleGoldDark else BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCareerTrack = CareerTrack.EMPLOYED_JOB }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💼", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Employed Job",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCareerTrack == CareerTrack.EMPLOYED_JOB) PureWhite else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Show Businesses or Jobs based on track
                if (selectedCareerTrack == CareerTrack.BUSINESS_OWNER) {
                    item {
                        Text(text = "4. Select Starting Enterprise", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                    }

                    items(businesses) { b ->
                        val isSelected = selectedBusiness.id == b.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) HustleGreenContainer else CardSurfaceSubtle),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) HustleGreen else BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBusiness = b }
                                .testTag("business_select_${b.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = b.iconEmoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = b.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(text = b.tagline, fontSize = 11.sp, color = TextMuted)
                                    Text(
                                        text = "Capital: ${CurrencyFormatter.formatNaira(b.startingCapital + selectedCharacter.startingCapitalBonus)} | Base Demand: ${b.baseWeeklyDemand}/wk",
                                        fontSize = 10.sp,
                                        color = HustleGoldDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(text = "4. Select Salary Role / Job", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                    }

                    items(availableJobs) { job ->
                        val isSelected = selectedJob.id == job.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) HustleGoldContainer else CardSurfaceSubtle),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) HustleGoldDark else BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedJob = job }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = job.iconEmoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = job.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = HustleGoldDark, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(text = "${job.company} • ${job.workHours}", fontSize = 11.sp, color = TextMuted)
                                    Text(
                                        text = "Weekly Salary: ${CurrencyFormatter.formatNaira(job.weeklySalary)}",
                                        fontSize = 11.sp,
                                        color = PositiveGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Start Hustle CTA
                item {
                    Button(
                        onClick = {
                            onStartGame(
                                customName,
                                selectedBusiness,
                                selectedCity,
                                selectedCharacter,
                                selectedCareerTrack,
                                if (selectedCareerTrack == CareerTrack.EMPLOYED_JOB) selectedJob else null
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_start_game_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PureWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Start Hustle as ${customName}!", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
