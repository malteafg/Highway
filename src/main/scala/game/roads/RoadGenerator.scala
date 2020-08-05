package game.roads

import utils.{Bezier, Vals}
import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

object RoadGenerator {

    def generateStraightMesh(startPoint: Vec3, endPoint: Vec3, lanesNo: Int): (Mesh, Array[Vec3]) = {
        val dir = endPoint.subtract(startPoint).normalize

        val vertices = new Array[Vec3](8)
        roadCut(startPoint, dir, Vals.LARGE_LANE_WIDTH * lanesNo).foldLeft(0: Int) ((i, c) => {vertices(i) = c; i + 1})
        roadCut(endPoint, dir, Vals.LARGE_LANE_WIDTH * lanesNo).foldLeft(4: Int) ((i, c) => {vertices(i) = c; i + 1})

        val indices = generateIndices(2)
        val dist = endPoint.subtract(startPoint)
        (new Mesh(vertices, indices, Array(3)), Array(startPoint, startPoint.add(dist.scale(1f / 3f)), startPoint.add(dist.scale(2f / 3f)), endPoint))
    }

    def generateCircularMesh(pos: Vec3, dir: Vec3, point: Vec3, lanesNo: Int): (Mesh, Array[Vec3]) = {
        val controlPoints = Bezier.circleCurve(pos, dir, point)
        (generateCurveMesh(controlPoints, lanesNo), controlPoints)
    }

    def generateCurveMesh(controlPoints: Array[Vec3], lanesNo: Int): Mesh = {
        var numOfCuts = (controlPoints(0).subtract(controlPoints.last).length * Vals.ROAD_VERTEX_DENSITY).toInt + Vals.ROAD_VERTEX_MINIMUM
        if (numOfCuts < 2) numOfCuts = 2
        val vertices = new Array[Vec3](numOfCuts * 4)

        for(p <- vertices.indices by 4) {
            val cut = 1.0f * p / (vertices.length - 4)
            roadCut(
                Bezier.getPoint(cut, controlPoints),
                Bezier.getDirection(cut, controlPoints).normalize,
                Vals.LARGE_LANE_WIDTH  * lanesNo
            ).foldLeft(p) ((p, c) => {vertices(p) = c; p + 1})
        }

        val indices = generateIndices(numOfCuts)
        new Mesh(vertices, indices, Array(3))
    }

    private def roadCut(pos: Vec3, dir: Vec3, roadWidth: Float): Array[Vec3] = {
        val points = new Array[Vec3](4)
        val normDir = dir.normalize
        val heightVector = Vec3(0, Vals.ROAD_HEIGHT)
        val left    = pos.add(normDir.leftHand().scale(roadWidth / 2.0f))
        val right   = pos.add(normDir.rightHand().scale(roadWidth / 2.0f))
        points(0)   = left.add(normDir.leftHand().scale(Vals.ROAD_HEIGHT))
        points(1)   = right.add(normDir.rightHand().scale(Vals.ROAD_HEIGHT))
        points(2)   = right.add(heightVector)
        points(3)   = left.add(heightVector)
        points
    }

    private def generateIndices(numOfCuts: Int): Array[Int] = {
        indicesMap.getOrElse(numOfCuts, {
            // 6 for triangles at each end
            val indices = new Array[Int](6 + (numOfCuts - 1) * 6 * 3 + 6)

            indices(0) = 0
            indices(1) = 1
            indices(2) = 2
            indices(3) = 2
            indices(4) = 3
            indices(5) = 0

            for(i <- 0 until numOfCuts - 1) {
                indices(i * 18 + 6 + 0)  = i * 4 + 0
                indices(i * 18 + 6 + 1)  = i * 4 + 3 + 4
                indices(i * 18 + 6 + 2)  = i * 4 + 0 + 4
                indices(i * 18 + 6 + 3)  = i * 4 + 3 + 4
                indices(i * 18 + 6 + 4)  = i * 4 + 0
                indices(i * 18 + 6 + 5)  = i * 4 + 3

                indices(i * 18 + 6 + 6)  = i * 4 + 3
                indices(i * 18 + 6 + 7)  = i * 4 + 2 + 4
                indices(i * 18 + 6 + 8)  = i * 4 + 3 + 4
                indices(i * 18 + 6 + 9)  = i * 4 + 2 + 4
                indices(i * 18 + 6 + 10) = i * 4 + 3
                indices(i * 18 + 6 + 11) = i * 4 + 2

                indices(i * 18 + 6 + 12) = i * 4 + 2
                indices(i * 18 + 6 + 13) = i * 4 + 1 + 4
                indices(i * 18 + 6 + 14) = i * 4 + 2 + 4
                indices(i * 18 + 6 + 15) = i * 4 + 1 + 4
                indices(i * 18 + 6 + 16) = i * 4 + 2
                indices(i * 18 + 6 + 17) = i * 4 + 1
            }

            indices(indices.length - 6) = (numOfCuts - 1) * 4 + 0
            indices(indices.length - 5) = (numOfCuts - 1) * 4 + 2
            indices(indices.length - 4) = (numOfCuts - 1) * 4 + 1
            indices(indices.length - 3) = (numOfCuts - 1) * 4 + 2
            indices(indices.length - 2) = (numOfCuts - 1) * 4 + 0
            indices(indices.length - 1) = (numOfCuts - 1) * 4 + 3

            indicesMap.put(numOfCuts, indices)
            indices
        })
    }

    private val indicesMap = mutable.Map[Int, Array[Int]]()

}
