/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList

/**
 * Monitors real-time audio levels during voice message recording and provides
 * quality assessment with smoothed level averaging.
 *
 * Audio quality thresholds:
 * - TOO_QUIET: Average level < 5% (0.05) - User should speak louder
 * - GOOD: Average level 5% - 95% (0.05 - 0.95) - Optimal recording quality
 * - CLIPPING: Average level > 95% (0.95) - Audio distortion likely
 *
 * Uses a rolling window of recent samples for smooth, stable feedback.
 */
object VoiceMessageAudioLevelMonitor {
    /**
     * Threshold for "too quiet" detection.
     * Audio below this level may be difficult to hear.
     */
    private const val QUIET_THRESHOLD = 0.05f

    /**
     * Threshold for clipping detection.
     * Audio above this level is likely to distort.
     */
    private const val CLIPPING_THRESHOLD = 0.95f

    /**
     * Number of recent samples to average for smooth feedback.
     * Reduces jitter and provides stable level indication.
     */
    private const val SMOOTHING_WINDOW_SIZE = 5

    /**
     * Minimum number of samples required before providing level feedback.
     * Prevents false warnings during recording startup.
     */
    private const val MIN_SAMPLES_FOR_FEEDBACK = 10

    /**
     * Analyzes audio level data and determines the current quality level.
     *
     * @param levels Recent audio level samples (normalized 0.0-1.0)
     * @return Pair of (AudioQualityLevel, smoothed average level)
     */
    fun analyzeLevel(levels: ImmutableList<Float>): Pair<AudioQualityLevel, Float> {
        // Need minimum samples for accurate assessment
        if (levels.size < MIN_SAMPLES_FOR_FEEDBACK) {
            return AudioQualityLevel.GOOD to 0f
        }

        // Get recent samples for smoothing
        val recentSamples = levels.takeLast(SMOOTHING_WINDOW_SIZE)
        val averageLevel = if (recentSamples.isNotEmpty()) {
            recentSamples.average().toFloat()
        } else {
            0f
        }

        // Determine quality level
        val qualityLevel = when {
            averageLevel < QUIET_THRESHOLD -> AudioQualityLevel.TOO_QUIET
            averageLevel > CLIPPING_THRESHOLD -> AudioQualityLevel.CLIPPING
            else -> AudioQualityLevel.GOOD
        }

        return qualityLevel to averageLevel
    }

    /**
     * Formats the average level as a percentage string for debugging.
     *
     * @param averageLevel Level from 0.0 to 1.0
     * @return Formatted string (e.g., "47%")
     */
    fun formatLevelPercentage(averageLevel: Float): String {
        return "${(averageLevel * 100).toInt()}%"
    }
}

/**
 * State holder for audio level monitoring during recording.
 *
 * @property qualityLevel Current audio quality assessment
 * @property averageLevel Smoothed average level from 0.0 to 1.0
 * @property levelPercentage Formatted percentage string (e.g., "47%")
 */
data class AudioLevelMonitorState(
    val qualityLevel: AudioQualityLevel = AudioQualityLevel.GOOD,
    val averageLevel: Float = 0f,
    val levelPercentage: String = "0%",
)

/**
 * Creates and remembers an audio level monitor state that automatically
 * updates based on the provided audio level data.
 *
 * @param levels Current audio level samples from recording
 * @return Current audio level monitor state
 */
@Composable
fun rememberAudioLevelMonitorState(
    levels: ImmutableList<Float>
): AudioLevelMonitorState {
    var state by remember { mutableStateOf(AudioLevelMonitorState()) }

    LaunchedEffect(levels.size) {
        val (qualityLevel, averageLevel) = VoiceMessageAudioLevelMonitor.analyzeLevel(levels)
        state = AudioLevelMonitorState(
            qualityLevel = qualityLevel,
            averageLevel = averageLevel,
            levelPercentage = VoiceMessageAudioLevelMonitor.formatLevelPercentage(averageLevel),
        )
    }

    return state
}
