package game

import game.roads.{RoadNode, RoadSegment}
import game.terrain.TerrainLine
import input.{Feedback, InputEvent, InputHandler, Mouse}
import rendering.{Camera, GameRenderer}
import utils.Vals
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
                    if (selectedPos == null) {
                        if (snappedNode == null) startRoadBuild()
                        else selectSnapNode(snappedNode)
                    }
                    else createRoad()
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) stopPreview()
                    else {
                        if (selectedPos == null) freeMode()
                        else stopPreview()
                    }
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case CurveRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) {
                        if (snappedNode == null) startRoadBuild()
                        else selectSnapNode(snappedNode)
                    }
                    else if (selectedDirection == null) selectedDirection = newNodePos.subtract(selectedPos)
                    else createRoad()
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) stopPreview()
                    else {
                        if (selectedPos == null) freeMode()
                        else if (selectedDirection == null) stopPreview()
                        else selectedDirection = null
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
        if (mode == StraightRoad) selectedDirection = null

        var (mesh, controlPoints): (Mesh, Array[Vec3]) =
            if (selectedDirection == null) RoadSegment.generateStraightMesh(selectedPos, newNodePos, noOfLanes)
            else RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, newNodePos, noOfLanes)

        var startNode: RoadNode = null
        var endNode: RoadNode = null
        var end = true

        if (selectedNode == null) {
            startNode = new RoadNode(selectedPos, if (selectedDirection == null) newNodePos.subtract(selectedPos) else selectedDirection, noOfLanes)
            endNode = new RoadNode(newNodePos, controlPoints(3).subtract(controlPoints(2)).normalize, noOfLanes)
            game.addNode(startNode)
            game.addNode(endNode)
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
    var selectedDirection: Vec3 = _
    var previewRoad: RoadSegment = _
    var previewRoadPos: Vec3 = _
    var selectedNode: RoadNode = _
    var snappedNode: RoadNode = _
    var newNodePos: Vec3 = _
    var cursorPos: Vec3 = Vec3()
    var opposite: Boolean = false
    var allowed: Boolean = true

    private def startRoadBuild(): Unit = startRoadBuild(newNodePos)
    private def startRoadBuild(startPos: Vec3): Unit = {
        selectedPos = startPos
        startRoadPreview(selectedPos)
    }

    private def startRoadPreview(startPos: Vec3): Unit = {
        previewRoadPos = startPos
        previewRoad = new RoadSegment(previewRoadPos)
    }

    def move(): Unit = {
        cursorMarker.setWidth(Vals.LARGE_LANE_WIDTH * noOfLanes)
        newNodePos = terrainCollisionFunc()
        cursorPos = newNodePos

        updateRoadPreview()
        if (selectedPos != null) cursorMarker.setPosAsPoint(Vec2(newNodePos.x, newNodePos.z))
    }

    private def updateRoadPreview(): Unit = {
        if (previewRoad != null) {
            if (mode == StraightRoad || selectedDirection == null) {
                newNodePos =
                    if (selectedDirection == null) cursorPos
                    else selectedPos.add(cursorPos.subtract(selectedPos).proj(selectedDirection)
                        .thisOrThat(v => v.dot(selectedDirection) > 0, selectedDirection.normalize.scale(Vals.MIN_SEGMENT_LENGTH)))
                if (newNodePos.subtract(selectedPos).length < Vals.MIN_SEGMENT_LENGTH)
                    newNodePos = selectedPos.add(newNodePos.subtract(selectedPos).normalize.scale(Vals.MIN_SEGMENT_LENGTH))
                previewRoad.updateMesh(RoadSegment.generateStraightMesh(selectedPos, newNodePos, noOfLanes)._1)
            }
            else {
                newNodePos =
                    if (selectedDirection.dot(newNodePos.subtract(selectedPos)) >= 0) newNodePos
                    else newNodePos.subtract(newNodePos.subtract(selectedPos).proj(selectedDirection))
                previewRoad.updateMesh(RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, newNodePos, noOfLanes)._1)
            }
        }
    }

    private def stopPreview(): Unit = {
        selectedPos = null
        selectedDirection = null
        previewRoad = null
        selectedNode = null
        opposite = false
        allowed = true
        snappedNode = null
        turnOnSnap()
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
        if (node.isEndNode) selectedDirection = selectedNode.direction.normalize
        else {
            selectedDirection = selectedNode.direction.normalize.negate
            opposite = true
        }
    }

    private def nodeSnapper(event: InputEvent): Feedback = {
        if (mode == Free || selectedNode != null) Feedback.Unsubscribe
        else {
            game.nodes.foreach(n => {
                if (n.position.subtract(newNodePos).length < Vals.LARGE_LANE_WIDTH) {
                    snappedNode = n
                    cursorMarker.setPosAsPoint(Vec2(n.position.x, n.position.z))



                    return Feedback.Passive
                }
            })
            snappedNode = null
            cursorMarker.setPosAsPoint(Vec2(newNodePos.x, newNodePos.z))
            Feedback.Passive
        }
    }

    private def turnOnSnap(): Unit = {
        InputHandler.addMouseMoveSub(nodeSnapper)
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
        selectedDirection = null
        stopPreview()
        turnOnSnap()
    }

    def curvedRoad(): Unit = {
        mode = CurveRoad
        stopPreview()
        turnOnSnap()
    }

    def freeMode(): Unit = {
        stopPreview()
        mode = Free
    }

}
