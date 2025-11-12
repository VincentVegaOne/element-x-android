/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.model

/**
 * Recording mode for voice messages, determining how users initiate and complete recording.
 *
 * ## Recording Modes:
 *
 * ### TAP Mode (Default):
 * - Tap microphone button to start recording
 * - Tap stop button to finish recording
 * - Supports pause/resume functionality
 * - Recording persists until explicitly stopped
 * - Best for: Longer messages, hands-free recording
 *
 * ### HOLD Mode (WhatsApp-style):
 * - Long-press and hold microphone button to record
 * - Release button to automatically stop and preview
 * - Cannot pause during recording
 * - Recording stops immediately on release
 * - Best for: Quick messages, one-handed operation
 *
 * User preference determines which mode is active. Default is TAP mode for
 * compatibility with existing behavior.
 */
enum class VoiceMessageRecordingMode {
    /**
     * Tap-to-start, tap-to-stop recording mode.
     * Default mode with full pause/resume support.
     */
    TAP,

    /**
     * Hold-to-record mode (WhatsApp-style).
     * Long-press to start, release to stop and preview.
     */
    HOLD,
}
