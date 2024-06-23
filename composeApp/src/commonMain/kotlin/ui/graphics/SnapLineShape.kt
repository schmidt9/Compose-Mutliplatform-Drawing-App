package ui.graphics

import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

class SnapLineShape(point1: Point, point2: Point) : LineShape(point1, point2) {

    override var properties: PathProperties = selectionPathProperties

}