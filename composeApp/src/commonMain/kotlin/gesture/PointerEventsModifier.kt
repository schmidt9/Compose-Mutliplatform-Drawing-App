package gesture

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

/**
 * https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary
 */
fun Modifier.pointerEvents(
    onTap: (Offset) -> Unit,
    onDoubleTap: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset, Offset) -> Unit,
    onDragEnd: () -> Unit
) = this.then(
    Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onTap(it)
                },
                onDoubleTap = {
                    onDoubleTap(it)
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    onDragStart(it)
                },
                onDrag = { change, dragAmount ->
                    onDrag(change.position, dragAmount)
                },
                onDragEnd = {
                    onDragEnd()
                }
            )
        }
)