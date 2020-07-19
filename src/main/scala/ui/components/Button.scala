package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vector2f, Vector4f}

class Button(val par: UIComponent, val p: Vector2f, val s: Vector2f, val c: Vector4f,
             val name: () => String, val func: () => Unit) extends UIComponent(par, p, s, c) {

    override def click(event: InputEvent): Boolean = {
        if(isInside(InputHandler.mousePos) && event.isPressed()) {
            func()
            true
        } else false
    }

}
