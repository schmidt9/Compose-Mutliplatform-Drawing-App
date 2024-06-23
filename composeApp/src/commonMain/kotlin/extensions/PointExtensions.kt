package extensions

import androidx.compose.ui.geometry.Offset
import ui.graphics.Point

fun Point.setX(other: Point) = Offset(other.x, y)
fun Point.setY(other: Point) = Offset(x, other.y)