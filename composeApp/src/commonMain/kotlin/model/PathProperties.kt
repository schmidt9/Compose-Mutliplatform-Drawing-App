package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class PathProperties(
    var strokeWidth: Float = 10f,
    var color: Color = Color.Black,
    var alpha: Float = 1f,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var pathEffect: PathEffect? = null,
    var eraseMode: Boolean = false
) {

    companion object

    fun copy(
        strokeWidth: Float = this.strokeWidth,
        color: Color = this.color,
        alpha: Float = this.alpha,
        strokeCap: StrokeCap = this.strokeCap,
        strokeJoin: StrokeJoin = this.strokeJoin,
        eraseMode: Boolean = this.eraseMode
    ) = PathProperties(
        strokeWidth, color, alpha, strokeCap, strokeJoin,  eraseMode = eraseMode
    )

    fun copyFrom(properties: PathProperties) {
        this.strokeWidth = properties.strokeWidth
        this.color = properties.color
        this.strokeCap = properties.strokeCap
        this.strokeJoin = properties.strokeJoin
        this.eraseMode = properties.eraseMode
    }
}

val PathProperties.Companion.selectionPathProperties
    get() = PathProperties(
        strokeWidth = 5f,
        color = Color.Blue,
        alpha = 1f,
        strokeCap = StrokeCap.Round,
        strokeJoin = StrokeJoin.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f)),
        eraseMode = false
    )