/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.COMBINED_BUTTON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.PLAY_BUTTON_AREA_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.PLAY_ICON_SIZE
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.SPEED_BUTTON_AREA_WIDTH
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * Combined Play/Pause and Speed control button for voice message preview.
 *
 * Matches the design used in timeline voice messages for consistency.
 * Inspired by Telegram's segmented pill button design.
 *
 * Features:
 * - Left side (52dp): Play/Pause with large touch target
 * - Visual divider
 * - Right side (48dp): Speed control (1×, 1.5×, 2×)
 *
 * @param isPlaying Whether audio is currently playing
 * @param speed Current playback speed
 * @param onPlayPauseClick Callback when play/pause is tapped
 * @param onSpeedClick Callback when speed control is tapped
 * @param enabled Whether the button is enabled
 * @param modifier Modifier for the component
 */
@Composable
internal fun VoiceMessagePreviewCombinedButton(
    isPlaying: Boolean,
    speed: Float,
    onPlayPauseClick: () -> Unit,
    onSpeedClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .height(COMBINED_BUTTON_SIZE)
            .clip(RoundedCornerShape(26.dp))
            .background(
                color = if (enabled) ElementTheme.colors.bgCanvasDefault
                else ElementTheme.colors.bgSubtleSecondary,
                shape = RoundedCornerShape(26.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: Play/Pause (52dp circle)
        Box(
            modifier = Modifier
                .size(PLAY_BUTTON_AREA_SIZE)
                .clip(CircleShape)
                .clickable(enabled = enabled, onClick = onPlayPauseClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) CompoundIcons.PauseSolid() else CompoundIcons.PlaySolid(),
                contentDescription = if (isPlaying) {
                    stringResource(CommonStrings.a11y_pause)
                } else {
                    stringResource(CommonStrings.a11y_play)
                },
                tint = if (enabled) ElementTheme.colors.iconSecondary
                else ElementTheme.colors.iconDisabled,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(PLAY_ICON_SIZE),
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(ElementTheme.colors.borderDisabled.copy(alpha = 0.3f))
        )

        // Right: Speed control (48dp)
        Box(
            modifier = Modifier
                .width(SPEED_BUTTON_AREA_WIDTH)
                .height(COMBINED_BUTTON_SIZE)
                .clickable(enabled = enabled, onClick = onSpeedClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = speed.toSpeedLabel(),
                style = ElementTheme.typography.fontBodyMdMedium,
                color = if (enabled) ElementTheme.colors.textPrimary
                else ElementTheme.colors.textDisabled,
            )
        }
    }
}

/**
 * Formats a playback speed float to a display label.
 *
 * Examples:
 * - 1.0f → "1×"
 * - 1.5f → "1.5×"
 * - 2.0f → "2×"
 */
private fun Float.toSpeedLabel(): String = when (this) {
    1.0f -> "1×"
    1.5f -> "1.5×"
    2.0f -> "2×"
    else -> "${this}×"
}

@PreviewsDayNight
@Composable
internal fun VoiceMessagePreviewCombinedButtonPreview() = ElementPreview {
    Row {
        VoiceMessagePreviewCombinedButton(
            isPlaying = false,
            speed = 1.0f,
            onPlayPauseClick = {},
            onSpeedClick = {},
        )
        VoiceMessagePreviewCombinedButton(
            isPlaying = true,
            speed = 1.5f,
            onPlayPauseClick = {},
            onSpeedClick = {},
        )
        VoiceMessagePreviewCombinedButton(
            isPlaying = false,
            speed = 2.0f,
            onPlayPauseClick = {},
            onSpeedClick = {},
            enabled = false,
        )
    }
}
