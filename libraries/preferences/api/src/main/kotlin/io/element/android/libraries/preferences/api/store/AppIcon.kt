/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.preferences.api.store

/**
 * Represents available app icon variants that can be displayed on the home screen.
 * Each variant corresponds to an activity-alias in the AndroidManifest.
 */
enum class AppIcon(
    val componentName: String,
    val displayName: String,
) {
    DEFAULT(
        componentName = "io.element.android.x.MainActivityDefault",
        displayName = "Default"
    ),
    BLUE(
        componentName = "io.element.android.x.MainActivityBlue",
        displayName = "Blue"
    ),
    PURPLE(
        componentName = "io.element.android.x.MainActivityPurple",
        displayName = "Purple"
    ),
    GREEN(
        componentName = "io.element.android.x.MainActivityGreen",
        displayName = "Green"
    ),
    PINK(
        componentName = "io.element.android.x.MainActivityPink",
        displayName = "Pink"
    );

    companion object {
        /**
         * Get the AppIcon from its component name, or null if not found.
         */
        fun fromComponentName(componentName: String): AppIcon? {
            return entries.find { it.componentName == componentName }
        }
    }
}
