package ui.graphics

import androidx.compose.ui.geometry.Offset

class PolygonShape(firstPoint: Offset) : PolylineShape() {

    override var shouldClose: Boolean = true

    init {
        // init two points at once to be able to refer later the last point
        // which initially is the same as first
        setPoints(listOf(firstPoint, firstPoint))
    }

}