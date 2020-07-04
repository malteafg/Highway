package ui

import input.InputHandler
import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

class Slider(p: Vector2f, s: Vector2f, c: Vector3f,
             val horizontal: Boolean, val func: Float => Unit) extends UIComponent(p, s, c) {

    var bar: UIComponent = null
    var value: Float = 0.0f
    var sliding: Boolean = false

    def this(_pos: Vector2f, size: Vector2f, color: Vector3f, horizontal: Boolean, topOrLeft: Boolean, thickness: Float, func: Float => Unit) {
        this(_pos, size, color, horizontal, func)
        val t = thickness * Vars.UNIT
        bar = new UIComponent(if(topOrLeft) new Vector2f() else if(horizontal) new Vector2f(0, size.y - t) else new Vector2f(size.x - t, 0),
                              new Vector2f(if(horizontal) size.x else t, if(horizontal) t else size.y), color)
    }

    override def click(vec: Vector2f, event: (Int, Int, Int)): Boolean = {
        if(bar.isInside(vec) && InputHandler.isPressed(event)) {
            InputHandler.addMousePressSub((event: (Int, Int, Int)) => { val b = InputHandler.isReleased(event); sliding = !b; b})
            InputHandler.addMouseMoveSub(slide)
            calcValue(new Vector2f(event._2, event._3))
            true
        } else false
    }

    def calcValue(vec: Vector2f): Unit = {
        val p = pos
        value = if(horizontal) restrain((vec.x - p.x) / size.x, 0, 1) else restrain((vec.y - p.y) / size.y, 0, 1)
        func(value)
        Options.log(s"Value is set to $value", Options.Button)
    }

    def restrain(value: Float, min: Float, max: Float): Float = {
        if(value < min) min else if(value > max) max else value
    }

    def slide(event: (Int, Int, Int)): Boolean = {
        if(sliding) calcValue(new Vector2f(event._2, event._3))
        !sliding
    }

}
