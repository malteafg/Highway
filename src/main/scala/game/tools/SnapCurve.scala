package game.tools

import game.roads.{RoadGenerator, RoadNode, RoadSegment}
import utils.math.Vec3

case class SnapCurve(selectedNode: RoadNode, snappedNode: RoadNode, opposite: Boolean, doubleCtrPts: Array[Array[Vec3]]) extends State {

    doubleCtrPts.foreach(ctrPts => roadMeshes.addOne(RoadGenerator.generateCurveMesh(ctrPts, Tools.getNoOfLanes)))

    override def onLeftClick(cursorPos: Vec3): Unit = {
        val startNode: RoadNode = if (opposite) snappedNode else selectedNode
        val endNode: RoadNode = if (opposite) selectedNode else snappedNode
        var firstNode: RoadNode = startNode
        var secondNode: RoadNode = null

        for(s <- 0 until doubleCtrPts.length) {
            if(s == doubleCtrPts.length - 1) secondNode = endNode
            else {
                secondNode = new RoadNode(doubleCtrPts(s)(2), doubleCtrPts(s)(1).subtract(doubleCtrPts(s)(2)).normalize, Tools.getNoOfLanes)
                game().addNode(secondNode)
            }
            val segment = new RoadSegment(firstNode, secondNode, null, doubleCtrPts(s), roadMeshes(s))
            firstNode.addOutgoingSegment(segment)
            secondNode.addIncomingSegment(segment)
            game().addSegment(segment)
            firstNode = secondNode
        }
    }
    Tools.resetStack()


    override def onRightClick(): Unit = Tools.resetStack()

    override def onNodeUnsnap(): Unit = Tools.back()

}
