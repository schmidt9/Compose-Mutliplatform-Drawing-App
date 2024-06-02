package ui.menu

import androidx.compose.foundation.layout.Arrangement
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
fun PolygonActionsMenu(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onButtonClick: (MenuAction) -> Unit
) {
    if (visible) {
        Box {
            Popup(
                alignment = Alignment.BottomCenter
            ) {
                Column {
                    Divider(color = Color.LightGray)

                    // apply action

                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            onButtonClick(MenuButton.PolygonApplyMenuButton.menuAction)
                        }) {
                            Icon(
                                MenuButton.PolygonApplyMenuButton.imagePainter,
                                contentDescription = null,
                                tint = Color.Blue
                            )
                        }

                        // cancel action

                        IconButton(onClick = {
                            onButtonClick(MenuButton.PolygonCancelMenuButton.menuAction)
                        }) {
                            Icon(
                                MenuButton.PolygonCancelMenuButton.imagePainter,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }

                    Divider(color = Color.LightGray)
                }
            }
        }
    }

}