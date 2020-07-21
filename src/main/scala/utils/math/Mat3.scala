package utils.math

import java.nio.FloatBuffer

case class Mat3(
            m00: Float = 0, m01: Float = 0, m02: Float = 0,
            m10: Float = 0, m11: Float = 0, m12: Float = 0,
            m20: Float = 0, m21: Float = 0, m22: Float = 0
          ) {

    /**
     * Creates a 3x3 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     */
    def this(col1: Vec3, col2: Vec3, col3: Vec3) {
        this(
            m00 = col1.x,
            m10 = col1.y,
            m20 = col1.z,
            m01 = col2.x,
            m11 = col2.y,
            m21 = col2.z,
            m02 = col3.x,
            m12 = col3.y,
            m22 = col3.z
        )
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    def add(other: Mat3): Mat3 = {
        Mat3(
            m00 = this.m00 + other.m00,
            m10 = this.m10 + other.m10,
            m20 = this.m20 + other.m20,
            m01 = this.m01 + other.m01,
            m11 = this.m11 + other.m11,
            m21 = this.m21 + other.m21,
            m02 = this.m02 + other.m02,
            m12 = this.m12 + other.m12,
            m22 = this.m22 + other.m22
        )
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    def negate: Mat3 = multiply(-1f)

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    def subtract(other: Mat3): Mat3 = this.add(other.negate)

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    def multiply(scalar: Float): Mat3 = {
        Mat3(
            m00 = this.m00 * scalar,
            m10 = this.m10 * scalar,
            m20 = this.m20 * scalar,
            m01 = this.m01 * scalar,
            m11 = this.m11 * scalar,
            m21 = this.m21 * scalar,
            m02 = this.m02 * scalar,
            m12 = this.m12 * scalar,
            m22 = this.m22 * scalar
        )
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def multiply(vector: Vec3): Vec3 = {
        val x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z
        val y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z
        val z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z
        Vec3(x, y, z)
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    def multiply(other: Mat3): Mat3 = {
        Mat3(
            m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20,
            m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20,
            m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20,
            m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21,
            m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21,
            m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21,
            m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22,
            m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22,
            m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22
        )
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    def transpose: Mat3 = {
        Mat3(
            m00 = this.m00,
            m10 = this.m01,
            m20 = this.m02,
            m01 = this.m10,
            m11 = this.m11,
            m21 = this.m12,
            m02 = this.m20,
            m12 = this.m21,
            m22 = this.m22
        )
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(m00).put(m10).put(m20)
        buffer.put(m01).put(m11).put(m21)
        buffer.put(m02).put(m12).put(m22)
        buffer.flip
    }
    
}

object Mat3 {

    /**
     * Returns the empty matrix.
     */
    def empty(): Mat4 = Mat4(0, 0, 0, 0, 0, 0, 0, 0, 0)

}
