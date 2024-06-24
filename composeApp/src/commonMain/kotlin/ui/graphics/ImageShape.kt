package ui.graphics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import model.PathProperties
import model.addImagePathProperties
import util.scaleSizeToSize
import kotlin.math.max
import kotlin.math.min

class ImageShape(rect: Rect = Rect.Zero) : RectShape(rect) {

    override var properties: PathProperties = PathProperties.addImagePathProperties

    var image by mutableStateOf<ImageBitmap?>(null)

    override fun draw(drawScope: DrawScope, properties: PathProperties) {
        super.draw(drawScope, properties)

        image?.let {
            val imageSize = scaleSizeToSize(Size(it.width.toFloat(), it.height.toFloat()), size)

            drawScope.drawImage(
                image = it,
                dstOffset = IntOffset(
                    points.first().x.toInt(),
                    points.first().y.toInt()
                ),
                dstSize = IntSize(imageSize.width.toInt(), imageSize.height.toInt())
            )
        }
    }

}