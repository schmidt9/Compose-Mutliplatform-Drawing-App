package ui.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import composemutliplatformdrawingapp.composeapp.generated.resources.Res
import composemutliplatformdrawingapp.composeapp.generated.resources.pen_size_2_24dp_fill0_wght400_grad0_opsz24
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
sealed class IconData(val shapeType: ShapeType,
                      private val drawableResource: DrawableResource? = null,
                      private val imageVector: ImageVector? = null) {

    val imagePainter: Painter
        @Composable
        get() {
            return if (drawableResource != null) {
                painterResource(drawableResource)
            } else {
                rememberVectorPainter(imageVector!!)
            }
        }

    data object FreeformIcon : IconData(
        ShapeType.Freeform,
        imageVector = Icons.Default.Draw
    )

    data object LineIcon : IconData(
        ShapeType.Line,
        Res.drawable.pen_size_2_24dp_fill0_wght400_grad0_opsz24
    )

    data object RectangleIcon : IconData(
        ShapeType.Line,
        imageVector = Icons.Outlined.Rectangle
    )

}