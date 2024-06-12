package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import util.sameValueAs
import util.setX
import util.setY

class RectShape(rect: Rect = Rect.Zero) : PolylineShape() {

    override var shouldClose: Boolean = true

    init {
        setPoints(listOf(
            rect.topLeft,
            rect.bottomLeft,
            rect.bottomRight,
            rect.topRight
        ))
    }

    override fun resize(offset: Offset) {
        super.resize(offset)

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

        // find points

        var currPoint = points[selectedHandleIndex]
        var prevPoint = points[prevIndex]
        var nextPoint = points[nextIndex]

        // change vertical and horizontal point

        prevPoint = if (currPoint.x.sameValueAs(prevPoint.x)) {
            prevPoint.setX(offset)
        } else {
            prevPoint.setY(offset)
        }

        nextPoint = if (currPoint.x.sameValueAs(nextPoint.x)) {
            nextPoint.setX(offset)
        } else {
            nextPoint.setY(offset)
        }

        // update points

        currPoint = offset

        points[selectedHandleIndex] = currPoint
        points[prevIndex] = prevPoint
        points[nextIndex] = nextPoint

        setPoints(points)
    }

}