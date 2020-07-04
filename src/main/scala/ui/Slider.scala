package ui

import input.InputHandler
import input.InputHandler.mouseMoveSubs
import math.{Vector2f, Vector3f}
import utils.{Options, Vars}

class Slider(p: Vector2f, s: Vector2f, c: Vector3f,
             val horizontal: Boolean, var value: Float, val func: Float => Unit) extends UIComponent(p, s, c) {

    var bar: UIComponent = null
    var sliding: Boolean = false

    def this(p: Vector2f, s: Vector2f, color: Vector3f, horizontal: Boolean, topOrLeft: Boolean, thickness: Float, value: Float, func: Float => Unit) {
        this(p, s, color, horizontal, value, func)
        bar = new UIComponent(if(topOrLeft) new Vector2f() else if(horizontal) new Vector2f(0, p.y + s.y - thickness) else new Vector2f(p.x + s.x - thickness, 0),
                              if(horizontal) new Vector2f(s.x, thickness) else new Vector2f(thickness, s.y), color)

    }

    override def click(event: (Int, Int, Int)): Boolean = {
        if(bar.isInside(InputHandler.mousePos) && InputHandler.isPressed(event)) {
            InputHandler.addMousePressSub((event: (Int, Int, Int)) => { val b = InputHandler.isReleased(event); sliding = !b; b})
            InputHandler.addMouseMoveSub(slide)
            sliding = true
            calcValue(InputHandler.mousePos)
            true
        } else if(isInside(InputHandler.mousePos) && InputHandler.isScrolling(event)) {
            value = restrain(value - event._3 * 0.1f, 0, 1)
            func(value)
            true
        } else false
    }

    def calcValue(vec: Vector2f): Unit = {
        val p = pos
        value = if(horizontal) restrain((vec.x - p.x) / size.x, 0, 1) else restrain((vec.y - p.y) / size.y, 0, 1)
        func(value)
    }

    def restrain(value: Float, min: Float, max: Float): Float = {
        if(value < min) min else if(value > max) max else value
    }

    def slide(event: (Int, Int, Int)): Boolean = {
        if(sliding) calcValue(new Vector2f(event._2, event._3))
        !sliding
    }

}
