package util

import androidx.compose.ui.geometry.Offset
import ui.graphics.Point
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Returns the distance from line segment AB to point C
 * https://stackoverflow.com/a/1079478/3004003
 */
fun getDistanceSegmentToPoint(a: Point, b: Point, c: Point): Float {
    // Compute vectors AC and AB
    val ac = sub(c, a)
    val ab = sub(b, a)

    // Get point D by taking the projection of AC onto AB then adding the offset of A
    val d = add(proj(ac, ab), a);

    val ad = sub(d, a);
    // D might not be on AB so calculate k of D down AB (aka solve AD = k * AB)
    // We can use either component, but choose larger value to reduce the chance of dividing by zero
    val k = if (abs(ab.x) > abs(ab.y)) ad.x / ab.x else ad.y / ab.y

    // Check if D is off either end of the line segment
    if (k <= 0.0) {
        return sqrt(hypot2(c, a));
    } else if (k >= 1.0) {
        return sqrt(hypot2(c, b));
    }

    return sqrt(hypot2(c, d));
}

fun add(a: Point, b: Point) = a.plus(b)

fun sub(a: Point, b: Point) = a.minus(b)

fun dot(a: Point, b: Point) = a.x * b.x + a.y * b.y

fun hypot2(a: Point, b: Point) = dot(sub(a, b), sub(a, b))

fun proj(a: Point, b: Point): Point {
    val k = dot(a, b) / dot(b, b)
    return Offset(k * b.x, k * b.y)
}