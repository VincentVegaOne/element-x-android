/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * Audio quality level for real-time monitoring during voice message recording.
 */
enum class AudioQualityLevel {
    /** Audio level is too quiet (< 5% of maximum) */
    TOO_QUIET,

    /** Audio level is good (5% - 95%) */
    GOOD,

    /** Audio is clipping (> 95% of maximum) */
    CLIPPING,
}

/**
 * Visual indicator showing real-time audio quality during voice message recording.
 * Displays color-coded dot and helpful message based on input level.
 *
 * @param level Current audio quality level (TOO_QUIET, GOOD, CLIPPING)
 * @param averageLevel Average audio level from 0.0 to 1.0
 * @param modifier Optional modifier for the indicator
 * @param showMessage Whether to show the text message (default: true)
 */
@Composable
internal fun VoiceMessageAudioLevelIndicator(
    level: AudioQualityLevel,
    averageLevel: Float,
    modifier: Modifier = Modifier,
    showMessage: Boolean = true,
) {
    // Only show if there's an issue
    val shouldShow = level != AudioQualityLevel.GOOD

    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Color-coded indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (level) {
                            AudioQualityLevel.TOO_QUIET -> ElementTheme.colors.iconSecondary
                            AudioQualityLevel.GOOD -> Color(0xFF4CAF50) // Green
                            AudioQualityLevel.CLIPPING -> ElementTheme.colors.iconCriticalPrimary
                        },
                        shape = CircleShape
                    )
            )

            if (showMessage) {
                Spacer(Modifier.size(6.dp))

                // Helpful message
                Text(
                    text = when (level) {
                        AudioQualityLevel.TOO_QUIET -> "Speak louder"
                        AudioQualityLevel.GOOD -> ""
                        AudioQualityLevel.CLIPPING -> "Too loud"
                    },
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = when (level) {
                        AudioQualityLevel.TOO_QUIET -> ElementTheme.colors.textSecondary
                        AudioQualityLevel.GOOD -> ElementTheme.colors.textPrimary
                        AudioQualityLevel.CLIPPING -> ElementTheme.colors.textCriticalPrimary
                    },
                )
            }
        }
    }
}

/**
 * Compact version showing only the colored dot without message text.
 * Useful for space-constrained layouts.
 *
 * @param level Current audio quality level
 * @param averageLevel Average audio level from 0.0 to 1.0
 * @param modifier Optional modifier for the indicator
 */
@Composable
internal fun VoiceMessageAudioLevelDot(
    level: AudioQualityLevel,
    averageLevel: Float,
    modifier: Modifier = Modifier,
) {
    VoiceMessageAudioLevelIndicator(
        level = level,
        averageLevel = averageLevel,
        modifier = modifier,
        showMessage = false,
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageAudioLevelIndicatorPreview() = ElementPreview {
    Column(modifier = Modifier.padding(16.dp)) {
        VoiceMessageAudioLevelIndicator(
            level = AudioQualityLevel.TOO_QUIET,
            averageLevel = 0.03f,
        )
        Spacer(Modifier.size(12.dp))
        VoiceMessageAudioLevelIndicator(
            level = AudioQualityLevel.GOOD,
            averageLevel = 0.5f,
        )
        Spacer(Modifier.size(12.dp))
        VoiceMessageAudioLevelIndicator(
            level = AudioQualityLevel.CLIPPING,
            averageLevel = 0.98f,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageAudioLevelDotPreview() = ElementPreview {
    Row(modifier = Modifier.padding(16.dp)) {
        VoiceMessageAudioLevelDot(
            level = AudioQualityLevel.TOO_QUIET,
            averageLevel = 0.03f,
        )
        Spacer(Modifier.size(12.dp))
        VoiceMessageAudioLevelDot(
            level = AudioQualityLevel.GOOD,
            averageLevel = 0.5f,
        )
        Spacer(Modifier.size(12.dp))
        VoiceMessageAudioLevelDot(
            level = AudioQualityLevel.CLIPPING,
            averageLevel = 0.98f,
        )
    }
}
