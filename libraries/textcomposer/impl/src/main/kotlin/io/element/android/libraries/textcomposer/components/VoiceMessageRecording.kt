/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_HORIZONTAL_PADDING_END
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_HORIZONTAL_PADDING_START
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_MIN_HEIGHT
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTAINER_VERTICAL_PADDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.CONTROL_GROUP_SPACING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.RECORDING_DOT_ANIMATION_DURATION_MS
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.RECORDING_DOT_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.TIME_CONTROL_SPACING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_HEIGHT_RECORDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_RECORDING_BRUSH
import io.element.android.libraries.ui.utils.time.formatShort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun VoiceMessageRecording(
    levels: ImmutableList<Float>,
    duration: Duration,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    onHapticFeedback: () -> Unit = {},
    showAudioLevelMonitor: Boolean = true,
) {
    // State for slide-to-cancel gesture
    val (slideToCancelState, updateSlideToCancelState) = rememberSlideToCancelState()

    // State for audio level monitoring
    val audioLevelState = rememberAudioLevelMonitorState(levels)

    Row(
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
            .heightIn(CONTAINER_MIN_HEIGHT)
            .then(
                if (onCancel != null) {
                    Modifier.slideToCancelGesture(
                        enabled = true,
                        onDragProgress = updateSlideToCancelState,
                        onCancel = onCancel,
                        onHapticFeedback = onHapticFeedback,
                    )
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Show cancel indicator if dragging
        if (slideToCancelState.shouldShowIndicator && onCancel != null) {
            VoiceMessageCancelIndicator(
                dragProgress = slideToCancelState.dragProgress,
            )
        } else {
            // Normal recording UI
            RedRecordingDot()

            Spacer(Modifier.size(CONTROL_GROUP_SPACING))

            // Timer
            Text(
                text = duration.formatShort(),
                color = ElementTheme.colors.textSecondary,
                style = ElementTheme.typography.fontBodyMdMedium
            )

            // Audio level indicator (shows only when there's an issue)
            if (showAudioLevelMonitor) {
                Spacer(Modifier.size(8.dp))
                VoiceMessageAudioLevelIndicator(
                    level = audioLevelState.qualityLevel,
                    averageLevel = audioLevelState.averageLevel,
                )
            }
        }

        Spacer(Modifier.size(TIME_CONTROL_SPACING))

        // Live waveform with vibrant gradient colors
        LiveWaveformView(
            modifier = Modifier
                .height(WAVEFORM_HEIGHT_RECORDING)
                .weight(1f),
            levels = levels,
            brush = WAVEFORM_RECORDING_BRUSH
        )
    }
}

@Composable
private fun RedRecordingDot() {
    val infiniteTransition = rememberInfiniteTransition("RedRecordingDot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = TweenSpec(durationMillis = RECORDING_DOT_ANIMATION_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RedRecordingDotAlpha",
    )
    Box(
        modifier = Modifier
            .size(RECORDING_DOT_SIZE)
            .alpha(alpha)
            .background(color = ElementTheme.colors.textCriticalPrimary, shape = CircleShape)
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingPreview() = ElementPreview {
    VoiceMessageRecording(
        levels = List(100) { it.toFloat() / 100 }.toImmutableList(),
        duration = 45.seconds,
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingWithCancelPreview() = ElementPreview {
    VoiceMessageRecording(
        levels = List(100) { it.toFloat() / 100 }.toImmutableList(),
        duration = 45.seconds,
        onCancel = {},
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingTooQuietPreview() = ElementPreview {
    VoiceMessageRecording(
        levels = List(20) { 0.02f }.toImmutableList(), // Very low levels
        duration = 12.seconds,
        onCancel = {},
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingClippingPreview() = ElementPreview {
    VoiceMessageRecording(
        levels = List(20) { 0.98f }.toImmutableList(), // Very high levels
        duration = 8.seconds,
        onCancel = {},
    )
}
