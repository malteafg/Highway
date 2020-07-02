package input

import utils.Options

object InputHandler {

    var x: Int = 0
    var y: Int = 0

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        Options.log(s"Key \'${key.toChar}\' was pressed", Options.Keys)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        Options.log(s"Button ${button} was pressed on (${x}, ${y})", Options.Mouse)
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        x = xpos.toInt
        y = ypos.toInt
        Options.log(s"Mouse was moved to (${x}, ${y})", Options.Mouse)
    }

}
