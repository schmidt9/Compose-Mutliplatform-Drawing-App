package ui.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import composemutliplatformdrawingapp.composeapp.generated.resources.Res
import composemutliplatformdrawingapp.composeapp.generated.resources.polygon_svgrepo_com
import composemutliplatformdrawingapp.composeapp.generated.resources.select_24dp_fill0_wght400_grad0_opsz24
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
sealed class MenuButton( // TODO: rename
    val menuAction: MenuAction,
    private val drawableResource: DrawableResource? = null,
    private val imageVector: ImageVector? = null
) {

    val imagePainter: Painter
        @Composable
        get() {
            return if (drawableResource != null) {
                painterResource(drawableResource)
            } else {
                rememberVectorPainter(imageVector!!)
            }
        }

    data object DrawPolygonMenuButton : MenuButton(
        MenuAction.DrawPolygon,
        Res.drawable.polygon_svgrepo_com
    )

    data object DrawRectangleMenuButton : MenuButton(
        MenuAction.DrawRectangle,
        imageVector = Icons.Outlined.Rectangle
    )

    data object DoSelectionMenuButton : MenuButton(
        MenuAction.DoSelection,
        Res.drawable.select_24dp_fill0_wght400_grad0_opsz24
    )

    data object DeleteSelectionMenuButton : MenuButton(
        MenuAction.DeleteSelection,
        imageVector = Icons.Outlined.DeleteForever
    )

    data object PolygonApplyMenuButton : MenuButton(
        MenuAction.PolygonApply,
        imageVector = Icons.Outlined.Check
    )

    data object PolygonCancelMenuButton : MenuButton(
        MenuAction.PolygonCancel,
        imageVector = Icons.Outlined.Close
    )

}