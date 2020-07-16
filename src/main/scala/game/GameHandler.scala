package game

import rendering.{Camera, GameRenderer}

object GameHandler {

    var game: Game = null
    val camera = new Camera

    def init() = {
        newGame()
    }

    def newGame() = {
        game = new Game()
    }

    def render() = {
        if(game != null) GameRenderer.render(game, camera)
    }

}
