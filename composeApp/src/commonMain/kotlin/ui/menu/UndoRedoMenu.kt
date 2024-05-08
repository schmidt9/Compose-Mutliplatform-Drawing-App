package ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun UndoRedoMenu(onUndo: () -> Unit,
                 onRedo: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
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
}