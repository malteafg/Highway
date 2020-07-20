package game.roads

import utils.{Bezier, Vals}
import utils.graphics.Mesh
import utils.math.Vector3f

// straight roads have empty controlpoints
class RoadSegment(startPoint: Node, endPoint: Node, controlPoints: Array[Vector3f], var mesh: Mesh) {

    /**
     * Constructors
     */
    // used for preview road
    def this(selectedPos: Vector3f) {
        this(new Node(new Vector3f(), new Vector3f()), new Node(new Vector3f(), new Vector3f()), new Array[Vector3f](0),
            RoadSegment.generateStraightMesh(selectedPos, selectedPos.add(new Vector3f(0, 0, 1f))))
    }

    def updateMesh(newMesh: Mesh): Unit = {
        mesh.delete()
        mesh = newMesh
    }
    def getMesh: Mesh = mesh

}

object RoadSegment {

    def generateStraightMesh(startPoint: Vector3f, endPoint: Vector3f): Mesh = {
        val dir = endPoint.subtract(startPoint).normalize

        val vertices = new Array[Vector3f](8)
        roadCut(startPoint, dir, Vals.LARGE_LANE_WIDTH).foldLeft(0: Int) ((i, c) => {vertices(i) = c; i + 1})
        roadCut(endPoint, dir, Vals.LARGE_LANE_WIDTH).foldLeft(4: Int) ((i, c) => {vertices(i) = c; i + 1})

        val indices = generateIndices(2)
        new Mesh(vertices, indices, Array(3))
    }

    def generateCurvedMesh(pos: Vector3f, dir: Vector3f, point: Vector3f): Mesh = {
        val controlPoints = Bezier.circleCurve(pos, dir, point)
        val numOfCuts = (controlPoints(0).subtract(controlPoints.last).length * Vals.ROAD_VERTEX_DENSITY).toInt
        val vertices = new Array[Vector3f](numOfCuts * 4)

        for(p <- 0 until vertices.length by 4) {
            val cut = 1.0f * p / (vertices.length - 4)
            roadCut(
                Bezier.getPoint(cut, controlPoints),
                Bezier.getDirection(cut, controlPoints).normalize,
                Vals.LARGE_LANE_WIDTH
            ).foldLeft(p) ((p, c) => {vertices(p) = c; p + 1})
        }

        val indices = generateIndices(numOfCuts)
        new Mesh(vertices, indices, Array(3))
    }

    private def roadCut(pos: Vector3f, dir: Vector3f, roadWidth: Float): Array[Vector3f] = {
        val points = new Array[Vector3f](4)
        dir.normalize
        val heightVector = new Vector3f(0, Vals.ROAD_HEIGHT, 0)
        val left    = pos.add(dir.leftHand().scale(roadWidth / 2.0f))
        val right   = pos.add(dir.rightHand().scale(roadWidth / 2.0f))
        points(0)   = left.add(dir.leftHand().scale(Vals.ROAD_HEIGHT))
        points(1)   = right.add(dir.rightHand().scale(Vals.ROAD_HEIGHT))
        points(2)   = right.add(heightVector)
        points(3)   = left.add(heightVector)
        points
    }

    private def generateIndices(numOfCuts: Int): Array[Int] = {
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
            indices(i * 18 + 6 + 1)  = i * 4 + 0 + 4
            indices(i * 18 + 6 + 2)  = i * 4 + 3 + 4
            indices(i * 18 + 6 + 3)  = i * 4 + 3 + 4
            indices(i * 18 + 6 + 4)  = i * 4 + 3
            indices(i * 18 + 6 + 5)  = i * 4 + 0

            indices(i * 18 + 6 + 6)  = i * 4 + 3
            indices(i * 18 + 6 + 7)  = i * 4 + 3 + 4
            indices(i * 18 + 6 + 8)  = i * 4 + 2 + 4
            indices(i * 18 + 6 + 9)  = i * 4 + 2 + 4
            indices(i * 18 + 6 + 10) = i * 4 + 2
            indices(i * 18 + 6 + 11) = i * 4 + 3

            indices(i * 18 + 6 + 12) = i * 4 + 2
            indices(i * 18 + 6 + 13) = i * 4 + 2 + 4
            indices(i * 18 + 6 + 14) = i * 4 + 1 + 4
            indices(i * 18 + 6 + 15) = i * 4 + 1 + 4
            indices(i * 18 + 6 + 16) = i * 4 + 1
            indices(i * 18 + 6 + 17) = i * 4 + 2
        }

        indices(indices.length - 6) = (numOfCuts - 1) * 4 + 0
        indices(indices.length - 5) = (numOfCuts - 1) * 4 + 1
        indices(indices.length - 4) = (numOfCuts - 1) * 4 + 2
        indices(indices.length - 3) = (numOfCuts - 1) * 4 + 2
        indices(indices.length - 2) = (numOfCuts - 1) * 4 + 3
        indices(indices.length - 1) = (numOfCuts - 1) * 4 + 0

        indices
    }

}
