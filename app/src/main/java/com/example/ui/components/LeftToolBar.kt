package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCameraFront
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundMusic
import com.example.model.GreenScreenBackdrop

@Composable
fun LeftToolBar(
    isVisible: Boolean,
    selectedMusic: BackgroundMusic?,
    greenScreenMode: GreenScreenBackdrop,
    timerSeconds: Int,
    speed: Float,
    isTeleprompterActive: Boolean,
    onMusicClick: () -> Unit,
    onGreenScreenClick: () -> Unit,
    onTimerClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onTeleprompterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            ToolItem(
                icon = Icons.Default.MusicNote,
                label = selectedMusic?.title?.take(8) ?: "Music",
                isActive = selectedMusic != null,
                onClick = onMusicClick
            )

            ToolItem(
                icon = Icons.Default.PhotoCameraFront,
                label = if (greenScreenMode != GreenScreenBackdrop.OFF) greenScreenMode.title.take(8) else "Green screen",
                isActive = greenScreenMode != GreenScreenBackdrop.OFF,
                onClick = onGreenScreenClick
            )

            ToolItem(
                icon = Icons.Default.AvTimer,
                label = if (timerSeconds > 0) "${timerSeconds}s" else "Timer",
                isActive = timerSeconds > 0,
                onClick = onTimerClick
            )

            ToolItem(
                icon = Icons.Default.SlowMotionVideo,
                label = if (speed != 1.0f) "${speed}x" else "Speed",
                isActive = speed != 1.0f,
                onClick = onSpeedClick
            )

            ToolItem(
                icon = Icons.Default.Subtitles,
                label = "Teleprompter",
                isActive = isTeleprompterActive,
                onClick = onTeleprompterClick
            )
        }
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFFFF176A) else Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isActive) Color(0xFFFF176A) else Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
