package ui.graphics

import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

open class PolylineShape : Shape() {

    private var handles = listOf<HandleShape>()

    var showHandles = false

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

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        super.draw(drawScope, properties)

        updateHandles()

        handles.forEach {
            it.draw(drawScope)
        }
    }

    private fun updateHandles() {
        handles = listOf()

        if (isSelected.not()) {
            showHandles = false
        }

        if (isSelected && showHandles) {
            handles = points.map { HandleShape(it) }
        }
    }

}