/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.media.WaveformPlaybackView
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_HORIZONTAL_PADDING_END
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_HORIZONTAL_PADDING_START
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_MIN_HEIGHT
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_VERTICAL_PADDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTROL_GROUP_SPACING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTROL_SPACING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.SKIP_BUTTON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.SKIP_ICON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_HEIGHT_PREVIEW
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_LINE_PADDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_LINE_WIDTH
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_PLAYED_BRUSH
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_UNPLAYED_BRUSH
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.time.formatShort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Voice message preview that matches the timeline UI design.
 *
 * Professional implementation with:
 * - Two-row layout matching timeline messages
 * - Combined play/speed button (Telegram-style)
 * - Vibrant blue-to-purple gradient waveform
 * - Skip forward/backward 15s controls
 * - 56dp waveform height for better interaction
 * - Consistent spacing and touch targets
 *
 * @param isInteractive Whether controls are enabled
 * @param isPlaying Whether audio is currently playing
 * @param showCursor Whether to show the playback cursor
 * @param waveform Waveform data points (0.0-1.0)
 * @param time Current playback time or total duration
 * @param playbackSpeed Current playback speed (1.0, 1.5, 2.0)
 * @param playbackProgress Progress through the audio (0.0-1.0)
 * @param onPlayClick Callback when play is tapped
 * @param onPauseClick Callback when pause is tapped
 * @param onSpeedClick Callback when speed control is tapped
 * @param onSeek Callback when waveform is seeked (0.0-1.0)
 * @param onSkipBackward Callback when skip backward is tapped
 * @param onSkipForward Callback when skip forward is tapped
 * @param modifier Modifier for the component
 * @param fileSize Optional file size to display (e.g., "2.1 MB")
 */
@Composable
internal fun VoiceMessagePreviewEnhanced(
    isInteractive: Boolean,
    isPlaying: Boolean,
    showCursor: Boolean,
    waveform: ImmutableList<Float>,
    time: Duration,
    playbackSpeed: Float,
    playbackProgress: Float,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    modifier: Modifier = Modifier,
    fileSize: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ElementTheme.colors.bgSubtleSecondary,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(
                start = CONTAINER_HORIZONTAL_PADDING_START,
                end = CONTAINER_HORIZONTAL_PADDING_END,
                top = CONTAINER_VERTICAL_PADDING,
                bottom = CONTAINER_VERTICAL_PADDING
            )
            .heightIn(CONTAINER_MIN_HEIGHT),
    ) {
        // Row 1: Combined Play/Speed button + Waveform
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Combined Play/Pause and Speed control
            VoiceMessagePreviewCombinedButton(
                isPlaying = isPlaying,
                speed = playbackSpeed,
                onPlayPauseClick = if (isPlaying) onPauseClick else onPlayClick,
                onSpeedClick = onSpeedClick,
                enabled = isInteractive,
            )

            Spacer(Modifier.width(CONTROL_SPACING))

            // Waveform with vibrant gradient colors
            WaveformPlaybackView(
                modifier = Modifier
                    .height(WAVEFORM_HEIGHT_PREVIEW)
                    .weight(1f),
                playbackProgress = playbackProgress,
                showCursor = showCursor,
                waveform = waveform,
                seekEnabled = isInteractive,
                onSeek = onSeek,
                brush = WAVEFORM_UNPLAYED_BRUSH,
                progressBrush = WAVEFORM_PLAYED_BRUSH,
                cursorBrush = SolidColor(Color.White),
                lineWidth = WAVEFORM_LINE_WIDTH,
                linePadding = WAVEFORM_LINE_PADDING,
            )
        }

        Spacer(Modifier.height(CONTROL_GROUP_SPACING))

        // Row 2: Time + File Size (left) | Skip controls (right)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Left: Time and file size
            Column {
                Text(
                    text = time.formatShort(),
                    color = ElementTheme.colors.textPrimary,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (fileSize != null) {
                    Text(
                        text = fileSize,
                        color = ElementTheme.colors.textSecondary,
                        style = ElementTheme.typography.fontBodySmRegular,
                        maxLines = 1,
                    )
                }
            }

            // Right: Skip controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(
                    onClick = onSkipBackward,
                    modifier = Modifier.size(SKIP_BUTTON_SIZE),
                    enabled = isInteractive,
                ) {
                    Icon(
                        imageVector = CompoundIcons.ArrowLeft(),
                        contentDescription = stringResource(CommonStrings.a11y_skip_backward),
                        tint = if (isInteractive) ElementTheme.colors.iconSecondary
                        else ElementTheme.colors.iconDisabled,
                        modifier = Modifier.size(SKIP_ICON_SIZE),
                    )
                }

                IconButton(
                    onClick = onSkipForward,
                    modifier = Modifier.size(SKIP_BUTTON_SIZE),
                    enabled = isInteractive,
                ) {
                    Icon(
                        imageVector = CompoundIcons.ArrowRight(),
                        contentDescription = stringResource(CommonStrings.a11y_skip_forward),
                        tint = if (isInteractive) ElementTheme.colors.iconSecondary
                        else ElementTheme.colors.iconDisabled,
                        modifier = Modifier.size(SKIP_ICON_SIZE),
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun VoiceMessagePreviewEnhancedPreview() = ElementPreview {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Playing state
        VoiceMessagePreviewEnhanced(
            isInteractive = true,
            isPlaying = true,
            showCursor = true,
            waveform = List(100) { it.toFloat() / 100 }.let { persistentListOf(*it.toTypedArray()) },
            time = 45.seconds,
            playbackSpeed = 1.0f,
            playbackProgress = 0.35f,
            onPlayClick = {},
            onPauseClick = {},
            onSpeedClick = {},
            onSeek = {},
            onSkipBackward = {},
            onSkipForward = {},
            fileSize = "2.1 MB",
        )

        // Paused state with 1.5x speed
        VoiceMessagePreviewEnhanced(
            isInteractive = true,
            isPlaying = false,
            showCursor = false,
            waveform = List(100) { it.toFloat() / 100 }.let { persistentListOf(*it.toTypedArray()) },
            time = 120.seconds,
            playbackSpeed = 1.5f,
            playbackProgress = 0.0f,
            onPlayClick = {},
            onPauseClick = {},
            onSpeedClick = {},
            onSeek = {},
            onSkipBackward = {},
            onSkipForward = {},
            fileSize = "4.5 MB",
        )

        // Sending state (disabled)
        VoiceMessagePreviewEnhanced(
            isInteractive = false,
            isPlaying = false,
            showCursor = false,
            waveform = List(60) { it.toFloat() / 60 }.let { persistentListOf(*it.toTypedArray()) },
            time = 30.seconds,
            playbackSpeed = 1.0f,
            playbackProgress = 0.0f,
            onPlayClick = {},
            onPauseClick = {},
            onSpeedClick = {},
            onSeek = {},
            onSkipBackward = {},
            onSkipForward = {},
            fileSize = "1.8 MB",
        )
    }
}
