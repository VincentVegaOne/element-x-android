/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.event

import io.element.android.libraries.matrix.api.core.UserId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents a reaction at a specific timestamp in a voice message.
 * Inspired by SoundCloud's comment system - allows users to react to specific moments.
 *
 * This enables collaborative listening where users can:
 * - React to specific moments (e.g., 😂 at 0:15)
 * - See what others found interesting
 * - Jump to timestamped moments by tapping reactions
 *
 * Example: In a 2-minute voice message:
 * - User A reacts with 😂 at 0:15
 * - User B reacts with ❤️ at 0:32
 * - User C also reacts with 😂 at 0:15
 * - Result: Shows 😂² at 0:15, ❤️ at 0:32 on waveform
 */
data class VoiceMessageTimestampReaction(
    /**
     * Timestamp in milliseconds where the reaction was placed
     */
    val timestampMs: Long,

    /**
     * Emoji reaction (e.g., "😂", "❤️", "👍", "🔥")
     */
    val emoji: String,

    /**
     * User who added this reaction
     */
    val userId: UserId,

    /**
     * Optional: User's display name for showing in tooltip
     */
    val userName: String? = null,
)

/**
 * Groups reactions by timestamp and emoji for display
 * Example: [😂² at 0:15] [❤️³ at 0:32]
 */
data class ReactionCluster(
    val timestampMs: Long,
    val emoji: String,
    val count: Int,
    val userNames: List<String>,
)

/**
 * Extension function to group reactions into clusters for UI display
 */
fun ImmutableList<VoiceMessageTimestampReaction>.toClusters(): List<ReactionCluster> {
    return this
        .groupBy { it.timestampMs to it.emoji }
        .map { (key, reactions) ->
            val (timestamp, emoji) = key
            ReactionCluster(
                timestampMs = timestamp,
                emoji = emoji,
                count = reactions.size,
                userNames = reactions.mapNotNull { it.userName },
            )
        }
        .sortedBy { it.timestampMs }
}

/**
 * State holder for timestamp reactions in voice messages
 */
data class VoiceMessageTimestampReactionsState(
    /**
     * All reactions for this voice message
     */
    val reactions: ImmutableList<VoiceMessageTimestampReaction> = persistentListOf(),

    /**
     * Whether the user can add/remove reactions
     */
    val canReact: Boolean = true,

    /**
     * Currently selected reaction for adding (null = not adding)
     */
    val selectedEmoji: String? = null,
)
