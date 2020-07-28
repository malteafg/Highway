package utils.graphics

import utils.math.{Vec3, VecUtils}

class Mesh(vertices: Array[Float], indices: Array[Int], layout: Array[Int]) {

    val va = new VertexArray
    val vb = new VertexBuffer(vertices)
    val vertexLayout = new VertexBufferLayout
    val ib = new IndexBuffer(indices, indices.length)
    layout.foreach(f => vertexLayout.pushFloat(f))
    va.addBuffer(vb, vertexLayout)

    /**
     * Constructors
     */
    def this(vertices: Array[Vec3], indices: Array[Int], layout: Array[Int]) {
        this(VecUtils.toFloatArray(vertices), indices, layout)
    }

    /**
     * Functions
     */
    def delete(): Unit = {
        va.delete()
        vb.delete()
        ib.delete()
    }

}

object Mesh {

    def generateCube(size: Float): Mesh = {
        val hsize = size / 2

        val vertices = Array(
            -hsize, -hsize, -hsize,
             hsize, -hsize, -hsize,
             hsize,  hsize, -hsize,
            -hsize,  hsize, -hsize,
            -hsize,  hsize,  hsize,
             hsize,  hsize,  hsize,
             hsize, -hsize,  hsize,
            -hsize, -hsize,  hsize
        )

        val indices = Array(
            0, 3, 2,
            2, 1, 0,
            3, 4, 5,
            5, 2, 3,
            1, 2, 5,
            5, 6, 1,
            7, 4, 3,
            3, 0, 7,
            6, 1, 0,
            0, 7, 6,
            6, 5, 4,
            4, 7, 6
        )


        new Mesh(vertices, indices, Array(3))
    }

    def generateCircle(detail: Int = 30, radius: Float): Mesh = {
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