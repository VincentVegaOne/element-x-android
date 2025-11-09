/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.event

/**
 * Supported playback speed options for voice messages.
 * Values represent the playback speed multiplier (e.g., 0.5 = half speed, 2.0 = double speed).
 */
enum class PlaybackSpeedOption(val speed: Float, val label: String) {
    HALF(0.5f, "0.5x"),
    NORMAL(1.0f, "1x"),
    ONE_AND_HALF(1.5f, "1.5x"),
    DOUBLE(2.0f, "2x");

    companion object {
        /**
         * Get the next playback speed in the cycle.
         */
        fun Float.nextSpeed(): Float {
            return when {
                this < 0.75f -> NORMAL.speed
                this < 1.25f -> ONE_AND_HALF.speed
                this < 1.75f -> DOUBLE.speed
                else -> HALF.speed
            }
        }

        /**
         * Get the label for a given speed value.
         */
        fun Float.toSpeedLabel(): String {
            return when {
                this < 0.75f -> HALF.label
                this < 1.25f -> NORMAL.label
                this < 1.75f -> ONE_AND_HALF.label
                else -> DOUBLE.label
            }
        }
    }
}
