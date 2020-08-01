package ui.components

import input.{Feedback, InputEvent, InputHandler}
import utils.math.{Vec2, Vec4}
import utils.Vals

class Slider(val par: UIComponent, p: Vec2, s: Vec2, c: Vec4,
             val horizontal: Boolean, var value: Float, val func: Float => Unit) extends UIComponent(par, p, s, c) {

    var bar: UIComponent = null
    var sliding: Boolean = false

    /**
     * Constructors
     */
    def this(par: UIComponent, p: Vec2, s: Vec2, color: Vec4, horizontal: Boolean, topOrLeft: Boolean, thickness: Float, value: Float, func: Float => Unit) {
        this(par, p, s, color, horizontal, value, func)
        bar = new UIComponent(this, if(topOrLeft) new Vec2() else if(horizontal) new Vec2(0, s.y - thickness) else new Vec2(s.x - thickness, 0),
                              if(horizontal) new Vec2(s.x, thickness) else new Vec2(thickness, s.y), color)
    }

    /**
     * Functions
     */
    override def click(event: InputEvent): Boolean = {
        if(bar.isInside(InputHandler.mousePos) && event.isPressed) {
            InputHandler.addMousePressSub((event: InputEvent) => { val b = event.isReleased; sliding = !b; Feedback.custom(b, b)})
            InputHandler.addMouseMoveSub(slide)
            sliding = true
            calcValue(InputHandler.mousePos)
            true
        } else if(isInside(InputHandler.mousePos) && event.isScrolling) {
            value = Vals.restrain(value - event.mods * 0.1f, 0, 1)
            func(value)
            true
        } else false
    }

    def calcValue(vec: Vec2): Unit = {
        val p = getPos
        value = if(horizontal) Vals.restrain((vec.x - p.x) / size.x, 0, 1) else Vals.restrain((vec.y - p.y) / size.y, 0, 1)
        func(value)
    }

    def slide(event: InputEvent): Feedback = {
        if(sliding) calcValue(new Vec2(event.action, event.mods))
        Feedback.custom(!sliding, false)
    }

}
