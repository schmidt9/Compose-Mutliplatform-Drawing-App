package ui.graphics

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import model.PathProperties
import model.addImagePathProperties

class ImageShape(rect: Rect = Rect.Zero,
                 val image: ImageBitmap? = null) : RectShape(rect) {

    override var properties: PathProperties = PathProperties.addImagePathProperties

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        super.draw(drawScope, properties)

        if (image != null) {
            drawScope.drawImage(
                image = image,
                topLeft = points.first()
            )
        }
    }

}