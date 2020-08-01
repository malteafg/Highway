package game.tools

import game.roads.RoadNode
import utils.math.Vec3

import scala.collection.mutable.ListBuffer

object NodeSnapper {

    private var nodes: ListBuffer[RoadNode] = _
    private var snappedNode: RoadNode = _

    def init(nodes: ListBuffer[RoadNode]): Unit = {
        this.nodes = nodes
    }

    def onMovement(cursorPos: Vec3): Unit = {
        var currentClosest: RoadNode = null
        var minDist = 1000f
        nodes.foreach(n => {
            val dist = n.position.subtract(cursorPos).length
            if (dist < n.getWidth / 2 && dist < minDist) {
                minDist = dist
                currentClosest = n
            }
        })
        if (currentClosest == null) {
            if (snappedNode != null) {
                Tools.current.onNodeUnsnap()
                snappedNode = null
            }
        } else {
            if (currentClosest != snappedNode) {
                snappedNode = currentClosest
                Tools.current.onNodeUnsnap()
                Tools.current.onNodeSnap(snappedNode, !snappedNode.isEndNode)
            }
        }
    }

    // for rendering purposes ONLY
    def getSnappedNode: RoadNode = snappedNode

}
