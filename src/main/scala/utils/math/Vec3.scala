package utils.math

import java.nio.FloatBuffer

case class Vec3(x: Float = 0, y: Float = 0, z: Float = 0) {

    /**
     * Calculates the squared length of the vector.
     *
     * @return Squared length of this vector
     */
    def lengthSquared: Float = x * x + y * y + z * z

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
    def normalize: Vec3 = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vec3): Vec3 = {
        val x = this.x + other.x
        val y = this.y + other.y
        val z = this.z + other.z
        Vec3(x, y, z)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vec3 = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vec3): Vec3 = this.add(other.negate)

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vec3 = {
        val x = this.x * scalar
        val y = this.y * scalar
        val z = this.z * scalar
        Vec3(x, y, z)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vec3 = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vec3): Float = this.x * other.x + this.y * other.y + this.z * other.z

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Cross product of this x other
     */
    def cross(other: Vec3): Vec3 = {
        val x = this.y * other.z - this.z * other.y
        val y = this.z * other.x - this.x * other.z
        val z = this.x * other.y - this.y * other.x
        Vec3(x, y, z)
    }

    /**
     * Calculates the vector given by the projection of this vector
     * onto the parameter vector.
     *
     * @param target The other vector
     * @return The projection of this vector
     */
    def proj(target: Vec3): Vec3 = {
        target.scale(dot(target) / target.dot(target))
    }

    def thisOrThat(func: Vec3 => Boolean, replacement: Vec3): Vec3 = if(func(this)) this else replacement

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vec3, alpha: Float): Vec3 = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(x).put(y).put(z)
        buffer.flip
    }

    /**
     * Returns a vector with the remainders of the old vector.
     */
    def mod(mod: Float): Vec3 = Vec3(x % mod, y % mod, z % mod)

    /**
     * If the values in the vector is more than half the entered number, it will subtract the number
     */
    def cent(num: Float): Vec3 = {
        var tx = if (x > num / 2f) x - num
        else x
        tx = if (x < -num / 2f) x + num
        else tx
        var ty = if (y > num / 2f) y - num
        else y
        ty = if (y < -num / 2f) y + num
        else ty
        var tz = if (z > num / 2f) z - num
        else z
        tz = if (z < -num / 2f) z + num
        else tz
        Vec3(tx, ty, tz)
    }

    def x(newX: Float): Vec3 = Vec3(newX, y, z)
    def y(newY: Float): Vec3 = Vec3(x, newY, z)
    def z(newZ: Float): Vec3 = Vec3(x, y, newZ)

    def rightHand(): Vec3 = Vec3(z, y, -x)
    def leftHand(): Vec3 = Vec3(-z, y, x)

    override def toString = s"$x, $y, $z"

}
