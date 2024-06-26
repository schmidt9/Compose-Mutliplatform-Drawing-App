package viewmodel

import DrawMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import cafe.adriel.voyager.core.model.ScreenModel
import gesture.PointerEvent
import ui.graphics.CircleShape
import ui.graphics.ImageShape
import ui.graphics.Point
import ui.graphics.PolygonShape
import ui.graphics.RectShape
import ui.graphics.Shape
import ui.menu.MenuAction
import ui.menu.MenuButton
import util.hitTestRadius
import kotlin.math.abs
import kotlin.math.min

class HomeScreenModel : ScreenModel {

    // region Properties

    /**
     * Shapes that are added, this is required to have shapes with different options and shapes
     *  ith erase to keep over each other
     */
    private val shapes = mutableStateListOf<Shape>()

    /**
     * Shapes that are undone via button. These shapes are restored if user pushes
     * redo button if there is no new shape drawn.
     *
     * If new shape is drawn after this list is cleared to not break shapes after undoing previous
     * ones.
     */
    val shapesUndone = mutableStateListOf<Shape>()

    private val selectedShapes get() = shapes.filter { it.isSelected }

    val hasSingleSelection get() = (selectedShapes.count() == 1)

    val hasSelection get() = selectedShapes.isNotEmpty()

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
    var currentPosition: Point by mutableStateOf(Point.Unspecified)

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition: Point by mutableStateOf(Point.Unspecified)

    /**
     * Draw mode, erase mode or touch mode to
     */
    var drawMode by mutableStateOf(DrawMode.Draw)

    val isMoveSelectionDrawMode get() = (drawMode == DrawMode.MoveSelection)

    val isResizeSelectionDrawMode get() = (drawMode == DrawMode.ResizeSelection)

    var shapesMenuButtonSelected by mutableStateOf(true)

    var selectionButtonSelected by mutableStateOf(false)

    var addImageButtonSelected by mutableStateOf(false)

    var addImageDialogVisible by mutableStateOf(false)

    var imageIsLoading by mutableStateOf(false)

    var shapesMenuVisible by mutableStateOf(false)

    var polygonMenuVisible by mutableStateOf(false)

    var selectionActionsMenuVisible by mutableStateOf(false)

    var shapesMenuButton: MenuButton by mutableStateOf(MenuButton.DrawRectangleMenuButton)

    var currentMenuButtonAction by mutableStateOf(MenuButton.DrawRectangleMenuButton.menuAction)

    val isPolygonAction get() = listOf(
        MenuAction.DrawPolygon,
        MenuAction.PolygonApply,
        MenuAction.PolygonCancel
    ).contains(currentMenuButtonAction)

    val isSelectionAction get() = (currentMenuButtonAction == MenuAction.DoSelection)

    val isAddImageAction get() = (currentMenuButtonAction == MenuAction.AddImage)

    var totalZoom by mutableStateOf(1f)

    // endregion

    // region Methods

    fun addCurrentShape() {
        shapes.add(copyShape(currentShape))
    }

    fun addCurrentImageShape(image: ImageBitmap) {
        if (currentShape !is ImageShape) {
            return
        }

        currentShape.isSelected = true

        with(currentShape as ImageShape) {
            this.image = image
            sizeToFitImage()
        }

        addCurrentShape()
    }

    private fun copyShape(shape: Shape): Shape {
        // using ::class comparison here instead of when..is pattern
        // because when..is gives returns true for subclasses when comparing with superclass
        val shapeClass = shape::class

        if (shapeClass == PolygonShape::class) {
            return shape.copy { PolygonShape() }
        }

        if (shapeClass == RectShape::class) {
            return shape.copy { RectShape() }
        }

        if (shapeClass == ImageShape::class) {
            return shape.copy { ImageShape() }
        }

        return shape.copy { Shape() }
    }

    fun drawShapes(drawScope: DrawScope) {
        shapes.forEach {
            it.draw(drawScope)
        }
    }

    fun drawCurrentShape(drawScope: DrawScope) {
        val pathProperties =
            if (isSelectionAction && isResizeSelectionDrawMode.not()) currentShape.selectionPathProperties
            else currentShape.properties

        currentShape.draw(drawScope, pathProperties)
    }

