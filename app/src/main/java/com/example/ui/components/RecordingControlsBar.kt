package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SpeakerNotesOff
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed

@Composable
fun RecordingControlsBar(
    isVisible: Boolean,
    audioLevel: Float,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onOptionsClick: () -> Unit,
    onSubtitlesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(end = 16.dp)
        ) {
            // 1. Sleek iOS Audio Capsule (Screenshot 3)
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMuteToggle
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Audio meter fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = audioLevel.coerceIn(0.15f, 1.0f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE5E7EB))
                )

                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = "Audio Level",
                    tint = if (isMuted) AlertRed else Color.Black,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .size(22.dp)
                )
            }

            // 2. Options pill ("...")
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOptionsClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More Options",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 3. Subtitles / Teleprompter quick toggle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSubtitlesClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Subtitles,
                    contentDescription = "Subtitles",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TopRecordingPill(
    durationSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AlertRed)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedTime,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
