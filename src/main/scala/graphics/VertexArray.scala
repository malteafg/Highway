package graphics

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import utils.Vars

class VertexArray {

    val renderID = glGenVertexArrays()

    def addBuffer(vb: VertexBuffer, layout: VertexBufferLayout): Unit = {
        bind()
        vb.bind()
        val elements = layout.getElements()
        elements.foldLeft(0: Int, 0: Int) {(i, e) =>
            glEnableVertexAttribArray(i._1)
            glVertexAttribPointer(i._1, e.count, e.layoutType, e.normalized, layout.getStride(), i._2)
            (i._1 + 1, i._2 + e.count * Vars.getSizeOf(e.layoutType))
        }
    }

    def bind(): Unit = glBindVertexArray(renderID)
    def unbind(): Unit = glBindVertexArray(0)

}