package game.roads

import utils.Vals
import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

class RoadNode(var position: Vec3, var direction: Vec3, noOfLanes: Int) {

    private val incomingSegments = new mutable.ListBuffer[RoadSegment]
    private val outgoingSegments = new mutable.ListBuffer[RoadSegment]

    // Lane nodes
    private val noOfLaneNodes = noOfLanes + 2
    private val laneNodes = new Array[LaneNode](noOfLaneNodes)
    for (i <- laneNodes.indices) {
        laneNodes(i) = new LaneNode(
            position.add(direction.rightHand().normalize.scale(i * Vals.LARGE_LANE_WIDTH - (Vals.LARGE_LANE_WIDTH * noOfLaneNodes / 2f) + (Vals.LARGE_LANE_WIDTH / 2))),
            direction)
    }

    def addIncomingSegment(segment: RoadSegment): Unit = incomingSegments.addOne(segment)
    def addOutgoingSegment(segment: RoadSegment): Unit = outgoingSegments.addOne(segment)

    // TODO make smarter
    def isEndNode: Boolean = incomingSegments.length >= outgoingSegments.length

    def getLaneNodes: Array[LaneNode] = laneNodes

}

object RoadNode {

    val mesh: Mesh = Mesh.generateCircle(radius = 0.5f)

}
