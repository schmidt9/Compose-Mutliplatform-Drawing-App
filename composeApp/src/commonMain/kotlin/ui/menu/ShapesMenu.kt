package ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun ShapesMenu(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onIconClick: (iconData: IconData) -> Unit
) {
    if (visible) {
        Box {
            Popup(
                alignment = Alignment.BottomCenter
            ) {
                Column {
                    Divider(color = Color.LightGray)
                    Row(modifier = modifier) {
                        val freeformIcon = IconData.FreeformIcon
                        val lineIcon = IconData.LineIcon
                        val rectangleIcon = IconData.RectangleIcon

                        IconButton(
                            onClick = {
                                onIconClick(freeformIcon);
                            }) {
                            Icon(
                                freeformIcon.imagePainter,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }

                        IconButton(
                            onClick = {
                                onIconClick(lineIcon);
                            }) {
                            Icon(
                                lineIcon.imagePainter,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }

                        IconButton(
                            onClick = {
                                onIconClick(rectangleIcon)
                            }) {
                            Icon(
                                rectangleIcon.imagePainter,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    }
                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}