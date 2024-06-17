package gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import ui.graphics.Point

/**
 * https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary
 */
fun Modifier.pointerEvents(
    onTap: (Point) -> Unit,
    onDoubleTap: (Point) -> Unit,
    onLongPress: (Point) -> Unit,
    onDragStart: (Point) -> Unit,
    onDrag: (Point, Offset) -> Unit,
    onDragEnd: () -> Unit,
    onPressReleased: (Point) -> Unit,
    onTransform:(centroid: Point, pan: Offset, zoom: Float) -> Unit,
    onPointerUp: () -> Unit
) = this.then(
    Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    onDoubleTap(it)
                },
                onLongPress = {
                    onLongPress(it)
                },
                onPress = {
                    val released = tryAwaitRelease()

                    if (released) {
                        onPressReleased(it)
                    }
                },
                onTap = {
                    onTap(it)
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    onDragStart(it)
                },
                onDragEnd = {
                    onDragEnd()
                },
                onDrag = { change, dragAmount ->
                    onDrag(change.position, dragAmount)
                }
            )
        }
        .pointerInput(Unit) {
            // https://stackoverflow.com/a/75425307/3004003
            // use this approach instead of built-in detectTransformGestures
            // because detectTransformGestures also fires with single pointer
            // preventing detectDragGestures to work
            awaitEachGesture {
                awaitFirstDown()

                do {
                    val event = awaitPointerEvent()

                    if (event.changes.size == 2) {
                        val centroid = event.calculateCentroid()
                        val pan = event.calculatePan()
                        val zoom = event.calculateZoom()
                        onTransform(centroid, pan, zoom)

                        // This is for preventing other gestures consuming events
                        // prevent scrolling or other continuous gestures
                        event.changes.forEach {
                            it.consume()
                        }
                    }
                } while (event.changes.any { it.pressed })

                onPointerUp()
            }
        }
)