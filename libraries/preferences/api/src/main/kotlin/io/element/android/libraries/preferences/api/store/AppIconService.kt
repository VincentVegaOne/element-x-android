/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.preferences.api.store

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Service for managing app icon changes on the home screen.
 */
interface AppIconService {
    /**
     * Changes the app icon to the specified variant.
     * This will disable the current icon and enable the new one.
     *
     * @param appIcon The icon variant to switch to
     */
    suspend fun changeIcon(appIcon: AppIcon)

    /**
     * Gets the currently active app icon.
     *
     * @return The currently active AppIcon variant
     */
    fun getCurrentIcon(): AppIcon
}

class DefaultAppIconService(
    private val context: Context,
    private val appPreferencesStore: AppPreferencesStore,
) : AppIconService {

    override suspend fun changeIcon(appIcon: AppIcon) {
        val packageManager = context.packageManager
        val packageName = context.packageName

        // Disable all icon aliases except the selected one
        AppIcon.entries.forEach { icon ->
            val componentName = ComponentName(packageName, icon.componentName)
            val newState = if (icon == appIcon) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )
        }

        // Save the preference
        appPreferencesStore.setAppIcon(appIcon)
    }

    override fun getCurrentIcon(): AppIcon {
        val packageManager = context.packageManager
        val packageName = context.packageName

        // Find which icon is currently enabled
        AppIcon.entries.forEach { icon ->
            val componentName = ComponentName(packageName, icon.componentName)
            val state = packageManager.getComponentEnabledSetting(componentName)

            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == AppIcon.DEFAULT)) {
                return icon
            }
        }

        return AppIcon.DEFAULT
    }
}
