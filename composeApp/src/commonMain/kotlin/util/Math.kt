package util

import androidx.compose.ui.geometry.Offset
import ui.graphics.Point
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * https://stackoverflow.com/a/29848387/3004003
 */
fun scalePoints(points: List<Point>, anchorPoint: Point, scaleFactor: Float): List<Point> {
    return points.map {
        // this is a vector that leads from mass center to current vertex
        val vec = Offset(
            it.x - anchorPoint.x,
            it.y - anchorPoint.y
        )
        return@map Offset(
            anchorPoint.x + scaleFactor * vec.x,
            anchorPoint.y + scaleFactor * vec.y
        )
    }
}

/**
 * Returns the distance from line segment AB to point C
 * https://stackoverflow.com/a/1079478/3004003
 */
fun getDistanceSegmentToPoint(a: Point, b: Point, c: Point): Float {
    val ab = sub(b, a)

    // Get point D by taking the projection of AC onto AB then adding the offset of A
    val d = getProjectionPoint(a, b, c)

    val ad = sub(d, a)
    // D might not be on AB so calculate k of D down AB (aka solve AD = k * AB)
    // We can use either component, but choose larger value to reduce the chance of dividing by zero
    val k = if (abs(ab.x) > abs(ab.y)) ad.x / ab.x else ad.y / ab.y

    // Check if D is off either end of the line segment
    if (k <= 0.0) {
        return sqrt(hypotenuse2(c, a))
    } else if (k >= 1.0) {
        return sqrt(hypotenuse2(c, b))
    }

    return sqrt(hypotenuse2(c, d))
}

/**
 * Returns projection point of C onto AB
 */
fun getProjectionPoint(a: Point, b: Point, c: Point): Point {
    // Compute vectors AC and AB
    val ac = sub(c, a)
    val ab = sub(b, a)

    return add(projection(ac, ab), a)
}

private fun add(a: Point, b: Point) = a.plus(b)

private fun sub(a: Point, b: Point) = a.minus(b)

private fun dot(a: Point, b: Point) = a.x * b.x + a.y * b.y

private fun hypotenuse2(a: Point, b: Point) = dot(sub(a, b), sub(a, b))

private fun projection(a: Point, b: Point): Point {
    val k = dot(a, b) / dot(b, b)
    return Offset(k * b.x, k * b.y)
}