package ui.graphics

import androidx.compose.ui.geometry.Rect

class RectShape(private val rect: Rect) : ShapePath() {

    init {
        composePath.addRect(rect)
        setPoints(listOf(
            rect.topLeft,
            rect.bottomLeft,
            rect.bottomRight,
            rect.topRight
        ))
    }

}