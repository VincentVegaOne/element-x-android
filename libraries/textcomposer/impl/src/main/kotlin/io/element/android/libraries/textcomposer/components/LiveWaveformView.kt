/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.media.drawWaveform
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.GRAPHICS_LAYER_ALPHA
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_HEIGHT_RECORDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_LINE_PADDING
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_LINE_WIDTH
import io.element.android.libraries.textcomposer.components.VoiceMessageConstants.WAVEFORM_RECORDING_BRUSH
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.lang.Float.min

/**
 * Live waveform view that displays audio levels in real-time during recording.
 *
 * Uses vibrant gradient colors and optimized dimensions to provide excellent
 * visual feedback during voice message recording.
 *
 * @param levels Audio level data points (0.0-1.0), displayed from right to left
 * @param modifier Modifier for the component
 * @param brush Brush for drawing the waveform (defaults to vibrant recording gradient)
 * @param lineWidth Width of individual waveform bars
 * @param linePadding Space between waveform bars
 */
@Composable
fun LiveWaveformView(
    levels: ImmutableList<Float>,
    modifier: Modifier = Modifier,
    brush: Brush = WAVEFORM_RECORDING_BRUSH,
    lineWidth: Dp = WAVEFORM_LINE_WIDTH,
    linePadding: Dp = WAVEFORM_LINE_PADDING,
) {
    var canvasSize by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }

    var parentWidth by remember { mutableIntStateOf(0) }

    val waveformWidth by remember(levels, lineWidth, linePadding) {
        derivedStateOf {
            levels.size * (lineWidth.value + linePadding.value)
        }
    }

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
                .fillMaxWidth()
                .onSizeChanged { parentWidth = it.width }
    ) {
        Canvas(
            modifier = Modifier
                    .width(Dp(waveformWidth))
                    .graphicsLayer(alpha = GRAPHICS_LAYER_ALPHA)
                    .then(modifier)
        ) {
            val width = min(waveformWidth, parentWidth.toFloat())
            canvasSize = DpSize(width.dp, size.height.toDp())
            val countThatFitsWidth = (parentWidth.toFloat() / (lineWidth.toPx() + linePadding.toPx())).toInt()
            drawWaveform(
                waveformData = levels.takeLast(countThatFitsWidth).toImmutableList(),
                canvasSizePx = Size(canvasSize.width.toPx(), size.height),
                brush = brush,
                lineWidth = lineWidth,
                linePadding = linePadding,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LiveWaveformViewPreview() = ElementPreview {
    Column {
        LiveWaveformView(
            levels = List(100) { it.toFloat() / 100 }.toImmutableList(),
            modifier = Modifier.height(WAVEFORM_HEIGHT_RECORDING),
        )
        LiveWaveformView(
            levels = List(40) { it.toFloat() / 40 }.toImmutableList(),
            modifier = Modifier.height(WAVEFORM_HEIGHT_RECORDING),
        )
    }
}
