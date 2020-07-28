package game

import game.roads.{RoadNode, RoadSegment}
import game.terrain.TerrainLine
import input.{Feedback, InputEvent, InputHandler, Mouse}
import rendering.{Camera, GameRenderer}
import utils.Vals
import utils.math.{Vec2, Vec3, Vec4}

import scala.collection.mutable

object GameHandler {

    var game: Game = _
    val camera = new Camera
    var tempSphere: Sphere = _
    var dragging = false
    val terrainCollisionFunc: () => Vec3 = () => Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)

    def init(): Unit = {
        newGame()
    }

    def newGame(): Unit = {
        game = new Game()
    }

    def update(): Unit = {
        camera.update
    }
    
    def render(): Unit = {
        if(game != null) GameRenderer.render(game, camera)
    }

    /**
     * Input
     */
    InputHandler.addMousePressSub(click)

    private def click(event: InputEvent): Feedback = mode match {
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
                        if (snappedNode == null) startRoadPreview()
                        else selectSnapNode(snappedNode)
                    }
                    else {
                        val currentPos = if (selectedDirection == null) terrainCollisionFunc() else selectedPos.add(selectedDirection.scale(selectedPos.subtract(terrainCollisionFunc()).length))
                        val newNode = RoadNode(currentPos, currentPos.subtract(selectedPos))
                        game.buildRoad(
                            if (selectedNode == null) RoadNode(selectedPos, currentPos.subtract(selectedPos)) else selectedNode,
                            newNode,
                            RoadSegment.generateStraightMesh(selectedPos, newNode.position)
                        )
                        selectSnapNode(newNode)
                    }
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) resetPreview()
                    else {
                        if (selectedPos == null) freeMode()
                        else resetPreview()
                    }
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case CurveRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) {
                        if (snappedNode == null) startRoadPreview()
                        else selectSnapNode(snappedNode)
                    }
                    else if (selectedDirection == null) selectedDirection = terrainCollisionFunc().subtract(selectedPos)
                    else {
                        val currentPos = terrainCollisionFunc()
                        val segment = RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, currentPos)
                        val newNode = RoadNode(currentPos, segment._2)
                        game.buildRoad(
                            if (selectedNode == null) RoadNode(selectedPos, selectedDirection) else selectedNode,
                            newNode,
                            segment._1
                        )
                        selectSnapNode(newNode)
                    }
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedNode != null) resetPreview()
                    else {
                        if (selectedPos == null) freeMode()
                        else if (selectedDirection == null) resetPreview()
                        else selectedDirection = null
                    }
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case _ => Feedback.Passive
    }

    private def resetPreview(): Unit = {
        selectedPos = null
        selectedDirection = null
        previewRoad = null
        selectedNode = null
        turnOnSnap()
    }

    private def startRoadPreview(): Unit = startRoadPreview(terrainCollisionFunc())
    private def startRoadPreview(startPos: Vec3): Unit = {
        selectedPos = startPos
        previewRoad = new RoadSegment(selectedPos)
    }

    // update preview road
    InputHandler.addMouseMoveSub(_ => {
        val cursorPos = terrainCollisionFunc()
        if (previewRoad != null) {
            if (mode == StraightRoad || selectedDirection == null) previewRoad.updateMesh(RoadSegment.generateStraightMesh(selectedPos,
//                if (selectedDirection == null) cursorPos else selectedPos.add(selectedDirection.scale(selectedPos.subtract(cursorPos).length))))
                if (selectedDirection == null) cursorPos else selectedPos.add(cursorPos.subtract(selectedPos).proj(selectedDirection).thisOrThat(v => v.dot(selectedDirection) > 0, Vec3()))))
            else previewRoad.updateMesh(RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, terrainCollisionFunc())._1)
            Feedback.Passive
        }
        Feedback.Passive
    })

    /**
     * Node Snapping
     */
    val cursorMarker = new TerrainLine(width = 6, color = Vec4(0.2f, 0.4f, 1f, 0.8f))

    private def selectSnapNode(node: RoadNode): Unit = {
        selectedNode = node
        snappedNode = null
        startRoadPreview(selectedNode.position)
        selectedDirection = selectedNode.direction.normalize
    }

    private def nodeSnapper(event: InputEvent): Feedback = {
        val cursorPos = terrainCollisionFunc()
        if (mode == Free || selectedNode != null) Feedback.Unsubscribe
        else {
            game.nodes.foreach(n => {
                if (n.position.subtract(cursorPos).length < Vals.LARGE_LANE_WIDTH) {
                    snappedNode = n
                    cursorMarker.setPosAsPoint(Vec2(n.position.x, n.position.z))
                    return Feedback.Passive
                }
            })
            snappedNode = null
            cursorMarker.setPosAsPoint(Vec2(cursorPos.x, cursorPos.z))
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
        resetPreview()
        turnOnSnap()
    }

    def curvedRoad(): Unit = {
        mode = CurveRoad
        resetPreview()
        turnOnSnap()
    }

    def freeMode(): Unit = {
        resetPreview()
        mode = Free
    }

    /**
     * Road preview
     */
    var selectedPos: Vec3 = _
    var selectedDirection: Vec3 = _
    var previewRoad: RoadSegment = _
    var selectedNode: RoadNode = _
    var snappedNode: RoadNode = _

}
