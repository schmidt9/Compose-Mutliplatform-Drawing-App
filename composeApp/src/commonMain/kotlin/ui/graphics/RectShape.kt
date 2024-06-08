package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import util.plusX
import util.plusY
import util.sameValueAs

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

        val currPoint = points[selectedHandleIndex]
        val prevPoint = points[prevIndex]
        val nextPoint = points[nextIndex]

        // change points

        if (prevPoint.x.sameValueAs(currPoint.x)) {
            prevPoint.plusY(offset)
        } else {
            prevPoint.plusX(offset)
        }

        if (nextPoint.x.sameValueAs(currPoint.x)) {
            nextPoint.plusY(offset)
        } else {
            nextPoint.plusX(offset)
        }

        currPoint.plus(offset)
    }

}