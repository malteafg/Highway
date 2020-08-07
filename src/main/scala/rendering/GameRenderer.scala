package rendering

import game.roads.RoadNode
import game.terrain.TerrainLine
import game.tools.{NodeSnapper, State, Tools}
import game.{Game, Sphere}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL43._
import utils.Vals
import utils.graphics.{IndexBuffer, Mesh, Shader, Texture, VertexArray}
import utils.loader.OBJLoader
import utils.math.{Mat4, Vec2, Vec4, VecUtils}

object GameRenderer {

    var darkEdges = false

    private val terrainShader = Shader.get("terrain")
    private val skybox = new Skybox
    private val arrow = OBJLoader.loadModel("arrow")

    def render(game: Game, camera: Camera, tool: State): Unit = {
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)

        Shader.get("sphere").bind()
        Shader.get("sphere").uniformMat4f("viewMatrix", camera.getViewMatrix)
        Shader.get("sphere").uniformVec3f("cameraPos", camera.getCameraPos)
        Shader.get("sphere").uniform1b("darkEdge", darkEdges)
        game.roads.foreach(r => {
            r.controlPoints.foreach(c => {
                Shader.get("sphere").uniformMat4f("transformationMatrix", Mat4.translate(c.y(2)).scale(4))
                Shader.get("sphere").uniformVec4f("color", Vec4(0.7f, 0.2f, 0.9f, 1f))
                draw(Sphere.mesh.va, Sphere.mesh.ib)
            })
        })

        terrainShader.bind()
        terrainShader.uniformMat4f("viewMatrix", camera.getViewMatrix)

        // load lines for terrain
        val lines = if (Tools.isFree) List[TerrainLine]() else tool.getGuidelinesToRender
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
        Texture.get("cleanRoad").bind()
        Texture.get("normalMap").bind(1)
        for(r <- game.roads) {
            draw(r.mesh, Tools.roadMode)
        }

        if (!Tools.isFree) {
            if (Tools.current.getRoadMeshesToRender.nonEmpty) {
                Shader.get("road").uniformVec4f("in_Color", Vec4(0.3f, 0.3f, 0.9f, 0.8f))
                Tools.current.getRoadMeshesToRender.foreach(m => draw(m, Tools.roadMode))
            }

            // render nodes
            Shader.get("node").bind()
            Shader.get("node").uniformMat4f("viewMatrix", camera.getViewMatrix)
            Shader.get("node").uniformVec4f("in_Color", Vec4(0.0f, 0.8f, 0.8f, 0.5f))
            if (NodeSnapper.getSnappedNode == null) {
                for(n <- game.nodes) {
                    Shader.get("node").uniformMat4f("transformationMatrix", Mat4.translate(n.position.y(Vals.ROAD_HEIGHT * 1.5f)).scale(n.getWidth))
                    draw(RoadNode.mesh)
                }
            }
            else {
                for(n <- NodeSnapper.getSnappedNode.getLaneNodes) {
                    Shader.get("node").uniformMat4f("transformationMatrix",
                        Mat4.translate(n.position.y(Vals.ROAD_HEIGHT * 1.5f)).scale(Vals.LARGE_LANE_WIDTH))
                    draw(RoadNode.mesh)
                }
            }
            Shader.get("basic").bind()
            Shader.get("basic").uniformMat4f("viewMatrix", camera.getViewMatrix)
            Shader.get("basic").uniformVec4f("in_Color", Vec4(0.8f, 0.2f, 0.3f, 0.6f))
            if (NodeSnapper.getSnappedNode == null) {
                for(n <- game.nodes) {
                    Shader.get("basic").uniformMat4f("transformationMatrix",
                        Mat4.translate(n.position.y(5)).multiply(Mat4.direction(n.direction)).scale(n.getWidth / 4))
                    draw(arrow)
                }
            }
            else {
                for(n <- NodeSnapper.getSnappedNode.getLaneNodes) {
                    Shader.get("basic").uniformMat4f("transformationMatrix",
                        Mat4.translate(n.position.y(5)).multiply(Mat4.direction(n.direction)).scale(Vals.LARGE_LANE_WIDTH / 4))
                    draw(arrow)
                }
            }
        }

        Texture.get("cleanRoad").unbind()
        Texture.get("normalMap").unbind(1)

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

    def draw(mesh: Mesh): Unit = draw(mesh.va, mesh.ib, GL_TRIANGLES)
    def draw(mesh: Mesh, mode: Int): Unit = draw(mesh.va, mesh.ib, mode)
    def draw(va: VertexArray, ib: IndexBuffer): Unit = draw(va, ib, GL_TRIANGLES)

    def draw(va: VertexArray, ib: IndexBuffer, mode: Int): Unit = {
        va.bind()
        ib.bind()
        glDrawElements(mode, ib.getCount, GL_UNSIGNED_INT, 0)
        va.unbind()
    }

}
