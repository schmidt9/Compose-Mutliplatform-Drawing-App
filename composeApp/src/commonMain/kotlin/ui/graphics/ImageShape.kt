package ui.graphics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import model.PathProperties
import model.addImagePathProperties
import model.hidden
import model.selectedPathProperties
import util.scaleSizeToSize

class ImageShape(rect: Rect = Rect.Zero) : RectShape(rect) {

    var image by mutableStateOf<ImageBitmap?>(null)

    override var properties: PathProperties
        get() = if (image == null) PathProperties.addImagePathProperties
        else if (isSelected) PathProperties.selectedPathProperties
        else PathProperties.hidden
        set(_) {}

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        image?.let {
            drawScope.drawImage(
                image = it,
                dstOffset = IntOffset(
                    points.first().x.toInt(),
                    points.first().y.toInt()
                ),
                dstSize = IntSize(width.toInt(), height.toInt())
            )
        }

        super.draw(drawScope, properties)
    }

    override fun <T : Shape> copy(factory: () -> T): T {
        val copy = super.copy(factory) as ImageShape
        copy.image = image

        return copy as T
    }

    fun sizeToFitImage() {
        image?.let {
            val imageSize = scaleSizeToSize(Size(it.width.toFloat(), it.height.toFloat()), size)
            val origin = points.first()
            setPoints(listOf(
                origin, // top left
                Offset(origin.x, origin.y + imageSize.height), // bottom left
                Offset(origin.x + imageSize.width, origin.y + imageSize.height), // bottom right
                Offset(origin.x + imageSize.width, origin.y) // top right
            ))
        }
    }

}