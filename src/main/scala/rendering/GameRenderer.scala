package rendering

import game.roads.RoadNode
import game.terrain.TerrainLine
import game.{Game, GameHandler, Sphere}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL43._
import utils.Vals
import utils.graphics.{IndexBuffer, Mesh, Shader, VertexArray}
import utils.math.{Mat4, Vec2, Vec4, VecUtils}

import scala.collection.mutable

object GameRenderer {

    var darkEdges = false

    private val terrainShader = Shader.get("terrain")
    private val skybox = new Skybox

    def render(game: Game, camera: Camera): Unit = {
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)

        Shader.get("sphere").bind()
        Shader.get("sphere").uniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("sphere").uniformVec3f("cameraPos", camera.getCameraPos)
        Shader.get("sphere").uniform1b("darkEdge", darkEdges)
        game.spheres.foreach(s => {
            Shader.get("sphere").uniformMat4f("transformationMatrix", Mat4.translate(s.position))
            Shader.get("sphere").uniformVec4f("color", s.color)
            draw(Sphere.mesh.va, Sphere.mesh.ib)
        })

        terrainShader.bind()
        terrainShader.uniformMat4f("viewMatrix", camera.getViewMatrix)

        // load lines for terrain
        val lines = mutable.ListBuffer[TerrainLine]()
        lines.addAll(game.terrain.lines ++ GameHandler.guidelines)
        if (GameHandler.mode != GameHandler.Free) lines.addOne(GameHandler.cursorMarker)
        val pos1 = new Array[Vec2](lines.length)
        val pos2 = new Array[Vec2](lines.length)
        val width = new Array[Float](lines.length)
        val color = new Array[Vec4](lines.length)
        var counter = 0
        lines.foreach(l => {
            pos1(counter) = l.getPos1
            pos2(counter) = l.getPos2
            width(counter) = l.getWidth
            color(counter) = l.getColor
            counter += 1
        })

        terrainShader.updateStorageBuffer(2, VecUtils.toFloatArray(pos1))
        terrainShader.updateStorageBuffer(3, VecUtils.toFloatArray(pos2))
        terrainShader.updateStorageBuffer(4, width)
        terrainShader.updateStorageBuffer(5, VecUtils.toFloatArray(color))
        terrainShader.uniform1i("numOfLines", lines.size)

        draw(game.terrain.terrainMesh)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0)

        // render road segments
        Shader.get("road").bind()
        Shader.get("road").uniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("road").uniformVec4f("in_Color", Vec4(0.4f, 0.4f, 0.4f, 1.0f))
        for(r <- game.roads) {
            draw(r.mesh.va, r.mesh.ib)
        }
        Shader.get("road").uniformVec4f("in_Color", Vec4(0.3f, 0.3f, 0.9f, 0.5f))
        if(GameHandler.previewRoad != null) draw(GameHandler.previewRoad.getMesh)

        Shader.get("node").bind()
        Shader.get("node").uniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("node").uniformVec4f("in_Color", Vec4(0.0f, 0.8f, 0.8f, 1.0f))

        for(n <- game.nodes) {
            Shader.get("node").uniformMat4f("transformationMatrix", Mat4.translate(n.position.y(Vals.ROAD_HEIGHT * 1.1f)))
            draw(RoadNode.mesh)
        }


        glDisable(GL_CULL_FACE)

        // render skybox
        skybox.shader.bind()
        skybox.shader.uniformMat4f("viewMatrix", camera.getViewMatrix)
        skybox.shader.uniformMat4f("transformationMatrix", Mat4.translate(0, -100, 0))
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
