package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.director.DirectorVoicePersona
import com.example.model.AiDirectorState
import com.example.model.BackgroundMusic
import com.example.model.CameraAspectRatio
import com.example.model.CameraResolution
import com.example.model.CinematicFilter
import com.example.model.ClipItem
import com.example.model.GreenScreenBackdrop
import com.example.model.QualityMetrics
import com.example.model.TeleprompterState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.IosCardGlass
import com.example.ui.theme.IosGlassBorder
import com.example.ui.theme.PerfectGreen
import com.example.ui.theme.ShutterPink
import kotlinx.coroutines.delay

@Composable
fun ResolutionSheet(
    isOpen: Boolean,
    currentRes: CameraResolution,
    currentFps: Int,
    isHdr: Boolean,
    aspectRatio: CameraAspectRatio,
    onResChange: (CameraResolution, Int) -> Unit,
    onHdrToggle: () -> Unit,
    onRatioChange: (CameraAspectRatio) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
                .liquidGlass(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    backgroundColor = Color(0xDD121724)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Format & Quality",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                // Resolution selector
                Text(
                    text = "RESOLUTION",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CameraResolution.values().forEach { res ->
                        val isSelected = res == currentRes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color(0x26FFFFFF))
                                .clickable { onResChange(res, currentFps) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.label,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // FPS selector
                Text(
                    text = "FRAME RATE",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(24, 30, 60).forEach { fps ->
                        val isSelected = fps == currentFps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color(0x26FFFFFF))
                                .clickable { onResChange(currentRes, fps) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${fps} FPS",
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Aspect Ratio selector
                Text(
                    text = "ASPECT RATIO",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CameraAspectRatio.values().forEach { ratio ->
                        val isSelected = ratio == aspectRatio
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color(0x26FFFFFF))
                                .clickable { onRatioChange(ratio) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ratio.displayName,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // HDR Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x20FFFFFF))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HDR Video Recording",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "High dynamic range for natural highlights",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isHdr,
                        onCheckedChange = { onHdrToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ShutterPink
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TeleprompterOverlay(
    state: TeleprompterState,
    onTogglePlay: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isVisible) return

    var scrollPosition by remember { mutableStateOf(0f) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(state.isPlaying, state.scrollSpeed) {
        while (state.isPlaying) {
            delay(40)
            scrollPosition += 1.5f * state.scrollSpeed
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .liquidGlass(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0x990F1420)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Teleprompter",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFFFFF))
                            .clickable { isEditing = !isEditing }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEditing) "Done" else "Edit",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onTogglePlay() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onClose() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (isEditing) {
                TextField(
                    value = state.scriptText,
                    onValueChange = onTextChange,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x22FFFFFF),
                        unfocusedContainerColor = Color(0x11FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x15FFFFFF))
                        .padding(8.dp)
                ) {
                    Text(
                        text = state.scriptText,
                        color = Color.White,
                        fontSize = state.fontSizeSp.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Speed slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Speed ${String.format("%.1f", state.scrollSpeed)}x",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Slider(
                    value = state.scrollSpeed,
                    onValueChange = onSpeedChange,
                    valueRange = 0.5f..3.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = ShutterPink
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MusicSheet(
    isOpen: Boolean,
    selectedMusic: BackgroundMusic?,
    onSelectMusic: (BackgroundMusic?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val tracks = listOf(
        BackgroundMusic("1", "Neon Sunset Vlog", "Creator Audio Lab", "2:14", 120),
        BackgroundMusic("2", "Lo-Fi Focus Beat", "Chillwave Studios", "3:02", 85),
        BackgroundMusic("3", "Cinematic Intro Hit", "Apex FX", "1:30", 110),
        BackgroundMusic("4", "Minimal Tech Ambiance", "FutureSound", "2:45", 96)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
                .liquidGlass(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    backgroundColor = Color(0xDD121724)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Soundtrack & Music",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                // None option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedMusic == null) Color.White else Color(0x1AFFFFFF))
                        .clickable { onSelectMusic(null) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No Background Music",
                        color = if (selectedMusic == null) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                tracks.forEach { track ->
                    val isSelected = selectedMusic?.id == track.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ShutterPink else Color(0x1AFFFFFF))
                            .clickable { onSelectMusic(track) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = track.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${track.artist} • ${track.bpm} BPM",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = track.duration,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreenScreenSheet(
    isOpen: Boolean,
    currentBackdrop: GreenScreenBackdrop,
    onSelectBackdrop: (GreenScreenBackdrop) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
                .liquidGlass(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    backgroundColor = Color(0xDD121724)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Virtual Backdrop / Green Screen",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                GreenScreenBackdrop.values().forEach { backdrop ->
                    val isSelected = backdrop == currentBackdrop
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ShutterPink else Color(0x1AFFFFFF))
                            .clickable { onSelectBackdrop(backdrop) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = backdrop.title,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleOptionSheet(
    title: String,
    isOpen: Boolean,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
                .liquidGlass(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    backgroundColor = Color(0xDD121724)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                options.forEachIndexed { index, opt ->
                    val isSelected = index == selectedIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color(0x1AFFFFFF))
                            .clickable { onSelect(index) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsView(
    directorState: AiDirectorState,
    qualityMetrics: QualityMetrics,
    isVoiceDirector: Boolean,
    onToggleVoice: () -> Unit,
    voicePersona: DirectorVoicePersona = DirectorVoicePersona.HOLLYWOOD_DIRECTOR,
    onOpenVoiceSettings: () -> Unit = {},
    isAutoFraming: Boolean,
    onToggleAutoFraming: () -> Unit,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE60A0D14))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Director Telemetry",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close View",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Primary Composition Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(18.dp), backgroundColor = Color(0x441F283D))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Framing Score", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Text(
                            "${directorState.framingScore}%",
                            color = if (directorState.isPerfectFrame) PerfectGreen else Color(0xFFFFD166),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Current Cue: ${directorState.directorCue}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Real-time sensor metrics
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill(
                    label = "Lighting Lux",
                    value = "${qualityMetrics.luminance.toInt()} Y",
                    statusColor = when (qualityMetrics.exposureStatus) {
                        com.example.model.ExposureStatus.OPTIMAL -> PerfectGreen
                        else -> AlertRed
                    },
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Lens Clarity",
                    value = if (qualityMetrics.isLensSmudged) "SMUDGED" else "SHARP",
                    statusColor = if (qualityMetrics.isLensSmudged) AlertRed else PerfectGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill(
                    label = "Audio Input",
                    value = "${(qualityMetrics.audioLevelDb).toInt()} dB",
                    statusColor = PerfectGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Frame Rate",
                    value = "${qualityMetrics.estimatedFps} FPS",
                    statusColor = PerfectGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // Solo Creator Features
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SOLO CREATOR SETTINGS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Voice Director Guidance", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Live spoken cues for rear-camera framing", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    Switch(
                        checked = isVoiceDirector,
                        onCheckedChange = { onToggleVoice() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ShutterPink)
                    )
                }

                if (isVoiceDirector) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF))
                            .clickable { onOpenVoiceSettings() }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Director Voice Persona", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(voicePersona.displayName, color = ShutterPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Change", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Smart Auto-Framing Zoom", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Automatically crops & scales to keep you centered", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    Switch(
                        checked = isAutoFraming,
                        onCheckedChange = { onToggleAutoFraming() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ShutterPink)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlass(shape = RoundedCornerShape(14.dp), backgroundColor = Color(0x331F283D))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Text(value, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun MediaClipsView(
    clips: List<ClipItem>,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE60A0D14))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recorded Takes",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close View",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (clips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No takes recorded yet.\nHit the shutter to record your first clip!",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(clips) { clip ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlass(shape = RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(ShutterPink.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = ShutterPink
                                        )
                                    }
                                    Column {
                                        Text(clip.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${clip.durationSeconds}s • ${clip.resolution} @ ${clip.fps}fps", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PerfectGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("${clip.framingScore}% Match", color = PerfectGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
