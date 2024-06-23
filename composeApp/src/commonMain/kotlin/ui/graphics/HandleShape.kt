package ui.graphics

import androidx.compose.ui.geometry.Offset
import model.PathProperties
import model.handleProperties
import util.handleRadius

class HandleShape(center: Offset) : CircleShape(center, handleRadius) {

    override var properties: PathProperties = PathProperties.handleProperties

}