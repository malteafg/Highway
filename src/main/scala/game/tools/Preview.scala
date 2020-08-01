package game.tools

import game.roads.{RoadNode, RoadSegment}
import game.terrain.TerrainLine
import input.InputHandler
import utils.{Bezier, Vals}
import utils.math.Vec3

case class Preview(selectedPos: Vec3, selectedDir: () => Vec3, opposite: Boolean, selectedNode: RoadNode) extends State {

    private var controlPoints: Array[Vec3] = _

    onMovement(Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f))

    if (Tools.getMode == Tools.Curved) addGuidelines()

    // place road
    override def onLeftClick(cursorPos: Vec3): Unit = {
        var startNode: RoadNode = null
        var endNode: RoadNode = null
        var end = true
        val allowedPos = Tools.getAllowedPos

        if (selectedNode == null) {
            startNode = new RoadNode(selectedPos, selectedDir(), Tools.getNoOfLanes)
            endNode = new RoadNode(allowedPos, controlPoints(3).subtract(controlPoints(2)).normalize, Tools.getNoOfLanes)
            game().addNode(startNode)
            game().addNode(endNode)
        } else {
            if (!opposite) {
                startNode = selectedNode
                endNode = new RoadNode(allowedPos, controlPoints(3).subtract(controlPoints(2)).normalize, Tools.getNoOfLanes)
                game().addNode(endNode)
            } else {
                controlPoints = controlPoints.reverse
                startNode = new RoadNode(allowedPos, controlPoints(2).subtract(controlPoints(3)).normalize, Tools.getNoOfLanes)
                endNode = selectedNode
                game().addNode(startNode)
                end = false
            }
        }

        val segment = new RoadSegment(startNode, endNode, null, controlPoints, roadMeshes.head)
        startNode.addOutgoingSegment(segment)
        endNode.addIncomingSegment(segment)
        game().addSegment(segment)

        val nodeToSnap = if (end) endNode else startNode
        Tools.replace(Preview(nodeToSnap.position, () => nodeToSnap.direction, opposite, nodeToSnap))
    }

    override def onRightClick(): Unit = {
        Tools.back()
        if (selectedNode != null) Tools.back()
        Tools.current.onModeSwitch(Tools.getMode)
    }

    // update preview
    override def onMovement(cursorPos: Vec3): Unit = {
        roadMeshes.clear()
        val dir = if (opposite) selectedDir().negate else selectedDir()
        var newPos: Vec3 = null
        Tools.getMode match {
            case Tools.Straight =>
                newPos = selectedPos.add(cursorPos.subtract(selectedPos).proj(dir)
                    .thisOrThat(v => v.dot(dir) > 0, dir.normalize.scale(Vals.MIN_SEGMENT_LENGTH)))
                if (newPos.subtract(selectedPos).length < Vals.MIN_SEGMENT_LENGTH)
                    newPos = selectedPos.add(newPos.subtract(selectedPos).normalize.scale(Vals.MIN_SEGMENT_LENGTH))
                val mesh = RoadSegment.generateStraightMesh(selectedPos, newPos, Tools.getNoOfLanes)
                roadMeshes.addOne(mesh._1)
                controlPoints = if (opposite) mesh._2.reverse else mesh._2
            case Tools.Curved =>
                newPos =
                    if (dir.dot(cursorPos.subtract(selectedPos)) >= 0) cursorPos
                    else cursorPos.subtract(cursorPos.subtract(selectedPos).proj(dir))
                val minDist = Math.max(Vals.MIN_SEGMENT_LENGTH, Vals.minRoadLength(dir, newPos.subtract(selectedPos), Tools.getNoOfLanes))
                if (newPos.subtract(selectedPos).length < minDist) newPos = selectedPos.add(newPos.subtract(selectedPos).rescale(minDist))
                val mesh = RoadSegment.generateCircularMesh(selectedPos, dir, newPos, Tools.getNoOfLanes)
                roadMeshes.addOne(mesh._1)
                controlPoints = if (opposite) mesh._2.reverse else mesh._2
            case _ =>
        }
        Tools.updateAllowedPos(newPos)
    }

    override def onNodeSnap(snappedNode: RoadNode, opposite: Boolean): Unit = if (selectedNode != null) {
        if (snappedNode.isEndNode != selectedNode.isEndNode) {
            val doubleCtrPts =
                if (opposite) Bezier.doubleSnapCurve(snappedNode.position, snappedNode.direction.negate, selectedNode.position, selectedNode.direction, Tools.getNoOfLanes)
                else Bezier.doubleSnapCurve(selectedNode.position, selectedNode.direction.negate, snappedNode.position, snappedNode.direction, Tools.getNoOfLanes)
            if (doubleCtrPts != null) Tools.push(SnapCurve(selectedNode, snappedNode, this.opposite, doubleCtrPts))
        }
    }

    override def onModeSwitch(mode: Tools.Mode): Unit = {
        mode match {
            case Tools.Straight => Tools.replace(Preview(selectedPos,
                if (selectedNode == null) () => Tools.getAllowedPos.subtract(selectedPos).normalize
                else selectedDir, opposite, selectedNode))
            case Tools.Curved =>
                if (selectedNode == null) Tools.back()
                else Tools.replace(Preview(selectedPos, selectedDir, opposite, selectedNode))
        }
        onMovement(Tools.getCursorPos)
    }

    def addGuidelines(): Unit = for (i <- controlPoints.indices.dropRight(1))
        guidelines.addOne(new TerrainLine(() => controlPoints(i).xz, () => controlPoints(i + 1).xz, Tools.roadWidth))

}
