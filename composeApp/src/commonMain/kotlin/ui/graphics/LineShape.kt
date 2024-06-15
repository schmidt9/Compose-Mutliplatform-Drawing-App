package ui.graphics

import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

open class LineShape(point1: Point, point2: Point) : PolylineShape() {

    init {
        setPoints(listOf(
            point1,
            point2
        ))
    }

}