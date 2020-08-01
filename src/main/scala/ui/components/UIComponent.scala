package ui.components

import input.InputHandler
import input.InputEvent
import utils.math.{Vec2, Vec4}
import utils.Vals
import utils.graphics.Texture

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class UIComponent(protected val parent: UIComponent, protected var pos: Vec2, protected var size: Vec2, protected val color: Vec4) {

    var tex: Texture = _

    private val children = mutable.ListBuffer.empty[UIComponent]

    if(parent != null) parent.children.addOne(this)
    pos = pos.scale(Vals.UNIT)
    size = size.scale(Vals.UNIT)
    var active = true

    /**
     * Constructors
     */
    def this(par: UIComponent, x: Float, y: Float, width: Float, height: Float, c: Vec4) {
        this(par, Vec2(x, y), Vec2(width, height), c)
    }

    /**
     * Functions
     */
    def isInside(vec: Vec2): Boolean = {
        val p = getPos
        active && vec.x > p.x && vec.y > p.y && vec.x < p.x + size.x && vec.y < p.y + size.y
    }

    def click(event: InputEvent): Boolean = {
        var b = false
        if(isInside(InputHandler.mousePos)) for (child <- children) b = b || child.click(event)
        b
    }

    def addTexture(tex: Texture): Unit = this.tex = tex

    /**
     * Getters and Setters
     */
    def getChildren: ListBuffer[UIComponent] = children
    def getPos: Vec2 = if(parent != null) parent.getPos.add(pos) else pos
    def getSize: Vec2 = size
    def getColor: Vec4 = color

    def isActive: Boolean = if(parent == null || !active) active else parent.isActive
    def activate(): Unit = active = true
    def deactivate(): Unit = active = false

    def depth(): Int = if(parent == null) 0 else parent.depth() + 1

}
