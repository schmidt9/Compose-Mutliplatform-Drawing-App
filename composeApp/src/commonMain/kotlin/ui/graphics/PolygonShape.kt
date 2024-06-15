package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import extensions.setX
import extensions.setY
import model.PathProperties
import util.snapDistance
import kotlin.math.abs

class PolygonShape(firstPoint: Offset = Offset.Zero) : PolylineShape() {

    override var shouldClose: Boolean = true
    private var prevSnapLineShape: SnapLineShape? = null
    private var nextSnapLineShape: SnapLineShape? = null

    init {
        // init two points at once to be able to refer later the last point
        // which initially is the same as first
        setPoints(listOf(firstPoint, firstPoint))
    }

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        super.draw(drawScope, properties)
        prevSnapLineShape?.draw(drawScope)
        nextSnapLineShape?.draw(drawScope)
    }

    override fun resize(point: Point) {
        super.resize(point)

        prevSnapLineShape = null
        nextSnapLineShape = null

        if (selectedHandleIndex == INDEX_NOT_SET) {
            return
        }

        // find indices

        val prevIndex =
            if (selectedHandleIndex == 0) points.count() - 1
            else selectedHandleIndex - 1
        val nextIndex =
            if (selectedHandleIndex == points.count() - 1) 0
            else selectedHandleIndex + 1

        // snap current point to vertical or horizontal axis if needed

        var currPoint = point
        val prevPoint = points[prevIndex]
        val nextPoint = points[nextIndex]

        if (abs(currPoint.x - prevPoint.x) <= snapDistance) {
            currPoint = currPoint.setX(prevPoint)
            prevSnapLineShape = SnapLineShape(currPoint, prevPoint)
        } else if (abs(currPoint.y - prevPoint.y) <= snapDistance) {
            currPoint = currPoint.setY(prevPoint)
            prevSnapLineShape = SnapLineShape(currPoint, prevPoint)
        }

        if (abs(currPoint.x - nextPoint.x) <= snapDistance) {
            currPoint = currPoint.setX(nextPoint)
            nextSnapLineShape = SnapLineShape(currPoint, nextPoint)
        } else if (abs(currPoint.y - nextPoint.y) <= snapDistance) {
            currPoint = currPoint.setY(nextPoint)
            nextSnapLineShape = SnapLineShape(currPoint, nextPoint)
        }

        points[selectedHandleIndex] = currPoint

        setPoints(points)
    }

    override fun endResizing() {
        super.endResizing()
        prevSnapLineShape = null
        nextSnapLineShape = null
    }

    override fun <T : Shape> copy(factory: () -> T): T {
        val shape = super.copy(factory) as PolygonShape
        shape.shouldClose = shouldClose

        return shape as T
    }

}