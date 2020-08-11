package game.tools

import game.roads.RoadGenerator
import game.tools.NodeSnapper.SnapPoint
import utils.Vals
import utils.math.Vec3

case class SnappedNode(snappedPoint: SnapPoint) extends State {

    roadMeshes.addOne((RoadGenerator.generateStraightMesh(snappedPoint.pos, snappedPoint.pos.add(
        if (snappedPoint.opposite) snappedPoint.dir.negate.scale(Vals.MIN_SEGMENT_LENGTH)
        else snappedPoint.dir.scale(Vals.MIN_SEGMENT_LENGTH)), Tools.getNoOfLanes)._1, Array(Vec3())))

    override def onLeftClick(cursorPos: Vec3): Unit = {
        Tools.replace(SelectDir(snappedPoint.pos))
        Tools.push(Preview(snappedPoint.pos, () => snappedPoint.dir, snappedPoint.opposite, snappedPoint))
    }

    override def onNodeUnsnap(): Unit = Tools.back()

}
