/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.preferences.api.store

import androidx.annotation.DrawableRes

/**
 * Represents available app icon variants that can be displayed on the home screen.
 * Each variant corresponds to an activity-alias in the AndroidManifest.
 */
enum class AppIcon(
    val componentName: String,
    val displayName: String,
    @DrawableRes val iconPreviewRes: Int,
) {
    BUNDESMESSENGER(
        componentName = "io.element.android.x.MainActivityBundesMessenger",
        displayName = "BundesMessenger",
        iconPreviewRes = io.element.android.libraries.preferences.api.R.drawable.ic_preview_bundesmessenger
    ),
    SUDOKU(
        componentName = "io.element.android.x.MainActivitySudoku",
        displayName = "Sudoku",
        iconPreviewRes = io.element.android.libraries.preferences.api.R.drawable.ic_preview_sudoku
    ),
    SANTA(
        componentName = "io.element.android.x.MainActivitySanta",
        displayName = "Santa Claus",
        iconPreviewRes = io.element.android.libraries.preferences.api.R.drawable.ic_preview_santa
    );

    companion object {
        /**
         * Get the AppIcon from its component name, or null if not found.
         */
        fun fromComponentName(componentName: String): AppIcon? {
            return entries.find { it.componentName == componentName }
        }

        /** Default app icon to use */
        val DEFAULT = BUNDESMESSENGER
    }
}
