package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import model.PathProperties
import model.selectedPathProperties
import model.selectionPathProperties

open class ShapePath(var properties: PathProperties = PathProperties()) {

    val composePath = Path()

    val selectedPathProperties = PathProperties.selectedPathProperties

    val selectionPathProperties = PathProperties.selectionPathProperties

    private var points = mutableListOf<Offset>()

    var isSelected = false

    fun translate(offset: Offset) {
        composePath.translate(offset)
    }

    fun moveTo(x: Float, y: Float) {
        composePath.moveTo(x, y)
    }

    fun reset() {
        composePath.reset()
    }

    fun setPoints(points: List<Offset>) {
        this.points = points.toMutableList()
    }

    fun intersects(path: ShapePath): Boolean {
        val intersectionPath = Path()
        val success = intersectionPath.op(this.composePath, path.composePath, PathOperation.Intersect)

        return intersectionPath.isEmpty.not() && success
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

}

