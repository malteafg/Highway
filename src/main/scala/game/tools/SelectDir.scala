package game.tools

import game.terrain.TerrainLine
import utils.math.Vec3

case class SelectDir(selectedPos: Vec3) extends State {

    guidelines.addOne(new TerrainLine(() => selectedPos.xz, () => Tools.getCursorPos.xz, width = Tools.roadWidth))

    override def onLeftClick(cursorPos: Vec3): Unit = Tools.push(Preview(selectedPos, () => cursorPos.subtract(selectedPos).normalize, opposite = false, null))

    override def onRightClick(): Unit = Tools.back()

    override def onModeSwitch(mode: Tools.Mode): Unit = if (mode == Tools.Straight) Tools.back()

}
