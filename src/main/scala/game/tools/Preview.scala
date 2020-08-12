package game.tools

import game.Sphere
import game.roads.{RoadGenerator, RoadNode, RoadSegment}
import game.terrain.TerrainLine
import game.tools.NodeSnapper.SnapPoint
import input.InputHandler
import utils.{Bezier, Vals}
import utils.math.Vec3

case class Preview(selectedPos: Vec3, selectedDir: () => Vec3, opposite: Boolean, selectedPoint: SnapPoint) extends State {

    private var controlPoints: Array[Array[Vec3]] = _

    onMovement(Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f))

//    if (Tools.getMode == Tools.Curved) addGuidelines()

    // place road
    override def onLeftClick(cursorPos: Vec3): Unit = {
        var startNode: RoadNode = if (selectedPoint != null) selectedPoint.node else null
        var endNode: RoadNode = if (selectedPoint != null) selectedPoint.node else null

        if (opposite) {
            startNode = new RoadNode(controlPoints.head.head, controlPoints.head(1).subtract(controlPoints.head.head).normalize, Tools.getNoOfLanes)
            game().addNode(startNode)
        } else {
            if (selectedPoint == null) {
                startNode = new RoadNode(selectedPos, selectedDir(), Tools.getNoOfLanes)
                game().addNode(startNode)
            }
            endNode = new RoadNode(Tools.getAllowedPos, controlPoints.last.last.subtract(controlPoints.last(controlPoints.last.length - 2)).normalize, Tools.getNoOfLanes)
            game().addNode(endNode)
        }
        var firstNode: RoadNode = startNode
        var secondNode: RoadNode = null
        var firstLaneMap: Array[Int] = if (opposite || selectedPoint == null) RoadGenerator.laneMapping(Tools.getNoOfLanes) else selectedPoint.laneNodes
        var secondLaneMap: Array[Int] = RoadGenerator.laneMapping(Tools.getNoOfLanes)

        for(s <- controlPoints.indices) {
            if (s == controlPoints.length - 1) {
                secondNode = endNode
                if (opposite && selectedPoint != null) secondLaneMap = selectedPoint.laneNodes
            } else {
                secondNode = new RoadNode(controlPoints(s)(3), controlPoints(s)(3).subtract(controlPoints(s)(2)).normalize, Tools.getNoOfLanes)
                game().addNode(secondNode)
            }
            val segment = new RoadSegment(firstNode, secondNode, firstLaneMap, secondLaneMap, Tools.getNoOfLanes, controlPoints(s),
                if (opposite) roadMeshes(s)._2.reverse else roadMeshes(s)._2, roadMeshes(s)._1)

            firstNode = secondNode
            game().getSegments.foreach(s => {
                val pos = Bezier.roadCollision(f => Bezier.getPoint(f, segment.getControlPoints), f => Bezier.getPoint(f, s.getControlPoints), Tools.getNoOfLanes * Vals.LARGE_LANE_WIDTH)
                if (pos != null) game().spheres.addOne(new Sphere(pos))
            })
            game().addSegment(segment)
            firstLaneMap = RoadGenerator.laneMapping(Tools.getNoOfLanes)
        }
        val nodeToSnap = if (!opposite) endNode else startNode
        Tools.replace(Preview(nodeToSnap.pos, () => nodeToSnap.dir, opposite, SnapPoint(nodeToSnap, nodeToSnap.pos, nodeToSnap.dir, opposite, RoadGenerator.laneMapping(Tools.getNoOfLanes))))
    }

    override def onRightClick(): Unit = {
        Tools.back()
        if (selectedPoint != null) Tools.back()
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
                val mesh = RoadGenerator.generateStraightMesh(selectedPos, newPos, Tools.getNoOfLanes)
                roadMeshes.addOne(mesh._1, mesh._3)
                controlPoints = new Array(1)
                controlPoints(0) = if (opposite) mesh._2.reverse else mesh._2
            case Tools.Curved =>
                val dotProduct = dir.ndot(cursorPos.subtract(selectedPos))
                if (dotProduct >= 0) {
                    newPos = cursorPos
                    val minDist = Math.max(Vals.MIN_SEGMENT_LENGTH, Vals.minRoadLength(dir, newPos.subtract(selectedPos), Tools.getNoOfLanes))
                    if (newPos.subtract(selectedPos).length < minDist) newPos = selectedPos.add(newPos.subtract(selectedPos).rescale(minDist))
                    val mesh = RoadGenerator.generateCircularMesh(selectedPos, dir, newPos, Tools.getNoOfLanes)
                    roadMeshes.addOne(mesh._1, mesh._3)
                    controlPoints = new Array(1)
                    controlPoints(0) = if (opposite) mesh._2.reverse else mesh._2
                } else {
                    if(dotProduct >= -Vals.COS_45) newPos = cursorPos
                    else newPos = selectedPos.add(cursorPos.subtract(selectedPos).equalComponents(dir))
                    var midPoint = Bezier.curveMidPoint(selectedPos, dir, newPos)

                    val minDist = Math.max(Vals.MIN_SEGMENT_LENGTH, Vals.minRoadLength(dir, midPoint.subtract(selectedPos), Tools.getNoOfLanes))
                    if (midPoint.subtract(selectedPos).length < minDist) {
                        newPos = selectedPos.add(newPos.subtract(selectedPos).scale(minDist / midPoint.subtract(selectedPos).length))
                        midPoint = selectedPos.add(midPoint.subtract(selectedPos).rescale(minDist))
                    }
                    val mesh1 = RoadGenerator.generateCircularMesh(selectedPos, dir, midPoint, Tools.getNoOfLanes)
                    val mesh2 = RoadGenerator.generateCircularMesh(midPoint, newPos.subtract(selectedPos), newPos, Tools.getNoOfLanes)
                    roadMeshes.addOne(mesh1._1, mesh1._3)
                    roadMeshes.addOne(mesh2._1, mesh2._3)
                    controlPoints = new Array(2)
                    if(opposite) {
                        controlPoints(1) = if (opposite) mesh1._2.reverse else mesh1._2
                        controlPoints(0) = if (opposite) mesh2._2.reverse else mesh2._2
                    } else {
                        controlPoints(0) = if (opposite) mesh1._2.reverse else mesh1._2
                        controlPoints(1) = if (opposite) mesh2._2.reverse else mesh2._2
                    }
                }
            case _ =>
        }
        Tools.updateAllowedPos(newPos)
    }

    override def onNodeSnap(snappedPoint: SnapPoint): Unit = if (selectedPoint != null) {
        if (snappedPoint.opposite != selectedPoint.opposite && selectedPoint.node != snappedPoint.node) {
            val doubleCtrPts =
                if (opposite) Bezier.doubleSnapCurve(selectedPoint.pos, selectedPoint.dir.negate, snappedPoint.pos, snappedPoint.dir, Tools.getNoOfLanes)
                else Bezier.doubleSnapCurve(snappedPoint.pos, snappedPoint.dir.negate, selectedPoint.pos, selectedPoint.dir, Tools.getNoOfLanes)
            if (doubleCtrPts != null) Tools.push(SnapCurve(selectedPoint, snappedPoint, opposite, doubleCtrPts))
        }
    }

    override def onModeSwitch(mode: Tools.Mode): Unit = {
        mode match {
            case Tools.Straight => Tools.replace(Preview(selectedPos,
                if (selectedPoint == null) () => Tools.getAllowedPos.subtract(selectedPos).normalize
                else selectedDir, opposite, selectedPoint))
            case Tools.Curved =>
                if (selectedPoint == null) Tools.back()
                else Tools.replace(Preview(selectedPos, selectedDir, opposite, selectedPoint))
            case _ =>
        }
        onMovement(Tools.getCursorPos)
    }

    def addGuidelines(): Unit =  for (cpa <- controlPoints.indices) for (i <- controlPoints(cpa).indices.dropRight(1))
        guidelines.addOne(new TerrainLine(() => controlPoints(cpa)(i).xz, () => controlPoints(cpa)(i + 1).xz, Tools.roadWidth))

}
