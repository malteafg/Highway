package game

import input.InputHandler
import rendering.{Camera, GameRenderer}
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

    InputHandler.addMousePressSub(click)

    def click(event: (Int, Int, Int)) = {
        if (InputHandler.isRightClick(event) && InputHandler.isPressed(event)) {
            game.spheres.addOne(new Sphere(terrainCollisionFunc()))
            (false, true)
        } else if (event._1 == 0 && InputHandler.isPressed(event)) {
            if(selectedPos == null || selectedDirection == null) {
                selectedPos = terrainCollisionFunc()
                game.spheres.addOne(new Sphere(selectedPos, Vals.CONTROL_POINT_COLOR))
                tempSphere = new Sphere(selectedPos, Vals.CONTROL_POINT_COLOR)
                game.spheres.addOne(tempSphere)
                dragging = true
                InputHandler.addMouseMoveSub( _ => {
                    if(dragging) {
                        val p = terrainCollisionFunc()
                        selectedDirection = p.subtract(selectedPos)
                        tempSphere.position = p
                        (false, false)
                    } else (true, false)
                })
            } else {
                val array = Bezier.curveRoad(selectedPos, selectedDirection, terrainCollisionFunc())
                val boundaries = Bezier.triangulate(array, 3f * Vals.LARGE_LANE_WIDTH)
                selectedPos = null
                selectedDirection = null
                tempSphere.position = array(1)
                game.spheres.addOne(new Sphere(array(2), Vals.CONTROL_POINT_COLOR))
                game.spheres.addOne(new Sphere(array(3), Vals.CONTROL_POINT_COLOR))
                val n = 20
                for(b <- boundaries._1) {
                    println(b)
                    game.spheres.addOne(new Sphere(b))
                }

            }

            (false, true)
        } else if (event._1 == 0 && InputHandler.isReleased(event)) {
            dragging = false
            (false, true)
        } else (false, false)
    }

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

}
