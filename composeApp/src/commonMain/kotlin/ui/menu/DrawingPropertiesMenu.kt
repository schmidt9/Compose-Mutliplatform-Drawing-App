package ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.PathProperties
import ui.ColorWheel

@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    shapesMenuButton: MenuButton,
    shapesMenuButtonSelected: Boolean,
    onShapesMenuButtonClick: (MenuAction) -> Unit,
    selectionButtonSelected: Boolean,
    onSelectionButtonClick: (MenuAction) -> Unit
) {
    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            ColorWheel(modifier = Modifier.size(24.dp))
        }

        SelectableIconButton(
            onClick = {
                showPropertiesDialog = true
            },
            imageVector = Icons.Filled.Brush,
            selected = showPropertiesDialog
        )

        SelectableIconButton(
            onClick = {
                onShapesMenuButtonClick(shapesMenuButton.menuAction)
            },
            painter = shapesMenuButton.imagePainter,
            selected = shapesMenuButtonSelected
        )

        SelectableIconButton(
            onClick = {
                onSelectionButtonClick(MenuButton.DoSelectionMenuButton.menuAction)
            },
            painter = MenuButton.DoSelectionMenuButton.imagePainter,
            selected = selectionButtonSelected
        )

    }

    if (showColorDialog) {
        ColorSelectionDialog(
            properties.strokeColor,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.strokeColor = color
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = false
        }
    }
}