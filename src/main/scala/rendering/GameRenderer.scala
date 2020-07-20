package rendering

import java.nio.FloatBuffer

import game.{Game, Sphere}
import org.lwjgl.opengl.GL11.{GL_TRIANGLES, GL_UNSIGNED_INT, glDrawElements}
import utils.Vals
import utils.graphics.{IndexBuffer, Mesh, Shader, VertexArray, VertexBuffer, VertexBufferLayout}
import utils.loader.OBJLoader
import utils.math.{Matrix4f, Vector2f, Vector4f}

import scala.collection.mutable._

object GameRenderer {

    val va = new VertexArray
    val vb = new VertexBuffer(Array(    -0.5f, 0,  0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        0f,  1f, -0.5f, 1.0f, 1.0f, 1.0f, 1.0f,
        0.5f, 0f,    0f, 1.0f, 0.0f, 1.0f, 1.0f,
        0f, 0f,   -1f, 0.0f, 1.0f, 1.0f, 1.0f))
    val layout = new VertexBufferLayout
    val ib = new IndexBuffer(Array(0, 1, 2, 2, 3, 0, 0, 1, 3, 3, 2, 1), 12)
    var transformationMatrix = Matrix4f.place(0, 0, 0, Vals.toRadians(90))
    var darkEdges = false;

    layout.pushFloat(3)
    layout.pushFloat(4)
    va.addBuffer(vb, layout)

    val terrainShader = Shader.get("terrain")

    def render(game: Game, camera: Camera) = {
    
        Shader.get("pyramid").bind()
        Shader.get("pyramid").loadUniformMat4f("transformationMatrix", transformationMatrix)
        Shader.get("pyramid").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        draw(va, ib)
        Shader.get("sphere").bind()
        Shader.get("sphere").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("sphere").loadUniformVec3f("cameraPos", camera.getCameraPos)
        Shader.get("sphere").loadUniformBoolean("darkEdge", darkEdges)
        game.spheres.foreach(s => {
            Shader.get("sphere").loadUniformMat4f("transformationMatrix", Matrix4f.translate(s.position))
            Shader.get("sphere").loadUniformVec4f("color", s.color)
            draw(Sphere.mesh.va, Sphere.mesh.ib)
        })

        terrainShader.bind()
        terrainShader.loadUniformMat4f("viewMatrix", camera.getViewMatrix)

        // load lines for terrain
        val lines = game.terrain.lines
        val pos1 = new Array[Vector2f](lines.length)
        val pos2 = new Array[Vector2f](lines.length)
        val width = new Array[Float](lines.length)
        val color = new Array[Vector4f](lines.length)
        var counter = 0
        lines.foreach(l => {
            val line = l.getLine()
            pos1(counter) = line._1
            pos2(counter) = line._2
            width(counter) = line._3
            color(counter) = line._4
            counter += 1
        })
        terrainShader.setUniformVec2fa("pos1", pos1)
        terrainShader.setUniformVec2fa("pos2", pos2)
        terrainShader.setUniform1fa("width", width)
        terrainShader.setUniformVec4fa("color", color)
        draw(game.terrain.terrainMesh)

        // render road segments
        Shader.get("road").bind()
        Shader.get("road").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        for(s <- game.roads) {
            draw(s.mesh.va, s.mesh.ib)
        }
    }

    def draw(mesh: Mesh): Unit = draw(mesh.va, mesh.ib)

    def draw(va: VertexArray, ib: IndexBuffer): Unit = {
        va.bind()
        ib.bind()
        glDrawElements(GL_TRIANGLES, ib.getCount(), GL_UNSIGNED_INT, 0)
        va.unbind()
    }

}
