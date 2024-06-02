package ui.graphics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import model.PathProperties
import model.selectedPathProperties
import model.selectionPathProperties
import kotlin.jvm.JvmName

open class Shape(var properties: PathProperties = PathProperties()) {

    enum class PropertiesType {
        Default,
        SelectedPath,
        SelectionPath
    }

    // region Properties

    private val path = Path()

    private val selectedPathProperties = PathProperties.selectedPathProperties

    val selectionPathProperties = PathProperties.selectionPathProperties

    val isEmpty get() = path.isEmpty

    open var shouldClose = false

    var isSelected by mutableStateOf(false)

    private var points = mutableListOf<Offset>()
        @JvmName("setShapePoints")
        set(value) {
            path.reset()

            if (value.isEmpty().not()) {
                path.moveTo(value.first())
            }

            value.forEach {
                path.lineTo(it)
            }

            field = value
        }

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

    fun draw(
        drawScope: DrawScope,
        properties: PathProperties
    ) {
        if (path.isEmpty) {
            return
        }

        drawScope.drawPath(
            path = path,
            color = properties.color,
            style = Stroke(
                width = properties.strokeWidth,
                cap = properties.strokeCap,
                join = properties.strokeJoin,
                pathEffect = properties.pathEffect
            )
        )
    }

    fun translate(offset: Offset) {
        path.translate(offset)
    }

    fun moveTo(x: Float, y: Float) {
        path.moveTo(x, y)
    }

    fun copy() : Shape {
        val shapePath = Shape()
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

    fun setPoints(points: List<Offset>) {
        this.points = points.toMutableList()

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

    fun getPoints() = points

    open fun intersects(path: Shape): Boolean {
        if (this.path.isEmpty || path.path.isEmpty) {
            return false
        }

        // detect if one path intersects another path from outside,
        // but it still returns non-empty intersection if one path
        // is completely inside another path
        // (ie. we started drawing one shape inside another)
        val outsidePath = Path()
        outsidePath.op(this.path, path.path, PathOperation.Intersect)
        val noIntersection = outsidePath.isEmpty

        if (noIntersection) {
            return false
        }

        // detect if one path is completely inside another path or vica versa
        val insidePath = Path()
        insidePath.op(this.path, path.path, PathOperation.Difference)
        val isInside1 = insidePath.isEmpty

        insidePath.op(this.path, path.path, PathOperation.ReverseDifference)
        val isInside2 = insidePath.isEmpty

        return isInside1.not() && isInside2.not()
    }

    // TODO: use for paths intersection or remove
    fun getPathPoints(path: Path): List<Offset> {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        val length = pathMeasure.length

        val step = 5f
        var i = 0f
        val points = mutableListOf<Offset>()

        do {
            // walking over path counterclockwise getting path points with given step (interval)
            val position = pathMeasure.getPosition(i)
            points.add(position)
            i += step
        } while (i < length)

        return points
    }

    // endregion

}

