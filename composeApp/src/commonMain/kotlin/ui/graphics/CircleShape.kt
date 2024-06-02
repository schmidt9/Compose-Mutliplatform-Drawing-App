package ui.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

open class CircleShape(center: Offset, private val radius: Float) : Shape() {

    init {
        setPoints(listOf(center))
    }

    override fun createPath() {
        super.createPath()

        path.reset()
        val center = points.firstOrNull() ?: Offset.Zero
        path.addOval(Rect(center = center, radius = radius))
    }

}