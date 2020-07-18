package utils

import utils.math.Vector3f

object Bezier {

    def getPoint(t: Float, points: Array[Vector3f]) = {
        var v = new Vector3f()
        var r = Math.pow(1 - t, points.length - 1).toFloat
        var l = 1.0f
        var i = 0
        for (p <- points) {
            val f = l * r
            v = v.add(p.scale(f))
            if (t == 1.0f) {
                if (i == points.length - 2) r = 1
                else r = 0
            }
            else r *= t / (1 - t)
            l *= points.length / (1.0f + i) - 1.0f
            i += 1
        }
        v
    }

    def getDirection(t: Float, points: Array[Vector3f]) = {
        var v = new Vector3f()
        var r = Math.pow(1 - t, points.length - 1).toFloat
        var l = 1.0f
        var i = 0
        for (p <- 0 to (points.length - 2)) {
            val f = l * r
            v = v.add(points(p + 1).subtract(points(p)).scale(f))
            if (t == 1.0f) {
                if (i == points.length - 3) r = 1
                else r = 0
            }
            else r *= t / (1 - t)
            l *= points.length / (1.0f + i) - 1.0f
            i += 1
        }
        v.scale(points.length)
    }

    def curveRoad(v1: Vector3f, r: Vector3f, v2: Vector3f) = {
        val points = new Array[Vector3f](4)

        val ab = v2.subtract(v1)
        val d = ab.normalize.dot(r.normalize)
        val f = (2.0f / 3.0f * ab.length * (1.0f - d) / (r.length * (1.0f - d * d)))
        val R = r.scale(f)
        //val u = ab.normalize.add(r.normalize).divide(2.0f)

        points(0) = v1
        points(1) = v1.add(R)
        points(2) = v2.add(R).subtract(ab.scale(2.0f * ab.dot(R) / ab.dot(ab)))
        points(3) = v2

        points
    }

    def triangulate(array: Array[Vector3f], roadWidth: Float) = {
        val points = new Array[Vector3f]((array(0).subtract(array.last).length * Vals.ROAD_VERTEX_DENSITY).toInt * 2)
        val indices = new Array[Int](points.length * 3)
        for(p <- 0 until points.length by 2) {
            val t = 1.0f * p / (points.length - 2)
            val v = getPoint(t, array)
            val d = getDirection(t, array).normalize
            points(p)       = v.add(d.leftHand.scale(roadWidth / 2.0f))
            points(p + 1)   = v.add(d.rightHand.scale(roadWidth / 2.0f))

            if(p < points.length / 2) {
                indices(p * 3 + 0) = p
                indices(p * 3 + 1) = p + 1
                indices(p * 3 + 2) = p + 2
                indices(p * 3 + 3) = p + 1
                indices(p * 3 + 4) = p + 2
                indices(p * 3 + 5) = p + 3
            }
        }
        (points, indices)
    }

}
