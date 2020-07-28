package game.terrain

import utils.graphics._
import utils.math.Vec4

import scala.collection.mutable.ListBuffer

class Terrain {

    final val color = Vec4(0.29f, 0.61f, 0.24f, 1.0f)

    // in meters
    final val mapsize = 1000f
    final val vertexLength = mapsize.toInt + 1

    // generate terrain
    val vertices = new Array[Float](vertexLength * vertexLength * 7)
    val indices = new Array[Int]((mapsize * mapsize * 6).toInt)
    for(x <- 0 until vertexLength) {
        for(y <- 0 until vertexLength) {
            val r = Math.random().toFloat * 0.06f + 0.97f
            vertices((x * vertexLength + y) * 7 + 0) = -mapsize / 2 + x
            vertices((x * vertexLength + y) * 7 + 1) = Math.random().toFloat * 0.1f * 0
            vertices((x * vertexLength + y) * 7 + 2) = -mapsize / 2 + y
            vertices((x * vertexLength + y) * 7 + 3) = color.x * r
            vertices((x * vertexLength + y) * 7 + 4) = color.y * r
            vertices((x * vertexLength + y) * 7 + 5) = color.z * r
            vertices((x * vertexLength + y) * 7 + 6) = 1.0f
            if(y < mapsize && x < mapsize) {
                indices((x * mapsize.toInt + y) * 6 + 0) = x * vertexLength + y
                indices((x * mapsize.toInt + y) * 6 + 1) = x * vertexLength + y + vertexLength
                indices((x * mapsize.toInt + y) * 6 + 2) = x * vertexLength + y + vertexLength + 1
                indices((x * mapsize.toInt + y) * 6 + 3) = x * vertexLength + y + vertexLength + 1
                indices((x * mapsize.toInt + y) * 6 + 4) = x * vertexLength + y + 1
                indices((x * mapsize.toInt + y) * 6 + 5) = x * vertexLength + y
            }
        }
    }

    val terrainMesh = new Mesh(vertices, indices, Array(3, 4))

    val lines = ListBuffer[TerrainLine]()

    def addLine(line: TerrainLine): Unit = lines.addOne(line)

}
