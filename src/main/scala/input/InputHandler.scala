package input

import math.Vector2f
import utils.Options

object InputHandler {

    var mousePos: Vector2f = new Vector2f()

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        Options.log(s"Key \'${key.toChar}\' was pressed", Options.Keys)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        Options.log(s"Button ${button} was pressed on (${mousePos.x}, ${mousePos.y})", Options.Mouse)
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        mousePos.set(xpos.toInt, ypos.toInt)
        Options.log(s"Mouse was moved to (${mousePos.x}, ${mousePos.y})", Options.Mouse)
    }

}