    fun updateSelection(hitTestShape: Shape = currentShape) {
        if (isResizeSelectionDrawMode.not()) {
            shapes.forEach {
                if (it.isSelected.not()) {
                    it.isSelected = (isSelectionAction || isAddImageAction) && it.intersects(hitTestShape)
                }
            }
        }

        showHandlesIfNeeded()

        if (selectionActionsMenuVisible.not() && selectedShapes.isNotEmpty()) {
            selectionActionsMenuVisible = true
        } else if (selectedShapes.isEmpty()) {
            selectionActionsMenuVisible = false
        }
    }

    fun updateSelectionAtPoint(point: Point) {
        updateSelection(hitTestShape = createHitTestCircleShapeWithPoint(point))
    }

    fun clearSelection() {
        drawMode = DrawMode.Draw

        shapes.forEach {
            it.isSelected = false
        }
    }

    fun removeSelection() {
        shapes.removeAll {
            it.isSelected
        }
    }

    private fun translateAllShapes(offset: Offset) {
        shapes.forEach {
            it.translate(offset)
        }
    }

    fun translateSelectedShapes(offset: Offset) {
        selectedShapes.forEach {
            it.translate(offset)
        }
    }

    private fun scaleAllShapes(scaleFactor: Float, anchor: Point) {
        shapes.forEach {
            it.scale(scaleFactor, anchor)
        }
    }

    private fun scaleSelectedShapes(scaleFactor: Float, anchor: Point) {
        selectedShapes.forEach {
            it.scale(scaleFactor, anchor)
        }
    }

    fun updateDrawMode(point: Point) {
        drawMode = if (isSelectionAction || isAddImageAction) {
            if (selectedShapes.isEmpty()) {
                DrawMode.Draw
            } else {
                if (hasSingleSelection &&
                    selectedShapes.first().hasHandleAtPoint(point)) {
                    DrawMode.ResizeSelection
                } else {
                    DrawMode.MoveSelection
                }
            }
        } else {
            DrawMode.Draw
        }
    }

    fun startCurrentShapeResizing(point: Point) {
        if (hasSingleSelection) {
            currentShape = selectedShapes.first()
            currentShape.updateSelectedHandleIndexAtPoint(point)
        }
    }

    fun endCurrentShapeResizing() {
        currentShape.endResizing()
    }

    fun resizeCurrentShape(point: Point) {
        currentShape.resize(point)
    }

    fun handleLongPress(point: Point) {
        if (hasSingleSelection) {
            selectedShapes.first().addPointIfNeeded(point)
        }
    }

    fun handleDoubleTap(point: Point) {
        if (hasSingleSelection) {
            selectedShapes.first().removePointIfNeeded(point)
        }
    }

    fun handlePan(pan: Offset) {
        if (selectedShapes.isEmpty()) {
            translateAllShapes(pan)
        } else {
            translateSelectedShapes(pan)
        }
    }

    fun handleZoom(centroid: Point, zoom: Float) {
        if (zoom >= 1) {
            val delta = zoom - 1
            this.totalZoom += delta
        } else {
            val delta = 1 - zoom
            this.totalZoom -= delta
        }

        if (centroid == Point.Unspecified) {
            return
        }

        if (selectedShapes.isEmpty()) {
            scaleAllShapes(zoom, centroid)
        } else {
            scaleSelectedShapes(zoom, centroid)
        }

    }

    fun handleAddImageAction() {
        // TODO: impl
    }

    fun setRectShapeAsCurrentShape() {
        currentShape = RectShape(createRect())
    }

    fun setAddImageShapeAsCurrentShape() {
        currentShape = ImageShape(createRect())
    }

    /**
     * Create circle shape for hit test detection using intersection
     */
    private fun createHitTestCircleShapeWithPoint(point: Point) : Shape {
        return CircleShape(point, hitTestRadius)
    }

    private fun createRect(): Rect {
        val left = min(previousPosition.x, currentPosition.x)
        val top = min(previousPosition.y, currentPosition.y)
        val right = left + abs(previousPosition.x - currentPosition.x)
        val bottom = top + abs(previousPosition.y - currentPosition.y)

        return Rect(
            left = left,
            top = top,
            right = right,
            bottom = bottom
        )
    }

    private fun showHandlesIfNeeded() {
        // show handles only if there is one shape selected
        if (hasSingleSelection) {
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