package ui.graphics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import model.PathProperties
import model.selectedPathProperties
import model.selectionPathProperties
import kotlin.jvm.JvmName

open class Shape(open var properties: PathProperties = PathProperties()){

    enum class PropertiesType {
        Default,
        SelectedPath,
        SelectionPath
    }

    // region Properties

    companion object {
        const val INDEX_NOT_SET = -1
    }

    protected val path = Path()

    private val selectedPathProperties = PathProperties.selectedPathProperties

    val selectionPathProperties = PathProperties.selectionPathProperties

    val isEmpty get() = path.isEmpty

    protected open var shouldClose = false

    var isSelected by mutableStateOf(false)

    protected var points = mutableListOf<Offset>()

    private var handles = listOf<HandleShape>()

    var showHandles = false

    var selectedHandleIndex = INDEX_NOT_SET

    // endregion

    // region Methods

    fun draw(
        drawScope: DrawScope,
        propertiesType: PropertiesType = PropertiesType.Default
    ) {
        if (isSelected) {
            draw(drawScope, selectedPathProperties)
        }

        val currentProperties = when (propertiesType) {
            PropertiesType.Default -> properties
            PropertiesType.SelectedPath -> selectedPathProperties
            PropertiesType.SelectionPath -> selectedPathProperties
        }

        draw(drawScope, currentProperties)
    }

    open fun draw(
        drawScope: DrawScope,
        properties: PathProperties
    ) {
        if (path.isEmpty) {
            return
        }

        properties.fillColor?.let {
            drawScope.drawPath(
                path = path,
                color = it
            )
        }

        drawScope.drawPath(
            path = path,
            color = properties.strokeColor,
            style = Stroke(
                width = properties.strokeWidth,
                cap = properties.strokeCap,
                join = properties.strokeJoin,
                pathEffect = properties.pathEffect
            )
        )

        updateHandles()

        handles.forEach {
            it.draw(drawScope)
        }
    }

    open fun translate(offset: Offset) {
        path.translate(offset)
        points = points.map { it.plus(offset) }.toMutableList()
    }

    fun moveTo(x: Float, y: Float) {
        path.moveTo(x, y)
    }

    open fun<T : Shape> copy(factory: () -> T): T {
        val shapePath = factory()
        shapePath.shouldClose = shouldClose
        shapePath.isSelected = isSelected
        shapePath.setPoints(points)

        return shapePath
    }

    fun reset() {
        points.clear()
        path.reset()
    }

    fun close() {
        path.close()
    }

    @JvmName("setShapePoints")
    fun setPoints(points: List<Offset>) {
        this.points = points.toMutableList()
        createPath()

        if (shouldClose) {
            close()
        }
    }

    fun addPoint(point: Offset) {
        points.add(point)
        // recreate path to close with the new point
        setPoints(points)
    }

    fun setLastPoint(point: Offset) {
        if (points.isEmpty().not()) {
            points.removeLast()
        }

        addPoint(point)
    }

    open fun intersects(shape: Shape): Boolean {
        if (this.path.isEmpty || shape.path.isEmpty) {
            return false
        }

        // detect if one path intersects another path from outside,
        // but it still returns non-empty intersection if one path
        // is completely inside another path
        // (ie. we started drawing one shape inside another)
        val outsidePath = Path()
        outsidePath.op(this.path, shape.path, PathOperation.Intersect)
        val noIntersection = outsidePath.isEmpty

        if (noIntersection) {
            return false
        }

        // detect if one path is completely inside another path or vica versa
        val insidePath = Path()
        insidePath.op(this.path, shape.path, PathOperation.Difference)
        val isInside1 = insidePath.isEmpty

        insidePath.op(this.path, shape.path, PathOperation.ReverseDifference)
        val isInside2 = insidePath.isEmpty

        return isInside1.not() && isInside2.not()
    }

    fun containsPoint(offset: Offset): Boolean {
        val hitTestShape = Shape()
        hitTestShape.path.addOval(Rect(center = offset, radius = 20f))

        return intersects(hitTestShape)
    }

    protected open fun createPath() {
    }

    private fun updateHandles() {
        handles = listOf()

        if (isSelected.not()) {
            showHandles = false
        }

        if (isSelected && showHandles) {
            handles = points.map { HandleShape(it) }
        }
    }

    fun getHandleAtOffset(offset: Offset): HandleShape? {
        if (showHandles.not()) {
            return null
        }

        return handles.firstOrNull {
            it.containsPoint(offset)
        }
    }

    fun updateSelectedHandleIndexAtOffset(offset: Offset) {
        selectedHandleIndex = handles.indexOfFirst {
            it.containsPoint(offset)
        }
    }

    fun hasHandleAtOffset(offset: Offset): Boolean {
        return getHandleAtOffset(offset) != null
    }

    open fun resize(offset: Offset) {
    }

    fun endResizing() {
        selectedHandleIndex = INDEX_NOT_SET
    }

    // endregion

}

