package game.roads

import utils.{Bezier, Vals}
import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

// straight roads have empty controlpoints
class RoadSegment(startPoint: RoadNode, endPoint: RoadNode, var mesh: Mesh) {

    /**
     * Constructors
     */
    // used for preview road
    def this(selectedPos: Vec3) {
        this(new RoadNode(Vec3(), Vec3()), new RoadNode(Vec3(), Vec3()),
            RoadSegment.generateStraightMesh(selectedPos, selectedPos.add(Vec3(z = 1f))))
    }

    def updateMesh(newMesh: Mesh): Unit = {
        mesh.delete()
        mesh = newMesh
    }
    def getMesh: Mesh = mesh

}

object RoadSegment {

    def generateStraightMesh(startPoint: Vec3, endPoint: Vec3): Mesh = {
        val dir = endPoint.subtract(startPoint).normalize

        val vertices = new Array[Vec3](8)
        roadCut(startPoint, dir, Vals.LARGE_LANE_WIDTH).foldLeft(0: Int) ((i, c) => {vertices(i) = c; i + 1})
        roadCut(endPoint, dir, Vals.LARGE_LANE_WIDTH).foldLeft(4: Int) ((i, c) => {vertices(i) = c; i + 1})

        val indices = generateIndices(2)
        new Mesh(vertices, indices, Array(3))
    }

    def generateCurvedMesh(pos: Vec3, dir: Vec3, point: Vec3): (Mesh, Vec3) = {
        val controlPoints = Bezier.circleCurve(pos, dir, point)
        var numOfCuts = (controlPoints(0).subtract(controlPoints.last).length * Vals.ROAD_VERTEX_DENSITY).toInt + Vals.ROAD_VERTEX_MINIMUM
        if (numOfCuts < 2) numOfCuts = 2
        val vertices = new Array[Vec3](numOfCuts * 4)

        for(p <- 0 until vertices.length by 4) {
            val cut = 1.0f * p / (vertices.length - 4)
            roadCut(
                Bezier.getPoint(cut, controlPoints),
                Bezier.getDirection(cut, controlPoints).normalize,
                Vals.LARGE_LANE_WIDTH
            ).foldLeft(p) ((p, c) => {vertices(p) = c; p + 1})
        }

        val indices = generateIndices(numOfCuts)
        (new Mesh(vertices, indices, Array(3)), controlPoints(3).subtract(controlPoints(2)))
    }

    private def roadCut(pos: Vec3, dir: Vec3, roadWidth: Float): Array[Vec3] = {
        val points = new Array[Vec3](4)
        dir.normalize
        val heightVector = new Vec3(0, Vals.ROAD_HEIGHT, 0)
        val left    = pos.add(dir.leftHand().scale(roadWidth / 2.0f))
        val right   = pos.add(dir.rightHand().scale(roadWidth / 2.0f))
        points(0)   = left.add(dir.leftHand().scale(Vals.ROAD_HEIGHT))
        points(1)   = right.add(dir.rightHand().scale(Vals.ROAD_HEIGHT))
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
