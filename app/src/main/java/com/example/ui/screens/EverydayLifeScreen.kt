package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BusinessData
import com.example.data.DailyLifeAndMealData
import com.example.model.Business
import com.example.model.CareerTrack
import com.example.model.ClubbingVenue
import com.example.model.CurrencyFormatter
import com.example.model.GamePhase
import com.example.model.GameState
import com.example.model.JobRole
import com.example.model.PartyEvent
import com.example.model.RelationshipStatus
import com.example.model.TimeOfDay
import com.example.ui.HustleHeroViewModel
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
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EverydayLifeScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val toastMessage by viewModel.routineActionToast.collectAsState()
    val routine = state.dailyRoutine

    var showPartySheet by remember { mutableStateOf(false) }
    var showClubSheet by remember { mutableStateOf(false) }
    var showBabyDialog by remember { mutableStateOf(false) }
    var showCareerDialog by remember { mutableStateOf(false) }
    var babyNameInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("everyday_life_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Life Morning Banner
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Lifestyle banner image
                    Box(modifier = Modifier.height(130.dp).fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_home_morning_life),
                            contentDescription = "Morning Life in Nigeria",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "DAY ${routine.currentDay} • ${routine.timeOfDay.displayName.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = HustleGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Living the Everyday Naija Life",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardSurface.copy(alpha = 0.9f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = routine.timeOfDay.iconEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = routine.timeOfDay.hourLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Vital Bars Row
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VitalPill(
                                label = "Energy",
                                value = routine.energyLevel,
                                emoji = "⚡",
                                color = HustleGold,
                                modifier = Modifier.weight(1f)
                            )
                            VitalPill(
                                label = "Hygiene",
                                value = routine.hygieneLevel,
                                emoji = "🚿",
                                color = HustleGreen,
                                modifier = Modifier.weight(1f)
                            )
                            VitalPill(
                                label = "Fullness",
                                value = (100 - routine.hungerLevel).coerceIn(0, 100),
                                emoji = "🍲",
                                color = PositiveGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Current Status message
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💬", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = routine.lastActionMessage,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Toast Feedback
        if (toastMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HustleGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = toastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HustleGoldDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "OK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark,
                            modifier = Modifier.clickable { viewModel.clearToast() }
                        )
                    }
                }
            }
        }

        // --- 🌅 DAILY MORNING & ROUTINE ACTIONS ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                        Column {
                            Text(
                                text = "🌅 Morning & Daily Routine",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Start your day with hygiene, breakfast, school and work",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.advanceTimeOfDay() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Forward ⏱️", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1: Wake Up
                    RoutineActionRow(
                        title = "Wake Up in the Morning",
                        description = "Rise and shake off sleepiness. Get ready for the grind.",
                        emoji = "🌅",
                        isCompleted = routine.hasWokenUp,
                        buttonLabel = "Wake Up",
                        onClick = { viewModel.wakeUp() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 2: Take Bath
                    RoutineActionRow(
                        title = "Take Warm Bath / Shower",
                        description = "Lather up with soap and water. 100% hygiene and stress relief.",
                        emoji = "🚿",
                        isCompleted = routine.hasTakenBath,
                        buttonLabel = "Take Bath",
                        onClick = { viewModel.takeBath() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 3: Brush Teeth
                    RoutineActionRow(
                        title = "Brush Teeth",
                        description = "Minty toothpaste, fresh breath to negotiate business deals.",
                        emoji = "🪥",
                        isCompleted = routine.hasBrushedTeeth,
                        buttonLabel = "Brush Teeth",
                        onClick = { viewModel.brushTeeth() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 4: Make Breakfast
                    RoutineActionRow(
                        title = "Make Wholesome Breakfast",
                        description = "Cook eggs, toast, tea or akara to fuel your morning energy.",
                        emoji = "🍳",
                        isCompleted = routine.hasHadBreakfast,
                        buttonLabel = "Make Breakfast",
                        onClick = { viewModel.prepareBreakfast() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 5: Take Kids to School (if has kids)
                    if (state.familyState.kids.isNotEmpty()) {
                        RoutineActionRow(
                            title = "Take Kids to School",
                            description = "Drive ${state.familyState.kids.joinToString { it.name }} to school. Pride and joy!",
                            emoji = "🎒",
                            isCompleted = routine.hasTakenKidsToSchool,
                            buttonLabel = "School Run",
                            onClick = { viewModel.takeKidsToSchool() }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Step 6: Go to Work or Run Business
                    RoutineActionRow(
                        title = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) "Go to Job Shift" else "Run Shop / Business",
                        description = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) {
                            "Shift as ${state.currentJob?.title ?: "Employee"} at ${state.currentJob?.company ?: "Firm"}"
                        } else {
                            "Sell items and serve customers at ${state.business.name}"
                        },
                        emoji = "💼",
                        isCompleted = routine.hasWorkedOrRunBusiness,
                        buttonLabel = "Work Shift",
                        onClick = { viewModel.workOrRunBusiness() }
                    )
                }
            }
        }

        // --- 💼 CAREER & PROFESSION CHOICES ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                        Column {
                            Text(
                                text = "💼 Career & Business Choice",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Run your own enterprise or work as corporate salary earner",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Button(
                            onClick = { showCareerDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Switch", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Change Career", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardSurfaceSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) state.currentJob?.iconEmoji ?: "💼" else state.business.iconEmoji,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) state.currentJob?.title ?: "Corporate Job" else state.business.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) {
                                        "Company: ${state.currentJob?.company} • Salary: ${CurrencyFormatter.formatNaira(state.currentJob?.weeklySalary ?: 0L)}/wk"
                                    } else {
                                        "${state.business.type.name.replace("_", " ")} • Starting Stock: ${state.business.defaultStock} units"
                                    },
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) HustleGoldContainer else HustleGreenContainer
                            ) {
                                Text(
                                    text = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) "Employed" else "Owner",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.careerTrack == CareerTrack.EMPLOYED_JOB) HustleGoldDark else HustleGreenDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- ❤️ DATING, MARRIAGE & KIDS FAMILY LIFE ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                        Column {
                            Text(
                                text = "❤️ Family, Marriage & Kids",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Build romance, tie the knot, and raise your children",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (state.familyState.relationshipStatus) {
                                RelationshipStatus.MARRIED -> PositiveGreen.copy(alpha = 0.2f)
                                RelationshipStatus.ENGAGED -> HustleGoldContainer
                                RelationshipStatus.DATING -> HustleGreenContainer
                                RelationshipStatus.SINGLE -> CardSurfaceSubtle
                            }
                        ) {
                            Text(
                                text = "${state.familyState.relationshipStatus.emoji} ${state.familyState.relationshipStatus.label}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Partner info
                    val partner = state.familyState.partner ?: com.example.model.PartnerProfile()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = partner.avatarEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${partner.name} (${partner.occupation})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Affection: ${partner.affection}% • Spot: ${partner.favoriteDateSpot}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date / Marry Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.goOnDate("Victoria Island Rooftop", 12_000L) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Go on Date (₦12k)", fontSize = 11.sp)
                        }

                        if (state.familyState.relationshipStatus != RelationshipStatus.MARRIED) {
                            Button(
                                onClick = { viewModel.proposeAndMarry() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                            ) {
                                Text(text = "Get Married! 💍", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = { showBabyDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark)
                            ) {
                                Text(text = "Have Baby 👶", fontSize = 11.sp)
                            }
                        }
                    }

                    // Kids Section
                    if (state.familyState.kids.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Children (${state.familyState.kids.size} Kids)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        state.familyState.kids.forEach { kid ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CardSurfaceSubtle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = kid.avatarEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${kid.name} (Age ${kid.age})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${kid.schoolGrade} • ${kid.schoolName}",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = "😊 ${kid.happiness}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PositiveGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 🎉 NIGHTLIFE, CLUBBING & HOSTING PARTIES ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(modifier = Modifier.height(110.dp).fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_party_clubbing),
                            contentDescription = "Nightlife Party",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "NIGHTLIFE & SOCIAL VIBES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = HustleGold
                            )
                            Text(
                                text = "Host Epic Parties & Hit Top Nightclubs",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Unwind from the weekly hustle! Dance to Afrobeats, invite friends, and wash off stress.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showPartySheet = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark)
                            ) {
                                Icon(imageVector = Icons.Default.Celebration, contentDescription = "Party", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Host Party 🎉", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showClubSheet = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Nightlife, contentDescription = "Club", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Go Clubbing 🍸", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 🌙 SLEEP & NIGHT REST ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                        Column {
                            Text(
                                text = "🌙 Night Sleep & Recovery",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Recharge 100% energy, reset stress, and advance to next day",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Text(text = "🛌", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.sleepNight() },
                        modifier = Modifier.fillMaxWidth().testTag("sleep_night_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGreenDark)
                    ) {
                        Icon(imageVector = Icons.Default.Bedtime, contentDescription = "Sleep")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sleep at Night (Advance to Day ${routine.currentDay + 1})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal: Host Party
    if (showPartySheet) {
        ModalBottomSheet(
            onDismissRequest = { showPartySheet = false },
            containerColor = CardSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🎉 Host a Party & Invite Friends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Choose your party style and treat fellow hustle heroes to good vibes",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                DailyLifeAndMealData.getPartyOptions().forEach { party ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${party.iconEmoji} ${party.title}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = CurrencyFormatter.formatNaira(party.cost), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HustleGreen)
                            }
                            Text(text = "Playlist: ${party.playlist}", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "Invited: ${party.invitedGuests.joinToString()}", fontSize = 10.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.hostParty(party)
                                    showPartySheet = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark)
                            ) {
                                Text(text = "Host (${CurrencyFormatter.formatNaira(party.cost)})", fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal: Go Clubbing
    if (showClubSheet) {
        ModalBottomSheet(
            onDismissRequest = { showClubSheet = false },
            containerColor = CardSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🍸 Nightclubs & Lounges",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Hit the best clubs in Lagos for unforgettable music and drinks",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                DailyLifeAndMealData.getClubbingVenues().forEach { venue ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "${venue.iconEmoji} ${venue.name} (${venue.location})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = venue.vibeDescription, fontSize = 11.sp, color = TextSecondary)
                            Text(text = "Genre: ${venue.musicGenre}", fontSize = 10.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.goClubbing(venue, isVip = false)
                                        showClubSheet = false
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Regular (${CurrencyFormatter.formatNaira(venue.regularEntryFee)})", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.goClubbing(venue, isVip = true)
                                        showClubSheet = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                                ) {
                                    Text(text = "VIP Table (${CurrencyFormatter.formatNaira(venue.vipTableCost)})", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal: Have Baby
    if (showBabyDialog) {
        ModalBottomSheet(
            onDismissRequest = { showBabyDialog = false },
            containerColor = CardSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "👶 Welcome a Child into the Family",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Enter a name for your baby boy or girl:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = babyNameInput,
                    onValueChange = { babyNameInput = it },
                    label = { Text("Child's Name (e.g. Tolu, Chioma, Emeka)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        viewModel.haveBaby(babyNameInput)
                        babyNameInput = ""
                        showBabyDialog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                ) {
                    Text(text = "Celebrate Baby Arrival 🎊", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal: Switch Career / Business Choice
    if (showCareerDialog) {
        ModalBottomSheet(
            onDismissRequest = { showCareerDialog = false },
            containerColor = CardSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "💼 Choose Business or Salary Job",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Select whether you want to run your own enterprise or work as an employed professional",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "AVAILABLE JOBS (SALARY ROLES)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                DailyLifeAndMealData.getAvailableJobs().forEach { job ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CardSurfaceSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                viewModel.switchCareer(CareerTrack.EMPLOYED_JOB, job, null)
                                showCareerDialog = false
                            }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = job.iconEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${job.title} (${job.company})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Salary: ${CurrencyFormatter.formatNaira(job.weeklySalary)}/wk • ${job.workHours}", fontSize = 10.sp, color = TextSecondary)
                            }
                            Text(text = "Select", fontSize = 11.sp, color = HustleGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "BUSINESSES TO RUN (ENTREPRENEUR)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                BusinessData.startingBusinesses.forEach { biz ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CardSurfaceSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                viewModel.switchCareer(CareerTrack.BUSINESS_OWNER, null, biz)
                                showCareerDialog = false
                            }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = biz.iconEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = biz.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "${biz.tagline} • Sells ${biz.itemUnitName}", fontSize = 10.sp, color = TextSecondary)
                            }
                            Text(text = "Run", fontSize = 11.sp, color = HustleGoldDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun VitalPill(
    label: String,
    value: Int,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardSurfaceSubtle,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$emoji $label", fontSize = 10.sp, color = TextSecondary)
                Text(text = "$value%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (value / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun RoutineActionRow(
    title: String,
    description: String,
    emoji: String,
    isCompleted: Boolean,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isCompleted) CardSurfaceSubtle else CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompleted) PositiveGreen.copy(alpha = 0.4f) else BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) TextSecondary else TextPrimary
                        )
                        if (isCompleted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done", tint = PositiveGreen, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(text = description, fontSize = 10.sp, color = TextMuted, maxLines = 1)
                }
            }

            if (isCompleted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PositiveGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "DONE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PositiveGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = buttonLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
