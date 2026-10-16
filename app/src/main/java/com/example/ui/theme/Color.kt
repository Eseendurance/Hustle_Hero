package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Global Theme Controller for seamless Light/Dark Mode switching
object ThemeState {
    val isDarkMode = mutableStateOf(false)
}

// Brand Greens
val HustleGreen = Color(0xFF008751) // Official Nigerian green
val HustleGreenDark = Color(0xFF04623A)
val HustleGreenLight = Color(0xFF10B981)
val HustleGreenContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF064E3B) else Color(0xFFDCFCE7)
val HustleOnGreenContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFA7F3D0) else Color(0xFF064E3B)

// Brand Gold / Amber
val HustleGold = Color(0xFFD97706)
val HustleGoldDark = Color(0xFF92400E)
val HustleGoldLight = Color(0xFFFBBF24)
val HustleGoldContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF451A03) else Color(0xFFFEF3C7)
val HustleOnGoldContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFFDE68A) else Color(0xFF78350F)

// Brand Blue
val HustleBlue = Color(0xFF0284C7)
val HustleBlueContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF082F49) else Color(0xFFE0F2FE)
val HustleOnBlueContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFBAE6FD) else Color(0xFF075985)

val HustleTerracotta = Color(0xFFE11D48)
val HustleTerracottaContainer: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF4C0519) else Color(0xFFFFE4E6)

// Dynamic Backgrounds & Surfaces adapting to Light/Dark mode
val LightBg: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF0B1120) else Color(0xFFF7FAF8)
val PureWhite: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFF8FAFC) else Color(0xFFFFFFFF)
val CardSurface: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF1E293B) else Color(0xFFFFFFFF)
val CardSurfaceSubtle: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF0F172A) else Color(0xFFF1F6F3)
val BorderSubtle: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF334155) else Color(0xFFE2EAE5)
val BorderMedium: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF475569) else Color(0xFFCBD5E1)

// Dynamic Typography Colors
val TextPrimary: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFF8FAFC) else Color(0xFF0F241A)
val TextSecondary: Color get() = if (ThemeState.isDarkMode.value) Color(0xFFCBD5E1) else Color(0xFF475569)
val TextMuted: Color get() = if (ThemeState.isDarkMode.value) Color(0xFF94A3B8) else Color(0xFF64748B)

// Status Colors
val PositiveGreen = Color(0xFF16A34A)
val WarningOrange = Color(0xFFF59E0B)
val DangerRed = Color(0xFFEF4444)
val InfoBlue = Color(0xFF0284C7)

// Dark Palette Specifics
val DarkBg = Color(0xFF0B1120)
val DarkCardSurface = Color(0xFF1E293B)
val DarkCardSurfaceSubtle = Color(0xFF0F172A)

// Solar Mode Warm Sunlit colors
val SolarBg = Color(0xFFFFFBEB)
val SolarSurface = Color(0xFFFFFFFF)
val SolarContainer = Color(0xFFFDE68A)
