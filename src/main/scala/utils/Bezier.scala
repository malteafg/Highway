package utils

import utils.math.Vec3

object Bezier {

    def getPoint(t: Float, points: Array[Vec3]) = {
        var v = new Vec3()
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

    def getDirection(t: Float, points: Array[Vec3]) = {
        var v = new Vec3()
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

    def circleCurve(v1: Vec3, r: Vec3, v2: Vec3): Array[Vec3] = {
        val points = new Array[Vec3](4)

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

    def circleCurve(v1: Vec3, r1: Vec3, v2: Vec3, r2: Vec3): Array[Vec3] = {
        val points = new Array[Vec3](4)

        val ab = v2.subtract(v1)
        val dot1 = ab.normalize.dot(r1.normalize)
        val dot2 = ab.normalize.dot(r2.normalize)
        val f1 = 2.0f / 3.0f * ab.length * (1.0f - dot1) / (1.0f - dot1 * dot1)
        val f2 = 2.0f / 3.0f * ab.length * (1.0f - dot2) / (1.0f - dot2 * dot2)
        //val u = ab.normalize.add(r.normalize).divide(2.0f)

        points(0) = v1
        points(1) = v1.add(r1.reScale(f1))
        points(2) = v2.add(R).subtract(ab.scale(2.0f * ab.dot(R) / ab.dot(ab)))
        points(3) = v2

        points
    }

}
