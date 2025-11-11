/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Constants for voice message recording and playback UI.
 * Centralized to ensure consistency across recording, preview, and timeline views.
 */
internal object VoiceMessageConstants {

    // Waveform Dimensions
    /** Height of waveform during recording - matches timeline for consistency */
    val WAVEFORM_HEIGHT_RECORDING = 56.dp

    /** Height of waveform in preview - matches timeline for consistency */
    val WAVEFORM_HEIGHT_PREVIEW = 56.dp

    /** Width of individual waveform lines */
    val WAVEFORM_LINE_WIDTH = 3.5.dp

    /** Padding between waveform lines */
    val WAVEFORM_LINE_PADDING = 3.dp

    // Button Dimensions
    /** Size of the combined play/speed button */
    val COMBINED_BUTTON_SIZE = 52.dp

    /** Size of the play/pause area within combined button */
    val PLAY_BUTTON_AREA_SIZE = 52.dp

    /** Width of the speed control area within combined button */
    val SPEED_BUTTON_AREA_WIDTH = 48.dp

    /** Size of play/pause icon */
    val PLAY_ICON_SIZE = 24.dp

    /** Size of skip control buttons */
    val SKIP_BUTTON_SIZE = 40.dp

    /** Size of skip control icons */
    val SKIP_ICON_SIZE = 20.dp

    /** Size of delete button */
    val DELETE_BUTTON_SIZE = 40.dp

    /** Size of delete icon */
    val DELETE_ICON_SIZE = 20.dp

    /** Size of record/stop button */
    val RECORD_BUTTON_SIZE = 48.dp

    /** Size of record/stop icon */
    val RECORD_ICON_SIZE = 24.dp

    /** Size of stop button background circle */
    val STOP_BUTTON_CIRCLE_SIZE = 36.dp

    /** Size of recording indicator dot */
    val RECORDING_DOT_SIZE = 8.dp

    // Spacing
    /** Spacing between recording controls */
    val CONTROL_SPACING = 12.dp

    /** Spacing between control groups */
    val CONTROL_GROUP_SPACING = 8.dp

    /** Spacing between time and controls */
    val TIME_CONTROL_SPACING = 20.dp

    /** Horizontal padding for recording/preview containers */
    val CONTAINER_HORIZONTAL_PADDING_START = 12.dp
    val CONTAINER_HORIZONTAL_PADDING_END = 20.dp

    /** Vertical padding for recording/preview containers */
    val CONTAINER_VERTICAL_PADDING = 8.dp

    /** Minimum height for recording/preview containers */
    val CONTAINER_MIN_HEIGHT = 56.dp

    // Colors - Vibrant gradients matching timeline
    /**
     * Waveform gradient for unplayed/unrecorded portion.
     * Subtle gray gradient for visual clarity.
     */
    val WAVEFORM_UNPLAYED_BRUSH = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFCCCCCC), // Light gray
            Color(0xFFAAAAAA), // Medium gray
        )
    )

    /**
     * Waveform gradient for played/recorded portion.
     * Vibrant blue-to-purple gradient inspired by modern audio apps.
     */
    val WAVEFORM_PLAYED_BRUSH = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0D6EFD), // Vibrant blue
            Color(0xFF6610F2), // Purple
            Color(0xFF0D6EFD), // Back to blue for shimmer effect
        )
    )

    /**
     * Live recording waveform gradient.
     * Uses vibrant colors to indicate active recording.
     */
    val WAVEFORM_RECORDING_BRUSH = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0D6EFD), // Vibrant blue
            Color(0xFF6610F2), // Purple
        )
    )

    // Animation
    /** Duration of recording dot pulse animation in milliseconds */
    const val RECORDING_DOT_ANIMATION_DURATION_MS = 1000

    /** Alpha for graphics layer (subtle optimization for performance) */
    const val GRAPHICS_LAYER_ALPHA = 0.99f

    // Gestures
    /** Threshold for slide-to-cancel gesture in dp */
    val SLIDE_TO_CANCEL_THRESHOLD = 120.dp

    /** Minimum drag distance to start canceling */
    val SLIDE_TO_CANCEL_MIN_DRAG = 40.dp

    // Audio Quality
    /** Threshold for "too quiet" warning (0.0-1.0) */
    const val AUDIO_LEVEL_TOO_QUIET_THRESHOLD = 0.05f

    /** Threshold for clipping warning (0.0-1.0) */
    const val AUDIO_LEVEL_CLIPPING_THRESHOLD = 0.95f

    /** Number of samples to average for level monitoring */
    const val AUDIO_LEVEL_SMOOTHING_SAMPLES = 5

    // Waveform Display
    /** Maximum number of waveform data points to display (optimization) */
    const val MAX_WAVEFORM_DISPLAY_POINTS = 300

    /** Minimum duration to show pause button (in milliseconds) */
    const val MIN_DURATION_FOR_PAUSE_MS = 1000L

    // File Size
    /** Minimum free disk space required to start recording (in bytes) */
    const val MIN_FREE_DISK_SPACE_BYTES = 50L * 1024 * 1024 // 50 MB

    /** Estimated bytes per second for recording (at medium quality) */
    const val ESTIMATED_BYTES_PER_SECOND = 6000L // ~6 KB/s for Opus at 48kbps
}
