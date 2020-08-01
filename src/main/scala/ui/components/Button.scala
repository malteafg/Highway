package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vec2, Vec4}

class Button(val par: UIComponent, val p: Vec2, val s: Vec2, val c: Vec4,
             val name: () => String, val func: () => Unit) extends UIComponent(par, p, s, c) {

    override def click(event: InputEvent): Boolean = {
        if(isInside(InputHandler.mousePos) && event.isPressed) {
            func()
            true
        } else false
    }

}
