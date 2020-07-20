package utils.graphics

import utils.math.Vector3f

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
    def this(vertices: Array[Vector3f], indices: Array[Int], layout: Array[Int]) {
        this(Vector3f.convertToFloatArray(vertices), indices, layout)
    }

}
