package ui.menu

import DrawMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PanTool
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.ColorWheel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    shapeMenuButtonAction: MenuAction,
    onShapesIconClick: () -> Unit,
    onSelectionIconClick: (menuAction: MenuAction) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {
    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode
    var doSelection by remember { mutableStateOf(false) }
    val shapeMenuButton = listOf(
        MenuButton.DrawPolygonMenuButton,
        MenuButton.DrawRectangleMenuButton
    ).firstOrNull { it.menuAction == shapeMenuButtonAction } ?: MenuButton.DrawRectangleMenuButton

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                    DrawMode.Draw
                } else {
                    DrawMode.Touch
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                Icons.Filled.PanTool,
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Touch) Color.Black else Color.LightGray
            )
        }

        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            ColorWheel(modifier = Modifier.size(24.dp))
        }

        IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
            Icon(Icons.Filled.Brush, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(
            onClick = {
                onShapesIconClick()
            }) {
            Icon(
                shapeMenuButton.imagePainter,
                contentDescription = null,
                tint = Color.LightGray
            )
        }

        IconButton(
            onClick = {
                onSelectionIconClick(MenuButton.DoSelectionMenuButton.menuAction)
                doSelection = !doSelection
            }) {
            Icon(
                MenuButton.DoSelectionMenuButton.imagePainter,
                contentDescription = null,
                tint = if (doSelection) Color.Black else Color.LightGray
            )
        }
    }

    if (showColorDialog) {
        ColorSelectionDialog(
            properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.color = color
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = !showPropertiesDialog
        }
    }
}