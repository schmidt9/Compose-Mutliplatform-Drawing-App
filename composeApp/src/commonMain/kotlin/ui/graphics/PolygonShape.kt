package ui.graphics

import androidx.compose.ui.geometry.Offset

class PolygonShape(firstPoint: Offset = Offset.Zero) : PolylineShape() {

    override var shouldClose: Boolean = true

    init {
        // init two points at once to be able to refer later the last point
        // which initially is the same as first
        setPoints(listOf(firstPoint, firstPoint))
    }

    override fun resize(point: Point) {
        super.resize(point)

        if (selectedHandleIndex == INDEX_NOT_SET) {
            return
        }

        points[selectedHandleIndex] = point

        setPoints(points)
    }

    override fun <T : Shape> copy(factory: () -> T): T {
        val shape = super.copy(factory) as PolygonShape
        shape.shouldClose = shouldClose

        return shape as T
    }

}