package input

import math.Vector2f
import utils.Options

object InputHandler {

    var mousePos: Vector2f = new Vector2f()

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        val event = (key, action, mods)
        Options.log(s"Key \'${if(key.toChar.isLetterOrDigit) key.toChar else key}\' was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"}", Options.Keys)
        Options.log(s"Scancode: \'${scancode}\', action: ${action} and mods: ${mods}", Options.Keys)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.Keys)
        Options.log("", Options.Keys)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        val event = (button, action, mods)
        Options.log(s"Button ${button} was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"} on (${mousePos.x}, ${mousePos.y})", Options.MousePressed)
        Options.log(s"Action: ${action} and mods: ${mods}", Options.MousePressed)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.MousePressed)
        Options.log("", Options.MousePressed)
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        mousePos.set(xpos.toInt, ypos.toInt)
        Options.log(s"Mouse was moved to (${mousePos.x}, ${mousePos.y})", Options.MouseMoved)
    }

    def isControlDown(event: (Int, Int, Int)) = event._3 == 2

    def isShiftDown(event: (Int, Int, Int)) = event._3 == 1

    def isAltDown(event: (Int, Int, Int)) = event._3 == 3

    def isUnAltered(event: (Int, Int, Int)) = event._3 == 0

    def isPressed(event: (Int, Int, Int)) = event._2 == 1

    def isContinued(event: (Int, Int, Int)) = event._2 == 2

    def isReleased(event: (Int, Int, Int)) = event._2 == 0

}
