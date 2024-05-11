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

@Composable
fun ShapesMenu(
    visible: Boolean,
    modifier: Modifier = Modifier,
    selectedMenuAction: MenuAction,
    onIconClick: (menuButton: MenuButton) -> Unit
) {
    if (visible) {
        Box {
            Popup(
                alignment = Alignment.BottomCenter
            ) {
                Column {
                    Divider(color = Color.LightGray)

                    Row(modifier = modifier) {
                        val menuButtons = listOf(
                            MenuButton.DrawPolygonMenuButton,
                            MenuButton.DrawRectangleMenuButton
                        )

                        menuButtons.forEach {
                            SelectableIconButton(
                                onClick = {
                                    onIconClick(it)
                                },
                                painter = it.imagePainter,
                                selected = (it.menuAction == selectedMenuAction)
                            )
                        }

                    }

                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}