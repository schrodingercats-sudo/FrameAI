package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.IosGlassBorder
import com.example.ui.theme.IosGlassBorderBright
import com.example.ui.theme.PerfectGreen

fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0x3B181E2E),
    borderColor: Color = IosGlassBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.42f),
                backgroundColor.copy(alpha = 0.28f),
                backgroundColor.copy(alpha = 0.35f)
            )
        ),
        shape = shape
    )
    .border(
        width = borderWidth,
        brush = Brush.verticalGradient(
            colors = listOf(
                IosGlassBorderBright,
                borderColor.copy(alpha = 0.4f),
                IosGlassBorder.copy(alpha = 0.15f)
            )
        ),
        shape = shape
    )

fun Modifier.frostedPill(
    isSelected: Boolean = false,
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color(0x331C2230)
): Modifier = this
    .clip(CircleShape)
    .background(if (isSelected) selectedColor else unselectedColor, CircleShape)
    .then(
        if (!isSelected) {
            Modifier.border(0.5.dp, IosGlassBorder, CircleShape)
        } else {
            Modifier
        }
    )

fun Modifier.pulseGlow(
    color: Color = PerfectGreen,
    radius: Dp = 24.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    this.drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha * 0.6f),
                    color.copy(alpha = alpha * 0.2f),
                    Color.Transparent
                ),
                center = center,
                radius = radius.toPx()
            )
        )
    }
}
