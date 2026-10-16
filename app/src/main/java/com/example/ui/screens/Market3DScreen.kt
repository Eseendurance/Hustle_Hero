package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessData
import com.example.model.CharacterProfile
import com.example.model.CommunityMember
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
import com.example.ui.theme.WarningOrange
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 3D Interactive Market World View
 *
 * Provides a 3D isometric perspective of the vibrant virtual Lagos marketplace,
 * complete with:
 * - 3D Camera Controls: Real-time touch drag rotation (Pitch, Yaw), presets & auto-drone orbit
 * - 3D Buildings & Stalls: Detailed shops with depth, canopies, and lighting
 * - 3D Advertising Landmarks: Giant billboard gantry, TV broadcast mast & cyber mobile tower
 * - 3D Characters & Avatars: Full cast of Nigerian entrepreneurs with speech bubbles & inspector
 * - Vibrant daylight color palette (Strictly NO dark colors)
 */
@Composable
fun Market3DScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    // 3D Camera State
    var cameraPitch by remember { mutableFloatStateOf(48f) }   // Tilt angle (20° to 72°)
    var cameraYaw by remember { mutableFloatStateOf(-20f) }    // Orbit rotation (-180° to 180°)
    var cameraZoom by remember { mutableFloatStateOf(1.05f) }   // Scale factor (0.75x to 1.5x)
    var isAutoOrbitEnabled by remember { mutableStateOf(false) }

    // Selected Character in 3D View
    var selectedCharacter by remember { mutableStateOf<CharacterProfile?>(state.character) }
    var showCharacterSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Smooth Infinite Drone Orbit Animation
    val infiniteTransition = rememberInfiniteTransition(label = "3d_orbit")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    // Bouncing Animation for Floating 3D Avatar Icons
    val avatarBounce by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_bounce"
    )

    // Danfo Bus Traffic Movement Animation
    val busPosition by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "traffic_bus"
    )

    val currentYaw = if (isAutoOrbitEnabled) orbitAngle else cameraYaw

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("market_3d_screen")
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE0F2FE), // Bright morning sky blue
                        Color(0xFFF0FDF4), // Fresh pastel mint
                        Color(0xFFFEF9C3)  // Solar amber glow at horizon
                    )
                )
            )
    ) {
        // --- 1. INTERACTIVE 3D WORLD CONTAINER ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isAutoOrbitEnabled) {
                    if (!isAutoOrbitEnabled) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            cameraYaw = (cameraYaw + dragAmount.x * 0.45f) % 360f
                            cameraPitch = (cameraPitch - dragAmount.y * 0.35f).coerceIn(22f, 70f)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // The 3D Projected Stage
            Box(
                modifier = Modifier
                    .size(560.dp, 560.dp)
                    .graphicsLayer {
                        rotationX = cameraPitch
                        rotationZ = currentYaw
                        scaleX = cameraZoom
                        scaleY = cameraZoom
                        cameraDistance = 18f * density
                    },
                contentAlignment = Alignment.Center
            ) {
                // 3D Ground Surface Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    draw3DGroundPlaza(this, state.solarModeEnabled)
                }

                // 3D Moving Traffic (Yellow Lagos Danfo Bus & Okada Bike)
                Box(
                    modifier = Modifier
                        .offset(x = busPosition.dp - 100.dp, y = 140.dp)
                        .size(64.dp, 30.dp)
                        .background(Color(0xFFFBBF24), RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color(0xFFB45309), RoundedCornerShape(6.dp))
                        .shadow(4.dp, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🚌", fontSize = 16.sp)
                        Text(text = "DANFO", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color(0xFF78350F))
                    }
                }

                // --- 3D BUILDINGS & SHOPS IN PLAZA ---

                // Shop 1: Player's Own Business (Spotlight Golden Aura)
                MarketBuilding3D(
                    xOffset = -120.dp,
                    yOffset = -110.dp,
                    businessName = state.business.name,
                    categoryEmoji = state.business.iconEmoji,
                    awningColor = HustleGreen,
                    wallColor = HustleGreenContainer,
                    isPlayerShop = true,
                    reputation = state.reputation,
                    onClick = {
                        selectedCharacter = state.character
                        showCharacterSheet = true
                    }
                )

                // Shop 2: Tunde's Computer Village Tech Hub
                MarketBuilding3D(
                    xOffset = 110.dp,
                    yOffset = -120.dp,
                    businessName = "Tunde Gadget Hub",
                    categoryEmoji = "📱",
                    awningColor = HustleBlue,
                    wallColor = HustleBlueContainer,
                    isPlayerShop = state.character.id == "tunde_tech",
                    reputation = 68,
                    onClick = {
                        selectedCharacter = BusinessData.characters.find { it.id == "tunde_tech" }
                        showCharacterSheet = true
                    }
                )

                // Shop 3: Mama Nkechi / Blessing Food Buka
                MarketBuilding3D(
                    xOffset = -130.dp,
                    yOffset = 50.dp,
                    businessName = "Mama Put Buka",
                    categoryEmoji = "🍲",
                    awningColor = Color(0xFFEA580C),
                    wallColor = Color(0xFFFFEDD5),
                    isPlayerShop = state.character.id == "iya_basira" || state.character.id == "mama_blessing",
                    reputation = 74,
                    onClick = {
                        selectedCharacter = BusinessData.characters.find { it.id == "iya_basira" }
                            ?: BusinessData.characters.find { it.id == "mama_blessing" }
                        showCharacterSheet = true
                    }
                )

                // Shop 4: Bayo's VIP Barbershop & Styling Salon
                MarketBuilding3D(
                    xOffset = 100.dp,
                    yOffset = 40.dp,
                    businessName = "Bayo VIP Cuts",
                    categoryEmoji = "💈",
                    awningColor = Color(0xFF9333EA),
                    wallColor = Color(0xFFF3E8FF),
                    isPlayerShop = state.character.id == "bayo_barber",
                    reputation = 72,
                    onClick = {
                        selectedCharacter = BusinessData.characters.find { it.id == "bayo_barber" }
                        showCharacterSheet = true
                    }
                )

                // Landmark 5: 3D Giant Highway Advertising Billboard
                MarketBillboard3D(
                    xOffset = 0.dp,
                    yOffset = -180.dp,
                    activeAdsCount = state.activeAds.size,
                    topAdTitle = state.activeAds.firstOrNull()?.name ?: "YOUR AD HERE",
                    onClick = {
                        viewModel.navigateToPhase(com.example.model.GamePhase.MARKETING_ADS)
                    }
                )

                // Landmark 6: 3D Mobile Telecom & TV Tower
                MarketTower3D(
                    xOffset = -180.dp,
                    yOffset = -190.dp,
                    pulseProgress = avatarBounce
                )

                // Landmark 7: Amaka's POS Agency Kiosk
                MarketPOSKiosk3D(
                    xOffset = 0.dp,
                    yOffset = 90.dp,
                    onClick = {
                        selectedCharacter = BusinessData.characters.find { it.id == "amaka_pos" }
                        showCharacterSheet = true
                    }
                )

                // --- 3D CHARACTERS & AVATARS PLACED ON PLAZA ---

                // Avatar 1: The Player (Golden Crown & High Floating Pedestal)
                CharacterAvatarPedestal3D(
                    xOffset = -90.dp,
                    yOffset = -30.dp,
                    profile = state.character,
                    isPlayer = true,
                    speechText = "Building my ₦5M empire!",
                    bounceOffset = avatarBounce,
                    isSelected = selectedCharacter?.id == state.character.id,
                    onClick = {
                        selectedCharacter = state.character
                        showCharacterSheet = true
                    }
                )

                // Avatar 2: Tunde (Tech Whiz)
                BusinessData.characters.find { it.id == "tunde_tech" }?.let { tunde ->
                    CharacterAvatarPedestal3D(
                        xOffset = 80.dp,
                        yOffset = -40.dp,
                        profile = tunde,
                        isPlayer = state.character.id == tunde.id,
                        speechText = "Wholesale power banks in stock!",
                        bounceOffset = -avatarBounce,
                        isSelected = selectedCharacter?.id == tunde.id,
                        onClick = {
                            selectedCharacter = tunde
                            showCharacterSheet = true
                        }
                    )
                }

                // Avatar 3: Mama Blessing (Wholesale Queen)
                BusinessData.characters.find { it.id == "mama_blessing" }?.let { mama ->
                    CharacterAvatarPedestal3D(
                        xOffset = -60.dp,
                        yOffset = 110.dp,
                        profile = mama,
                        isPlayer = state.character.id == mama.id,
                        speechText = "Importer just offloaded bags!",
                        bounceOffset = avatarBounce * 0.8f,
                        isSelected = selectedCharacter?.id == mama.id,
                        onClick = {
                            selectedCharacter = mama
                            showCharacterSheet = true
                        }
                    )
                }

                // Avatar 4: Chinedu (Alaba Apprentice Graduate)
                BusinessData.characters.find { it.id == "chinedu_trader" }?.let { chinedu ->
                    CharacterAvatarPedestal3D(
                        xOffset = 60.dp,
                        yOffset = 120.dp,
                        profile = chinedu,
                        isPlayer = state.character.id == chinedu.id,
                        speechText = "Discipline is true capital.",
                        bounceOffset = -avatarBounce * 0.7f,
                        isSelected = selectedCharacter?.id == chinedu.id,
                        onClick = {
                            selectedCharacter = chinedu
                            showCharacterSheet = true
                        }
                    )
                }

                // Avatar 5: Iya Basira (Mama Put Legend)
                BusinessData.characters.find { it.id == "iya_basira" }?.let { basira ->
                    CharacterAvatarPedestal3D(
                        xOffset = -140.dp,
                        yOffset = 120.dp,
                        profile = basira,
                        isPlayer = state.character.id == basira.id,
                        speechText = "Hot Party Jollof is ready!",
                        bounceOffset = avatarBounce * 0.9f,
                        isSelected = selectedCharacter?.id == basira.id,
                        onClick = {
                            selectedCharacter = basira
                            showCharacterSheet = true
                        }
                    )
                }
            }
        }

        // --- 2. TOP OVERLAY: CAMERA CONTROLS & AVATAR QUICK SWITCHER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .align(Alignment.TopCenter)
        ) {
            // Header Bar with 3D Status & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurface.copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, tint = HustleGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D Market Plaza • ${state.cityName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }

                // Camera Presets & Orbit Toggle
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Auto Orbit Toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isAutoOrbitEnabled) HustleGold else CardSurface.copy(alpha = 0.92f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAutoOrbitEnabled) HustleGold else BorderSubtle),
                        modifier = Modifier.clickable { isAutoOrbitEnabled = !isAutoOrbitEnabled },
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isAutoOrbitEnabled) "🛸 Orbiting" else "🛸 Orbit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isAutoOrbitEnabled) PureWhite else TextPrimary)
                        }
                    }

                    // Reset Camera Button
                    Surface(
                        shape = CircleShape,
                        color = CardSurface.copy(alpha = 0.92f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .size(34.dp)
                            .clickable {
                                cameraPitch = 48f
                                cameraYaw = -20f
                                cameraZoom = 1.05f
                                isAutoOrbitEnabled = false
                            },
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Camera", tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Camera Presets Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CameraPresetChip(label = "📐 Isometric", isActive = cameraPitch == 48f && cameraYaw == -20f) {
                    cameraPitch = 48f
                    cameraYaw = -20f
                    isAutoOrbitEnabled = false
                }
                CameraPresetChip(label = "🚶 Street View", isActive = cameraPitch == 65f && cameraYaw == 0f) {
                    cameraPitch = 65f
                    cameraYaw = 0f
                    isAutoOrbitEnabled = false
                }
                CameraPresetChip(label = "🦅 Bird's Eye", isActive = cameraPitch == 25f && cameraYaw == 0f) {
                    cameraPitch = 25f
                    cameraYaw = 0f
                    isAutoOrbitEnabled = false
                }

                Spacer(modifier = Modifier.weight(1f))

                // Zoom controls
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CardSurface.copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { cameraZoom = (cameraZoom - 0.15f).coerceAtLeast(0.70f) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextPrimary, modifier = Modifier.size(14.dp))
                        }
                        IconButton(
                            onClick = { cameraZoom = (cameraZoom + 0.15f).coerceAtMost(1.50f) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = TextPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Characters & Avatars Quick Selector Strip (User Requirement)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CardSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎭 Market Avatars & Entrepreneurs (Tap to inspect)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "${BusinessData.characters.size} Traders",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BusinessData.characters.forEach { charProfile ->
                            val isPlayer = charProfile.id == state.character.id
                            val isSelected = selectedCharacter?.id == charProfile.id

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> HustleGoldContainer
                                    isPlayer -> HustleGreenContainer
                                    else -> CardSurfaceSubtle
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isSelected -> HustleGold
                                        isPlayer -> HustleGreen
                                        else -> BorderSubtle
                                    }
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedCharacter = charProfile
                                        showCharacterSheet = true
                                    }
                                    .testTag("avatar_chip_${charProfile.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = charProfile.avatarEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = charProfile.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (isPlayer) {
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "👑", fontSize = 9.sp)
                                            }
                                        }
                                        Text(
                                            text = charProfile.perkName,
                                            fontSize = 9.sp,
                                            color = if (isPlayer) HustleGreenDark else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. BOTTOM OVERLAY: INTERACTIVE 3D CHARACTER INSPECTOR SHEET ---
        AnimatedVisibility(
            visible = showCharacterSheet && selectedCharacter != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedCharacter?.let { profile ->
                val isPlayer = profile.id == state.character.id

                Card(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .testTag("character_3d_inspector")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (isPlayer) HustleGreenContainer else HustleGoldContainer,
                                            CircleShape
                                        )
                                        .border(
                                            2.dp,
                                            if (isPlayer) HustleGreen else HustleGold,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = profile.avatarEmoji, fontSize = 24.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary
                                        )
                                        if (isPlayer) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = HustleGreen
                                            ) {
                                                Text(
                                                    text = "👑 YOU",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = PureWhite
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = profile.title,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showCharacterSheet = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Backstory Quote
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "“${profile.backstory}”",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Perks & Vitals Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HustleGoldContainer.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "UNIQUE PERK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HustleGoldDark)
                                    Text(text = profile.perkName, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                                    Text(text = profile.perkDescription, fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HustleGreenContainer.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "BASE REPUTATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                                    Text(text = "${profile.startingReputation}%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HustleGreenDark)
                                    Text(text = "Demand: +${profile.demandBoostPercent}%", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interaction Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isPlayer) {
                                Button(
                                    onClick = {
                                        viewModel.navigateToPhase(com.example.model.GamePhase.BUSINESS)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Manage Shop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.partnerWithTrader(profile.id)
                                        showCharacterSheet = false
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Handshake, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Partner Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.navigateToPhase(com.example.model.GamePhase.COMMUNITY)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Community Plaza", fontSize = 12.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3D CANVAS & WORLD DRAWING FUNCTIONS ---

private fun draw3DGroundPlaza(drawScope: DrawScope, isSolar: Boolean) {
    val canvasWidth = drawScope.size.width
    val canvasHeight = drawScope.size.height

    // Main Plaza Tiles (Warm Stone Paving - Light, no dark colors)
    val plazaColor = if (isSolar) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
    val tileLineColor = if (isSolar) Color(0xFFFDE68A) else Color(0xFFCBD5E1)

    drawScope.drawRect(
        color = plazaColor,
        topLeft = Offset(0f, 0f),
        size = drawScope.size
    )

    // Grid Paving Pattern
    val tileSize = 70f
    var x = 0f
    while (x <= canvasWidth) {
        drawScope.drawLine(
            color = tileLineColor,
            start = Offset(x, 0f),
            end = Offset(x, canvasHeight),
            strokeWidth = 1.5f
        )
        x += tileSize
    }

    var y = 0f
    while (y <= canvasHeight) {
        drawScope.drawLine(
            color = tileLineColor,
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 1.5f
        )
        y += tileSize
    }

    // Asphalt Commercial Street Road Lane
    val roadTop = canvasHeight * 0.58f
    val roadHeight = 90f
    drawScope.drawRect(
        color = Color(0xFF94A3B8), // Clean light slate road (not black)
        topLeft = Offset(0f, roadTop),
        size = Size(canvasWidth, roadHeight)
    )

    // Road White Dashed Line
    var dashX = 0f
    while (dashX < canvasWidth) {
        drawScope.drawLine(
            color = PureWhite,
            start = Offset(dashX, roadTop + roadHeight / 2),
            end = Offset(dashX + 30f, roadTop + roadHeight / 2),
            strokeWidth = 3f
        )
        dashX += 60f
    }

    // Pedestrian Crossing Stripes
    var crossX = canvasWidth * 0.40f
    while (crossX < canvasWidth * 0.58f) {
        drawScope.drawRect(
            color = PureWhite.copy(alpha = 0.9f),
            topLeft = Offset(crossX, roadTop + 8f),
            size = Size(16f, roadHeight - 16f)
        )
        crossX += 26f
    }
}

// --- 3D BUILDING COMPOSABLES ---

@Composable
private fun MarketBuilding3D(
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    businessName: String,
    categoryEmoji: String,
    awningColor: Color,
    wallColor: Color,
    isPlayerShop: Boolean,
    reputation: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(110.dp, 100.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Drop Shadow in 3D
        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = 14.dp)
                .size(90.dp, 75.dp)
                .background(Color(0x33000000), RoundedCornerShape(10.dp))
        )

        // Building Walls & Front
        Column(
            modifier = Modifier
                .size(96.dp, 84.dp)
                .background(wallColor, RoundedCornerShape(10.dp))
                .border(
                    if (isPlayerShop) 2.5.dp else 1.dp,
                    if (isPlayerShop) HustleGold else BorderSubtle,
                    RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Awning Roof Canopy
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = awningColor,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = categoryEmoji, fontSize = 14.sp)
                    if (isPlayerShop) {
                        Text(text = "👑 YOU", fontSize = 8.sp, fontWeight = FontWeight.Black, color = PureWhite)
                    } else {
                        Text(text = "★$reputation", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Shop Front Glass Display & Sign
            Text(
                text = businessName,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Glass Counter Display
            Box(
                modifier = Modifier
                    .size(68.dp, 28.dp)
                    .background(Color(0x33FFFFFF), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0x66000000), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "OPEN", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
            }
        }
    }
}

// --- 3D ADVERTISING BILLBOARD GANTRY ---

@Composable
private fun MarketBillboard3D(
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    activeAdsCount: Int,
    topAdTitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(130.dp, 80.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Billboard Board Face
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (activeAdsCount > 0) HustleBlue else CardSurface,
                border = androidx.compose.foundation.BorderStroke(2.dp, HustleGold),
                shadowElevation = 6.dp,
                modifier = Modifier.size(120.dp, 48.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪧", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (activeAdsCount > 0) "LIVE AD" else "RENT BILLBOARD",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activeAdsCount > 0) PureWhite else HustleGoldDark
                        )
                    }
                    Text(
                        text = topAdTitle,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeAdsCount > 0) PureWhite else TextPrimary,
                        maxLines = 1
                    )
                }
            }

            // Steel Supporting Pillar Legs
            Row(
                modifier = Modifier.width(70.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.size(6.dp, 26.dp).background(Color(0xFF64748B)))
                Box(modifier = Modifier.size(6.dp, 26.dp).background(Color(0xFF64748B)))
            }
        }
    }
}

// --- 3D TELECOM & TV BROADCAST TOWER ---

@Composable
private fun MarketTower3D(
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    pulseProgress: Float
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(60.dp, 100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pulsing Transmission Rings
            Box(
                modifier = Modifier
                    .size((18 + pulseProgress.toInt()).dp)
                    .background(Color(0x33EF4444), CircleShape)
                    .border(1.5.dp, DangerRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📡", fontSize = 12.sp)
            }

            // Steel Lattice Tower
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(65.dp)
                    .background(Color(0xFF94A3B8), RoundedCornerShape(2.dp))
                    .border(1.dp, Color(0xFF475569))
            )
        }
    }
}

// --- 3D POS KIOSK ---

@Composable
private fun MarketPOSKiosk3D(
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(54.dp, 54.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFACC15),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCA8A04)),
                modifier = Modifier.size(34.dp),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "💳", fontSize = 16.sp)
                }
            }
            Text(text = "POS Kiosk", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

// --- 3D CHARACTER AVATAR PEDESTAL (USER REQUIREMENT) ---

@Composable
private fun CharacterAvatarPedestal3D(
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    profile: CharacterProfile,
    isPlayer: Boolean,
    speechText: String,
    bounceOffset: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Floating 3D Speech Bubble
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlayer) HustleGreen else BorderSubtle),
                shadowElevation = 3.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = speechText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1
                )
            }

            // Bouncing Avatar Badge
            Box(
                modifier = Modifier
                    .offset(y = bounceOffset.dp)
                    .size(if (isPlayer) 44.dp else 36.dp)
                    .background(
                        when {
                            isSelected -> HustleGoldContainer
                            isPlayer -> HustleGreenContainer
                            else -> CardSurface
                        },
                        CircleShape
                    )
                    .border(
                        if (isPlayer || isSelected) 2.5.dp else 1.5.dp,
                        when {
                            isSelected -> HustleGold
                            isPlayer -> HustleGreen
                            else -> BorderSubtle
                        },
                        CircleShape
                    )
                    .shadow(if (isPlayer) 6.dp else 3.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = profile.avatarEmoji, fontSize = if (isPlayer) 22.sp else 18.sp)
            }

            // 3D Pedestal Base / Ground Shadow
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 10.dp)
                    .background(Color(0x33000000), CircleShape)
            )

            // Name Banner
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isPlayer) HustleGreen else CardSurface,
                modifier = Modifier.offset(y = (-2).dp)
            ) {
                Text(
                    text = if (isPlayer) "👑 ${profile.name}" else profile.name,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlayer) PureWhite else TextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

// --- HELPER PRESET CHIP ---

@Composable
private fun CameraPresetChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) HustleGreen else CardSurface.copy(alpha = 0.92f),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) HustleGreen else BorderSubtle),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) PureWhite else TextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}
