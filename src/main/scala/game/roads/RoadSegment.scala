package game.roads

import utils.graphics.Mesh
import utils.math.Vec3

class RoadSegment(startNode: RoadNode, endNode: RoadNode, startLaneMap: Array[Int], endLaneMap: Array[Int], noOfLanes: Int, controlPoints: Array[Vec3], var mesh: Mesh) {

    startNode.addOutgoingSegment(this)
    endNode.addIncomingSegment(this)

    private val lanes: Array[Lane] = new Array[Lane](noOfLanes)
    for (i <- 0 until noOfLanes) lanes(i) = new Lane(startNode.getLaneNode(startLaneMap(i)), endNode.getLaneNode(endLaneMap(i)))

    def updateMesh(mesh: Mesh): Unit = {
        this.mesh.delete()
        this.mesh = mesh
    }

    def getMesh: Mesh = mesh
    def getControlPoints: Array[Vec3] = controlPoints

    def getStartNode: RoadNode = startNode
    def getEndNode: RoadNode = endNode

    def getLanes: Array[Lane] = lanes

}