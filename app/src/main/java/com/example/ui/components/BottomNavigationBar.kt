package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.viewmodel.BottomTab

@Composable
fun FrameAiBottomNav(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab 1: Media Library
        NavIconItem(
            icon = if (currentTab == BottomTab.MEDIA) Icons.Default.Close else Icons.Default.VideoLibrary,
            isSelected = currentTab == BottomTab.MEDIA,
            onClick = {
                if (currentTab == BottomTab.MEDIA) onTabSelected(BottomTab.DIRECTOR) else onTabSelected(BottomTab.MEDIA)
            }
        )

        // Tab 2: Clips Mode
        NavIconItem(
            icon = if (currentTab == BottomTab.CLIPS) Icons.Default.Close else Icons.Default.PlayArrow,
            isSelected = currentTab == BottomTab.CLIPS,
            onClick = {
                if (currentTab == BottomTab.CLIPS) onTabSelected(BottomTab.DIRECTOR) else onTabSelected(BottomTab.CLIPS)
            }
        )

        // Tab 3: Grid / Templates Mode
        NavIconItem(
            icon = if (currentTab == BottomTab.MODES) Icons.Default.Close else Icons.Default.GridView,
            isSelected = currentTab == BottomTab.MODES,
            onClick = {
                if (currentTab == BottomTab.MODES) onTabSelected(BottomTab.DIRECTOR) else onTabSelected(BottomTab.MODES)
            }
        )

        // Tab 4: Central AI Director Reticle (Active by default)
        NavIconItem(
            icon = Icons.Default.FiberManualRecord,
            isSelected = currentTab == BottomTab.DIRECTOR,
            onClick = { onTabSelected(BottomTab.DIRECTOR) }
        )

        // Tab 5: Analytics / Telemetry
        NavIconItem(
            icon = if (currentTab == BottomTab.ANALYTICS) Icons.Default.Close else Icons.Default.BarChart,
            isSelected = currentTab == BottomTab.ANALYTICS,
            onClick = {
                if (currentTab == BottomTab.ANALYTICS) onTabSelected(BottomTab.DIRECTOR) else onTabSelected(BottomTab.ANALYTICS)
            }
        )
    }
}

@Composable
private fun NavIconItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Nav Item",
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp)
        )
    }
}
