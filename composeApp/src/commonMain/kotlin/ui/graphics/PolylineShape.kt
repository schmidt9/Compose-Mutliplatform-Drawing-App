package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties

open class PolylineShape : Shape() {

    private var handles = listOf<HandleShape>()

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

    override fun translate(offset: Offset) {
        super.translate(offset)


    }

    private fun updateHandles() {
        handles = listOf()

        if (isSelected) {
            handles = points.map { HandleShape(it) }
        }
    }

}