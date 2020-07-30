package game

import game.roads.{RoadNode, RoadSegment}
import game.terrain.TerrainLine
import input.{Feedback, InputEvent, InputHandler, Mouse}
import rendering.{Camera, GameRenderer}
import utils.{Bezier, Vals}
import utils.graphics.Mesh
import utils.math.{Vec2, Vec3, Vec4}

import scala.collection.mutable

object GameHandler {

    var game: Game = _
    val camera = new Camera
    val terrainCollisionFunc: () => Vec3 = () => Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)

    def init(): Unit = {
        newGame()
    }

    def newGame(): Unit = {
        game = new Game()
    }

    def update(): Unit = {
        camera.update()
    }
    
    def render(): Unit = {
        if(game != null) GameRenderer.render(game, camera)
    }

    /**
     * Input
     */
    InputHandler.addMousePressSub(event => mode match {
        case Free =>
            event match {
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    game.spheres.addOne(new Sphere(terrainCollisionFunc()))
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case StraightRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (!hasSelPos) {
                        if (snappedNode == null) startRoadBuild()
                        else selectSnapNode(snappedNode)
                    }
                    else createRoad()
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) stopPreview()
                    else {
                        if (!hasSelPos) freeMode()
                        else stopPreview()
                    }
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case CurveRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (!hasSelPos) {
                        if (snappedNode == null) startRoadBuild()
                        else selectSnapNode(snappedNode)
                    }
                    else if (!hasSelDir) {
                        selectedDir = newNodePos.subtract(selectedPos)
                        hasSelDir = true
                    }
                    else createRoad()
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) stopPreview()
                    else {
                        if (!hasSelPos) freeMode()
                        else if (!hasSelDir) stopPreview()
                        else {
                            selectedDir = null
                            hasSelDir = false
                        }
                    }
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case _ => Feedback.Passive
    })

    InputHandler.addMouseMoveSub(_ => {
        move()
        Feedback.Passive
    })

    /**
     * Create road
     */
    private def createRoad(): Unit = {
        var (mesh, controlPoints): (Mesh, Array[Vec3]) =
            if (!curve) {
                if (!hasSelDir) RoadSegment.generateStraightMesh(selectedPos, newNodePos, noOfLanes)
                else RoadSegment.generateCircularMesh(selectedPos, selectedDir, newNodePos, noOfLanes)
            } else curveMesh

        var startNode: RoadNode = null
        var endNode: RoadNode = null
        var end = true

        if (selectedNode == null) {
            startNode = new RoadNode(selectedPos, if (!hasSelDir) newNodePos.subtract(selectedPos) else selectedDir, noOfLanes)
            endNode = new RoadNode(newNodePos, controlPoints(3).subtract(controlPoints(2)).normalize, noOfLanes)
            game.addNode(startNode)
            game.addNode(endNode)
        }
        else {
            if (curve) {
                startNode =
                    if (opposite) snappedNode else selectedNode
                endNode =
                    if (opposite) selectedNode else snappedNode
            }
            else {
                if (!opposite) {
                    startNode = selectedNode
                    endNode = new RoadNode(newNodePos, controlPoints(3).subtract(controlPoints(2)).normalize, noOfLanes)
                    game.addNode(endNode)
                }
                else {
                    controlPoints = controlPoints.reverse
                    startNode = new RoadNode(newNodePos, controlPoints(1).subtract(controlPoints(0)).normalize, noOfLanes)
                    endNode = selectedNode
                    game.addNode(startNode)
                    end = false
                }
            }
        }

        val segment = new RoadSegment(startNode, endNode, null, controlPoints, mesh)
        startNode.addOutgoingSegment(segment)
        endNode.addIncomingSegment(segment)
        selectSnapNode(if (end) endNode else startNode)
        game.addSegment(segment)
    }

    /**
     * Road preview
     */
    var selectedPos: Vec3 = _
    var selectedDir: Vec3 = _
    var hasSelPos: Boolean = false
    var hasSelDir: Boolean = false
    var previewRoad: RoadSegment = _
    var selectedNode: RoadNode = _
    var snappedNode: RoadNode = _
    var newNodePos: Vec3 = _
    var cursorPos: Vec3 = Vec3()
    var opposite: Boolean = false
    var allowed: Boolean = true
    var snapping: Boolean = false
    var curveMesh: (Mesh, Array[Vec3]) = _
    var curve: Boolean = false

    private def startRoadBuild(): Unit = startRoadBuild(newNodePos)
    private def startRoadBuild(startPos: Vec3): Unit = {
        hasSelPos = true
        startRoadPreview(startPos)
    }

    private def startRoadPreview(startPos: Vec3): Unit = {
        selectedPos = startPos
        previewRoad = new RoadSegment(selectedPos)
    }

    def move(): Unit = {
        newNodePos = terrainCollisionFunc()
        cursorPos = newNodePos

        updateRoadPreview()

        cursorMarker.setWidth(Vals.LARGE_LANE_WIDTH * noOfLanes)
        if (snappedNode == null) cursorMarker.setPosAsPoint(Vec2(newNodePos.x, newNodePos.z))
    }

    private def updateRoadPreview(): Unit = {
        // snapping
        if (snapping) {
            var currentClosest: RoadNode = null
            var minDist = 1000f
            game.nodes.foreach(n => {
                val dist = n.position.subtract(cursorPos).length
                if (dist < n.getWidth / g2) {
                    if (dist < minDist) {
                        minDist = dist
                        currentClosest = n
                    }
                }
            })
            if (currentClosest != null && selectedNode != snappedNode) {
                snappedNode = currentClosest
                cursorMarker.setPosAsPoint(Vec2(snappedNode.position.x, snappedNode.position.z))
            } else {
                snappedNode = null
                curve = null
            }
        }

        // preview
        if (previewRoad != null) {
            if (selectedNode != null && snappedNode != null) {
                val controlPoints =
                    if (opposite) Bezier.doubleSnapCurve(snappedNode.position, snappedNode.direction.negate, selectedNode.position, selectedNode.direction, noOfLanes)
                    else Bezier.doubleSnapCurve(selectedNode.position, selectedNode.direction, snappedNode.position, snappedNode.direction.negate, noOfLanes)
                if (controlPoints == null) {
                    curve = false
                    // TODO let user know
                }
                else {
                    newNodePos = snappedNode.position
                    curveMesh = (RoadSegment.generateCurveMesh(controlPoints, noOfLanes), controlPoints)
                    curve = true
                    previewRoad.updateMesh(curveMesh._1)
                    return
                }
            }

            if (mode == StraightRoad || selectedDir == null) {
                newNodePos =
                    if (selectedDir == null) cursorPos
                    else selectedPos.add(cursorPos.subtract(selectedPos).proj(selectedDir)
                        .thisOrThat(v => v.dot(selectedDir) > 0, selectedDir.normalize.scale(Vals.MIN_SEGMENT_LENGTH)))
                if (newNodePos.subtract(selectedPos).length < Vals.MIN_SEGMENT_LENGTH)
                    newNodePos = selectedPos.add(newNodePos.subtract(selectedPos).normalize.scale(Vals.MIN_SEGMENT_LENGTH))
                previewRoad.updateMesh(RoadSegment.generateStraightMesh(selectedPos, newNodePos, noOfLanes)._1)
            }
            else {
                newNodePos =
                    if (selectedDir.dot(newNodePos.subtract(selectedPos)) >= 0) newNodePos
                    else newNodePos.subtract(newNodePos.subtract(selectedPos).proj(selectedDir))
                val minDist = Math.max(Vals.MIN_SEGMENT_LENGTH, Vals.LARGE_LANE_WIDTH * noOfLanes * 3 * selectedDir.antiDot(newNodePos.subtract(selectedPos)))
                if (newNodePos.subtract(selectedPos).length < minDist) newNodePos = selectedPos.add(newNodePos.subtract(selectedPos).rescale(minDist))
                previewRoad.updateMesh(RoadSegment.generateCircularMesh(selectedPos, selectedDir, newNodePos, noOfLanes)._1)
            }
        }
    }

    private def stopPreview(): Unit = {
        selectedPos = null
        selectedDir = null
        hasSelPos = false
        hasSelDir = false
        previewRoad = null
        selectedNode = null
        opposite = false
        allowed = true
        snappedNode = null
        snapping = true
        curve = false
    }

    /**
     * No of lanes
     */
    var noOfLanes: Int = 3

    InputHandler.addKeyPressSub {
        case InputEvent(49, _, _) =>
            noOfLanes = 1
            updateRoadPreview()
            Feedback.Block
        case InputEvent(50, _, _) =>
            noOfLanes = 2
            updateRoadPreview()
            Feedback.Block
        case InputEvent(51, _, _) =>
            noOfLanes = 3
            updateRoadPreview()
            Feedback.Block
        case InputEvent(52, _, _) =>
            noOfLanes = 4
            updateRoadPreview()
            Feedback.Block
        case _ => Feedback.Passive
    }

    /**
     * Node Snapping
     */
    val cursorMarker = new TerrainLine(width = noOfLanes * Vals.LARGE_LANE_WIDTH, color = Vec4(0.2f, 0.4f, 1f, 0.8f))

    private def selectSnapNode(node: RoadNode): Unit = {
        selectedNode = node
        snappedNode = null
        startRoadBuild(selectedNode.position)
        move()
        if (node.isEndNode) selectedDir = selectedNode.direction.normalize
        else {
            selectedDir = selectedNode.direction.normalize.negate
            opposite = true
        }
        hasSelDir = if (mode == StraightRoad) false else true
    }

    /**
     * Road guidelines
     */
    val guidelines = new mutable.ListBuffer[TerrainLine]()

    /**
     * Modes
     */
    sealed trait Mode
    case object Free extends Mode
    case object StraightRoad extends Mode
    case object CurveRoad extends Mode

    var mode: Mode = Free

    def straightRoad(): Unit = {
        mode = StraightRoad
        stopPreview()
    }

    def curvedRoad(): Unit = {
        mode = CurveRoad
        stopPreview()
    }

    def freeMode(): Unit = {
        stopPreview()
        snapping = false
        mode = Free
    }

}
