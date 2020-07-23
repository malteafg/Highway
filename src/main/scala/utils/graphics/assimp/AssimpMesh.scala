package utils.graphics.assimp

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL45._

class AssimpMesh(vertices: Array[Vertex], indices: Array[Int], textures: Array[AssimpTexture]) {

    val VAO = glCreateVertexArrays()
    val VBO = glCreateBuffers()
    val EBO = glCreateBuffers()

    glBindVertexArray(VAO)
    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    glBufferData(GL_ARRAY_BUFFER, Vertex.toFloatArray(vertices), GL_STATIC_DRAW)

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 3, 0)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 3, 4 * 3)

    glEnableVertexAttribArray(2)
    glVertexAttribPointer(2, 2, GL_FLOAT, false, 4 * 2, 4 * 3 + 4 * 3)

    glBindVertexArray(0)

}
