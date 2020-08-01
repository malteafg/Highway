package game.tools

import game.roads.{RoadNode, RoadSegment}
import utils.math.Vec3

case class SnapCurve(selectedNode: RoadNode, snappedNode: RoadNode, opposite: Boolean, doubleCtrPts: Array[Array[Vec3]]) extends State {

    doubleCtrPts.foreach(ctrPts => roadMeshes.addOne(RoadSegment.generateCurveMesh(ctrPts, Tools.getNoOfLanes)))

    override def onLeftClick(cursorPos: Vec3): Unit = {
        val startNode: RoadNode = if (opposite) snappedNode else selectedNode
        val endNode: RoadNode = if (opposite) selectedNode else snappedNode

        if (doubleCtrPts.length == 1) {
            val segment = new RoadSegment(startNode, endNode, null, doubleCtrPts(0), roadMeshes.head)
            startNode.addOutgoingSegment(segment)
            endNode.addIncomingSegment(segment)
            game().addSegment(segment)
        } else {
            val newNode = new RoadNode(doubleCtrPts(0)(3), doubleCtrPts(0)(2).subtract(doubleCtrPts(0)(3)).normalize, Tools.getNoOfLanes)
            game().addNode(newNode)
            val segment1 = new RoadSegment(startNode, newNode, null, doubleCtrPts(0), roadMeshes.head)
            val segment2 = new RoadSegment(newNode, endNode, null, doubleCtrPts(1), roadMeshes.last)
            newNode.addIncomingSegment(segment1)
            newNode.addOutgoingSegment(segment2)
            startNode.addOutgoingSegment(segment1)
            endNode.addIncomingSegment(segment2)
            game().addSegment(segment1)
            game().addSegment(segment2)
        }
    }

    override def onNodeUnsnap(): Unit = Tools.back()

}
