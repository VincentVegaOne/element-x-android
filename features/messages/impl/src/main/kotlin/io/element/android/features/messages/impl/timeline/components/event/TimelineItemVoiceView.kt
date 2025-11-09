/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.timeline.components.event.PlaybackSpeedOption.Companion.nextSpeed
import io.element.android.features.messages.impl.timeline.components.event.PlaybackSpeedOption.Companion.toSpeedLabel
import io.element.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContentProvider
import io.element.android.libraries.designsystem.components.media.WaveformPlaybackView
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.time.isTalkbackActive
import io.element.android.libraries.voiceplayer.api.VoiceMessageEvents
import io.element.android.libraries.voiceplayer.api.VoiceMessageState
import io.element.android.libraries.voiceplayer.api.VoiceMessageStateProvider
import kotlinx.coroutines.delay

@Composable
fun TimelineItemVoiceView(
    state: VoiceMessageState,
    content: TimelineItemVoiceContent,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun playPause() {
        state.eventSink(VoiceMessageEvents.PlayPause)
    }

    fun skipBackward() {
        state.eventSink(VoiceMessageEvents.SkipBackward)
    }

    fun skipForward() {
        state.eventSink(VoiceMessageEvents.SkipForward)
    }

    fun cyclePlaybackSpeed() {
        val nextSpeed = state.playbackSpeed.nextSpeed()
        state.eventSink(VoiceMessageEvents.SetPlaybackSpeed(nextSpeed))
    }

    val a11y = stringResource(CommonStrings.common_voice_message)
    val a11yActionLabel = stringResource(
        when (state.button) {
            VoiceMessageState.Button.Play -> CommonStrings.a11y_play
            VoiceMessageState.Button.Pause -> CommonStrings.a11y_pause
            VoiceMessageState.Button.Downloading -> CommonStrings.common_downloading
            VoiceMessageState.Button.Retry -> CommonStrings.action_retry
            VoiceMessageState.Button.Disabled -> CommonStrings.error_unknown
        }
    )

    // Modern card-style container inspired by WhatsApp & Telegram
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                contentDescription = a11y
                if (state.button == VoiceMessageState.Button.Disabled) {
                    disabled()
                } else if (state.button in listOf(VoiceMessageState.Button.Play, VoiceMessageState.Button.Pause)) {
                    onClick(label = a11yActionLabel) {
                        playPause()
                        true
                    }
                }
            }
            .onSizeChanged {
                onContentLayoutChange(
                    ContentAvoidingLayoutData(
                        contentWidth = it.width,
                        contentHeight = it.height,
                    )
                )
            },
        shape = RoundedCornerShape(16.dp),
        color = ElementTheme.colors.bgSubtleSecondary,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Main playback controls row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (!isTalkbackActive()) {
                    when (state.button) {
                        VoiceMessageState.Button.Play -> PlayButton(onClick = ::playPause)
                        VoiceMessageState.Button.Pause -> PauseButton(onClick = ::playPause)
                        VoiceMessageState.Button.Downloading -> ProgressButton()
                        VoiceMessageState.Button.Retry -> RetryButton(onClick = ::playPause)
                        VoiceMessageState.Button.Disabled -> PlayButton(onClick = {}, enabled = false)
                    }
                }
                Spacer(Modifier.width(12.dp))

                // Skip backward button - larger touch target
                if (state.button in listOf(VoiceMessageState.Button.Play, VoiceMessageState.Button.Pause)) {
                    IconButton(
                        onClick = { skipBackward() },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = CompoundIcons.ArrowLeft(),
                            contentDescription = "Skip backward 15s",
                            tint = ElementTheme.colors.iconSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                // Larger, bolder time display
                Text(
                    text = state.time,
                    color = ElementTheme.colors.textPrimary,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(12.dp))

                // Enhanced waveform with vibrant gradient colors
                // Unplayed portion: Subtle gray gradient
                val waveformBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFCCCCCC), // Light gray
                        Color(0xFFAAAAAA), // Medium gray
                    )
                )

                // Played portion: Vibrant blue-to-purple gradient (inspired by modern audio apps)
                val progressBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0D6EFD), // Vibrant blue
                        Color(0xFF6610F2), // Purple
                        Color(0xFF0D6EFD), // Back to blue for shimmer effect
                    )
                )

                // Cursor: Bright accent color that stands out
                val cursorBrush = SolidColor(Color(0xFFFFFFFF)) // White cursor for maximum contrast

                WaveformPlaybackView(
                    showCursor = state.showCursor,
                    playbackProgress = state.progress,
                    waveform = content.waveform,
                    modifier = Modifier
                        .height(56.dp) // Larger waveform for better touch interaction
                        .weight(1f),
                    seekEnabled = !isTalkbackActive(),
                    onSeek = { state.eventSink(VoiceMessageEvents.Seek(it)) },
                    brush = waveformBrush,
                    progressBrush = progressBrush,
                    cursorBrush = cursorBrush,
                    lineWidth = 3.5.dp, // Thicker lines for better visibility
                    linePadding = 3.dp, // More spacing between bars
                )
                Spacer(Modifier.width(12.dp))

                // Skip forward button - larger touch target
                if (state.button in listOf(VoiceMessageState.Button.Play, VoiceMessageState.Button.Pause)) {
                    IconButton(
                        onClick = { skipForward() },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = CompoundIcons.ArrowRight(),
                            contentDescription = "Skip forward 15s",
                            tint = ElementTheme.colors.iconSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            // Playback speed control row - more prominent design
            if (state.button in listOf(VoiceMessageState.Button.Play, VoiceMessageState.Button.Pause)) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Left-aligned speed control button (inspired by Telegram)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = ::cyclePlaybackSpeed)
                            .background(
                                color = ElementTheme.colors.bgActionPrimaryRest.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = ElementTheme.colors.borderInteractivePrimary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Icon(
                            imageVector = CompoundIcons.PlaySolid(),
                            contentDescription = null,
                            tint = ElementTheme.colors.iconAccentTertiary,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = state.playbackSpeed.toSpeedLabel(),
                            style = ElementTheme.typography.fontBodySmMedium,
                            color = ElementTheme.colors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    CustomIconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        ControlIcon(
            imageVector = CompoundIcons.PlaySolid(),
            contentDescription = stringResource(id = CommonStrings.a11y_play),
        )
    }
}

@Composable
private fun PauseButton(
    onClick: () -> Unit,
) {
    CustomIconButton(
        onClick = onClick,
    ) {
        ControlIcon(
            imageVector = CompoundIcons.PauseSolid(),
            contentDescription = stringResource(id = CommonStrings.a11y_pause),
        )
    }
}

@Composable
private fun RetryButton(
    onClick: () -> Unit,
) {
    CustomIconButton(
        onClick = onClick,
    ) {
        ControlIcon(
            imageVector = CompoundIcons.Restart(),
            contentDescription = stringResource(id = CommonStrings.action_retry),
        )
    }
}

@Composable
private fun ControlIcon(
    imageVector: ImageVector,
    contentDescription: String?,
) {
    Icon(
        modifier = Modifier.padding(vertical = 10.dp),
        imageVector = imageVector,
        contentDescription = contentDescription,
    )
}

/**
 * Progress button is shown when the voice message is being downloaded.
 *
 * The progress indicator is optimistic and displays a pause button (which
 * indicates the audio is playing) for 2 seconds before revealing the
 * actual progress indicator.
 */
@Composable
private fun ProgressButton(
    displayImmediately: Boolean = false,
) {
    var canDisplay by remember { mutableStateOf(displayImmediately) }
    LaunchedEffect(Unit) {
        delay(2000L)
        canDisplay = true
    }
    CustomIconButton(
        onClick = {},
        enabled = false,
    ) {
        if (canDisplay) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(2.dp)
                    .size(16.dp),
                color = ElementTheme.colors.iconSecondary,
                strokeWidth = 2.dp,
            )
        } else {
            ControlIcon(
                imageVector = CompoundIcons.PauseSolid(),
                contentDescription = stringResource(id = CommonStrings.a11y_pause),
            )
        }
    }
}

