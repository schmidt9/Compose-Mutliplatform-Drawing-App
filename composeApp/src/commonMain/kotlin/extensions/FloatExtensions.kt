package extensions

import kotlin.math.abs

fun Float.sameValueAs(other: Float) = abs(this - other) < 0.0000000001