package ui

import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

class UIComponent(val children: Array[UIComponent], protected val _pos: Vector2f, val size: Vector2f, val color: Vector3f) {

    var parent: UIComponent = null
    for (child <- children) child.parent = this

    def pos: Vector2f = if(parent != null) parent.pos.add(_pos) else new Vector2f()

    def this(children: Array[UIComponent], p: Vector2f, s: Vector2f, scale: Float, color: Vector3f) {
        this(children, p.scale(scale), s.scale(scale), color)
    }

    def this(p: Vector2f, s: Vector2f, color: Vector3f) {
        this(Array(), p, s, Vars.UNIT, color)
    }

    def isInside(vec: Vector2f): Boolean = {
        val p = pos
        vec.x > p.x && vec.y > p.y && vec.x < p.x + size.x && vec.y < p.y + size.y
    }

    def click(vec: Vector2f, event: (Int, Int, Int)): Boolean = {
        var b = false
        if(isInside(vec)) for (child <- children) b = b || child.click(vec, event)
        b
    }

}
