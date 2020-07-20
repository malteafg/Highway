package game

import game.roads.{Node, RoadSegment}
import input.{Feedback, InputEvent, InputHandler, Keys, Mouse}
import rendering.{Camera, GameRenderer}
import utils.Vals
import utils.math.Vector3f

object GameHandler {

    var game: Game = _
    val camera = new Camera
    var tempSphere: Sphere = _
    var dragging = false
    val terrainCollisionFunc = () => Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)

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

    def click(event: InputEvent): Feedback = mode match {
        case Free => event match {
            case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                game.spheres.addOne(new Sphere(terrainCollisionFunc()))
                Feedback.Block
            case _ => Feedback.Passive
        }
        case StraightRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) startRoadPreview()
                    else {
                        val currentPos = terrainCollisionFunc()
                        game.roads.addOne(new RoadSegment(
                            new Node(selectedPos, currentPos.subtract(selectedPos)),
                            new Node(currentPos, currentPos.subtract(selectedPos)),
                            new Array[Vector3f](0),
                            RoadSegment.generateStraightMesh(selectedPos, currentPos)
                        ))
                        resetPreview()
                    }
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) freeMode()
                    else resetPreview()
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case CurveRoad =>
            event match {
                case InputEvent(Mouse.LEFT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) startRoadPreview()
                    else if (selectedDirection == null) selectedDirection = terrainCollisionFunc().subtract(selectedPos)
                    else {
                        val currentPos = terrainCollisionFunc()
                        game.roads.addOne(new RoadSegment(
                            new Node(selectedPos, selectedDirection),
                            new Node(currentPos, currentPos.subtract(selectedPos)),
                            new Array[Vector3f](0),
                            RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, currentPos)
                        ))
                        selectedPos = null
                        selectedDirection = null
                        previewRoad = null
                    }
                    Feedback.Block
                case InputEvent(Mouse.RIGHT, Mouse.PRESSED, _) =>
                    if (selectedPos == null) freeMode()
                    else if (selectedDirection == null) resetPreview()
                    else selectedDirection = null
                    Feedback.Block
                case _ => Feedback.Passive
            }
        case _ => Feedback.Passive
    }

    private def resetPreview(): Unit = {
        selectedPos = null
        selectedDirection = null
        previewRoad = null
    }

    private def startRoadPreview(): Unit = {
        selectedPos = terrainCollisionFunc()
        previewRoad = new RoadSegment(selectedPos)
        InputHandler.addMouseMoveSub(_ => {
            if (previewRoad == null) Feedback.Unsubscribe else {
                if (selectedDirection == null) previewRoad.updateMesh(RoadSegment.generateStraightMesh(selectedPos, terrainCollisionFunc()))
                else previewRoad.updateMesh(RoadSegment.generateCurvedMesh(selectedPos, selectedDirection, terrainCollisionFunc()))
                Feedback.Passive
            }
        })
    }

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
    }

    def curvedRoad(): Unit = {
        mode = CurveRoad
    }

    def freeMode(): Unit = {
        resetPreview()
        mode = Free
    }

    /**
     * Road preview
     */
    var selectedPos: Vector3f = _
    var selectedDirection: Vector3f = _
    var previewRoad: RoadSegment = _

}
