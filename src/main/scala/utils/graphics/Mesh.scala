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
