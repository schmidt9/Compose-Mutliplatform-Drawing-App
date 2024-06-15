package ui.graphics

open class LineShape(point1: Point, point2: Point) : PolylineShape() {

    init {
        setPoints(listOf(
            point1,
            point2
        ))
    }

}