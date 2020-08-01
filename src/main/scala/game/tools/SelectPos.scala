package game.tools

import game.roads.RoadNode
import utils.math.Vec3

case class SelectPos() extends State {

    override def onLeftClick(cursorPos: Vec3): Unit = Tools.getMode match {
        case Tools.Straight => Tools.push(Preview(cursorPos, () => Tools.getAllowedPos.subtract(cursorPos).normalize, opposite = false, null))
        case Tools.Curved => Tools.push(SelectDir(cursorPos))
    }

    override def onRightClick(): Unit = Tools.freeMode()

    override def onNodeSnap(snappedNode: RoadNode, opposite: Boolean): Unit = Tools.push(SnappedNode(snappedNode, opposite))

}
