/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * Overlay that displays reaction markers on top of the waveform.
 * Inspired by SoundCloud's timestamp comments.
 *
 * Shows emoji reactions at specific positions along the waveform,
 * allowing users to see what moments others found interesting.
 */
@Composable
fun WaveformReactionOverlay(
    reactions: List<ReactionCluster>,
    durationMs: Long,
    waveformWidthPx: Float,
    onReactionClick: (ReactionCluster) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        reactions.forEach { cluster ->
            val positionFraction = if (durationMs > 0) {
                cluster.timestampMs.toFloat() / durationMs.toFloat()
            } else {
                0f
            }

            val xOffset = (positionFraction * waveformWidthPx).toInt()

            ReactionMarker(
                emoji = cluster.emoji,
                count = cluster.count,
                onClick = { onReactionClick(cluster) },
                modifier = Modifier
                    .offset { IntOffset(xOffset, 0) }
                    .align(Alignment.TopStart)
            )
        }
    }
}

/**
 * Individual reaction marker displayed on the waveform
 */
@Composable
private fun ReactionMarker(
    emoji: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = ElementTheme.colors.bgCanvasDefault.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Emoji
        Text(
            text = emoji,
            style = ElementTheme.typography.fontBodyMdRegular,
        )

        // Count (if more than 1)
        if (count > 1) {
            Text(
                text = count.toString(),
                style = ElementTheme.typography.fontBodyXsRegular,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

/**
 * Reaction picker that appears when long-pressing the waveform
 */
@Composable
fun ReactionPicker(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val popularEmojis = listOf("😂", "❤️", "👍", "🔥", "🤔", "😮", "👏", "💯")

    Row(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = ElementTheme.colors.bgCanvasDefault,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        popularEmojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        onEmojiSelected(emoji)
                        onDismiss()
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emoji,
                    style = ElementTheme.typography.fontBodyLgMedium,
                )
            }
        }
    }
}
