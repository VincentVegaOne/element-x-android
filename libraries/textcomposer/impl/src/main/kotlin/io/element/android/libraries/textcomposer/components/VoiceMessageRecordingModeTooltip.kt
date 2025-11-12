/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.textcomposer.model.VoiceMessageRecordingMode

/**
 * Tooltip explaining how to use the current voice message recording mode.
 * Provides clear instructions for either TAP or HOLD mode.
 *
 * @param mode Current recording mode
 * @param modifier Optional modifier
 */
@Composable
internal fun VoiceMessageRecordingModeTooltip(
    mode: VoiceMessageRecordingMode,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = ElementTheme.colors.bgSubtlePrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = when (mode) {
                VoiceMessageRecordingMode.TAP -> "Tap to Record"
                VoiceMessageRecordingMode.HOLD -> "Hold to Record"
            },
            style = ElementTheme.typography.fontBodyMdMedium,
            color = ElementTheme.colors.textPrimary,
        )

        Spacer(Modifier.size(4.dp))

        Text(
            text = when (mode) {
                VoiceMessageRecordingMode.TAP ->
                    "Tap the microphone to start recording, then tap stop when finished. You can pause and resume anytime."
                VoiceMessageRecordingMode.HOLD ->
                    "Press and hold the microphone to record. Release to automatically stop and preview your message."
            },
            style = ElementTheme.typography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary,
        )
    }
}

/**
 * Compact tooltip showing just the key instruction for the recording mode.
 *
 * @param mode Current recording mode
 * @param modifier Optional modifier
 */
@Composable
internal fun VoiceMessageRecordingModeHint(
    mode: VoiceMessageRecordingMode,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = when (mode) {
            VoiceMessageRecordingMode.TAP -> "Tap mic to start"
            VoiceMessageRecordingMode.HOLD -> "Hold mic to record"
        },
        style = ElementTheme.typography.fontBodySmRegular,
        color = ElementTheme.colors.textSecondary,
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingModeTooltipPreview() = ElementPreview {
    Column(modifier = Modifier.padding(16.dp)) {
        VoiceMessageRecordingModeTooltip(
            mode = VoiceMessageRecordingMode.TAP
        )
        Spacer(Modifier.size(16.dp))
        VoiceMessageRecordingModeTooltip(
            mode = VoiceMessageRecordingMode.HOLD
        )
    }
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageRecordingModeHintPreview() = ElementPreview {
    Column(modifier = Modifier.padding(16.dp)) {
        VoiceMessageRecordingModeHint(
            mode = VoiceMessageRecordingMode.TAP
        )
        Spacer(Modifier.size(8.dp))
        VoiceMessageRecordingModeHint(
            mode = VoiceMessageRecordingMode.HOLD
        )
    }
}
