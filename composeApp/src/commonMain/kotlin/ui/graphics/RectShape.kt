package ui.graphics

import androidx.compose.ui.geometry.Rect

class RectShape(rect: Rect) : ShapePath() {

    override val shouldClose: Boolean
        get() = true

    init {
        setPoints(listOf(
            rect.topLeft,
            rect.bottomLeft,
            rect.bottomRight,
            rect.topRight
        ))
    }

}