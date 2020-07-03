package ui

import input.InputHandler
import math.{Vector2f, Vector3f}

class Button(override val _pos: Vector2f, override val size: Vector2f, override val color: Vector3f, val name: () => String, val func: () => Unit)
      extends UIComponent(Array(), _pos, size, color) {

    override def click(vec: Vector2f, event: (Int, Int, Int)): Boolean = {
        if(isInside(vec) && InputHandler.isPressed(event)) {
            func()
            true
        } else false
    }

}
