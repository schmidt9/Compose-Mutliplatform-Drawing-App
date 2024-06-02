package ui.graphics

import androidx.compose.ui.geometry.Rect

class RectShape(rect: Rect) : PolylineShape() {

    override var shouldClose: Boolean = true

    init {
        setPoints(listOf(
            rect.topLeft,
            rect.bottomLeft,
            rect.bottomRight,
            rect.topRight
        ))
    }

}