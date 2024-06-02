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
import ui.menu.MenuAction
import ui.menu.MenuButton

class HomeScreenModel : ScreenModel {

    // region Properties

    /**
     * Shapes that are added, this is required to have shapes with different options and shapes
     *  ith erase to keep over each other
     */
    val shapes = mutableStateListOf<Shape>()

    /**
     * Shapes that are undone via button. These shapes are restored if user pushes
     * redo button if there is no new shape drawn.
     *
     * If new shape is drawn after this list is cleared to not break shapes after undoing previous
     * ones.
     */
    val shapesUndone = mutableStateListOf<Shape>()

    val selectedShapes get() = shapes.filter { it.isSelected }

    /**
     * Shapes that is being drawn between [PointerEvent.DragStart] and [PointerEvent.DragEnd]. When
     * pointer is up this shape is saved to **shapes** and new instance is created
     */
    var currentShape by mutableStateOf(Shape())

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

    var shapesMenuButtonSelected by mutableStateOf(true)

    var selectionButtonSelected by mutableStateOf(false)

    var shapesMenuVisible by mutableStateOf(false)

    var polygonMenuVisible by mutableStateOf(false)

    var shapesMenuButton: MenuButton by mutableStateOf(MenuButton.DrawRectangleMenuButton)

    var currentMenuButtonAction by mutableStateOf(MenuButton.DrawRectangleMenuButton.menuAction)

    val isSelectionAction get() = (currentMenuButtonAction == MenuAction.DoSelection)

    val isPolygonAction get() = listOf(
        MenuAction.DrawPolygon,
        MenuAction.PolygonApply,
        MenuAction.PolygonCancel
    ).contains(currentMenuButtonAction)

    // endregion

    // region Methods

    fun clearSelection() {
        drawMode = DrawMode.Draw

        shapes.forEach {
            it.isSelected = false
        }
    }

    fun performUndo() {
        if (shapes.isEmpty()) {
            return
        }

        val lastItem = shapes.last()
        shapes.remove(lastItem)
        shapesUndone.add(lastItem)
    }

    fun performRedo() {
        if (shapesUndone.isEmpty()) {
            return
        }

        val lastShape = shapesUndone.last()
        shapesUndone.removeLast()
        shapes.add(lastShape)
    }

    // endregion

}