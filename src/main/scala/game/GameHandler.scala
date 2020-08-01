package game

import game.tools.Tools
import input.{Feedback, InputHandler}
import rendering.{Camera, GameRenderer}

object GameHandler {

    var game: Game = _
    val camera = new Camera

    def init(): Unit = {
        newGame()
        Tools.init(game)
    }

    def newGame(): Unit = {
        game = new Game()
    }

    def update(): Unit = {
        camera.update()
    }
    
    def render(): Unit = {
        if(game != null) GameRenderer.render(game, camera, Tools.current)
    }

    /**
     * Input
     */
    InputHandler.addMousePressSub(event => {
        Tools.onMousePress(event)
        Feedback.Passive
    })

    InputHandler.addMouseMoveSub(_ => {
        onMovement()
        Feedback.Passive
    })

    def onMovement(): Unit = Tools.onMovement()

}
