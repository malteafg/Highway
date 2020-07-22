package rendering

import game.{Game, GameHandler, Sphere}
import org.lwjgl.opengl.GL11.{GL_BACK, GL_CULL_FACE, GL_TRIANGLES, GL_UNSIGNED_INT, glBindTexture, glCullFace, glDisable, glDrawElements, glEnable}
import org.lwjgl.opengl.GL13.{GL_TEXTURE0, GL_TEXTURE_CUBE_MAP, glActiveTexture}
import utils.graphics.{IndexBuffer, Mesh, Shader, VertexArray}
import utils.math.{Mat4, Vec2, Vec4}

object GameRenderer {

    var darkEdges = false

    private val terrainShader = Shader.get("terrain")
    private val skybox = new Skybox

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
        terrainShader.setUniformVec2fa("pos1", pos1)
        terrainShader.setUniformVec2fa("pos2", pos2)
        terrainShader.setUniform1fa("width", width)
        terrainShader.setUniformVec4fa("color", color)
        draw(game.terrain.terrainMesh)

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
