package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vector2f, Vector4f}

class RadioButton(val par: UIComponent, val p: Vector2f, val s: Vector2f, val c: Vector4f,
                  val name: () => String, val func: Boolean => Unit, var pressed: Boolean, val series: Int) extends UIComponent(par, p, s, c) {

    val id = (Math.random() * Int.MaxValue).toInt

    override def click(event: InputEvent): Boolean = {
        if(isInside(InputHandler.mousePos) && event.isPressed()) {
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
