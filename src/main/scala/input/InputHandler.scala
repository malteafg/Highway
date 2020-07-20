package input

import game.GameHandler
import utils.math.Vector2f
import ui.Interface
import utils.{Options, Vals}

object InputHandler {

    var mousePos: Vector2f = new Vector2f()

    val keyPressSubs: Subscriber    = new Subscriber(_ => Feedback.Passive, null)
    val charSubs: Subscriber        = new Subscriber(_ => Feedback.Passive, null)
    val mousePressSubs: Subscriber  = new Subscriber(_ => Feedback.Passive, null)
    val mouseMoveSubs: Subscriber   = new Subscriber(_ => Feedback.Passive, null)
    val mouseScrollSubs: Subscriber = new Subscriber(_ => Feedback.Passive, null)

    def keyPressed(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        val event = InputEvent(key, action, mods)
        Options.log(s"Key '${key.toChar}'/$key was ${if(event.isPressed()) "pressed" else if(event.isReleased()) "released" else if(event.isContinued()) "held down" else "interacted with"}", Options.Keys)
//        Options.log(s"Scancode: \'${scancode}\', action: ${action} and mods: ${mods}", Options.Keys)
        Options.log(s"Ctrl: ${event.isControlDown()}, Alt: ${event.isAltDown()}, Shift: ${event.isShiftDown()}, none: ${event.isUnAltered()}", Options.Keys)
        Options.log("", Options.Keys)

        Keys.CONTROL_DOWN = event.isControlDown()
        Keys.SHIFT_DOWN = event.isShiftDown()
        Keys.ALT_DOWN = event.isAltDown()

        if(charSubs.next == null) keyPressSubs.iterate(event)
        else charSubs.iterate(event)
    }

    def charEntered(window: Long, codePoint: Int): Unit = {
        val event = InputEvent(codePoint, 0, -1)
        Options.log(s"'${codePoint.toChar}' has a value of $codePoint \n", Options.Characters)

        charSubs.iterate(event)
    }

    def mousePressed(window: Long, button: Int, action: Int, mods: Int): Unit = {
        val event = InputEvent(button, action, mods)
        Options.log(s"Button ${button} was ${if(event.isPressed()) "pressed" else if(event.isReleased()) "released" else if(event.isContinued()) "held down" else "interacted with"} on (${mousePos.x}, ${mousePos.y})", Options.MousePressed)
//        Options.log(s"Action: ${action} and mods: ${mods}", Options.MousePressed)
        Options.log(s"Ctrl: ${event.isControlDown()}, Alt: ${event.isAltDown()}, Shift: ${event.isShiftDown()}, none: ${event.isUnAltered()}", Options.MousePressed)
        Options.log("", Options.MousePressed)

        mousePressSubs.iterate(event)
    }

    def mouseMoved(window: Long, xpos: Double, ypos: Double): Unit = {
        val event = InputEvent(0, xpos.toInt, ypos.toInt)

        Options.log(s"Mouse was moved to (${mousePos.x}, ${mousePos.y})", Options.MouseMoved)
        mouseMoveSubs.iterate(event)

        mousePos.set(xpos.toInt, ypos.toInt)
    }

    def mouseScrolled(window: Long, xScroll: Double, yScroll: Double): Unit = {
        val event = InputEvent(-1, xScroll.toInt, yScroll.toInt)
        Options.log(s"Mouse was scrolled to (${xScroll.toInt}, ${yScroll.toInt})", Options.MouseScrolled)

        mouseScrollSubs.iterate(event)
    }

    /**
     * An object storing a function to be executed and references to two other Subscriber objects
     *
     * @param func A function that returns two booleans, the first unsubscribes the object if true and the second blocks the call
     *             to the next subscriber if true
     * @param prev A reference to another subscriber object usually the head of the list
     */
    class Subscriber(func: InputEvent => Feedback, var prev: Subscriber) {

        var next: Subscriber = null
        if(prev != null) {
            if(prev.next != null) next = prev.next
            prev.next = this
        }

        def iterate(event: InputEvent): Unit = {
            val t = func(event)
            if(t.unsubscribe) {
                if(prev != null) prev.next = next
                if(next != null) next.prev = prev
            }
            if(next != null && !t.block) next.iterate(event)
        }
    }

    def addKeyPressSub(func: InputEvent => Feedback) = new Subscriber(func, keyPressSubs)
    def addCharSub(func: InputEvent => Feedback) = new Subscriber(func, charSubs)
    def addMousePressSub(func: InputEvent => Feedback) = new Subscriber(func, mousePressSubs)
    def addMouseMoveSub(func: InputEvent => Feedback) = new Subscriber(func, mouseMoveSubs)
    def addMouseScrollSub(func: InputEvent => Feedback) = new Subscriber(func, mouseScrollSubs)

}
