package ui.menu

import DrawMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.TouchApp
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
import composemutliplatformdrawingapp.composeapp.generated.resources.Res
import composemutliplatformdrawingapp.composeapp.generated.resources.ink_eraser_24dp_fill0_wght400_grad0_opsz24
import model.PathProperties
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.ColorWheel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onPathPropertiesChange: (PathProperties) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {

    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier
//            .background(getRandomColor())
        ,
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
                Icons.Filled.TouchApp,
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Touch) Color.Black else Color.LightGray
            )
        }
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                    DrawMode.Draw
                } else {
                    DrawMode.Erase
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ink_eraser_24dp_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = if (currentDrawMode == DrawMode.Erase) Color.Black else Color.LightGray
            )
        }

        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            ColorWheel(modifier = Modifier.size(24.dp))
        }

        IconButton(onClick = { showPropertiesDialog = !showPropertiesDialog }) {
            Icon(Icons.Filled.Brush, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(onClick = {
            onUndo()
        }) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = Color.LightGray)
        }

        IconButton(onClick = {
            onRedo()
        }) {
            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = null, tint = Color.LightGray)
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