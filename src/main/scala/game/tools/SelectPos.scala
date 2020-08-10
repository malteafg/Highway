package game.tools

import game.tools.NodeSnapper.SnapPoint
import utils.math.Vec3

case class SelectPos() extends State {

    override def onLeftClick(cursorPos: Vec3): Unit = Tools.getMode match {
        case Tools.Straight =>
            Tools.push(SelectDir(cursorPos))
            Tools.push(Preview(cursorPos, () => Tools.getAllowedPos.subtract(cursorPos).normalize, opposite = false, null))
        case Tools.Curved => Tools.push(SelectDir(cursorPos))
    }

    override def onRightClick(): Unit = Tools.freeMode()

    override def onNodeSnap(snappedPoint: SnapPoint): Unit = Tools.push(SnappedNode(snappedPoint))

}
