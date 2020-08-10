package utils.math

import java.nio.FloatBuffer

import utils.Vals

case class Vec2(x: Float = 0, y: Float = 0) {

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
    def normalize: Vec2 = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vec2): Vec2 = {
        val x = this.x + other.x
        val y = this.y + other.y
        Vec2(x, y)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vec2 = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vec2): Vec2 = this.add(other.negate)
    def subtract(f: Float): Vec2 = Vec2(x - f, y - f)
    
    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vec2 = {
        val x = this.x * scalar
        val y = this.y * scalar
        Vec2(x, y)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vec2 = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vec2): Float = this.x * other.x + this.y * other.y

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vec2, alpha: Float): Vec2 = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Returns the angle between this and another vector in radians
     *
     * @param other The other vector
     * @return The angle between this and the other vector in radians
     */
    def angle(other: Vec2): Float = Math.acos(this.dot(other) / (this.length * other.length)).toFloat

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): FloatBuffer = {
        buffer.put(x).put(y)
        buffer.flip
    }

    def x(newX: Float): Vec2 = Vec2(newX, y)
    def y(newY: Float): Vec2 = Vec2(x, newY)

    def toScreenVector: Vec2 = Vec2(1.0f - 2.0f * x / Vals.WIDTH, 2.0f * y / Vals.HEIGHT - 1.0f)
    
    def fill(z: Float, w: Float): Vec4 = Vec4(x, y, z, w)
    
    override def toString = s"($x, $y)"

}
