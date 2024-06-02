package viewmodel

import DrawMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import cafe.adriel.voyager.core.model.ScreenModel
import gesture.PointerEvent
import ui.graphics.Shape

class HomeScreenModel : ScreenModel {

    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    val paths = mutableStateListOf<Shape>()

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    val pathsUndone = mutableStateListOf<Shape>()

    val selectedPaths get() = paths.filter { it.isSelected }

    /**
     * Path that is being drawn between [PointerEvent.DragStart] and [PointerEvent.DragEnd]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPath by mutableStateOf(Shape())

    /**
     * Canvas touch state. [PointerEvent.Idle] by default, [PointerEvent.DragStart] at first contact,
     * [PointerEvent.Drag] while dragging and [PointerEvent.DragEnd] when first pointer is up
     */
    var pointerEvent by mutableStateOf(PointerEvent.Idle)

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by mutableStateOf(Offset.Unspecified)

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition by mutableStateOf(Offset.Unspecified)

    /**
     * Draw mode, erase mode or touch mode to
     */
    var drawMode by mutableStateOf(DrawMode.Draw)

    val isMoveSelectionDrawMode get() = (drawMode == DrawMode.MoveSelection)
}