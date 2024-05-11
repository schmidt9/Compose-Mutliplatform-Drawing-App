package ui.menu

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun SelectableIconButton(
    onClick: () -> Unit,
    painter: Painter,
    selected: Boolean = false
) {
    IconButton(
        onClick = {
            onClick()
        }) {
        Icon(
            painter,
            contentDescription = null,
            tint = if (selected) Color.Black else Color.LightGray
        )
    }
}

@Composable
fun SelectableIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    selected: Boolean = false
) {
    SelectableIconButton(
        onClick = onClick,
        painter = rememberVectorPainter(imageVector),
        selected = selected)
}