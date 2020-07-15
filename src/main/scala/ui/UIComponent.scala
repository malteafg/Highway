package ui

import input.InputHandler
import math.{Vector2f, Vector3f, Vector4f}
import utils.Vals

class UIComponent private(val parent: UIComponent, var active: Boolean, protected val _pos: Vector2f, val size: Vector2f, val color: Vector4f,
                          val children: scala.collection.mutable.ListBuffer[UIComponent]) {

    if(parent != null) parent.children.addOne(this)

    def pos: Vector2f = if(parent != null) parent.pos.add(_pos) else _pos

    def this(parent: UIComponent, active: Boolean, p: Vector2f, s: Vector2f, scale: Float, color: Vector4f) {
        this(parent, active, p.scale(scale), s.scale(scale), color, scala.collection.mutable.ListBuffer.empty[UIComponent])
    }

    /**
     * This is the constructer that should be used
     * @param par Parent
     * @param p Position
     * @param s Size
     * @param c Color
     */
    def this(par: UIComponent, active: Boolean, p: Vector2f, s: Vector2f, c: Vector4f) {
        this(par, active, p, s, Vals.UNIT, c)
    }

    def this(par: UIComponent, p: Vector2f, s: Vector2f, c: Vector4f) {
        this(par, true, p, s, Vals.UNIT, c)
    }

    def isInside(vec: Vector2f): Boolean = {
        val p = pos
        vec.x > p.x && vec.y > p.y && vec.x < p.x + size.x && vec.y < p.y + size.y
    }

    def isActive(): Boolean = if(parent == null || !active) active else parent.isActive()

    def depth(): Int = if(parent == null) 0 else parent.depth() + 1

    def click(event: (Int, Int, Int)): Boolean = {
        var b = false
        if(isInside(InputHandler.mousePos)) for (child <- children) b = b || (active && child.click(event))
        b
    }

    def getChildren() = children

}
