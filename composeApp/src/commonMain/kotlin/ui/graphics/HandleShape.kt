package ui.graphics

import androidx.compose.ui.geometry.Offset
import model.PathProperties
import model.handleProperties

class HandleShape(center: Offset) : CircleShape(center, 20f) {

    override var properties: PathProperties
        get() = PathProperties.handleProperties
        set(_) {}

}