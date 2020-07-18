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
        val points = new Array[Vector3f]((array(0).subtract(array.last).length * Vals.ROAD_VERTEX_DENSITY).toInt * 4)
        val indices = new Array[Int](points.length * 9 / 2 - 6)
        val heightVector = new Vector3f(0, Vals.ROAD_HEIGHT, 0)
        for(p <- 0 until points.length by 4) {
            val t = 1.0f * p / (points.length - 4)
            val v = getPoint(t, array)
            val d = getDirection(t, array).normalize
            val n = p * 9 / 2
            points(p + 0)   = v.add(d.leftHand.scale(roadWidth / 2.0f))
            points(p + 1)   = v.add(d.rightHand.scale(roadWidth / 2.0f))
            points(p + 2)   = points(p + 0).add(heightVector)
            points(p + 3)   = points(p + 1).add(heightVector)

            if(p < points.length - 4) {
                indices(n + 0)  = p
                indices(n + 1)  = p + 2
                indices(n + 2)  = p + 4
                
                indices(n + 3)  = p + 2
                indices(n + 4 ) = p + 4
                indices(n + 5)  = p + 6
                
                indices(n + 6)   = p + 2
                indices(n + 7)   = p + 3
                indices(n + 8)   = p + 6
                
                indices(n + 9)   = p + 3
                indices(n + 10)  = p + 6
                indices(n + 11)  = p + 7
                
                indices(n + 12) = p + 1
                indices(n + 13) = p + 3
                indices(n + 14) = p + 7
                
                indices(n + 15) = p + 1
                indices(n + 16) = p + 5
                indices(n + 17) = p + 7
            } else {
                indices(n + 0)  = p
                indices(n + 1)  = p + 2
                indices(n + 2)  = p + 3

                indices(n + 3)  = p + 0
                indices(n + 4)  = p + 1
                indices(n + 5)  = p + 4

                indices(n + 6)  = 0
                indices(n + 7)  = 1
                indices(n + 8)  = 2

                indices(n + 9)  = 1
                indices(n + 10) = 2
                indices(n + 11) = 3
            }
        }
        (points, indices)
    }

}
