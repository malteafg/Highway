package ui.components

import input.InputHandler
import math.{Vector2f, Vector4f}

class Button(val par: UIComponent, val p: Vector2f, val s: Vector2f, val c: Vector4f,
             val name: () => String, val func: () => Unit) extends UIComponent(par, p, s, c) {

    override def click(event: (Int, Int, Int)): Boolean = {
        if(isInside(InputHandler.mousePos) && InputHandler.isPressed(event)) {
            func()
            true
        } else false
    }

}
