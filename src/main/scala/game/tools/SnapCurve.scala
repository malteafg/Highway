package game.tools

import utils.math.Vec3

case class SnapCurve(selectedPos: Vec3, selectedDir: () => Vec3) extends State {

    // TODO set preview

    override def onLeftClick(cursorPos: Vec3): Unit = {
        // TODO place squishy segment
    }

    override def onNodeUnsnap(): Unit = Tools.back()

}
