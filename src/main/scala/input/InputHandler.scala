package input

import utils.math.Vector2f
import ui.Interface
import utils.Options

object InputHandler {

    var mousePos: Vector2f = new Vector2f()

    val keyPressSubs: Subscriber    = new Subscriber(_ => (false, false), null)
    val mousePressSubs: Subscriber  = new Subscriber(_ => (false, false), null)
    val mouseMoveSubs: Subscriber   = new Subscriber(_ => (false, false), null)
    val mouseScrollSubs: Subscriber = new Subscriber(_ => (false, false), null)

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        val event = (key, action, mods)
        Options.log(s"Key '${key.toChar}'/$key was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"}", Options.Keys)
//        Options.log(s"Scancode: \'${scancode}\', action: ${action} and mods: ${mods}", Options.Keys)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.Keys)
        Options.log("", Options.Keys)

        keyPressSubs.iterate(event)
    }

    def addKeyPressSub(func: ((Int, Int, Int)) => (Boolean, Boolean)) = new Subscriber(func, keyPressSubs)

    def charEntered(window: Long, codePoint: Int): Unit = {
        val event = (codePoint, 0, -1)
        Options.log(s"'${codePoint.toChar}' has a value of $codePoint \n", Options.Characters)

        keyPressSubs.iterate(event)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        val event = (button, action, mods)
        Options.log(s"Button ${button} was ${if(isPressed(event)) "pressed" else if(isReleased(event)) "released" else if(isContinued(event)) "held down" else "interacted with"} on (${mousePos.x}, ${mousePos.y})", Options.MousePressed)
//        Options.log(s"Action: ${action} and mods: ${mods}", Options.MousePressed)
        Options.log(s"Ctrl: ${isControlDown(event)}, Alt: ${isAltDown(event)}, Shift: ${isShiftDown(event)}, none: ${isUnAltered(event)}", Options.MousePressed)
        Options.log("", Options.MousePressed)

        mousePressSubs.iterate(event)
    }

    def addMousePressSub(func: ((Int, Int, Int)) => (Boolean, Boolean)) = new Subscriber(func, mousePressSubs)

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        val event = (0, xpos.toInt, ypos.toInt)
        mousePos.set(xpos.toInt, ypos.toInt)
        Options.log(s"Mouse was moved to (${mousePos.x}, ${mousePos.y})", Options.MouseMoved)
        mouseMoveSubs.iterate(event)
    }

    def addMouseMoveSub(func: ((Int, Int, Int)) => (Boolean, Boolean)) = new Subscriber(func, mouseMoveSubs)

    def mouseScrolled(window: Long, xScroll: Double, yScroll: Double): Unit = {
        val event = (-1, xScroll.toInt, yScroll.toInt)
        Options.log(s"Mouse was scrolled to (${xScroll.toInt}, ${yScroll.toInt})", Options.MouseScrolled)

        mouseScrollSubs.iterate(event)
    }

    def addMouseScrollSub(func: ((Int, Int, Int)) => (Boolean, Boolean)) = new Subscriber(func, mouseScrollSubs)

    def isControlDown(event: (Int, Int, Int))   = event._3 == 2

    def isShiftDown(event: (Int, Int, Int))     = event._3 == 1

    def isAltDown(event: (Int, Int, Int))       = event._3 == 3

    def isCodePoint(event: (Int, Int, Int))     = event._3 == -1

    def isUnAltered(event: (Int, Int, Int))     = event._3 == 0

    def isContinued(event: (Int, Int, Int))     = event._2 == 2

    def isPressed(event: (Int, Int, Int))       = event._1 != -1 && event._2 == 1

    def isReleased(event: (Int, Int, Int))      = event._1 != -1 && event._2 == 0

    def isLefClick(event: (Int, Int, Int))      = event._1 == 1

    def isWheelClick(event: (Int, Int, Int))    = event._1 == 2

    def isRightClick(event: (Int, Int, Int))    = event._1 == 3

    def isScrolling(event: (Int, Int, Int))     = event._1 == -1

    /**
     * An object storing a function to be executed and references to two other Subscriber objects
     *
     * @param func A function that returns two booleans, the first unsubscribes the object if true and the second blocks the call
     *             to the next subscriber if true
     * @param prev A reference to another subscriber object usually the head of the list
     */
    class Subscriber(func: ((Int, Int, Int)) => (Boolean, Boolean), var prev: Subscriber) {

        var next: Subscriber = null
        if(prev != null) {
            if(prev.next != null) next = prev.next
            prev.next = this
        }

        def iterate(event: (Int, Int, Int)): Unit = {
            val t = func(event)
            if(t._1) {
                if(prev != null) prev.next = next
                if(next != null) next.prev = prev
            }
            if(next != null && !t._2) next.iterate(event)
        }

    }

}
