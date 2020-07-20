package utils.graphics

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL45._
import utils.Vals

class VertexArray {

    val renderID = glCreateVertexArrays()

    def addBuffer(vb: VertexBuffer, layout: VertexBufferLayout): Unit = {
        bind()
        vb.bind()
        layout.getElements().foldLeft(0: Int, 0: Int) {(i, e) =>
            glEnableVertexAttribArray(i._1)
            glVertexAttribPointer(i._1, e.count, e.layoutType, e.normalized, layout.getStride(), i._2)
            (i._1 + 1, i._2 + e.count * Vals.getSizeOf(e.layoutType))
        }
        unbind()
    }

    def bind(): Unit = glBindVertexArray(renderID)
    def unbind(): Unit = glBindVertexArray(0)

    def delete(): Unit = glDeleteVertexArrays(renderID)

}