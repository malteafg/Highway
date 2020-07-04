package math

import java.nio.FloatBuffer

class Vector2f(var x: Float, var y: Float) {

    /**
     * Creates a default 2-tuple vector with all values set to 0.
     */
    def this() = this(0, 0)

    /**
     * Calculates the squared length of the vector.
     *
     * @return Squared length of this vector
     */
    def lengthSquared: Float = x * x + y * y

    /**
     * Calculates the length of the vector.
     *
     * @return Length of this vector
     */
    def length: Float = Math.sqrt(lengthSquared).toFloat

    /**
     * Normalizes the vector.
     *
     * @return Normalized vector
     */
    def normalize: Vector2f = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vector2f): Vector2f = {
        val x = this.x + other.x
        val y = this.y + other.y
        new Vector2f(x, y)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vector2f = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vector2f): Vector2f = this.add(other.negate)

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vector2f = {
        val x = this.x * scalar
        val y = this.y * scalar
        new Vector2f(x, y)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vector2f = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vector2f): Float = this.x * other.x + this.y * other.y

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vector2f, alpha: Float): Vector2f = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): Unit = {
        buffer.put(x).put(y)
        buffer.flip
    }

    def copy = new Vector2f(x, y)

    def equals(other: Vector2f): Boolean = other.x == this.x && other.y == this.y

    def set(x: Float, y: Float): Unit = {
        this.x = x
        this.y = y
    }

    override def toString = {
        s"($x, $y)"
    }

}
