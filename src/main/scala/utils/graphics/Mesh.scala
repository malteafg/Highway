package utils.graphics

import utils.math.Vector3f

class Mesh(vertices: Array[Float], indices: Array[Int]) {

    val va = new VertexArray
    val vb = new VertexBuffer(vertices)
    val layout = new VertexBufferLayout
    val ib = new IndexBuffer(indices, indices.length)
    layout.pushFloat(3)
    va.addBuffer(vb, layout)

    /**
     * Constructors
     */
    def this(vertices: Array[Vector3f], indices: Array[Int]) {
        this(Vector3f.convertToFloatArray(vertices), indices)
    }

}
