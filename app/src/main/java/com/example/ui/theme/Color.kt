package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// FrameAI iOS Liquid Glass Palette
val IosObsidian = Color(0xFF05070B)
val IosDarkBackground = Color(0xFF0C0E14)
val IosCardBlack = Color(0xFF12141C)
val IosCardGlass = Color(0x331C2230)
val IosCardGlassElevated = Color(0x55252D3D)
val IosGlassBorder = Color(0x2EFFFFFF)
val IosGlassBorderBright = Color(0x4DFFFFFF)

// Accent & Shutter Colors (matching screenshots)
val ShutterPink = Color(0xFFFF176A)
val ShutterPinkDark = Color(0xFFE00052)
val ShutterRingWhite = Color(0xFFFFFFFF)
val ShutterRingBorder = Color(0x66FFFFFF)

// Status & AI Director Cues
val PerfectGreen = Color(0xFF10B981)
val PerfectGreenGlow = Color(0x3310B981)
val WarningAmber = Color(0xFFF59E0B)
val AlertRed = Color(0xFFFF3B30)
val FocusCyan = Color(0xFF38BDF8)

// iOS Text Colors
val IosTextPrimary = Color(0xFFFFFFFF)
val IosTextSecondary = Color(0x99FFFFFF)
val IosTextMuted = Color(0x66FFFFFF)

// Glass Gradients
val LiquidGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x4DFFFFFF),
        Color(0x1AFFFFFF),
        Color(0x08FFFFFF)
    )
)

val LiquidGlassDarkGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x662A3245),
        Color(0x40161B26),
        Color(0x550F131C)
    )
)

val PerfectFrameGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF10B981),
        Color(0xFF059669),
        Color(0xFF34D399)
    )
)

val ShutterGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFF337F),
        Color(0xFFFF176A),
        Color(0xFFD60055)
    )
)
