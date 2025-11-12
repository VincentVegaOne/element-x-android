/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modifier that adds slide-to-cancel gesture detection to voice message recording UI.
 * Detects horizontal left swipe and triggers cancellation when threshold is reached.
 *
 * @param enabled Whether the gesture detection is active
 * @param threshold The horizontal distance (in Dp) required to trigger cancellation. Negative for left swipe.
 * @param onDragProgress Callback invoked during drag with progress from 0.0 to 1.0
 * @param onCancel Callback invoked when the drag reaches the cancellation threshold
 * @param onHapticFeedback Callback invoked when haptic feedback should be triggered
 */
@Composable
fun Modifier.slideToCancelGesture(
    enabled: Boolean,
    threshold: Dp = SLIDE_TO_CANCEL_THRESHOLD_DP,
    onDragProgress: (Float, Offset) -> Unit = { _, _ -> },
    onCancel: () -> Unit,
    onHapticFeedback: () -> Unit = {},
): Modifier {
    if (!enabled) return this

    val thresholdPx = with(LocalDensity.current) { threshold.toPx() }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var hasCancelled by remember { mutableStateOf(false) }

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = { offset ->
                isDragging = true
                hasCancelled = false
                dragOffset = 0f
            },
            onDragEnd = {
                isDragging = false
                if (!hasCancelled) {
                    // Reset if drag ended without reaching threshold
                    dragOffset = 0f
                    onDragProgress(0f, Offset.Zero)
                }
            },
            onDragCancel = {
                isDragging = false
                dragOffset = 0f
                onDragProgress(0f, Offset.Zero)
            },
            onHorizontalDrag = { change, dragAmount ->
                if (hasCancelled) return@detectHorizontalDragGestures

                // Only track leftward drag (negative X)
                if (dragAmount < 0 || dragOffset < 0) {
                    dragOffset += dragAmount
                    change.consume()

                    // Calculate progress (0.0 to 1.0)
                    val progress = (-dragOffset / -thresholdPx).coerceIn(0f, 1f)
                    onDragProgress(progress, Offset(dragOffset, 0f))

                    // Trigger cancellation when threshold reached
                    if (dragOffset <= thresholdPx && !hasCancelled) {
                        hasCancelled = true
                        onHapticFeedback()
                        onCancel()
                    }
                }
            }
        )
    }
}

/**
 * State holder for slide-to-cancel gesture animation and progress tracking.
 *
 * @property dragProgress Current drag progress from 0.0 to 1.0
 * @property dragOffset Current horizontal offset in pixels
 * @property shouldShowIndicator Whether the cancel indicator should be visible
 */
data class SlideToCancelState(
    val dragProgress: Float = 0f,
    val dragOffset: Offset = Offset.Zero,
    val shouldShowIndicator: Boolean = false,
)

/**
 * Creates and remembers a state holder for slide-to-cancel animation with spring physics.
 *
 * @return Pair of current state and update function
 */
@Composable
fun rememberSlideToCancelState(): Pair<SlideToCancelState, (Float, Offset) -> Unit> {
    val animatedOffset = remember { Animatable(0f) }
    var state by remember { mutableStateOf(SlideToCancelState()) }

    val updateState: (Float, Offset) -> Unit = { progress, offset ->
        state = SlideToCancelState(
            dragProgress = progress,
            dragOffset = offset,
            shouldShowIndicator = progress > 0.05f
        )
    }

    // Animate the offset with spring physics
    LaunchedEffect(state.dragOffset.x) {
        animatedOffset.animateTo(
            targetValue = state.dragOffset.x,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    return state to updateState
}

/**
 * Threshold distance for slide-to-cancel gesture.
 * User must drag at least this distance (leftward) to cancel the recording.
 */
private val SLIDE_TO_CANCEL_THRESHOLD_DP = (-120).dp
