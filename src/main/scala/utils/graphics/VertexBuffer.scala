package utils.graphics

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL45._

class VertexBuffer(data: Array[Float]) {

    val renderID = glCreateBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, renderID)
    glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)

    def bind(): Unit = glBindBuffer(GL_ARRAY_BUFFER, renderID)
    def unbind(): Unit = glBindBuffer(GL_ARRAY_BUFFER, 0)

    def delete(): Unit = glDeleteBuffers(renderID)

}