@Composable
private fun CustomIconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(color = ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
            .size(52.dp), // Larger button for better touch accessibility
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = ElementTheme.colors.iconSecondary,
            disabledContentColor = ElementTheme.colors.iconDisabled,
        ),
        content = content,
    )
}

open class TimelineItemVoiceViewParametersProvider : PreviewParameterProvider<TimelineItemVoiceViewParameters> {
    private val voiceMessageStateProvider = VoiceMessageStateProvider()
    private val timelineItemVoiceContentProvider = TimelineItemVoiceContentProvider()
    override val values: Sequence<TimelineItemVoiceViewParameters>
        get() = timelineItemVoiceContentProvider.values.flatMap { content ->
            voiceMessageStateProvider.values.map { state ->
                TimelineItemVoiceViewParameters(
                    state = state,
                    content = content,
                )
            }
        }
}

data class TimelineItemVoiceViewParameters(
    val state: VoiceMessageState,
    val content: TimelineItemVoiceContent,
)

@PreviewsDayNight
@Composable
internal fun TimelineItemVoiceViewPreview(
    @PreviewParameter(TimelineItemVoiceViewParametersProvider::class) timelineItemVoiceViewParameters: TimelineItemVoiceViewParameters,
) = ElementPreview {
    TimelineItemVoiceView(
        state = timelineItemVoiceViewParameters.state,
        content = timelineItemVoiceViewParameters.content,
        onContentLayoutChange = {},
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemVoiceViewUnifiedPreview() = ElementPreview {
    val timelineItemVoiceViewParametersProvider = TimelineItemVoiceViewParametersProvider()
    Column {
        timelineItemVoiceViewParametersProvider.values.forEach {
            TimelineItemVoiceView(
                state = it.state,
                content = it.content,
                onContentLayoutChange = {},
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ProgressButtonPreview() = ElementPreview {
    Row {
        ProgressButton(displayImmediately = true)
        ProgressButton(displayImmediately = false)
    }
}
