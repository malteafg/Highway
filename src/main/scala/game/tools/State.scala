package game.tools

import game.Game
import game.roads.RoadNode
import game.terrain.TerrainLine
import utils.graphics.Mesh
import utils.math.Vec3

import scala.collection.mutable

trait State {

    protected var roadMesh: Mesh = _
    protected val guidelines = new mutable.ListBuffer[TerrainLine]()
    protected val game: () => Game = () => Tools.getGame

    // override functions if functionality is desired
    def onLeftClick(cursorPos: Vec3): Unit = ()
    def onRightClick(): Unit = ()
    def onMovement(cursorPos: Vec3): Unit = ()
    def onNodeSnap(snappedNode: RoadNode, opposite: Boolean): Unit = ()
    def onNodeUnsnap(): Unit = ()
    def onModeSwitch(mode: Tools.Mode): Unit = ()

    final def getRoadMeshToRender: Mesh = roadMesh
    final def getGuidelinesToRender: List[TerrainLine] = guidelines.toList.::(Tools.getCursorMarker)

}
