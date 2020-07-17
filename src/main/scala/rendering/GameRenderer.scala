package rendering

import game.Game
import org.lwjgl.opengl.GL11.{GL_TRIANGLES, GL_UNSIGNED_INT, glDrawElements}
import utils.Vals
import utils.graphics.{IndexBuffer, Shader, VertexArray, VertexBuffer, VertexBufferLayout}
import utils.loader.OBJLoader
import utils.math.Matrix4f

object GameRenderer {

    val va = new VertexArray
    val vb = new VertexBuffer(Array(    -0.5f, 0,  0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        0f,  1f, -0.5f, 1.0f, 1.0f, 1.0f, 1.0f,
        0.5f, 0f,    0f, 1.0f, 0.0f, 1.0f, 1.0f,
        0f, 0f,   -1f, 0.0f, 1.0f, 1.0f, 1.0f))
    val layout = new VertexBufferLayout
    val ib = new IndexBuffer(Array(0, 1, 2, 2, 3, 0, 0, 1, 3, 3, 2, 1), 12)
    var transformationMatrix = Matrix4f.place(0, 1, 0, Vals.toRadians(90))

    layout.pushFloat(3)
    layout.pushFloat(4)
    va.addBuffer(vb, layout)

    val mesh = OBJLoader.loadModel("sphere")

    def render(game: Game, camera: Camera) = {
    
        transformationMatrix
        transformationMatrix
        Shader.get("Pyramid").bind()
        Shader.get("Pyramid").loadUniformMat4f("transformationMatrix", transformationMatrix)
        Shader.get("Pyramid").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        draw(va, ib)
        Shader.get("sphere").bind()
        Shader.get("sphere").loadUniformMat4f("transformationMatrix", transformationMatrix.translate(5, 0, 0))
        Shader.get("sphere").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        draw(mesh.va, mesh.ib)

        game.terrain.render(camera.getViewMatrix)


    }

    def draw(va: VertexArray, ib: IndexBuffer): Unit = {
        va.bind()
        ib.bind()
        glDrawElements(GL_TRIANGLES, ib.getCount(), GL_UNSIGNED_INT, 0)
        va.unbind()
    }

}
