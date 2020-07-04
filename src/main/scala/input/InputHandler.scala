package input

import math.Vector2f
import ui.Interface
import utils.Options

object InputHandler {

    var mousePos: Vector2f = new Vector2f()

    val keyPressSubs: Subscriber = new Subscriber(_ => false, null)
    val mousePressSubs: Subscriber = new Subscriber(_ => false, null)
    val mouseMoveSubs: Subscriber = new Subscriber(_ => false, null)

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        val event = (key, action, mods)
        Options.log(s"Key \'${if(key.toChar.isLetterOrDigit) key.toChar else key}\' was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"}", Options.Keys)
//        Options.log(s"Scancode: \'${scancode}\', action: ${action} and mods: ${mods}", Options.Keys)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.Keys)
        Options.log("", Options.Keys)

        keyPressSubs.iterate(event)
    }

    def addKeyPressSub(func: ((Int, Int, Int)) => Boolean) = {
        new Subscriber(func, keyPressSubs)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        val event = (button, action, mods)
        Options.log(s"Button ${button} was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"} on (${mousePos.x}, ${mousePos.y})", Options.MousePressed)
//        Options.log(s"Action: ${action} and mods: ${mods}", Options.MousePressed)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.MousePressed)
        Options.log("", Options.MousePressed)

        Interface.mousePressed(mousePos, event)
        mousePressSubs.iterate(event)
    }

    def addMousePressSub(func: ((Int, Int, Int)) => Boolean) = {
        new Subscriber(func, mousePressSubs)
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        val event = (0, xpos.toInt, ypos.toInt)
        mousePos.set(xpos.toInt, ypos.toInt)
        Options.log(s"Mouse was moved to (${mousePos.x}, ${mousePos.y})", Options.MouseMoved)
        mouseMoveSubs.iterate(event)
    }

    def addMouseMoveSub(func: ((Int, Int, Int)) => Boolean) = {
        new Subscriber(func, mouseMoveSubs)
    }

    def isControlDown(event: (Int, Int, Int)) = event._3 == 2

    def isShiftDown(event: (Int, Int, Int)) = event._3 == 1

    def isAltDown(event: (Int, Int, Int)) = event._3 == 3

    def isUnAltered(event: (Int, Int, Int)) = event._3 == 0

    def isPressed(event: (Int, Int, Int)) = event._2 == 1

    def isContinued(event: (Int, Int, Int)) = event._2 == 2

    def isReleased(event: (Int, Int, Int)) = event._2 == 0

    class Subscriber(func: ((Int, Int, Int)) => Boolean, var prev: Subscriber) {

        var next: Subscriber = null
        if(prev != null) prev.next = this

        def iterate(event: (Int, Int, Int)): Unit = {
            if(func(event)) {
                if(prev != null) prev.next = next
                if(next != null) next.prev = prev
            }
            if(next != null) next.iterate(event)
        }

    }

}
