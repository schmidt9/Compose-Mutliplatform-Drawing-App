package ui.graphics

import extensions.lineTo
import extensions.moveTo

abstract class PolylineShape : Shape() {

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