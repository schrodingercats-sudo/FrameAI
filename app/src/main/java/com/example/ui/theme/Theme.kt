package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FrameAiColorScheme = darkColorScheme(
    primary = ShutterPink,
    onPrimary = Color.White,
    primaryContainer = ShutterPinkDark,
    onPrimaryContainer = Color.White,
    secondary = FocusCyan,
    onSecondary = Color.Black,
    background = IosObsidian,
    onBackground = IosTextPrimary,
    surface = IosCardBlack,
    onSurface = IosTextPrimary,
    surfaceVariant = IosCardGlassElevated,
    onSurfaceVariant = IosTextSecondary,
    outline = IosGlassBorder,
    error = AlertRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FrameAiColorScheme,
        typography = Typography,
        content = content
    )
}
