package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

open class PolylineShape : Shape() {

    override fun createPath() {
        super.createPath()

        path.reset()

        if (points.isEmpty().not()) {
            path.moveTo(points.first())
        }

        points.forEach {
            path.lineTo(it)
        }
    }

}