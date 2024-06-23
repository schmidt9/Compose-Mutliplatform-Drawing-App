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
fun SelectionActionsMenu(
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

                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            onButtonClick(MenuButton.DeleteSelectionMenuButton.menuAction)
                        }) {
                            Icon(
                                MenuButton.DeleteSelectionMenuButton.imagePainter,
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