package game

import rendering.{Camera, GameRenderer}
import utils.graphics.{IndexBuffer, Shader, VertexArray, VertexBuffer, VertexBufferLayout}
import utils.math.Matrix4f

import scala.collection.mutable.ListBuffer

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

    val lines = ListBuffer[TerrainLine]()

    def addLine(line: TerrainLine): Unit = lines.addOne(line)

}
