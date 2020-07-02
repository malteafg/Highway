package math

import java.nio.FloatBuffer

class Vector3f(var x: Float, var y: Float, var z: Float) {

    /**
     * Creates a default 3-tuple vector with all values set to 0.
     */
    def this() = this(0f, 0f, 0f)

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
    def normalize: Vector3f = divide(length)

    /**
     * Adds this vector to another vector.
     *
     * @param other The other vector
     * @return Sum of this + other
     */
    def add(other: Vector3f): Vector3f = {
        val x = this.x + other.x
        val y = this.y + other.y
        val z = this.z + other.z
        new Vector3f(x, y, z)
    }

    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    def negate: Vector3f = scale(-1f)

    /**
     * Subtracts this vector from another vector.
     *
     * @param other The other vector
     * @return Difference of this - other
     */
    def subtract(other: Vector3f): Vector3f = this.add(other.negate)

    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar product of this * scalar
     */
    def scale(scalar: Float): Vector3f = {
        val x = this.x * scalar
        val y = this.y * scalar
        val z = this.z * scalar
        new Vector3f(x, y, z)
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param scalar Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    def divide(scalar: Float): Vector3f = scale(1f / scalar)

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Dot product of this * other
     */
    def dot(other: Vector3f): Float = this.x * other.x + this.y * other.y + this.z * other.z

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return Cross product of this x other
     */
    def cross(other: Vector3f): Vector3f = {
        val x = this.y * other.z - this.z * other.y
        val y = this.z * other.x - this.x * other.z
        val z = this.x * other.y - this.y * other.x
        new Vector3f(x, y, z)
    }

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     *
     * @param other The other vector
     * @param alpha The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    def lerp(other: Vector3f, alpha: Float): Vector3f = this.scale(1f - alpha).add(other.scale(alpha))

    /**
     * Stores the vector in a given Buffer.
     *
     * @param buffer The buffer to store the vector data
     */
    def toBuffer(buffer: FloatBuffer): Unit = {
        buffer.put(x).put(y).put(z)
        buffer.flip
    }

    /**
     * Prints the vector to the console.
     */
    def printVec(): Unit = System.out.println(x + " " + y + " " + z)

    /**
     * Returns a vector with the remainders of the old vector.
     */
    def mod(mod: Float) = new Vector3f(x % mod, y % mod, z % mod)

    /**
     * If the values in the vector is more than half the entered number, it will subtract the number
     */
    def cent(num: Float): Vector3f = {
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
        new Vector3f(tx, ty, tz)
    }

    def copy = new Vector3f(x, y, z)

    def set(x: Float, y: Float, z: Float): Unit = {
        this.x = x
        this.y = y
        this.z = z
    }

}
