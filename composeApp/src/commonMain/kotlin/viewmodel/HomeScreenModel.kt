package viewmodel

import DrawMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import cafe.adriel.voyager.core.model.ScreenModel
import gesture.PointerEvent
import ui.graphics.CircleShape
import ui.graphics.PolygonShape
import ui.graphics.PolylineShape
import ui.graphics.RectShape
import ui.graphics.Shape
import ui.menu.MenuAction
import ui.menu.MenuButton
import kotlin.math.abs
import kotlin.math.min

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

    val isResizeSelectionDrawMode get() = (drawMode == DrawMode.ResizeSelection)

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

    fun copyShape(shape: Shape): Shape {
        return when (shape) {
            is PolygonShape -> shape.copy { PolygonShape() }
            is RectShape -> shape.copy { RectShape() }
            else -> shape.copy { Shape() }
        }
    }

    fun drawShapes(drawScope: DrawScope) {
        shapes.forEach {
            it.draw(drawScope)
        }
    }

    fun drawCurrentShape(drawScope: DrawScope) {
        val pathProperties =
            if (isSelectionAction) currentShape.selectionPathProperties
            else currentShape.properties

        currentShape.draw(drawScope, pathProperties)
    }

    fun updateSelection(hitTestShape: Shape = currentShape) {
        shapes.forEach {
            if (it.isSelected.not()) {
                it.isSelected = isSelectionAction && it.intersects(hitTestShape)
            }
        }

        showHandlesIfNeeded()
    }

    fun updateSelectionAtOffset(offset: Offset) {
        updateSelection(hitTestShape = createHitTestCircleShapeWithOffset(offset))
    }

    fun clearSelection() {
        drawMode = DrawMode.Draw

        shapes.forEach {
            it.isSelected = false
        }
    }

    fun translateSelectedShapes(offset: Offset) {
        selectedShapes.forEach {
            it.translate(offset)
        }
    }

    fun updateDrawMode(offset: Offset) {
        drawMode = if (isSelectionAction) {
            if (selectedShapes.isEmpty()) {
                DrawMode.Draw
            } else {
                if (selectedShapes.count() == 1 &&
                    selectedShapes.first().hasHandleAtOffset(offset)) {
                    DrawMode.ResizeSelection
                } else {
                    DrawMode.MoveSelection
                }
            }
        } else {
            DrawMode.Draw
        }
    }

    fun startCurrentShapeResizing(offset: Offset) {
        if (selectedShapes.count() == 1) {
            currentShape = selectedShapes.first()
            currentShape.updateSelectedHandleIndexAtOffset(offset)
        }
    }

    fun endCurrentShapeResizing() {
        currentShape.endResizing()
    }

    fun resizeCurrentShape(dragAmount: Offset) {
        currentShape.resize(dragAmount)
    }

    fun setRectShapeAsCurrentShape() {
        val left = min(previousPosition.x, currentPosition.x)
        val top = min(previousPosition.y, currentPosition.y)
        val right = left + abs(previousPosition.x - currentPosition.x)
        val bottom = top + abs(previousPosition.y - currentPosition.y)

        val rect = Rect(
            left = left,
            top = top,
            right = right,
            bottom = bottom
        )

        currentShape = RectShape(rect)
    }

    /**
     * Create circle shape for hit test detection using intersection
     */
    private fun createHitTestCircleShapeWithOffset(offset: Offset) : Shape {
        return CircleShape(offset, 20f)
    }

    fun showHandlesIfNeeded() {
        // show handles only if there is one shape selected
        if (selectedShapes.count() == 1) {
            selectedShapes.first().showHandles = true
        } else {
            selectedShapes.forEach {
                it.showHandles = false
            }
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