package graphics

import org.lwjgl.opengl.GL15.{GL_ARRAY_BUFFER, GL_STATIC_DRAW, glBindBuffer, glBufferData, glGenBuffers}

class VertexBuffer(data: Array[Float]) {

    val renderID = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, renderID)
    glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)

    def bind(): Unit = glBindBuffer(GL_ARRAY_BUFFER, renderID)
    def unbind(): Unit = glBindBuffer(GL_ARRAY_BUFFER, 0)

}
