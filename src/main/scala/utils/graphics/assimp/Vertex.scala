package utils.graphics.assimp

import utils.math.{Vec2, Vec3}

case class Vertex(pos: Vec3, normal: Vec3, texCoords: Vec2) {

}

object Vertex {

    def toFloatArray(vertices: Array[Vertex]): Array[Float] = {
        val result = new Array[Float](8 * vertices.length)

        for(i <- vertices.indices) {
            result(i * 8 + 0) = vertices(i).pos.x
            result(i * 8 + 1) = vertices(i).pos.y
            result(i * 8 + 2) = vertices(i).pos.z

            result(i * 8 + 3) = vertices(i).normal.x
            result(i * 8 + 4) = vertices(i).normal.y
            result(i * 8 + 5) = vertices(i).normal.z

            result(i * 8 + 6) = vertices(i).texCoords.x
            result(i * 8 + 7) = vertices(i).texCoords.y
        }

        result
    }

}