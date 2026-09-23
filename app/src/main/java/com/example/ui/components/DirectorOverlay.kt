package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiDirectorState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.IosGlassBorder
import com.example.ui.theme.PerfectGreen
import com.example.ui.theme.WarningAmber

@Composable
fun DirectorOverlay(
    directorState: AiDirectorState,
    countdownActive: Boolean,
    countdownValue: Int,
    showGrid: Boolean = true,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight

        // 1. Subtle Rule of Thirds & Headroom Grid
        if (showGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = Color.White.copy(alpha = 0.12f)
                val stroke = Stroke(width = 1f)

                // Vertical thirds
                drawLine(lineColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1f)
                drawLine(lineColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1f)

                // Horizontal thirds
                drawLine(lineColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1f)
                drawLine(lineColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1f)

                // Golden Headroom Line (ideal top head alignment at ~18%)
                drawLine(
                    color = Color(0x3338BDF8),
                    start = Offset(0f, h * 0.18f),
                    end = Offset(w, h * 0.18f),
                    strokeWidth = 1.5f
                )
            }
        }

        // 2. AI Tracking Reticle (Double ring matching Screenshot 4)
        if (directorState.hasSubject && directorState.faceBoundsNormalized != null) {
            val face = directorState.faceBoundsNormalized
            val faceCenterX = (face.left + face.right) / 2f
            val faceCenterY = (face.top + face.bottom) / 2f

            val animX by animateFloatAsState(
                targetValue = faceCenterX,
                animationSpec = tween(120, easing = FastOutSlowInEasing),
                label = "faceX"
            )
            val animY by animateFloatAsState(
                targetValue = faceCenterY,
                animationSpec = tween(120, easing = FastOutSlowInEasing),
                label = "faceY"
            )

            val reticleSize = 90.dp
            val reticleColor = if (directorState.isPerfectFrame) PerfectGreen else Color.White

            Box(
                modifier = Modifier
                    .offset(
                        x = (width * animX) - (reticleSize / 2),
                        y = (height * animY) - (reticleSize / 2)
                    )
                    .size(reticleSize),
                contentAlignment = Alignment.Center
            ) {
                // Outer circle (Screenshot 4 style)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = reticleColor.copy(alpha = 0.35f),
                        radius = size.minDimension / 2f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Center inner circle
                    drawCircle(
                        color = reticleColor.copy(alpha = 0.85f),
                        radius = size.minDimension / 4.5f,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                // AI Director bracket accents
                if (directorState.isPerfectFrame) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PerfectGreen)
                    )
                }
            }
        }

        // 3. Top Director Feedback Banner (Visible for both front & rear camera)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = directorState.hasSubject,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                if (directorState.isPerfectFrame) {
                    // Perfect Frame Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xCC10B981),
                                        Color(0xEE059669),
                                        Color(0xCC10B981)
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Perfect",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "PERFECT FRAME",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "100%",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Live Director Actionable Cue Pill (iOS Liquid Glass)
                    Box(
                        modifier = Modifier
                            .liquidGlass(
                                shape = RoundedCornerShape(30.dp),
                                backgroundColor = Color(0x66182236)
                            )
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val cueIcon = when {
                                directorState.directorCue.contains("right", ignoreCase = true) -> Icons.AutoMirrored.Filled.ArrowForward
                                directorState.directorCue.contains("left", ignoreCase = true) -> Icons.AutoMirrored.Filled.ArrowBack
                                directorState.directorCue.contains("raise", ignoreCase = true) -> Icons.Default.KeyboardArrowUp
                                directorState.directorCue.contains("lower", ignoreCase = true) -> Icons.Default.KeyboardArrowDown
                                else -> Icons.Default.Visibility
                            }

                            Icon(
                                imageVector = cueIcon,
                                contentDescription = "Cue",
                                tint = Color(0xFFFFD166),
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = directorState.directorCue,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Composition score
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33FFFFFF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${directorState.framingScore}%",
                                    color = if (directorState.framingScore > 75) PerfectGreen else WarningAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Large Countdown Overlay (3, 2, 1 before record)
        if (countdownActive && countdownValue > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x40000000)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .liquidGlass(shape = CircleShape, backgroundColor = Color(0x66000000))
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdownValue",
                        color = Color.White,
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
