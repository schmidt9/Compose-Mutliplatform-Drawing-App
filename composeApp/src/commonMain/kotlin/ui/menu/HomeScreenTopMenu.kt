package ui.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable

@Composable
fun HomeScreenTopMenu(onUndo: () -> Unit,
                      onRedo: () -> Unit) {
    Row {
        UndoRedoMenu(
            onUndo = {
                onUndo()
            },
            onRedo = {
                onRedo()
            })
    }
}