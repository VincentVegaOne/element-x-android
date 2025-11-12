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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
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
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.MIN_DURATION_FOR_PAUSE_MS
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.RECORDING_DOT_ANIMATION_DURATION_MS
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.RECORDING_DOT_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.SKIP_BUTTON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.SKIP_ICON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.TIME_CONTROL_SPACING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_HEIGHT_RECORDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_RECORDING_BRUSH
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.time.formatShort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Voice message recording UI with pause/resume support.
 *
 * Features:
 * - Red pulsing dot when actively recording
 * - Gray static dot when paused
 * - Recording duration timer
 * - Live waveform with vibrant gradient
 * - Pause/Resume button (shown when duration > 1 second)
 *
 * @param levels Audio level data for waveform visualization
 * @param duration Current recording duration
 * @param isPaused Whether recording is currently paused
 * @param onPause Callback when pause button is tapped
 * @param onResume Callback when resume button is tapped
 * @param modifier Modifier for the component
 */
@Composable
internal fun VoiceMessageRecording(
    levels: ImmutableList<Float>,
    duration: Duration,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            .heightIn(CONTAINER_MIN_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Recording indicator: pulsing red dot (recording) or gray dot (paused)
        if (isPaused) {
            PausedIndicator()
        } else {
            RedRecordingDot()
        }

        Spacer(Modifier.size(CONTROL_GROUP_SPACING))

        // Timer
        Text(
            text = duration.formatShort(),
            color = if (isPaused) ElementTheme.colors.textDisabled else ElementTheme.colors.textSecondary,
            style = ElementTheme.typography.fontBodyMdMedium
        )

        // Pause/Resume button (only shown after 1 second of recording)
        val showPauseResumeButton = duration.inWholeMilliseconds > MIN_DURATION_FOR_PAUSE_MS
        if (showPauseResumeButton) {
            Spacer(Modifier.size(CONTROL_GROUP_SPACING))

            IconButton(
                onClick = if (isPaused) onResume else onPause,
                modifier = Modifier.size(SKIP_BUTTON_SIZE),
            ) {
                Icon(
                    imageVector = if (isPaused) CompoundIcons.PlaySolid() else CompoundIcons.PauseSolid(),
                    contentDescription = if (isPaused) {
                        stringResource(CommonStrings.a11y_play)
                    } else {
                        stringResource(CommonStrings.a11y_pause)
                    },
                    tint = ElementTheme.colors.iconSecondary,
                    modifier = Modifier.size(SKIP_ICON_SIZE),
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

/**
 * Red pulsing dot indicator for active recording.
 */
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

/**
 * Gray static dot indicator for paused recording.
 */
@Composable
private fun PausedIndicator() {
    Box(
        modifier = Modifier
            .size(RECORDING_DOT_SIZE)
            .background(color = ElementTheme.colors.iconDisabled, shape = CircleShape)
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingPreview() = ElementPreview {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Recording - less than 1 second (no pause button)
        VoiceMessageRecording(
            levels = List(20) { it.toFloat() / 20 }.toImmutableList(),
            duration = 500.milliseconds,
            isPaused = false,
            onPause = {},
            onResume = {},
        )

        // Recording - active with pause button
        VoiceMessageRecording(
            levels = List(50) { it.toFloat() / 50 }.toImmutableList(),
            duration = 5.seconds,
            isPaused = false,
            onPause = {},
            onResume = {},
        )

        // Recording - paused with resume button
        VoiceMessageRecording(
            levels = List(80) { it.toFloat() / 80 }.toImmutableList(),
            duration = 12.seconds,
            isPaused = true,
            onPause = {},
            onResume = {},
        )
    }
}
