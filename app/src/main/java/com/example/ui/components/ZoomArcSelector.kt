package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosCardGlass
import com.example.ui.theme.IosGlassBorder
import kotlin.math.abs

@Composable
fun ZoomArcSelector(
    currentZoom: Float,
    onZoomSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val zoomOptions = listOf(
        Pair(0.6f, ".6x"),
        Pair(1.0f, "1x"),
        Pair(2.0f, "2x"),
        Pair(5.0f, "5x")
    )

    // Arched container matching Screenshots 1, 2, 3, 4
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0x991E2430))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            zoomOptions.forEach { (ratio, label) ->
                val isSelected = abs(currentZoom - ratio) < 0.25f

                val pillBgColor = if (isSelected) Color.White else Color.Transparent
                val pillTextColor = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f)

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onZoomSelected(ratio)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = pillTextColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
