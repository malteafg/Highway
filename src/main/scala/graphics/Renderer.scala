package graphics

import input.InputHandler
import org.lwjgl.opengl.GL11._

class Renderer {

    def draw(va: VertexArray, ib: IndexBuffer): Unit = {
        va.bind()
        ib.bind()
        glDrawElements(GL_TRIANGLES, ib.getCount(), GL_UNSIGNED_INT, 0)
    }

    def clear(): Unit =  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

}
