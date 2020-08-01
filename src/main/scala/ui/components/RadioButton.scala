package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vec2, Vec4}

class RadioButton(val par: UIComponent, val p: Vec2, val s: Vec2, val c: Vec4,
                  val name: () => String, val func: Boolean => Unit, var pressed: Boolean, val series: Int) extends UIComponent(par, p, s, c) {

    val id = (Math.random() * Int.MaxValue).toInt

    override def click(event: InputEvent): Boolean = {
        if(isInside(InputHandler.mousePos) && event.isPressed) {
            pressed = !pressed
            func(pressed)
            parent.click(InputEvent(-series - 10, 0, id))
          true
        } else if(event.key == - series - 10 && event.mods != id) {
            pressed = false
            func(pressed)
            true
        } else false
    }

}
