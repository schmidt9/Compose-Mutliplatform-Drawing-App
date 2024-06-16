package gesture

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

/**
 * https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary
 */
fun Modifier.pointerEvents(
    onTap: (Offset) -> Unit,
    onDoubleTap: (Offset) -> Unit,
    onLongPress: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset, Offset) -> Unit,
    onDragEnd: () -> Unit,
    onPressReleased: (Offset) -> Unit,
    onTransform:(centroid: Offset, pan: Offset, zoom: Float) -> Unit
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
            detectTransformGestures(
                panZoomLock = true,
                onGesture = { centroid, pan, zoom, _ ->
                    onTransform(centroid, pan, zoom)
                }
            )
        }
)