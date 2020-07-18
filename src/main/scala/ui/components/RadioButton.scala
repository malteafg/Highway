package ui.components

import input.InputHandler
import utils.math.{Vector2f, Vector4f}

class RadioButton(val par: UIComponent, val p: Vector2f, val s: Vector2f, val c: Vector4f,
                  val name: () => String, val func: Boolean => Unit, var pressed: Boolean, val series: Int) extends UIComponent(par, p, s, c) {

    val id = (Math.random() * Int.MaxValue).toInt

    override def click(event: (Int, Int, Int)): Boolean = {
        if(isInside(InputHandler.mousePos) && InputHandler.isPressed(event)) {
            pressed = !pressed
            func(pressed)
            parent.click((- series - 10, 0 , id))
          true
        } else if(event._1 == - series - 10 && event._3 != id) {
            pressed = false
            func(pressed)
            true
        } else false
    }

}
