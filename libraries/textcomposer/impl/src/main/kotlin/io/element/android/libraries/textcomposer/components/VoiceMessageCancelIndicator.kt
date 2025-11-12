/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * Visual indicator showing "← Slide to cancel" with animated arrow and text.
 * Displayed during voice message recording to guide users on the cancellation gesture.
 *
 * @param modifier Optional modifier for the indicator
 * @param dragProgress Progress of the drag gesture from 0.0 (no drag) to 1.0 (threshold reached).
 *                     Used to adjust visibility and provide visual feedback.
 */
@Composable
internal fun VoiceMessageCancelIndicator(
    modifier: Modifier = Modifier,
    dragProgress: Float = 0f,
) {
    // Animate alpha for pulsing effect
    val alpha = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Increase visibility as user drags
    val combinedAlpha = (0.5f + dragProgress * 0.5f) * alpha.value

    Row(
        modifier = modifier.alpha(combinedAlpha),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Animated arrow pointing left
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = ElementTheme.colors.iconSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.size(4.dp))

        // "Slide to cancel" text
        Text(
            text = "Slide to cancel",
            style = ElementTheme.typography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageCancelIndicatorPreview() = ElementPreview {
    VoiceMessageCancelIndicator()
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageCancelIndicatorHighProgressPreview() = ElementPreview {
    VoiceMessageCancelIndicator(dragProgress = 0.8f)
}
