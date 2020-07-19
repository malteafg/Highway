package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vector2f, Vector4f}
import utils.Vals

class Slider(val par: UIComponent, p: Vector2f, s: Vector2f, c: Vector4f,
             val horizontal: Boolean, var value: Float, val func: Float => Unit) extends UIComponent(par, p, s, c) {

    var bar: UIComponent = null
    var sliding: Boolean = false

    /**
     * Constructors
     */
    def this(par: UIComponent, p: Vector2f, s: Vector2f, color: Vector4f, horizontal: Boolean, topOrLeft: Boolean, thickness: Float, value: Float, func: Float => Unit) {
        this(par, p, s, color, horizontal, value, func)
        bar = new UIComponent(this, if(topOrLeft) new Vector2f() else if(horizontal) new Vector2f(0, s.y - thickness) else new Vector2f(s.x - thickness, 0),
                              if(horizontal) new Vector2f(s.x, thickness) else new Vector2f(thickness, s.y), color)
    }

    /**
     * Functions
     */
    override def click(event: InputEvent): Boolean = {
        if(bar.isInside(InputHandler.mousePos) && event.isPressed()) {
            InputHandler.addMousePressSub((event: InputEvent) => { val b = event.isReleased(); sliding = !b; (b, b)})
            InputHandler.addMouseMoveSub(slide)
            sliding = true
            calcValue(InputHandler.mousePos)
            true
        } else if(isInside(InputHandler.mousePos) && event.isScrolling()) {
            value = Vals.restrain(value - event.mods * 0.1f, 0, 1)
            func(value)
            true
        } else false
    }

    def calcValue(vec: Vector2f): Unit = {
        val p = getPos
        value = if(horizontal) Vals.restrain((vec.x - p.x) / size.x, 0, 1) else Vals.restrain((vec.y - p.y) / size.y, 0, 1)
        func(value)
    }

    def slide(event: InputEvent) = {
        if(sliding) calcValue(new Vector2f(event.action, event.mods))
        (!sliding, false)
    }

}
