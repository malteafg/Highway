package ui

import input.InputHandler
import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

class Button(val p: Vector2f, val s: Vector2f, val c: Vector3f,
             val name: () => String, val func: () => Unit) extends UIComponent(p, s, c) {

    override def click(event: (Int, Int, Int)): Boolean = {
        if(isInside(InputHandler.mousePos) && InputHandler.isPressed(event)) {
            func()
            true
        } else false
    }

}
