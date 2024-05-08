package ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Popup
import composemutliplatformdrawingapp.composeapp.generated.resources.Res
import composemutliplatformdrawingapp.composeapp.generated.resources.pen_size_2_24dp_fill0_wght400_grad0_opsz24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShapesMenu(visible: Boolean,
               modifier: Modifier = Modifier,
               onIconClick: (shapeType: ShapeType) -> Unit,
               onPopupDismissRequest: () -> Unit) {
    if (visible) {
        Box {
            Popup(
                alignment = Alignment.BottomCenter,
                onDismissRequest = {
                    onPopupDismissRequest()
            }) {
                Column {
                    Divider(color = Color.LightGray)
                    Row(modifier = modifier) {
                        IconButton(
                            onClick = {
                                onIconClick(ShapeType.Line);
                            }) {
                            Icon(painter = painterResource(Res.drawable.pen_size_2_24dp_fill0_wght400_grad0_opsz24),
                                contentDescription = null,
                                tint = Color.LightGray)
                        }

                        IconButton(
                            onClick = {
                                onIconClick(ShapeType.Rectangle)
                            }) {
                            Icon(Icons.Outlined.Rectangle,
                                contentDescription = null,
                                tint = Color.LightGray)
                        }
                    }
                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}