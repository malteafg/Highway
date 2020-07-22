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
        this(VecUtils.convertToFloatArray(vertices), indices, layout)
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

}