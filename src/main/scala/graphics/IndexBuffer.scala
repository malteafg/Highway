package graphics

import org.lwjgl.opengl.GL15.{GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW, glBindBuffer, glBufferData, glGenBuffers}

class IndexBuffer(data: Array[Int], count: Int) {

    val renderID = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderID)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW)

    def bind(): Unit = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderID)
    def unbind(): Unit = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    def getCount(): Int = count

}
