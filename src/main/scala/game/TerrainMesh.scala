package game

import rendering.{Camera, GameRenderer}
import utils.graphics.{IndexBuffer, Shader, VertexArray, VertexBuffer, VertexBufferLayout}
import utils.math.Matrix4f

class TerrainMesh {

    // in meters
    final val size = 1000f

    val terrainShader = Shader.get("Terrain")

    val va = new VertexArray
    val vb = new VertexBuffer(Array(-size / 2, 0.0f, size / 2,  size / 2, 0.0f, size / 2, size / 2, 0.0f, -size / 2, -size / 2, 0.0f, -size / 2))
    val layout = new VertexBufferLayout
    layout.pushFloat(3)
    val ib = new IndexBuffer(Array(0, 1, 2, 2, 3, 0), 6)
    va.addBuffer(vb, layout)

    def render(viewMatrix: Matrix4f) = {
        terrainShader.bind()
        terrainShader.loadUniformMat4f("viewMatrix", viewMatrix)
        GameRenderer.draw(va, ib)
    }

}
