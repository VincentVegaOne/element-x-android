/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.roomcall.api.RoomCallStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
internal fun CallMenuItem(
    roomCallState: RoomCallState,
    activeMembersCount: Long,
    onJoinCallClick: (videoEnabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (roomCallState) {
        RoomCallState.Unavailable -> {
            Box(modifier)
        }
        is RoomCallState.StandBy -> {
            StandByCallMenuItem(
                roomCallState = roomCallState,
                activeMembersCount = activeMembersCount,
                onJoinCallClick = onJoinCallClick,
                modifier = modifier,
            )
        }
        is RoomCallState.OnGoing -> {
            OnGoingCallMenuItem(
                roomCallState = roomCallState,
                activeMembersCount = activeMembersCount,
                onJoinCallClick = onJoinCallClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun StandByCallMenuItem(
    roomCallState: RoomCallState.StandBy,
    activeMembersCount: Long,
    onJoinCallClick: (videoEnabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // For 1-on-1 chats, show separate voice and video buttons
    if (activeMembersCount == 2L) {
        Row(modifier = modifier) {
            // Audio call button (phone icon)
            IconButton(
                onClick = { onJoinCallClick(false) },
                enabled = roomCallState.canStartCall,
            ) {
                Icon(
                    imageVector = CompoundIcons.VoiceCallSolid(),
                    contentDescription = stringResource(CommonStrings.a11y_start_call),
                )
            }
            Spacer(Modifier.width(4.dp))
            // Video call button (camera icon)
            IconButton(
                onClick = { onJoinCallClick(true) },
                enabled = roomCallState.canStartCall,
            ) {
                Icon(
                    imageVector = CompoundIcons.VideoCallSolid(),
                    contentDescription = stringResource(CommonStrings.a11y_start_call),
                )
            }
        }
    } else {
        // For group chats, show single headphones button
        IconButton(
            modifier = modifier,
            onClick = { onJoinCallClick(true) },
            enabled = roomCallState.canStartCall,
        ) {
            Icon(
                imageVector = getCallIcon(activeMembersCount),
                contentDescription = stringResource(CommonStrings.a11y_start_call),
            )
        }
    }
}

@Composable
private fun OnGoingCallMenuItem(
    roomCallState: RoomCallState.OnGoing,
    activeMembersCount: Long,
    onJoinCallClick: (videoEnabled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!roomCallState.isUserLocallyInTheCall) {
        Button(
            onClick = { onJoinCallClick(true) },
            colors = ButtonDefaults.buttonColors(
                contentColor = ElementTheme.colors.bgCanvasDefault,
                containerColor = ElementTheme.colors.iconAccentTertiary
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            modifier = modifier.heightIn(min = 36.dp),
            enabled = roomCallState.canJoinCall,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = getCallIcon(activeMembersCount),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(CommonStrings.action_join),
                style = ElementTheme.typography.fontBodyMdMedium
            )
            Spacer(Modifier.width(8.dp))
        }
    } else {
        // Else user is already in the call, hide the button.
        Box(modifier)
    }
}

@Composable
private fun getCallIcon(activeMembersCount: Long): ImageVector {
    return when {
        activeMembersCount == 2L -> CompoundIcons.VoiceCallSolid()
        activeMembersCount > 2L -> CompoundIcons.HeadphonesSolid()
        else -> CompoundIcons.VideoCallSolid()
    }
}

@PreviewsDayNight
@Composable
internal fun CallMenuItemPreview(
    @PreviewParameter(RoomCallStateProvider::class) roomCallState: RoomCallState
) = ElementPreview {
    CallMenuItem(
        roomCallState = roomCallState,
        activeMembersCount = 2L,
        onJoinCallClick = {}
    )
}
