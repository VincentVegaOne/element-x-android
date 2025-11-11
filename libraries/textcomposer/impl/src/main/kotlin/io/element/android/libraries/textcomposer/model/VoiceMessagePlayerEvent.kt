/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.model

/**
 * Events for controlling voice message playback in the composer preview.
 */
sealed interface VoiceMessagePlayerEvent {
    /** Start or resume playback */
    data object Play : VoiceMessagePlayerEvent

    /** Pause playback */
    data object Pause : VoiceMessagePlayerEvent

    /** Seek to a position (0.0-1.0) */
    data class Seek(
        val position: Float
    ) : VoiceMessagePlayerEvent

    /** Skip backward (typically 15 seconds) */
    data object SkipBackward : VoiceMessagePlayerEvent

    /** Skip forward (typically 15 seconds) */
    data object SkipForward : VoiceMessagePlayerEvent

    /** Cycle through playback speeds (1.0× → 1.5× → 2.0× → 1.0×) */
    data object CycleSpeed : VoiceMessagePlayerEvent
}
