package game.tools

import game.roads.{RoadGenerator, RoadNode, RoadSegment}
import game.tools.NodeSnapper.SnapPoint
import utils.math.Vec3

case class SnapCurve(selectedPoint: SnapPoint, snappedPoint: SnapPoint, opposite: Boolean, doubleCtrPts: Array[Array[Vec3]]) extends State {

    doubleCtrPts.foreach(ctrPts => roadMeshes.addOne(RoadGenerator.generateCurveMesh(ctrPts, Tools.getNoOfLanes)))

    override def onLeftClick(cursorPos: Vec3): Unit = {
        val startNode: RoadNode = if (opposite) snappedPoint.node else selectedPoint.node
        val endNode: RoadNode = if (opposite) selectedPoint.node else snappedPoint.node
        var firstNode: RoadNode = startNode
        var secondNode: RoadNode = null
        var firstLaneMap: Array[Int] = if (opposite) snappedPoint.laneNodes else selectedPoint.laneNodes
        var secondLaneMap: Array[Int] = RoadGenerator.laneMapping(Tools.getNoOfLanes)

        for(s <- doubleCtrPts.indices) {
            if(s == doubleCtrPts.length - 1) {
                secondNode = endNode
                secondLaneMap = if (opposite) selectedPoint.laneNodes else snappedPoint.laneNodes
            } else {
                secondNode = new RoadNode(doubleCtrPts(s)(doubleCtrPts(s).length - 1), doubleCtrPts(s)(doubleCtrPts(s).length - 2).subtract(doubleCtrPts(s)(doubleCtrPts(s).length - 1)).normalize, Tools.getNoOfLanes)
                game().addNode(secondNode)
            }
            val segment = new RoadSegment(firstNode, secondNode, firstLaneMap, secondLaneMap, Tools.getNoOfLanes, doubleCtrPts(s), roadMeshes(s)._2.reverse, roadMeshes(s)._1)
            game().addSegment(segment)
            firstNode = secondNode
            firstLaneMap = RoadGenerator.laneMapping(Tools.getNoOfLanes)
        }
        Tools.resetStack()
    }

    override def onRightClick(): Unit = Tools.resetStack()

    override def onNodeUnsnap(): Unit = Tools.back()

}
