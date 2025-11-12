/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * Dialog to warn users about low disk space before recording.
 *
 * Shows available space and allows user to proceed with warning or cancel.
 *
 * @param availableMB Available disk space in megabytes
 * @param isCritical Whether space is critically low (should block recording)
 * @param onDismiss Callback when dialog is dismissed
 * @param onProceed Callback when user chooses to proceed anyway (only for low, not critical)
 */
@Composable
internal fun VoiceMessageLowSpaceDialog(
    availableMB: Double,
    isCritical: Boolean,
    onDismiss: () -> Unit,
    onProceed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isCritical) {
                    "Storage Full"
                } else {
                    "Low Storage Space"
                },
                style = ElementTheme.typography.fontHeadingMdBold,
            )
        },
        text = {
            Text(
                text = if (isCritical) {
                    "You have only ${String.format("%.1f MB", availableMB)} of storage space remaining. " +
                            "Please free up space before recording a voice message."
                } else {
                    "You have ${String.format("%.1f MB", availableMB)} of storage space remaining. " +
                            "Voice messages may use significant storage. Consider freeing up space."
                },
                style = ElementTheme.typography.fontBodyMdRegular,
            )
        },
        confirmButton = {
            if (isCritical) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(CommonStrings.action_ok))
                }
            } else {
                TextButton(onClick = {
                    onProceed()
                    onDismiss()
                }) {
                    Text("Record Anyway")
                }
            }
        },
        dismissButton = if (!isCritical) {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(CommonStrings.action_cancel))
                }
            }
        } else null,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageLowSpaceDialogPreview() = ElementPreview {
    VoiceMessageLowSpaceDialog(
        availableMB = 35.2,
        isCritical = false,
        onDismiss = {},
        onProceed = {},
    )
}

@Preview
@Composable
internal fun VoiceMessageCriticalSpaceDialogPreview() = ElementPreview {
    VoiceMessageLowSpaceDialog(
        availableMB = 5.8,
        isCritical = true,
        onDismiss = {},
    )
}
