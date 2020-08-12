package game.roads

import utils.Vals
import utils.graphics.Mesh
import utils.math.Vec3

class RoadSegment(startNode: RoadNode, endNode: RoadNode, startLaneMap: Array[Int], endLaneMap: Array[Int], noOfLanes: Int, controlPoints: Array[Vec3], path: Array[Vec3], var mesh: Mesh) {

    startNode.addOutgoingSegment(this)
    endNode.addIncomingSegment(this)

    // TODO generate lane markings

    private val lanes: Array[Lane] = new Array[Lane](noOfLanes)
    for (i <- 0 until noOfLanes) {
        val lanepath = new Array[Vec3](path.length)
        var dir = startNode.dir
        for (j <- lanepath.indices) {
            lanepath(j) = path(j).add(dir.rightHand.normalize.scale(Vals.LARGE_LANE_WIDTH * i - Vals.LARGE_LANE_WIDTH * (noOfLanes - 1) / 2))
            if (j < lanepath.length - 2) dir = path(j + 1).subtract(path(j))
            else dir = endNode.dir
        }
        lanes(i) = new Lane(startNode.getLaneNode(startLaneMap(i)), endNode.getLaneNode(endLaneMap(i)), lanepath)
    }

    def updateMesh(mesh: Mesh): Unit = {
        this.mesh.delete()
        this.mesh = mesh
    }

    def getMesh: Mesh = mesh
    def getControlPoints: Array[Vec3] = controlPoints

    def getStartNode: RoadNode = startNode
    def getEndNode: RoadNode = endNode

    def getLanes: Array[Lane] = lanes

    def length: Float = path.dropRight(1).zip(path.drop(1)).foldLeft(0: Float) {(length, point) =>
        length + point._1.subtract(point._2).length
    }

}