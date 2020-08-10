package game.tools

import game.roads.RoadNode
import utils.Vals
import utils.math.Vec3

import scala.collection.mutable.ListBuffer

object NodeSnapper {

    private var nodes: ListBuffer[RoadNode] = _
    private var snappedNode: RoadNode = _
    private var snappedPoint: SnapPoint = _
    private var snapMap: ListBuffer[SnapPoint] = _

    def init(nodes: ListBuffer[RoadNode]): Unit = {
        this.nodes = nodes
    }

    def onMovement(cursorPos: Vec3): Unit = {
        var currentClosest: RoadNode = null
        var minDist = 1000f
        nodes.foreach(n => {
            val dist = n.pos.subtract(cursorPos).length
            if (dist < n.getWidth / 2 + Vals.LARGE_LANE_WIDTH && dist < minDist) {
                minDist = dist
                currentClosest = n
            }
        })
        if (currentClosest == null) {
            if (snappedNode != null) unsnap()
        } else if (currentClosest != snappedNode) {
            snappedNode = currentClosest
            calculateSnapMap(Tools.getNoOfLanes, snappedNode)
            if (snapMap == null) unsnap()
        }
        // TODO give feedback to user
        if (snappedNode != null && snapMap != null) {
            var closestSnapPoint: SnapPoint = snapMap.head
            minDist = closestSnapPoint.pos.subtract(cursorPos).length
            snapMap.drop(1).foreach(s => {
                val dist = s.pos.subtract(cursorPos).length
                if (dist <= minDist) {
                    if (closestSnapPoint.pos == s.pos && dist == minDist) {
                        if (snappedNode.dir.xz.angle(cursorPos.xz.subtract(closestSnapPoint.pos.xz).normalize) > Math.PI / 2) {
                            if (s.opposite) closestSnapPoint = s
                        }
                    } else {
                        minDist = dist
                        closestSnapPoint = s
                    }
                }
            })
            if (closestSnapPoint != snappedPoint) {
                Tools.current.onNodeUnsnap()
                snappedPoint = closestSnapPoint
                Tools.current.onNodeSnap(closestSnapPoint)
            }
        }
    }

    private def unsnap(): Unit = {
        Tools.current.onNodeUnsnap()
        snappedNode = null
        snappedPoint = null
    }

    def reset(cursorPos: Vec3): Unit = {
        snappedNode = null
        snappedPoint = null
        onMovement(cursorPos)
    }

    private def calculateSnapMap(noOfLanes: Int, snappedNode: RoadNode): Unit = {
        val snapMap = new ListBuffer[SnapPoint]

        for (i <- snappedNode.getLaneNodes.indices.dropRight(noOfLanes - 1)) {
            val asStart = new ListBuffer[Int]
            val asEnd = new ListBuffer[Int]
            var startPos = Vec3()
            var endPos = Vec3()
            for (l <- 0 until noOfLanes) {
                val laneNode = snappedNode.getLaneNode(i + l)
                val map = laneNode.map
                if (map._1) {
                    asStart.addOne(i + l)
                    startPos = startPos.add(laneNode.pos)
                }
                if (map._2) {
                    asEnd.addOne(i + l)
                    endPos = endPos.add(laneNode.pos)
                }
            }
            if (asStart.length == noOfLanes) snapMap.addOne(SnapPoint(snappedNode, startPos.divide(noOfLanes), snappedNode.dir, opposite = false, asStart.toArray))
            if (asEnd.length == noOfLanes) snapMap.addOne(SnapPoint(snappedNode, endPos.divide(noOfLanes), snappedNode.dir, opposite = true, asEnd.toArray))
        }

        this.snapMap = if (snapMap.isEmpty) null else snapMap
    }

    case class SnapPoint(node: RoadNode, pos: Vec3, dir: Vec3, opposite: Boolean, laneNodes: Array[Int])

    // for rendering purposes ONLY
    def getSnappedNode: RoadNode = snappedNode

}
