package ui.components

import input.InputHandler
import utils.math.{Vector2f, Vector4f}
import utils.Vals
import utils.graphics.Texture

import scala.collection.mutable

class UIComponent(protected val parent: UIComponent, protected var pos: Vector2f, protected var size: Vector2f, protected val color: Vector4f) {

    var tex: Texture = null

    val children = mutable.ListBuffer.empty[UIComponent]

    if(parent != null) parent.children.addOne(this)
    pos = pos.scale(Vals.UNIT)
    size = size.scale(Vals.UNIT)
    var active = true

    /*
     * Constructors
     */
    def this(par: UIComponent, x: Float, y: Float, width: Float, height: Float, c: Vector4f) {
        this(par, new Vector2f(x, y), new Vector2f(width, height), c)
    }

    /*
     * Functions
     */
    def isInside(vec: Vector2f): Boolean = {
        val p = getPos
        vec.x > p.x && vec.y > p.y && vec.x < p.x + size.x && vec.y < p.y + size.y
    }

    def click(event: (Int, Int, Int)): Boolean = {
        var b = false
        if(isInside(InputHandler.mousePos)) for (child <- children) b = b || (active && child.click(event))
        b
    }

    def addTexture(tex: Texture) = this.tex = tex

    /*
     * Getters and Setters
     */
    def getChildren() = children
    def getPos: Vector2f = if(parent != null) parent.getPos.add(pos) else pos
    def getSize: Vector2f = size
    def getColor: Vector4f = color

    def isActive(): Boolean = if(parent == null || !active) active else parent.isActive()
    def activate() = active = true
    def deactivate() = active = false

    def depth(): Int = if(parent == null) 0 else parent.depth() + 1

}
