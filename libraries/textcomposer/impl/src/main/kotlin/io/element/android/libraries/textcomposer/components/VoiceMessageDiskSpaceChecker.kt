/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import android.os.Environment
import android.os.StatFs
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.ESTIMATED_BYTES_PER_SECOND
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.MIN_FREE_DISK_SPACE_BYTES
import kotlin.time.Duration

/**
 * Utility to check available disk space before starting voice message recording.
 *
 * Prevents recording failures due to insufficient storage and provides
 * user-friendly warnings when space is running low.
 */
object VoiceMessageDiskSpaceChecker {

    /**
     * Result of a disk space check.
     */
    sealed interface CheckResult {
        /** Sufficient space available for recording */
        data object Sufficient : CheckResult

        /** Low space warning (< 50 MB but > 10 MB) */
        data class LowSpace(
            val availableBytes: Long,
            val availableMB: Double
        ) : CheckResult

        /** Critically low space (< 10 MB) - recording should be blocked */
        data class CriticallyLow(
            val availableBytes: Long,
            val availableMB: Double
        ) : CheckResult
    }

    /**
     * Checks if there's sufficient disk space for voice message recording.
     *
     * @return CheckResult indicating space status
     */
    fun checkDiskSpace(): CheckResult {
        val availableBytes = getAvailableStorageBytes()
        val availableMB = availableBytes / (1024.0 * 1024.0)

        return when {
            availableBytes >= MIN_FREE_DISK_SPACE_BYTES -> CheckResult.Sufficient
            availableBytes >= 10L * 1024 * 1024 -> CheckResult.LowSpace(availableBytes, availableMB)
            else -> CheckResult.CriticallyLow(availableBytes, availableMB)
        }
    }

    /**
     * Estimates file size for a given recording duration.
     *
     * @param duration Expected recording duration
     * @return Estimated file size in bytes
     */
    fun estimateFileSize(duration: Duration): Long {
        return (duration.inWholeSeconds * ESTIMATED_BYTES_PER_SECOND)
    }

    /**
     * Gets available storage space in bytes.
     *
     * @return Available bytes on the primary external storage
     */
    private fun getAvailableStorageBytes(): Long {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            // If we can't check, assume sufficient space to avoid blocking user
            Long.MAX_VALUE
        }
    }

    /**
     * Formats bytes to human-readable storage format.
     *
     * @param bytes Number of bytes
     * @return Formatted string (e.g., "45.2 MB", "1.2 GB")
     */
    fun formatStorageSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }
}
