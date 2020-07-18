package game

import input.InputHandler
import rendering.{Camera, GameRenderer}
import utils.Vals
import utils.math.Vector3f

object GameHandler {

    var game: Game = null
    val camera = new Camera

    var selectedPos: Vector3f = null
    var selectedDirection: Vector3f = null
    var dragging = false
    val terrainCollisionFunc = () => Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)

    InputHandler.addMousePressSub(click)

    def click(event: (Int, Int, Int)) = {
        if (event._1 == 2 && InputHandler.isPressed(event)) {
            game.spheres.addOne(new Sphere(terrainCollisionFunc()))
            (false, true)
        } else if (event._1 == 0 && InputHandler.isPressed(event)) {
            if(selectedPos == null && selectedDirection == null) {
                selectedPos = terrainCollisionFunc()
                dragging = true
                InputHandler.addMouseMoveSub( _ => {
                    if(dragging) {
                        selectedDirection = terrainCollisionFunc().subtract(selectedPos)
                        (false, false)
                    } else (true, false)
                })
            } else {
                placeRoad(selectedPos, selectedDirection, terrainCollisionFunc())
            }

            (false, true)
        } else if (event._1 == 0 && InputHandler.isReleased(event)) {
            dragging = false
            (false, true)
        } else (false, false)
    }

    def placeRoad(v1: Vector3f, r: Vector3f, v2: Vector3f) = {
        val points = new Array[Vector3f](4)

        val ab = v2.subtract(v1)
        val d = ab.normalize.dot(r.normalize)
        val f = (2.0f / 3.0f * ab.length * (1.0f - d) / (r.length * (1.0f - d * d)))
        val R = r.scale(f)
        //val u = ab.normalize.add(r.normalize).divide(2.0f)

        points(0) = v1
        points(1) = v1.add(R)
        points(2) = v2.add(R).subtract(ab.scale(2.0f * ab.dot(R) / ab.dot(ab)))
        points(3) = v2

        points
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
