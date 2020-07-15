package math

import java.nio.FloatBuffer

class Vector4f(var x: Float, var y: Float, var z: Float, var w: Float) {

    /**
     * Creates a default 4-tuple vector with all values set to 0.
     */
    def this() = this(0f, 0f, 0f, 0f)

    /**
     * Creates a 4-tuple vector with specified values.
     *
     * @param v        Vector3f value
     * @param w        w value
     */
    def this(v: Vector3f, w: Float) = this(v.x, v.y, v.z, w)

    /**
     * Calculates the squared length of the vector.
     *
     * @return Squared length of this vector
     */
    def lengthSquared: Float = x * x + y * y + z * z + w * w

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
    def normalize: Vector4f = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vector4f): Vector4f = {
        val x = this.x + other.x
        val y = this.y + other.y
        val z = this.z + other.z
        val w = this.w + other.w
        new Vector4f(x, y, z, w)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vector4f = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vector4f): Vector4f = this.add(other.negate)

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vector4f = {
        val x = this.x * scalar
        val y = this.y * scalar
        val z = this.z * scalar
        val w = this.w * scalar
        new Vector4f(x, y, z, w)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vector4f = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vector4f): Float = this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vector4f, alpha: Float): Vector4f = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): Unit = {
        buffer.put(x).put(y).put(z).put(w)
        buffer.flip
    }

    def copy = new Vector4f(x, y, z, w)

    def set(x: Float, y: Float, z: Float, w: Float): Unit = {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    @Override
    override def toString: String = s"$x, $y, $z, $w"

}
