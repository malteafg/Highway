package game.tools

import game.Game
import game.roads.RoadNode
import game.terrain.TerrainLine
import game.tools.NodeSnapper.SnapPoint
import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

trait State {

    protected var roadMeshes = new mutable.ListBuffer[Mesh]()
    protected val guidelines = new mutable.ListBuffer[TerrainLine]()
    protected val game: () => Game = () => Tools.getGame

    // override functions if functionality is desired
    def onLeftClick(cursorPos: Vec3): Unit = ()
    def onRightClick(): Unit = ()
    def onMovement(cursorPos: Vec3): Unit = ()
    def onNodeSnap(snappedPoint: SnapPoint): Unit = ()
    def onNodeUnsnap(): Unit = ()
    def onModeSwitch(mode: Tools.Mode): Unit = ()

    final def getRoadMeshesToRender: mutable.ListBuffer[Mesh] = roadMeshes
    final def getGuidelinesToRender: List[TerrainLine] = guidelines.toList.::(Tools.getCursorMarker)

}
