package game.tools

import game.roads.{RoadNode, RoadSegment}
import game.terrain.TerrainLine
import utils.Vals
import utils.math.Vec3

case class Preview(selectedPos: Vec3, selectedDir: () => Vec3, opposite: Boolean, selectedNode: RoadNode) extends State {

    private var controlPoints: Array[Vec3] = _

    onMovement(selectedPos.add(selectedDir().normalize))

    if (Tools.getMode == Tools.Curved) addGuidelines() else guidelines.clear()

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
                startNode = new RoadNode(allowedPos, controlPoints(1).subtract(controlPoints(0)).normalize, Tools.getNoOfLanes)
                endNode = selectedNode
                game().addNode(startNode)
                end = false
            }
        }

        val segment = new RoadSegment(startNode, endNode, null, controlPoints, roadMesh)
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
        val dir = selectedDir()
        var newPos: Vec3 = null
        Tools.getMode match {
            case Tools.Straight =>
                newPos = selectedPos.add(cursorPos.subtract(selectedPos).proj(dir)
                    .thisOrThat(v => v.dot(dir) > 0, dir.normalize.scale(Vals.MIN_SEGMENT_LENGTH)))
                if (newPos.subtract(selectedPos).length < Vals.MIN_SEGMENT_LENGTH)
                    newPos = selectedPos.add(newPos.subtract(selectedPos).normalize.scale(Vals.MIN_SEGMENT_LENGTH))
                val mesh = RoadSegment.generateStraightMesh(selectedPos, newPos, Tools.getNoOfLanes)
                roadMesh = mesh._1
                controlPoints = if (opposite) mesh._2.reverse else mesh._2
            case Tools.Curved =>
                newPos =
                    if (dir.dot(cursorPos.subtract(selectedPos)) >= 0) cursorPos
                    else cursorPos.subtract(cursorPos.subtract(selectedPos).proj(dir))
                val minDist = Math.max(Vals.MIN_SEGMENT_LENGTH, Vals.minRoadLength(dir, newPos.subtract(selectedPos), Tools.getNoOfLanes))
                if (newPos.subtract(selectedPos).length < minDist) newPos = selectedPos.add(newPos.subtract(selectedPos).rescale(minDist))
                val mesh = RoadSegment.generateCircularMesh(selectedPos, dir, newPos, Tools.getNoOfLanes)
                roadMesh = mesh._1
                controlPoints = if (opposite) mesh._2.reverse else mesh._2
            case _ =>
        }
        Tools.updateAllowedPos(newPos)
    }

    override def onNodeSnap(snappedNode: RoadNode, opposite: Boolean): Unit = ()/*Tools.push(SnapCurve(selectedPos, selectedDir))*/

    override def onModeSwitch(mode: Tools.Mode): Unit = mode match {
        case Tools.Straight => guidelines.clear()
        case Tools.Curved => addGuidelines()
    }

    def addGuidelines(): Unit = for (i <- controlPoints.indices.dropRight(1))
        guidelines.addOne(new TerrainLine(() => controlPoints(i).xz, () => controlPoints(i + 1).xz, Tools.roadWidth))

}
