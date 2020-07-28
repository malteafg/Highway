package game.roads

import utils.graphics.Mesh
import utils.math.Vec3

case class RoadNode(position: Vec3, direction: Vec3) {



}

object RoadNode {

    val mesh = generateMesh(radius = 1f)

    def generateMesh(detail: Int = 30, radius: Float): Mesh = {
        val vertices = new Array[Float](detail * 3 + 3)
        val indices = new Array[Int](detail * 3)

        vertices(0) = 0
        vertices(1) = 0
        vertices(2) = 0

        for(i <- 3 until vertices.length by 3) {
            val angle = 2f * (i - 3) / (vertices.length - 3) * Math.PI.toFloat
            vertices(i + 0) = Math.cos(angle).toFloat * radius
            vertices(i + 1) = 0
            vertices(i + 2) = Math.sin(angle).toFloat * radius
        }

        for(i <- indices.indices by 3) {
            indices(i + 0) = 0
            indices(i + 1) = i / 3 + 1
            indices(i + 2) = if ((i / 3 + 2) >= (vertices.length / 3)) 1 else i / 3 + 2
        }


        new Mesh(vertices, indices, Array(3))
    }

}
