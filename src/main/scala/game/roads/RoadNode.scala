package game.roads

import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

class RoadNode(var position: Vec3, var direction: Vec3, laneNodes: mutable.ListBuffer[LaneNode]) {

    private val incomingSegments = new mutable.ListBuffer[RoadSegment]
    private val outgoingSegments = new mutable.ListBuffer[RoadSegment]

    def addIncomingSegment(segment: RoadSegment): Unit = incomingSegments.addOne(segment)
    def addOutgoingSegment(segment: RoadSegment): Unit = outgoingSegments.addOne(segment)

}

object RoadNode {

    val mesh: Mesh = Mesh.generateCircle(radius = 1f)

}
