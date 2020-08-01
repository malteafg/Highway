package game.tools

import game.roads.RoadNode
import utils.math.Vec3

case class SnappedNode(snappedNode: RoadNode, opposite: Boolean) extends State {

    override def onLeftClick(cursorPos: Vec3): Unit = Tools.replace(Preview(snappedNode.position, () => snappedNode.direction, opposite, snappedNode))

    override def onNodeUnsnap(): Unit = Tools.back()

}
