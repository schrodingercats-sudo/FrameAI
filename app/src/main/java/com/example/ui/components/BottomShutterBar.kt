package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraResolution
import com.example.model.FlashMode
import com.example.ui.theme.ShutterPink
import com.example.ui.theme.ShutterRingWhite

@Composable
fun BottomShutterBar(
    isRecording: Boolean,
    flashMode: FlashMode,
    isAutoEnhanceActive: Boolean,
    resolution: CameraResolution,
    fps: Int,
    onFlashToggle: () -> Unit,
    onAutoEnhanceToggle: () -> Unit,
    onShutterClick: () -> Unit,
    onCameraFlip: () -> Unit,
    onResolutionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Flash Toggle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFlashToggle
                ),
            contentAlignment = Alignment.Center
        ) {
            val flashIcon = when (flashMode) {
                FlashMode.OFF -> Icons.Default.FlashOff
                FlashMode.TORCH, FlashMode.ON -> Icons.Default.FlashOn
                FlashMode.AUTO -> Icons.Default.FlashAuto
            }
            Icon(
                imageVector = flashIcon,
                contentDescription = "Flash",
                tint = if (flashMode != FlashMode.OFF) Color(0xFFFFD166) else Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // 2. Magic Wand (AI Auto Enhancement)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAutoEnhanceToggle
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Enhance",
                tint = if (isAutoEnhanceActive) Color(0xFF67E8F9) else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(26.dp)
            )
        }

        // 3. Central Shutter Button (Matching Screenshots 1, 2, 3)
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onShutterClick
                ),
            contentAlignment = Alignment.Center
        ) {
            val innerSize by animateDpAsState(
                targetValue = if (isRecording) 32.dp else 68.dp,
                animationSpec = tween(200),
                label = "shutterInnerSize"
            )
            val innerCorner by animateDpAsState(
                targetValue = if (isRecording) 8.dp else 34.dp,
                animationSpec = tween(200),
                label = "shutterCorner"
            )

            // Inner Pink Circle / Square
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(RoundedCornerShape(innerCorner))
                    .background(ShutterPink)
            )
        }

        // 4. Camera Switch (Flip)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCameraFlip
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Flip Camera",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // 5. Resolution / FPS Badge ("HD 30")
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onResolutionClick
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = resolution.label,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$fps",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
