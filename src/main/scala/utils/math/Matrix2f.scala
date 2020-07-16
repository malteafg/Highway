package utils.math

import java.nio.FloatBuffer

class Matrix2f(var m00: Float, var m01: Float,
               var m10: Float, var m11: Float) {

    /**
     * Create a 2x2 identity matrix.
     */
    def this() = this(1f, 0f, 0f, 1f)

    /**
     * Creates a 2x2 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     */
    def this(col1: Vector2f, col2: Vector2f) = this(col1.x, col1.y, col2.x, col2.y)

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Sum of this + other
     */
    def add(other: Matrix2f): Matrix2f = {
        val result = new Matrix2f
        result.m00 = this.m00 + other.m00
        result.m10 = this.m10 + other.m10
        result.m01 = this.m01 + other.m01
        result.m11 = this.m11 + other.m11
        result
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    def negate: Matrix2f = multiply(-1f)

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     * @return Difference of this - other
     */
    def subtract(other: Matrix2f): Matrix2f = this.add(other.negate)

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     * @return Scalar product of this * scalar
     */
    def multiply(scalar: Float): Matrix2f = {
        val result = new Matrix2f
        result.m00 = this.m00 * scalar
        result.m10 = this.m10 * scalar
        result.m01 = this.m01 * scalar
        result.m11 = this.m11 * scalar
        result
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     * @return Vector product of this * other
     */
    def multiply(vector: Vector2f): Vector2f = {
        val x = this.m00 * vector.x + this.m01 * vector.y
        val y = this.m10 * vector.x + this.m11 * vector.y
        new Vector2f(x, y)
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     * @return Matrix product of this * other
     */
    def multiply(other: Matrix2f): Matrix2f = {
        val result = new Matrix2f
        result.m00 = this.m00 * other.m00 + this.m01 * other.m10
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10
        result.m01 = this.m00 * other.m01 + this.m01 * other.m11
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11
        result
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    def transpose: Matrix2f = {
        val result = new Matrix2f
        result.m00 = this.m00
        result.m10 = this.m01
        result.m01 = this.m10
        result.m11 = this.m11
        result
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    def toBuffer(buffer: FloatBuffer): Unit = {
        buffer.put(m00).put(m10)
        buffer.put(m01).put(m11)
        buffer.flip
    }

}
