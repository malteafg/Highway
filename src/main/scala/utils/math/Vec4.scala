package utils.math

import java.nio.FloatBuffer

case class Vec4(x: Float = 0, y: Float = 0, z: Float = 0, w: Float = 0) {

    /**
     * Creates a 4-tuple vector with specified values.
     *
     * @param v        Vector3f value
     * @param w        w value
     */
    def this(v: Vec3, w: Float) = this(v.x, v.y, v.z, w)

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
    def normalize: Vec4 = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vec4): Vec4 = {
        val x = this.x + other.x
        val y = this.y + other.y
        val z = this.z + other.z
        val w = this.w + other.w
        Vec4(x, y, z, w)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vec4 = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vec4): Vec4 = this.add(other.negate)

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vec4 = {
        val x = this.x * scalar
        val y = this.y * scalar
        val z = this.z * scalar
        val w = this.w * scalar
        Vec4(x, y, z, w)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vec4 = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vec4): Float = this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vec4, alpha: Float): Vec4 = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(x).put(y).put(z).put(w)
        buffer.flip
    }

    def x(newX: Float): Vec4 = Vec4(newX, y, z, w)
    def y(newY: Float): Vec4 = Vec4(x, newY, z, w)
    def z(newZ: Float): Vec4 = Vec4(x, y, newZ, w)
    def w(newW: Float): Vec4 = Vec4(x, y, z, newW)

    def xyz: Vec3 = Vec3(x, y, z)
    
    @Override
    override def toString: String = s"$x, $y, $z, $w"

}
