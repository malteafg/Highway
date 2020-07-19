package game

import game.roads.{RoadSegment, SnapPoint}
import input.{Feedback, InputEvent, InputHandler, Mouse}
import rendering.{Camera, GameRenderer}
import utils.graphics.Mesh
import utils.{Bezier, Vals}
import utils.math.Vector3f

object GameHandler {

    var game: Game = _
    val camera = new Camera

    var selectedPos: Vector3f = _
    var selectedDirection: Vector3f = _
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
        case Free => (event.key, event.action) match {
            case (Mouse.RIGHT, Mouse.PRESSED) =>
                game.spheres.addOne(new Sphere(terrainCollisionFunc()))
                Feedback.Block
            case (_, _) => Feedback.Passive
        }
        case Road =>
            if (event.key == 0 && event.isPressed()) {
                if (selectedPos == null || selectedDirection == null) {
                    selectedPos = terrainCollisionFunc()
                    game.spheres.addOne(new Sphere(selectedPos, Vals.CONTROL_POINT_COLOR))
                    tempSphere = new Sphere(selectedPos, Vals.CONTROL_POINT_COLOR)
                    game.spheres.addOne(tempSphere)
                    dragging = true
                    InputHandler.addMouseMoveSub(_ => {
                        if (dragging) {
                            val p = terrainCollisionFunc()
                            selectedDirection = p.subtract(selectedPos)
                            tempSphere.position = p
                            Feedback.Passive
                        } else Feedback.Unsubscribe
                    })
                } else {
                    val array = Bezier.circleCurve(selectedPos, selectedDirection, terrainCollisionFunc())
                    val boundaries = Bezier.triangulate(array, 3f * Vals.LARGE_LANE_WIDTH)
                    selectedPos = null
                    selectedDirection = null
                    tempSphere.position = array(1)
                    game.spheres.addOne(new Sphere(array(2), Vals.CONTROL_POINT_COLOR))
                    game.spheres.addOne(new Sphere(array(3), Vals.CONTROL_POINT_COLOR))
                    game.roads.addOne(new RoadSegment(
                        new SnapPoint(array(0), array(1).subtract(array(0))),
                        new SnapPoint(array(3), array(2).subtract(array(3))),
                        array,
                        new Mesh(boundaries._1, boundaries._2)))
                }

                Feedback.Block
            } else if (event.key == 0 && event.isReleased()) {
                dragging = false
                Feedback.Block
            } else Feedback.Passive
    }

    /**
     * Modes
     */
    sealed trait Mode
    case object Free extends Mode
    case object Road extends Mode

    var mode: Mode = Free

    def roadMode(): Unit = mode = Road

}
