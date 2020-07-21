package utils.math

import java.nio.FloatBuffer

case class Mat2(
                 m00: Float = 0, m01: Float = 0,
                 m10: Float = 0, m11: Float = 0
               ) {

    /**
     * Creates a 2x2 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     */
    def this(col1: Vec2, col2: Vec2) = this(col1.x, col1.y, col2.x, col2.y)

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    def add(other: Mat2): Mat2 = {
        Mat2(
            m00 = this.m00 + other.m00,
            m10 = this.m10 + other.m10,
            m01 = this.m01 + other.m01,
            m11 = this.m11 + other.m11
        )
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    def negate: Mat2 = multiply(-1f)

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    def subtract(other: Mat2): Mat2 = this.add(other.negate)

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    def multiply(scalar: Float): Mat2 = {
        Mat2(
            m00 = this.m00 * scalar,
            m10 = this.m10 * scalar,
            m01 = this.m01 * scalar,
            m11 = this.m11 * scalar
        )
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def multiply(vector: Vec2): Vec2 = {
        val x = this.m00 * vector.x + this.m01 * vector.y
        val y = this.m10 * vector.x + this.m11 * vector.y
        Vec2(x, y)
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    def multiply(other: Mat2): Mat2 = {
        Mat2(
            m00 = this.m00 * other.m00 + this.m01 * other.m10,
            m10 = this.m10 * other.m00 + this.m11 * other.m10,
            m01 = this.m00 * other.m01 + this.m01 * other.m11,
            m11 = this.m10 * other.m01 + this.m11 * other.m11
        )
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    def transpose: Mat2 = {
        Mat2(
            m00 = this.m00,
            m10 = this.m01,
            m01 = this.m10,
            m11 = this.m11
        )
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(m00).put(m10)
        buffer.put(m01).put(m11)
        buffer.flip
    }

}