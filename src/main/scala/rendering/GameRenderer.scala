package rendering

import game.{Game, GameHandler, Sphere}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL43._
import org.lwjgl.opengl.GL45._
import utils.graphics.{IndexBuffer, Mesh, Shader, VertexArray}
import utils.math.{Mat4, Vec2, Vec4, VecUtils}

object GameRenderer {

    var darkEdges = false

    private val terrainShader = Shader.get("terrain")
    private val skybox = new Skybox

    val pos1ID = glCreateBuffers()
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, pos1ID)
    glBufferData(GL_SHADER_STORAGE_BUFFER, 20 * 4, GL_DYNAMIC_DRAW)
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, pos1ID)

    val pos2ID = glCreateBuffers()
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, pos2ID)
    glBufferData(GL_SHADER_STORAGE_BUFFER, 20 * 4, GL_DYNAMIC_DRAW)
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, pos2ID)

    val widthID = glCreateBuffers()
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, widthID)
    glBufferData(GL_SHADER_STORAGE_BUFFER, 10 * 4, GL_DYNAMIC_DRAW)
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 4, widthID)

    val colorID = glCreateBuffers()
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, colorID)
    glBufferData(GL_SHADER_STORAGE_BUFFER, 40 * 4, GL_DYNAMIC_DRAW)
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 5, colorID)
    glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0)


    def render(game: Game, camera: Camera): Unit = {
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)

        Shader.get("sphere").bind()
        Shader.get("sphere").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("sphere").loadUniformVec3f("cameraPos", camera.getCameraPos)
        Shader.get("sphere").loadUniformBoolean("darkEdge", darkEdges)
        game.spheres.foreach(s => {
            Shader.get("sphere").loadUniformMat4f("transformationMatrix", Mat4.translate(s.position))
            Shader.get("sphere").loadUniformVec4f("color", s.color)
            draw(Sphere.mesh.va, Sphere.mesh.ib)
        })

        terrainShader.bind()
        terrainShader.loadUniformMat4f("viewMatrix", camera.getViewMatrix)

        // load lines for terrain
        val lines = game.terrain.lines
        val pos1 = new Array[Vec2](lines.length)
        val pos2 = new Array[Vec2](lines.length)
        val width = new Array[Float](lines.length)
        val color = new Array[Vec4](lines.length)
        var counter = 0
        lines.foreach(l => {
            val line = l.getLine()
            pos1(counter) = line._1
            pos2(counter) = line._2
            width(counter) = line._3
            color(counter) = line._4
            counter += 1
        })

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, pos1ID)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, VecUtils.toFloatArray(pos1))

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, pos2ID)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, VecUtils.toFloatArray(pos2))

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, widthID)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, width)

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, colorID)
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, VecUtils.toFloatArray(color))

        draw(game.terrain.terrainMesh)

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0)

        // render road segments
        Shader.get("road").bind()
        Shader.get("road").loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("road").loadUniformVec4f("in_Color", Vec4(0.4f, 0.4f, 0.4f, 1.0f))
        for(s <- game.roads) {
            draw(s.mesh.va, s.mesh.ib)
        }
        Shader.get("road").loadUniformVec4f("in_Color", Vec4(0.3f, 0.3f, 0.9f, 0.5f))
        if(GameHandler.previewRoad != null) draw(GameHandler.previewRoad.getMesh)

        glDisable(GL_CULL_FACE)

        // render skybox
        skybox.shader.bind()
        skybox.shader.loadUniformMat4f("viewMatrix", camera.getViewMatrix)
        skybox.shader.loadUniformMat4f("transformationMatrix", Mat4.translate(0, -100, 0))
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, skybox.texture.getTextureID)
        draw(skybox.mesh)
        glActiveTexture(0)
    }

    def draw(mesh: Mesh): Unit = draw(mesh.va, mesh.ib)

    def draw(va: VertexArray, ib: IndexBuffer): Unit = {
        va.bind()
        ib.bind()
        glDrawElements(GL_TRIANGLES, ib.getCount(), GL_UNSIGNED_INT, 0)
        va.unbind()
    }

}
