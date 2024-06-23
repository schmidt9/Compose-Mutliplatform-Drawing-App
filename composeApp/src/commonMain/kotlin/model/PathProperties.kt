package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class PathProperties(
    var strokeWidth: Float = 10f,
    var strokeColor: Color = Color.Black,
    var fillColor: Color? = null,
    var alpha: Float = 1f,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var pathEffect: PathEffect? = null,
) {

    companion object

    fun copy(
        strokeWidth: Float = this.strokeWidth,
        strokeColor: Color = this.strokeColor,
        fillColor: Color? = this.fillColor,
        alpha: Float = this.alpha,
        strokeCap: StrokeCap = this.strokeCap,
        strokeJoin: StrokeJoin = this.strokeJoin
    ) = PathProperties(
        strokeWidth, strokeColor, fillColor, alpha, strokeCap, strokeJoin
    )

    fun copyFrom(properties: PathProperties) {
        this.strokeWidth = properties.strokeWidth
        this.strokeColor = properties.strokeColor
        this.strokeCap = properties.strokeCap
        this.strokeJoin = properties.strokeJoin
    }
}

val PathProperties.Companion.selectionPathProperties
    get() = PathProperties(
        strokeWidth = 5f,
        strokeColor = Color.Cyan,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f))
    )

val PathProperties.Companion.selectedPathProperties
    get() = PathProperties(
        strokeWidth = 20f,
        strokeColor = Color.Cyan,
    )

val PathProperties.Companion.addImagePathProperties
    get() = PathProperties(
        strokeWidth = 5f,
        strokeColor = Color.Red,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f))
    )

val PathProperties.Companion.handleProperties
    get() = PathProperties(
        strokeWidth = 2f,
        fillColor = Color.White.copy(alpha = 0.8f),
    )