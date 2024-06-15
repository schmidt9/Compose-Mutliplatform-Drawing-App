package ui.graphics

import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

class SnapLine(point1: Point, point2: Point) : PolylineShape() {

    init {
        setPoints(listOf(
            point1,
            point2
        ))
    }

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        super.draw(drawScope, selectionPathProperties)
    }

}