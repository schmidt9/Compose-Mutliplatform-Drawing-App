package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import model.PathProperties
import model.selectedPathProperties
import model.selectionPathProperties

open class ShapePath(var properties: PathProperties = PathProperties()) {

    val composePath = Path()

    val selectedPathProperties = PathProperties.selectedPathProperties

    val selectionPathProperties = PathProperties.selectionPathProperties

    var isSelected = false

    fun translate(offset: Offset) {
        composePath.translate(offset)
    }

    fun moveTo(x: Float, y: Float) {
        composePath.moveTo(x, y)
    }

    fun addRect(rect: Rect) {
        composePath.addRect(rect)
    }

    fun reset() {
        composePath.reset()
    }

    fun intersects(path: ShapePath): Boolean {
        val intersectionPath = Path()
        val success = intersectionPath.op(this.composePath, path.composePath, PathOperation.Intersect)

        return intersectionPath.isEmpty.not() && success
    }

}

