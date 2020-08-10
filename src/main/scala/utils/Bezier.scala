package utils

import utils.math.Vec3

object Bezier {

    def getPoint(t: Float, points: Array[Vec3]): Vec3 = {
        var v = Vec3()
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

    def getDirection(t: Float, points: Array[Vec3]): Vec3 = {
        var v = Vec3()
        var r = Math.pow(1 - t, points.length - 2).toFloat
        var l = 1.0f
        var i = 0
        for (p <- 0 to (points.length - 2)) {
            v = v.add(points(p + 1).subtract(points(p)).scale(l * r))
            if (t == 1.0f) {
                if (i == points.length - 3) r = 1
                else r = 0
            }
            else r *= t / (1 - t)
            l *= (points.length - 1) / (1.0f + i) - 1.0f
            i += 1
        }
        v.scale(points.length)
    }

    def getCurvature(points: Array[Vec3]): Float = {
        var c = 1f
        for(p <- 1 until points.length - 1) c *= points(p-1).subtract(points(p)).normalize.
                                                dot(points(p+1).subtract(points(p)).normalize)
        1f - Math.abs(c)
    }

    def getLanePaths(laneCount: Int, points: Array[Vec3]): Array[Array[Vec3]] = {
        val pointsPrLane = 3 + (getCurvature(points) * points.last.subtract(points(0)).length).toInt
        val paths = Array.ofDim[Vec3](laneCount, pointsPrLane)

        for(p <- 0 until pointsPrLane) {

        }
        paths
    }

    def circleCurve(pos1: Vec3, dir1: Vec3, pos2: Vec3): Array[Vec3] = {
        val points = new Array[Vec3](4)

        val ab = pos2.subtract(pos1)
        val d = ab.normalize.dot(dir1.normalize)
        val f = 2.0f / 3.0f * ab.length * (1.0f - d) / (dir1.length * (1.0f - d * d))
        val R = dir1.scale(f)

        points(0) = pos1
        points(1) = pos1.add(R)
        points(2) = pos2.add(R).subtract(ab.scale(2.0f * ab.dot(R) / ab.dot(ab)))
        points(3) = pos2

        points
    }

    def circleCurve(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3): Array[Vec3] = {
        val points = new Array[Vec3](4)

        val ab = pos2.subtract(pos1)
        val d = ab.normalize.dot(dir1.normalize)
        val f = 2.0f / 3.0f * ab.length * (1.0f - d) / (dir1.length * (1.0f - d * d))
        val R = dir1.scale(f)

        points(0) = pos1
        points(1) = pos1.add(R)
        points(2) = pos2.add(dir2.rescale(R.length))
        points(3) = pos2

        points
    }

    def curveCornerPoint(pos1: Vec3, dir: Vec3, pos2: Vec3): Vec3 = pos1.intersection(dir, pos2, dir.mirror(pos2.subtract(pos1)))

    def shortCurvePoints(pos1: Vec3, dir: Vec3, pos2: Vec3): Array[Vec3] = Array(pos1, curveCornerPoint(pos1, dir, pos2), pos2)

    def shortCurvePoints(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3): Array[Vec3] = Array(pos1, pos1.intersection(dir1, pos2, dir2), pos2)

    def curveMidPoint(pos1: Vec3, dir: Vec3, pos2: Vec3): Vec3 = {
        val deltaPos = pos2.subtract(pos1)
        val dir2 = dir.bisector(deltaPos)
        pos1.add(dir2.scale(deltaPos.dot(deltaPos) / 2.0f / dir2.dot(deltaPos)))
    }

    def arcSplit(pos1: Vec3, dir: Vec3, pos2: Vec3): Array[Array[Vec3]] = {
        var points: Array[Array[Vec3]] = null
        if(dir.ndot(pos2.subtract(pos1)) > Vals.COS_45) {
            points = new Array(1)
            points(0) = shortCurvePoints(pos1, dir, pos2)
        } else {
            points = new Array(2)
            val midPoint = curveMidPoint(pos1, dir, pos2)
            points(0) = shortCurvePoints(pos1, dir, midPoint)
            points(1) = shortCurvePoints(midPoint, pos2.subtract(pos1), pos2)
        }
        points
    }

    def arcSplit(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3): Array[Array[Vec3]] = {
        var points: Array[Array[Vec3]] = null
        if(dir1.ndot(pos2.subtract(pos1)) > Vals.COS_45) {
            points = new Array(1)
            points(0) = shortCurvePoints(pos1, dir1, pos2, dir2)
        } else {
            points = new Array(2)
            val midPoint = curveMidPoint(pos1, dir1, pos2)
            points(0) = shortCurvePoints(pos1, dir1, midPoint)
            points(1) = shortCurvePoints(midPoint, pos2.subtract(pos1), pos2, dir2)
        }
        points
    }

    def isEliptical(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3): Boolean = {
        val deltaPos = pos2.subtract(pos1)
        if(dir1.dot(dir2) > 0) return false
        if(deltaPos.negate.ndot(dir2) < Vals.PRETTY_CLOSE - 1 ||
           deltaPos.ndot(dir1) < Vals.PRETTY_CLOSE - 1) return false
        if(dir1.ortho(deltaPos).dot(dir2.ortho(deltaPos)) < 0) return false
        val intersection = pos1.intersection(dir1, pos2, dir2)
        val f1 = intersection.subtract(pos1).length
        val f2 = intersection.subtract(pos2).length
        val rel = Vals.rel(f1, f2)
        if(f1 * rel < Vals.MIN_SEGMENT_LENGTH || f2 * rel < Vals.MIN_SEGMENT_LENGTH) return false
        if(rel > Vals.CLOSE_ENOUGH) false
        else true
    }

    def doubleSnapCurve(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3, laneCount: Int): Array[Array[Vec3]] = {
        var points: Array[Array[Vec3]] = null

        if(dir1.mirror(pos2.subtract(pos1)).ndot(dir2) > Vals.PRETTY_CLOSE &&
          pos1.subtract(pos2).dot(dir2) >= Vals.PRETTY_CLOSE - 1 &&
          pos2.subtract(pos1).dot(dir1) >= Vals.PRETTY_CLOSE - 1) {
            points = new Array(1)
            points(0) = circleCurve(pos1, dir1, pos2, dir2)
        } else {
            val t = Vals.sCurveSegmentLength(pos1, dir1.normalize, pos2, dir2.normalize)
            val center = pos1.add(pos2).add(dir1.rescale(t)).add(dir2.rescale(t)).divide(2f)
            if(Vals.isCurveTooSmall(dir1, center.subtract(pos1), laneCount) || Vals.isCurveTooSmall(dir2, center.subtract(pos2), laneCount) ) return null
            if(dir1.dot(center.subtract(pos1)) < 0 || dir2.dot(center.subtract(pos2)) < 0) return null
            if(pos2.subtract(pos1).dot(center.subtract(pos1)) < 0 || pos1.subtract(pos2).dot(center.subtract(pos2)) < 0) return null
            if(isEliptical(pos1, dir1, pos2, dir2)) {
                points = new Array(1)
                points(0) = shortCurvePoints(pos1, dir1, pos2, dir2)
            } else {
                points = new Array(2)
                points(0) = circleCurve(pos1, dir1, center)
                points(1) = circleCurve(pos2, dir2, center).reverse
            }
        }

        points
    }

    def doubleSnapCurveAlternate(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3, laneCount: Int): Array[Array[Vec3]] = {
        var points: Array[Array[Vec3]] = null

        if (dir1.mirror(pos2.subtract(pos1)).ndot(dir2) > Vals.PRETTY_CLOSE &&
          pos1.subtract(pos2).dot(dir2) >= Vals.PRETTY_CLOSE - 1 &&
          pos2.subtract(pos1).dot(dir1) >= Vals.PRETTY_CLOSE - 1) {
            points = arcSplit(pos1: Vec3, dir1: Vec3, pos2: Vec3, dir2: Vec3)
        } else {
            val t = Vals.sCurveSegmentLength(pos1, dir1.normalize, pos2, dir2.normalize)
            val center = pos1.add(pos2).add(dir1.rescale(t)).add(dir2.rescale(t)).divide(2f)
            if (Vals.isCurveTooSmall(dir1, center.subtract(pos1), laneCount) || Vals.isCurveTooSmall(dir2, center.subtract(pos2), laneCount)) return null
            if (dir1.dot(center.subtract(pos1)) < 0 || dir2.dot(center.subtract(pos2)) < 0) return null
            if (pos2.subtract(pos1).dot(center.subtract(pos1)) < 0 || pos1.subtract(pos2).dot(center.subtract(pos2)) < 0) return null
            val curve1 = arcSplit(pos1, dir1, center)
            val curve2 = arcSplit(pos2, dir2, center)
            points = new Array(curve1.length + curve2.length)
            points(0) = curve1(0)
            if (curve1.length == 1) {
                if (curve2.length == 1) {
                    points(1) = curve2(0).reverse
                } else {
                    points(1) = curve2(1).reverse
                    points(2) = curve2(0).reverse
                }
            } else {
                points(1) = curve1(1)
                if (curve2.length == 1) {
                    points(2) = curve2(0).reverse
                } else {
                    points(2) = curve2(1).reverse
                    points(3) = curve2(0).reverse
                }
            }
        }
        points
    }

    def oldDoubleSnapCurve(v1: Vec3, r1: Vec3, v2: Vec3, r2: Vec3, laneCount: Int): Array[Vec3] = {
        val points = new Array[Vec3](4)

        if(r1.dot(v2.subtract(v1)) <= 0 || r2.dot(v1.subtract(v2)) <= 0) return null

        val intersection = v1.intersection(r1, v2, r2)
        if(intersection == null) return null

        val b1 = r1.dot(intersection.subtract(v1)) > 0
        val b2 = r2.dot(intersection.subtract(v2)) > 0

        if (!b1 && !b2) return null

        if(b1 && b2) {
            val minLength = r1.antiDot(r2) * Vals.LARGE_LANE_WIDTH * laneCount
            if(intersection.subtract(v1).length < minLength ||
                intersection.subtract(v2).length < minLength) return null
        }

        val ab = v2.subtract(v1)
        val dot1 = ab.normalize.dot(r1.normalize)
        val dot2 = ab.negate.normalize.dot(r2.normalize)
        val f1 = 2.0f / 3.0f * ab.length * (1.0f - dot1) / (1.0f - dot1 * dot1)
        val f2 = 2.0f / 3.0f * ab.length * (1.0f - dot2) / (1.0f - dot2 * dot2)

        points(0) = v1
        points(1) = v1.add(r1.rescale(f1))
        points(2) = v2.add(r2.rescale(f2))
        points(3) = v2

        points
    }

    def roadCollision(road1: Float => Vec3, road2: Float => Vec3, laneWidth: Float): Vec3 = {
        val values = Array(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)
        val points1 = values.map[Vec3](f => road1(f))
        val points2 = values.map[Vec3](f => road2(f))
        val (center1a, radius1a) = circleAround(points1)
        val (center2a, radius2a) = circleAround(points2)
        if(center1a.distTo(center2a) > radius1a + radius2a) return null
        for(p1 <- points1.indices.drop(1)) {
            for(p2 <- points2.indices.drop(1)) {
                if(points1(p1 - 1).isIntersecting(points1(p1), points2(p2 - 1), points2(p2)))
                    return points1(p1 - 1).intersection(points1(p1).subtract(points1(p1 - 1)), points2(p2 - 1), points2(p2).subtract(points2(p2 - 1)))
            }
        }
        null
    }

    def oldCollision(): Unit = {
//        var shortDist = Float.MaxValue
//        var n = 10
//        while(shortDist > laneWidth / 5f) {
//            val lastDist = shortDist
//            var dist = points(0).distTo(points(2))
//            var option = 0
//            if(dist < shortDist) shortDist = dist
//            dist = points(0).distTo(points(3))
//            if(dist < shortDist) {
//                shortDist = dist
//                option = 1
//            }
//            dist = points(1).distTo(points(2))
//            if(dist < shortDist) {
//                shortDist = dist
//                option = 2
//            }
//            dist = points(1).distTo(points(3))
//            if(dist < shortDist) {
//                shortDist = dist
//                option = 3
//            }
//            if(shortDist >= lastDist) n -= 0
//            else n = 10
//            if(n <= 0) return null
//            if(option <= 1) {
//                values(1) = (values(0) + values(1)) / 2f
//                points(1) = road1(values(1))
//            } else {
//                values(0) = (values(0) + values(1)) / 2f
//                points(0) = road1(values(0))
//            }
//            if(option % 2 == 0) {
//                values(3) = (values(2) + values(3)) / 2f
//                points(3) = road2(values(3))
//            } else {
//                values(2) = (values(2) + values(3)) / 2f
//                points(2) = road2(values(2))
//            }
//            println("loop")
//        }
    }

    def avg(f: Array[Vec3]): Vec3 = f.fold[Vec3](Vec3())((a: Vec3, b: Vec3) => a.add(b)).divide(1f * f.length)

    def circleAround(f: Array[Vec3]): (Vec3, Float) = {
        val center = avg(f)
        (center, f.foldLeft[Float](0.0f)((a: Float, b: Vec3) => Math.max(center.distTo(b), a)))
    }

}
