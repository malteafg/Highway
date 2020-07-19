package game

import game.roads.{RoadSegment, SnapPoint}
import input.InputHandler
import input.InputEvent
import rendering.{Camera, GameRenderer}
import utils.graphics.Mesh
import utils.{Bezier, Vals}
import utils.math.{Vector3f, Vector4f}

object GameHandler {

    var game: Game = null
    val camera = new Camera

    var selectedPos: Vector3f = null
    var selectedDirection: Vector3f = null
    var tempSphere: Sphere = null
    var dragging = false
    val terrainCollisionFunc = () => Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)

    def init() = {
        newGame()
    }

    def newGame() = {
        game = new Game()
    }

    def update() = {
        camera.update
    }
    
    def render() = {
        if(game != null) GameRenderer.render(game, camera)
    }

    /**
     * Input
     */
    InputHandler.addMousePressSub(click)

    def click(event: InputEvent) = {
            if (event.isRightClick() && event.isPressed()) {
                game.spheres.addOne(new Sphere(terrainCollisionFunc()))
                (false, true)
            }
             else if (event.key == 0 && event.isPressed()) {
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
                            (false, false)
                        } else (true, false)
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

                (false, true)
            } else if (event.key == 0 && event.isReleased()) {
                dragging = false
                (false, true)
            } else (false, false)
    }

    /**
     * Modes
     */
    sealed trait Mode
    case object Free extends Mode
    case object Road extends Mode

    var mode: Mode = Free

    def placeRoad() = mode = Road

}
