package ui

import input.InputHandler
import math.{Vector2f, Vector3f}
import utils.{Options, Vals}

class UIComponent(val children: Array[UIComponent], protected val _pos: Vector2f, val size: Vector2f, val color: Vector3f) {

    var parent: UIComponent = null
    for (child <- children) child.parent = this

    def pos: Vector2f = if(parent != null) parent.pos.add(_pos) else _pos

    def this(children: Array[UIComponent], p: Vector2f, s: Vector2f, scale: Float, color: Vector3f) {
        this(children, p.scale(scale), s.scale(scale), color)
    }

    def this(p: Vector2f, s: Vector2f, color: Vector3f) {
        this(Array(), p, s, Vals.UNIT, color)
    }

    def isInside(vec: Vector2f): Boolean = {
        val p = pos
        vec.x > p.x && vec.y > p.y && vec.x < p.x + size.x && vec.y < p.y + size.y
    }

    def click(event: (Int, Int, Int)): Boolean = {
        var b = false
        if(isInside(InputHandler.mousePos)) for (child <- children) b = b || child.click(event)
        b
    }

}
