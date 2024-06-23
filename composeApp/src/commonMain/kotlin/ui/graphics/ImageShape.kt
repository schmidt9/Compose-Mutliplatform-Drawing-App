package ui.graphics

import androidx.compose.ui.geometry.Rect
import model.PathProperties
import model.addImagePathProperties

class ImageShape(rect: Rect = Rect.Zero) : RectShape(rect) {

    override var properties: PathProperties = PathProperties.addImagePathProperties

}