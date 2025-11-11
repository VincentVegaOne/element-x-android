/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.model

/**
 * Events for controlling voice message recording.
 */
sealed interface VoiceMessageRecorderEvent {
    /** Start recording */
    data object Start : VoiceMessageRecorderEvent

    /** Stop recording (finalize the recording) */
    data object Stop : VoiceMessageRecorderEvent

    /** Cancel recording (discard the recording) */
    data object Cancel : VoiceMessageRecorderEvent

    /** Pause recording (can be resumed) */
    data object Pause : VoiceMessageRecorderEvent

    /** Resume a paused recording */
    data object Resume : VoiceMessageRecorderEvent
}
