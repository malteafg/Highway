package game

import input.InputHandler
import rendering.{Camera, GameRenderer}
import utils.Vals

object GameHandler {

    var game: Game = null
    val camera = new Camera

    InputHandler.addMousePressSub(click)

    def click(event: (Int, Int, Int)) = {
        if (event._1 == 0 && InputHandler.isPressed(event)) {
            game.spheres.addOne(new Sphere(Vals.terrainRayCollision(Vals.getRay(InputHandler.mousePos), (_, _) => 0, 0.1f)))
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
