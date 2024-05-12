package ui.graphics

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

open class ShapePath(var properties: PathProperties = PathProperties()) {

    enum class PropertiesType {
        Default,
        SelectedPath,
        SelectionPath
    }

    // region Properties

    private val composePath = Path()

    private val selectedPathProperties = PathProperties.selectedPathProperties

    val selectionPathProperties = PathProperties.selectionPathProperties

    val isEmpty get() = composePath.isEmpty

    open val shouldClose = false

    var isSelected = false

    private var points = mutableListOf<Offset>()
        @JvmName("setShapePoints")
        set(value) {
            composePath.reset()

            if (value.isEmpty().not()) {
                composePath.moveTo(value.first())
            }

            value.forEach {
                composePath.lineTo(it)
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
        drawScope.drawPath(
            path = composePath,
            color = properties.color,
            style = Stroke(
                width = properties.strokeWidth,
                cap = properties.strokeCap,
                join = properties.strokeJoin
            )
        )
    }

    fun translate(offset: Offset) {
        composePath.translate(offset)
    }

    fun moveTo(x: Float, y: Float) {
        composePath.moveTo(x, y)
    }

    fun reset() {
        composePath.reset()
    }

    fun close() {
        composePath.close()
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

        println("POINTS $points")
    }

    fun getPoints() = points

    open fun intersects(path: ShapePath): Boolean {
        if (composePath.isEmpty || path.composePath.isEmpty) {
            return false
        }

        // detect if one path intersects another path from outside,
        // but it still returns non-empty intersection if one path
        // is completely inside another path
        // (ie. we started drawing one shape inside another)
        val outsidePath = Path()
        outsidePath.op(composePath, path.composePath, PathOperation.Intersect)
        val noIntersection = outsidePath.isEmpty

        if (noIntersection) {
            return false
        }

        // detect if one path is completely inside another path or vica versa
        val insidePath = Path()
        insidePath.op(composePath, path.composePath, PathOperation.Difference)
        val isInside1 = insidePath.isEmpty

        insidePath.op(composePath, path.composePath, PathOperation.ReverseDifference)
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

