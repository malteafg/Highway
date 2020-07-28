package utils.graphics

import org.lwjgl.opengl.GL11._
import utils.Vals

import scala.collection.mutable.ListBuffer

class VertexBufferLayout {

    case class VertexBufferElement(layoutType: Int, count: Int, normalized: Boolean)

    private val elements = new ListBuffer[VertexBufferElement]()
    private var stride: Int = 0

    def pushFloat(count: Int): Unit = {
        elements.addOne(VertexBufferElement(GL_FLOAT, count, normalized = false))
        stride += Vals.getSizeOf(GL_FLOAT) * count
    }

    def pushInt(count: Int): Unit = {
        elements.addOne(VertexBufferElement(GL_UNSIGNED_INT, count, normalized = false))
        stride += Vals.getSizeOf(GL_UNSIGNED_INT) * count
    }

    def pushChar(count: Int): Unit = {
        elements.addOne(VertexBufferElement(GL_UNSIGNED_BYTE, count, normalized = true))
        stride += Vals.getSizeOf(GL_UNSIGNED_BYTE) * count
    }

    def getElements: List[VertexBufferElement] = elements.toList
    def getStride: Int = stride

}
